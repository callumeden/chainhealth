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

    private Map<Long, Map<Relationship, CsvWriter>> perThreadRelationWriters = new HashMap<>();
    private Map<Long, Map<Label, CsvWriter>> perThreadNodeWriters = new HashMap<>();
    private Map<Label, String> filePrefixes = new HashMap<>();

    private Map<Relationship, String> relationFilePrefixes = new HashMap<>();

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
        //Populate map of label -> file prefix
        filePrefixes.put(Label.BLOCK, BLOCK_FILE_PREFIX);
        filePrefixes.put(Label.TRANSACTION, TRANSACTION_FILE_PREFIX);
        filePrefixes.put(Label.OUTPUT, OUTPUT_FILE_PREFIX);
        filePrefixes.put(Label.ADDRESS, ADDRESS_FILE_PREFIX);
        filePrefixes.put(Label.COINBASE, COINBASE_FILE_PREFIX);

        relationFilePrefixes.put(Relationship.CHAINED_FROM, CHAINED_FROM_RELATION_PREFIX);
        relationFilePrefixes.put(Relationship.MINED_IN, MINED_IN_RELATION_PREFIX);
        relationFilePrefixes.put(Relationship.LOCKED_TO, LOCKED_TO_RELATION_PREFIX);
        relationFilePrefixes.put(Relationship.OUTPUTS, OUTPUTS_RELATION_PREFIX);
        relationFilePrefixes.put(Relationship.COINBASE, COINBASE_RELATION_PREFIX);
        relationFilePrefixes.put(Relationship.INPUTS, INPUTS_RELATION_PREFIX);
    }

    private CsvWriter getNodeWriterForThread(Label label) {
        long threadId = Thread.currentThread().getId();

        if (perThreadNodeWriters.containsKey(threadId)) {
            Map<Label, CsvWriter> nodeWriters = perThreadNodeWriters.get(threadId);

            if (nodeWriters.containsKey(label)) {
                return nodeWriters.get(label);
            }

            int recordsPerFile = label == Label.ADDRESS ? Integer.MAX_VALUE : RECORDS_PER_FILE;

            CsvWriter newNodeWriter = new CsvWriter(label.name(), DATA_FILE_PATH, getFilePrefix(threadId, label), recordsPerFile);
            nodeWriters.put(label, newNodeWriter);
            return newNodeWriter;
        }

        perThreadNodeWriters.put(threadId, new HashMap<>());
        return getNodeWriterForThread(label);
    }

    private CsvWriter getRelationshipWriterForThread(Relationship relationship) {
        long threadId = Thread.currentThread().getId();

        if (perThreadRelationWriters.containsKey(threadId)) {
            Map<Relationship, CsvWriter> relationWriter = perThreadRelationWriters.get(threadId);

            if (relationWriter.containsKey(relationship)) {
                return relationWriter.get(relationship);
            }

            CsvWriter newRelationWriter = new CsvWriter(relationship.name(), RELATIONS_FILE_PATH, getFilePrefix(threadId, relationship), RECORDS_PER_FILE);
            relationWriter.put(relationship, newRelationWriter);
            return newRelationWriter;
        }

        perThreadRelationWriters.put(threadId, new HashMap<>());
        return getRelationshipWriterForThread(relationship);
    }

    private String getFilePrefix(long threadId, Label label) {
        String filePrefix = filePrefixes.get(label);
        return filePrefix + "-thread-" + threadId;
    }

    private String getFilePrefix(long threadId, Relationship relationship) {
        String filePrefix = relationFilePrefixes.get(relationship);
        return filePrefix + "-thread-" + threadId;
    }

    public Mono<B> writeBlock(B block) {

        String blockHash = block.getHash();
        String prevBlockHash = block.getPrevBlockHash();
        CsvWriter blockWriter = getNodeWriterForThread(Label.BLOCK);
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
        CsvWriter coinbaseWriter = getNodeWriterForThread(Label.COINBASE);
        coinbaseWriter.write(coinbaseId, Label.OUTPUT.name());
        return coinbaseId;
    }

    public Mono<BitcoinTransaction> writeTransaction(BitcoinTransaction transaction) {
        CsvWriter transactionWriter = getNodeWriterForThread(Label.TRANSACTION);
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
        CsvWriter outputWriter = getNodeWriterForThread(Label.OUTPUT);
        String outputId = output.getTxid() + '-' + output.getIndex();
        outputWriter.write(outputId, output.getValue(), Label.OUTPUT.name());
        output.getAddresses().forEach(address -> {
            writeAddress(address);
            createRelationship(outputId, Relationship.LOCKED_TO, address);
        });
        return outputId;
    }

    private void writeAddress(String address) {
        CsvWriter addressWriter = getNodeWriterForThread(Label.ADDRESS);
        addressWriter.write(address, Label.ADDRESS.name());
    }

    private void createRelationship(Object o1, Relationship relationship, Object o2) {
        CsvWriter relationWriter = getRelationshipWriterForThread(relationship);
        relationWriter.write(o1, o2, relationship.name());
    }
}
