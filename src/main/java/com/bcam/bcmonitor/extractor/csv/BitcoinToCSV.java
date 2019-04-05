package com.bcam.bcmonitor.extractor.csv;

import com.bcam.bcmonitor.model.AbstractBlock;
import com.bcam.bcmonitor.model.BitcoinTransaction;
import com.bcam.bcmonitor.model.TransactionInput;
import com.bcam.bcmonitor.model.TransactionOutput;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class BitcoinToCSV<B extends AbstractBlock> {

    private static final String DATA_FILE_PATH = "./import/data";
    private static final String RELATIONS_FILE_PATH = "./import/relations";
    private static final int RECORDS_PER_FILE = 10000000;

    private static final String BLOCK_FILE_PREFIX = "sample-block-data";
    private static final String TRANSACTION_FILE_PREFIX = "sample-transaction-data";
    private static final String ADDRESS_FILE_PREFIX = "sample-address-data";
    private static final String OUTPUT_FILE_PREFIX = "sample-output-data";
    private static final String COINBASE_FILE_PREFIX = "sample-coinbase-data";


    private static final String CHAINED_FROM_RELATION_PREFIX = "relation-chained-from";
    private static final String MINED_IN_RELATION_PREFIX = "relation-mined-in";
    private static final String LOCKED_TO_RELATION_PREFIX = "relation-locked-to";
    private static final String OUTPUTS_RELATION_PREFIX = "relation-outputs";
    private static final String COINBASE_RELATION_PREFIX = "relation-coinbase";
    private static final String INPUTS_RELATION_PREFIX = "relation-inputs";

    private Map<Relationship, CsvWriter> relationshipFileWriters = new HashMap<>();
    private Map<Label, CsvWriter> dataFileWriters = new HashMap<>();
    private enum Relationship {
        CHAINED_FROM,
        MINED_IN,
        LOCKED_TO,
        INPUTS,
        OUTPUTS,
        COINBASE
    }

    private enum Label {
        BLOCK,
        TRANSACTION,
        COINBASE,
        OUTPUT,
        ADDRESS
    }

    public BitcoinToCSV() {
        this.dataFileWriters.put(Label.BLOCK,  new CsvWriter(DATA_FILE_PATH, BLOCK_FILE_PREFIX, RECORDS_PER_FILE));
        this.dataFileWriters.put(Label.TRANSACTION,new CsvWriter(DATA_FILE_PATH, TRANSACTION_FILE_PREFIX, RECORDS_PER_FILE));
        this.dataFileWriters.put(Label.OUTPUT, new CsvWriter(DATA_FILE_PATH, OUTPUT_FILE_PREFIX, RECORDS_PER_FILE));
        this.dataFileWriters.put(Label.ADDRESS, new CsvWriter(DATA_FILE_PATH, ADDRESS_FILE_PREFIX, Integer.MAX_VALUE));
        this.dataFileWriters.put(Label.COINBASE, new CsvWriter(DATA_FILE_PATH, COINBASE_FILE_PREFIX, RECORDS_PER_FILE));

        this.relationshipFileWriters.put(Relationship.CHAINED_FROM, new CsvWriter(RELATIONS_FILE_PATH, CHAINED_FROM_RELATION_PREFIX, RECORDS_PER_FILE));
        this.relationshipFileWriters.put(Relationship.MINED_IN, new CsvWriter(RELATIONS_FILE_PATH, MINED_IN_RELATION_PREFIX, RECORDS_PER_FILE));
        this.relationshipFileWriters.put(Relationship.LOCKED_TO, new CsvWriter(RELATIONS_FILE_PATH, LOCKED_TO_RELATION_PREFIX, RECORDS_PER_FILE));
        this.relationshipFileWriters.put(Relationship.OUTPUTS, new CsvWriter(RELATIONS_FILE_PATH, OUTPUTS_RELATION_PREFIX, RECORDS_PER_FILE));
        this.relationshipFileWriters.put(Relationship.COINBASE, new CsvWriter(RELATIONS_FILE_PATH, COINBASE_RELATION_PREFIX, RECORDS_PER_FILE));
        this.relationshipFileWriters.put(Relationship.INPUTS, new CsvWriter(RELATIONS_FILE_PATH, INPUTS_RELATION_PREFIX, RECORDS_PER_FILE));
    }

    public Mono<B> writeBlock(B block) {
        String blockHash = block.getHash();
        String prevBlockHash = block.getPrevBlockHash();
        CsvWriter blockWriter = dataFileWriters.get(Label.BLOCK);
        blockWriter.write(blockHash, prevBlockHash, block.getTimeStamp(), block.getSizeBytes(), Label.BLOCK.name());

        if (prevBlockHash == null) {
            //Handle Genesis block with no prev block hash
            return Mono.just(block);
        }
        createRelationship(blockHash, Relationship.CHAINED_FROM, block.getPrevBlockHash());
        String coinbaseId = writeCoinbase(blockHash);
        createRelationship(blockHash, Relationship.COINBASE, coinbaseId);

        return Mono.just(block);
    }

    private String writeCoinbase(String blockHash) {
        //todo : figure out value
        String coinbaseId = "coinbase-" + blockHash;
        CsvWriter coinbaseWriter = dataFileWriters.get(Label.COINBASE);
        coinbaseWriter.write(coinbaseId, Label.OUTPUT.name());
        return coinbaseId;
    }

    public Mono<BitcoinTransaction> writeTransaction(BitcoinTransaction transaction) {
        CsvWriter transactionWriter = dataFileWriters.get(Label.TRANSACTION);
        transactionWriter.write(transaction.getHash(), Label.TRANSACTION.name());
        createRelationship(transaction.getHash(), Relationship.MINED_IN, transaction.getBlockHash());

        transaction.getVin().forEach(input -> writeInputNode(transaction, input));

        transaction.getVout().forEach(output -> {
            String outputId = writeOutputNode(output);
            createRelationship(transaction.getHash(), Relationship.OUTPUTS, outputId);
        });

        return Mono.just(transaction);
    }

    private void writeInputNode(BitcoinTransaction transaction, TransactionInput input) {
        if (input.isCoinbase()) {
            createRelationship("coinbase-" + transaction.getBlockHash(), Relationship.INPUTS, transaction.getHash());
            return;
        }
        String inputId = input.getTxid() + '-' + input.getVout();
        createRelationship(inputId, Relationship.INPUTS, transaction.getHash());
    }

    private String writeOutputNode(TransactionOutput output) {
        CsvWriter outputWriter = dataFileWriters.get(Label.OUTPUT);
        String outputId = output.getTxid() + '-' + output.getIndex();
        outputWriter.write(outputId, output.getValue(), Label.OUTPUT.name());
        output.getAddresses().forEach(address -> {
            writeAddress(address);
            createRelationship(outputId, Relationship.LOCKED_TO, address);
        });
        return outputId;
    }

    private void writeAddress(String address) {
        CsvWriter addressWriter = dataFileWriters.get(Label.ADDRESS);
        addressWriter.write(address, Label.ADDRESS.name());
    }

    private void createRelationship(Object o1, Relationship relationship, Object o2) {
        CsvWriter relationWriter = relationshipFileWriters.get(relationship);
        relationWriter.write(o1, o2, relationship.name());
    }
}
