package com.bcam.bcmonitor.extractor.bulk;

import com.bcam.bcmonitor.extractor.client.ReactiveClient;
import com.bcam.bcmonitor.model.AbstractBlock;
import com.bcam.bcmonitor.model.AbstractTransaction;
import com.bcam.bcmonitor.storage.BlockRepository;
import com.bcam.bcmonitor.storage.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public class BulkExtractorImpl<B extends AbstractBlock, T extends AbstractTransaction> implements BulkExtractor<B, T> {

    private static final Logger logger = LoggerFactory.getLogger(BulkExtractorImpl.class);

    final private BlockRepository<B> blockRepository;
    final private TransactionRepository<T> transactionRepository;

    final private ReactiveClient<B, T> client;


    public BulkExtractorImpl(
            BlockRepository<B> repository,
            TransactionRepository<T> transactionRepository,
            ReactiveClient<B, T> client) {

        this.blockRepository = repository;
        this.transactionRepository = transactionRepository;
        this.client = client;
    }


    // see https://github.com/reactor/reactive-streams-commons/issues/21#issuecomment-210178344

    /**
     * if fails, could delay manually Flux<document> findAll() {
     * return Flux.interval(Duration.ofSeconds(1))
     * .takeWhile(i -> i < 9)
     * .map(i -> i.intValue() + 1)
     * .map(Document::new);
     */
    @Override
    public Flux<B> saveBlocks(long fromHeight, long toHeight) {

        return fetchBlocks(fromHeight, toHeight)
                .concatMap(blockRepository::save)
                .doOnNext(bitcoinBlock -> logger.info("Saved block " + bitcoinBlock));
    }

    private Flux<B> fetchBlocks(long fromHeight, long toHeight) {
        int fromInt = (int) fromHeight;
        int count = (int) (toHeight - fromInt) + 1;

        logger.info("Count: " + count);

        return Flux.range(fromInt, count)
                .concatMap(client::getBlockHash)
                .doOnNext(hash -> logger.info("Got block hash from client " + hash))
                .concatMap(client::getBlock)
                .doOnNext(bitcoinBlock -> logger.info("Created block " + bitcoinBlock));
    }

    public Flux<T> saveTransactions(B block) {
        return fetchTransactions(block)
                .flatMap(transactionRepository::save)
                .doOnNext(txids -> logger.info("Saved txids " + txids));
    }

    private Flux<T> fetchTransactions(B block) {
        return Flux.fromIterable(block.getTxids())
                .doOnNext(txids -> logger.info("Got txids " + txids))
                .filter(tx -> tx != null && !tx.equals(""))
                .concatMap(client::getTransaction)
                .doOnNext(txids -> logger.info("Created transactions from client " + txids));
    }

    @Override
    public Flux<T> saveTransactions(Flux<B> blocks) {

        return blocks
                .map(AbstractBlock::getTxids)
                .flatMap(Flux::fromIterable)
                .flatMap(client::getTransaction)
                .flatMap(transactionRepository::save);
    }

    @Override
    public Disposable saveBlocksAndTransactions(long fromHeight, long toHeight) {

        return saveBlocks(fromHeight, toHeight)
                .doOnNext(
                        block -> saveTransactions(block)
                                .subscribe(transaction -> logger.info("Saved transaction " + transaction))
                )
                .subscribe(block -> logger.info("Saved block " + block));
    }

    @Override
    public Flux<T> saveBlocksAndTransactionsForward(long fromHeight, long toHeight) {

        logger.info("About to save and forward blocks and transactions from " + fromHeight + " to " + toHeight);

        return saveBlocks(fromHeight, toHeight)
                .concatMap(
                        this::saveTransactions
                );
    }

    @Override
    public String toString() {
        return "BulkExtractorImpl{" +
                "blockRepository=" + blockRepository + " " + blockRepository.getClass() +
                ", transactionRepository=" + transactionRepository + " " + transactionRepository.getClass() +
                ", client=" + client +
                '}';
    }
}
