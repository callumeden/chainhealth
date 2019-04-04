package com.bcam.bcmonitor.extractor.mapper;

import com.bcam.bcmonitor.model.DashTransaction;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import static com.bcam.bcmonitor.extractor.mapper.BitcoinTransactionDeserializer.readInputs;
import static com.bcam.bcmonitor.extractor.mapper.BitcoinTransactionDeserializer.readOutputs;

public class DashTransactionDeserializer extends StdDeserializer<DashTransaction> {

    public DashTransactionDeserializer() {
        this(null);
    }

    public DashTransactionDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public DashTransaction deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        DashTransaction transaction = new DashTransaction();

        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        if (!node.get("error").isNull()) {
            throw new RPCResponseException("Error received from RPC. Message: " + node.get("error").get("message").textValue());
        }


        JsonNode result = node.get("result");
        String txid = result.get("txid").asText();
        transaction.setHash(txid);
        transaction.setSizeBytes(result.get("size").asInt());
        transaction.setBlockHash(result.get("blockhash").asText());
        transaction.setTimeReceived(new java.util.Date(System.currentTimeMillis()).toInstant().getEpochSecond());

        transaction.setVin(readInputs(result));
        transaction.setVout(readOutputs(result, txid));


        return transaction;
    }
}
