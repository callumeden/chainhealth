package com.bcam.bcmonitor.extractor.mapper;

import com.bcam.bcmonitor.model.BitcoinTransaction;
import com.bcam.bcmonitor.model.TransactionInput;
import com.bcam.bcmonitor.model.TransactionOutput;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BitcoinTransactionDeserializer extends StdDeserializer<BitcoinTransaction> {


    public BitcoinTransactionDeserializer() {
        this(null);
    }


    public BitcoinTransactionDeserializer(Class<?> vc) {
        super(vc);
    }

    public static ArrayList<TransactionInput> readInputs(JsonNode result) {
        ArrayList<TransactionInput> vin = new ArrayList<>();

        result.get("vin").forEach(jsonNode -> {
            TransactionInput in = new TransactionInput();
            if (jsonNode.has("coinbase")) {
                in.setCoinbase();
            } else {
                in.setTxid(jsonNode.get("txid").textValue());
                in.setVout(jsonNode.get("vout").intValue());
            }

            vin.add(in);
        });


        return vin;
    }

    public static ArrayList<TransactionOutput> readOutputs(JsonNode result, String txid) {
        // get outputs
        ArrayList<TransactionOutput> vout = new ArrayList<>();

        result.get("vout").forEach(jsonNode -> {
            // System.out.println("THROUGH VOUT");
            TransactionOutput out = new TransactionOutput();
            out.setTxid(txid);
            out.setValue(jsonNode.get("value").floatValue());
            out.setIndex(jsonNode.get("n").intValue());
            JsonNode scriptPubKey = jsonNode.get("scriptPubKey");
            if (scriptPubKey != null) {
                out.setAddresses(readStringArray(scriptPubKey.get("addresses")));
            }
            vout.add(out);

        });
        return vout;

    }

    private static List<String> readStringArray(JsonNode array) {
        List<String> list = new ArrayList<>();
        if (array != null) {
            array.forEach(item -> list.add(item.toString().replaceAll("^\"|\"$", "")));
        }
        return list;
    }

    @Override
    public BitcoinTransaction deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        BitcoinTransaction transaction = new BitcoinTransaction();

        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        if (!node.get("error").isNull()) {
            throw new RPCResponseException("Error received from RPC. Message: " + node.get("error").get("message").textValue());
        } else {
            JsonNode result = node.get("result");
            String txid = result.get("txid").asText();
            transaction.setHash(txid);  // fine where "hash" and "txid" are same (change for segwit)
            transaction.setSizeBytes(result.get("size").asInt());
            transaction.setBlockHash(result.get("blockhash").asText());

            transaction.setVin(readInputs(result));
            transaction.setVout(readOutputs(result, txid));

            transaction.setTimeReceived(new java.util.Date(System.currentTimeMillis()).toInstant().getEpochSecond());
        }

        return transaction;
    }

    /**
     * alternative:
     * create a dto
     * https://stackoverflow.com/questions/20832015/how-do-i-iterate-over-a-json-response-using-jackson-api-of-a-list-inside-a-list
     *
     * could be a private static class json inside this class
     *
     *https://stackoverflow.com/questions/19158345/custom-json-deserialization-with-jackson
     */

}
