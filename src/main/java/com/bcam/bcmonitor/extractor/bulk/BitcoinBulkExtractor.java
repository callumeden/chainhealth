package com.bcam.bcmonitor.extractor.bulk;

import com.bcam.bcmonitor.extractor.client.ReactiveBitcoinClient;
import com.bcam.bcmonitor.model.*;
import com.bcam.bcmonitor.storage.BlockRepository;
import com.bcam.bcmonitor.storage.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;


/**
 * https://github.com/spring-projects/spring-data-examples/blob/master/redis/reactive/src/test/java/example/springdata/redis/operations/ValueOperationsTests.java
 * <p>
 * batch downloads https://dzone.com/articles/bulk-operations-in-mongodb
 * api to select attributes for bulk downloads
 * <p>
 * <p>
 * 20 mins. cache
 * <p>
 * <p>
 * .delete .then(mono.just) https://codereview.stackexchange.com/questions/159139/is-my-implementation-of-a-simple-crud-service-with-spring-webflux-correct
 * <p>
 * <p>
 * <p>
 * can make a repo by creating client the to construtor
 */
@Component
public class BitcoinBulkExtractor implements BulkExtractor {

    private static final Logger logger = LoggerFactory.getLogger(BitcoinBulkExtractor.class);

    final private BlockRepository blockRepository;
    final private TransactionRepository transactionRepository;

    final private ReactiveBitcoinClient client;

    @Autowired
    public BitcoinBulkExtractor(BlockRepository blockRepository, TransactionRepository transactionRepository, ReactiveBitcoinClient bitcoinClient) {
        this.transactionRepository = transactionRepository;
        client = bitcoinClient;
        this.blockRepository = blockRepository;
    }


    // TODO also check fromHeight
    private long validateHeight(long toHeight) {

        BlockchainInfo info = client.getBlockchainInfo().block();
        assert info != null;

        if (toHeight > info.getBlocks()) {

            logger.info("Only " + info.getBlocks() + " blocks found. Requesting to block " + info.getBlocks() + " instead of block " + toHeight);
            toHeight = info.getBlocks();
        }

        return toHeight;

    }

    private Mono<Void> saveHash(long height) {
        logger.info("Saving hash for height " + height);

        return client
                .getBlockHash(height)
                .map(
                        hash -> {
                            logger.info("Got hash" + hash);
                            return hash;
                        }
                )
                .map(hash -> new BitcoinBlock(hash, height))
                .flatMap(blockRepository::save)
                .then();
    }

    // @Async
    public void saveHashes(long fromHeight, long toHeight) {

        logger.debug("About to save hashes between height " + fromHeight + " - " + toHeight);

        toHeight = validateHeight(toHeight);

        for (long i = fromHeight; i < toHeight; i++) {

            String hash = client.getBlockHash(i).block();

            logger.debug("Got hash from client " + hash);

            Disposable d = blockRepository
                    .save(new BitcoinBlock(hash, i))
                    .subscribe(bitcoinBlock -> logger.info("Inserted block" + bitcoinBlock));

            d.dispose();
        }
    }

    public Flux<BitcoinBlock> saveHashes2(long fromHeight, long toHeight) {

        // UnicastProcessor<String> eventPublisher = UnicastProcessor.create();
        //
        // eventPublisher.onNext();

        int fromInt = (int) fromHeight;
        int count = (int) (fromHeight - fromInt) + 1;


        return Flux.range(fromInt, count)
                .index((i, v) -> Tuples.of(i + fromHeight, v))
                .doOnNext(tup -> logger.info("TUPa" + tup.toString()))
                .map(tup -> Tuples.of(tup.getT1(), client.getBlockHash(tup.getT2())))
                .doOnNext(tup -> logger.info("TUPb" + tup.toString()))
                .map(tup -> {
                    String hash = tup.getT2().block();
                    return Tuples.of(tup.getT1(), hash);
                })
                .doOnNext(tup -> logger.info("TUPc" + tup.toString()))
                .map(tup -> new BitcoinBlock(tup.getT2(), tup.getT1()))
                .doOnNext(bitcoinBlock -> logger.info("Created block " + bitcoinBlock))
                .doOnNext(bitcoinBlock -> blockRepository.save(bitcoinBlock));

        // Flux<BitcoinBlock> fbb = Flux.range(fromInt, count)
        //         .index((i, v) -> Tuples.of(i + fromHeight, v))
        //         .map(tup -> Tuples.of(tup.getT1(), client.getBlockHash(tup.getT2())))
        //         .map(tup -> Tuples.of(tup.getT1(), tup.getT2().block()))
        //         .map(tup -> new BitcoinBlock(tup.getT2(), tup.getT1()));
        //
        // return blockRepository.saveAll(fbb);

    }

    /**
     *
     * @param block containing hashes of transactions to be saved
     * @return a flux of the saved transactions for further processing
     */
    public Flux<BitcoinTransaction> saveTransactions(BitcoinBlock block) {
        return Flux.fromIterable(block.getTxids())
                .map(x -> client.getTransaction(x))
                .flatMap(source -> source)
                .flatMap(bitcoinTransaction -> transactionRepository.save(bitcoinTransaction));
    }

    public Flux<BitcoinTransaction> saveTransactions(Flux<BitcoinBlock> blocks) {
        return blocks
                .map(block -> block.getTxids())
                .flatMap(listIds -> Flux.fromIterable(listIds))
                .flatMap(id -> client.getTransaction(id))
                .flatMap(bitcoinTransaction -> transactionRepository.save(bitcoinTransaction));
    }

    public Flux<BitcoinBlock> saveBlocks(long fromHeight, long toHeight) {


        int fromInt = (int) fromHeight;
        int count = (int) (toHeight - fromInt) + 1;

        System.out.println("Count: " + count);


        return Flux.range(fromInt, count)
                .map(height -> client.getBlockHash(height))
                .doOnNext(hash -> logger.info("Got hash " + hash))
                .flatMap(source -> source) // == merge()
                .flatMap(hash -> client.getBlock(hash))
                .doOnNext(bitcoinBlock -> logger.info("Created block " + bitcoinBlock))
                .flatMap(block -> blockRepository.save(block))
                .doOnNext(bitcoinBlock -> logger.info("Saved block " + bitcoinBlock));


                // .index((i, v) -> Tuples.of(i + fromHeight, v))
                // .doOnNext(tup -> logger.info("TUPa" + tup.toString()))
                // .map(tup -> Tuples.of(tup.getT1(), client.getBlockHash(tup.getT2())))
                // .doOnNext(tup -> logger.info("TUPb" + tup.toString()))
                // .map(tup -> Tuples.of(tup.getT1(), tup.getT2().block()))
                // .doOnNext(tup -> logger.info("TUPc" + tup.toString()))
                // .map(tup -> new BitcoinBlock(tup.getT2(), tup.getT1()))
                // .doOnNext(bitcoinBlock -> logger.info("Created block " + bitcoinBlock))
                // .doOnNext(bitcoinBlock -> blockRepository.save(bitcoinBlock));

        // Flux<BitcoinBlock> fbb = Flux.range(fromInt, count)
        //         .index((i, v) -> Tuples.of(i + fromHeight, v))
        //         .map(tup -> Tuples.of(tup.getT1(), client.getBlockHash(tup.getT2())))
        //         .map(tup -> Tuples.of(tup.getT1(), tup.getT2().block()))
        //         .map(tup -> new BitcoinBlock(tup.getT2(), tup.getT1()));
        //
        // return blockRepository.saveAll(fbb);

    }



    private Mono<BitcoinBlock> getStubBlock(long height) {
        return client.getBlockHash(height)
                .flatMap(h -> Mono.just(new BitcoinBlock(h, height)));

    }

    public Flux<BitcoinBlock> saveBlocksFromHashes(long fromHeight, long toHeight) {

        return blockRepository
                .findAllByHeightInRange(fromHeight, toHeight)
                .map(bitcoinBlock -> bitcoinBlock.getHash())
                // .doOnNext(hash -> logger.info("Mapped to hash " + hash))
                .map(hash -> client.getBlock(hash))
                .flatMap(source -> source)
                // .doOnNext(bitcoinBlock -> logger.info("Got block from client " + bitcoinBlock + " confirmations " + bitcoinBlock.getConfirmations()))
                .flatMap(block -> blockRepository.save(block));

    }


    // public Flux<Transaction>

    // public Flux<BitcoinBlock> saveBlocksFromHeights(long fromHeight, long toHeight) {
    //
    //     saveHashes(fromHeight, toHeight);
    //
    //     saveBlocksFromHashes(fromHeight, toHeight).subscribe(
    //             bitcoinBlock ->
    //     );
    //
    //
    //     // /**
    //     //  *
    //     //  * flux heights
    //     //  * collect list
    //     //  * then same as below
    //     //  *
    //     //  */
    //     //
    //     // int fromInt = (int) fromHeight;
    //     // int count = (int) (fromHeight - fromInt) + 1;
    //     //
    //     // List<String> hashes = new ArrayList<>();
    //     //
    //     // Flux.range(fromInt, count)
    //     //         .map(client::getBlockHash)
    //     //         .flatMap(source -> source)
    //     //         .subscribe(
    //     //                 hashes::add
    //     //         );
    //     //
    //     // return blockRepository
    //     //         .findAllByHeightInRange(fromHeight, toHeight)
    //     //         .map(bitcoinBlock -> bitcoinBlock.getHash())
    //     //         // .doOnNext(hash -> logger.info("Mapped to hash " + hash))
    //     //         .map(hash -> client.getBlock(hash))
    //     //         .flatMap(source -> source)
    //     //         // .doOnNext(bitcoinBlock -> logger.info("Got block from client " + bitcoinBlock + " confirmations " + bitcoinBlock.getConfirmations()))
    //     //         .flatMap(block -> blockRepository.save(block));
    //
    // }
}
