package com.bcam.bcmonitor.extractor.bulk;

import com.bcam.bcmonitor.extractor.client.ReactiveClient;
import com.bcam.bcmonitor.extractor.csv.BitcoinToCSV;
import com.bcam.bcmonitor.model.BitcoinBlock;
import com.bcam.bcmonitor.model.BitcoinTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

public class BulkExtractorBitcoinToCSV {

    final private ReactiveClient<BitcoinBlock, BitcoinTransaction> client;
    private static final Logger logger = LoggerFactory.getLogger(BulkExtractorBitcoinToCSV.class);
    private BitcoinToCSV<BitcoinBlock> csvWriter;

    public BulkExtractorBitcoinToCSV(
            ReactiveClient<BitcoinBlock, BitcoinTransaction> client) {
        this.client = client;
    }

    public Disposable saveBlocksAndTransactionsToNeo4j(long fromHeight, long toHeight) {
        csvWriter = new BitcoinToCSV<>();

        if (fromHeight == 0) {
            //For the genesis block, do not fetch the transaction, since it is not a valid tx. Do block only.
            client.getBlockHash(0)
                    .flatMap(client::getBlock)
                    .flatMap(csvWriter::writeBlock)
                    .subscribe(block -> logger.info("Wrote the Genesis block."));
            fromHeight++;
        }

        return fetchBlocksParallel(fromHeight, toHeight)
                .flatMap(csvWriter::writeBlock)
                .concatMap(this::fetchTransactionsParallel)
                .flatMap(transaction -> csvWriter.writeTransaction(transaction))
                .doOnError(error -> logger.error("Transaction failed to write" + error))
                .subscribe();
    }

    private ParallelFlux<BitcoinBlock> fetchBlocksParallel(long fromHeight, long toHeight) {

        int fromInt = (int) fromHeight;
        int count = (int) (toHeight - fromInt) + 1;

        logger.info("Count: " + count);

        return Flux.range(fromInt, count)
                .parallel(3)
                .runOn(Schedulers.elastic())
                .concatMap(client::getBlockHash)
                .concatMap(client::getBlock);
    }

    private ParallelFlux<BitcoinTransaction> fetchTransactionsParallel(BitcoinBlock block) {
        return Flux.fromIterable(block.getTxids())
                .parallel(3)
                .runOn(Schedulers.elastic())
                .concatMap(client::getTransaction);
    }
}
