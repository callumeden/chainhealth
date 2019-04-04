package com.bcam.bcmonitor.extractor.client;


import com.bcam.bcmonitor.extractor.mapper.*;
import com.bcam.bcmonitor.extractor.rpc.JSONRPCRequest;
import com.bcam.bcmonitor.extractor.rpc.ReactiveHTTPClient;
import com.bcam.bcmonitor.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;

// @Qualifier("ReactiveBitcoinClient")
@Component
public class ReactiveBitcoinClient extends ReactiveClientImpl implements ReactiveClient<BitcoinBlock, BitcoinTransaction>, BlockchainInfoClient {

    @Value("${BITCOIN_HOSTNAME}")
    private String hostName;

    @Value("${BITCOIN_PORT}")
    private int port;

    @Value("${BITCOIN_UN}")
    private String userName;

    @Value("${BITCOIN_PW}")
    private String password;

    @Value("${CLIENT_PROXY_ENABLED}")
    private int proxyEnabled;

    @Value("${CLIENT_PROXY_HOST}")
    private String proxyHost;

    @Value("${CLIENT_PROXY_PORT}")
    private int proxyPort;

    protected ReactiveHTTPClient client;
    private Logger logger = LoggerFactory.getLogger(ReactiveBitcoinClient.class);

    public ReactiveBitcoinClient() {
    }

    @PostConstruct
    protected void buildClient() {
        System.out.println("Building Bitcoin client with hostname " + hostName);

        ObjectMapper mapper = buildMapper();

        client = proxyEnabled == 1 ? new ReactiveHTTPClient(hostName, port, userName, password, mapper, proxyHost, proxyPort) :
                new ReactiveHTTPClient(hostName, port, userName, password, mapper);
    }

    protected ObjectMapper buildMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        // module.addDeserializer(String.class, new SingleResultDeserializer());
        module.addDeserializer(BitcoinBlock.class, new BitcoinBlockDeserializer());
        module.addDeserializer(BitcoinTransaction.class, new BitcoinTransactionDeserializer());
        module.addDeserializer(TransactionPoolInfo.class, new BitcoinTransactionPoolInfoDeserializer());
        module.addDeserializer(TransactionPool.class, new BitcoinTransactionPoolDeserializer());
        module.addDeserializer(BlockchainInfo.class, new BlockchainInfoDeserializer());
        module.addDeserializer(RPCResult.class, new RPCResultDeserializer());

        mapper.registerModule(module);

        return mapper;
    }

    @Override
    ReactiveHTTPClient getClient() {
        return client;
    }

    // parameterised queries
    public Mono<BitcoinBlock> getBlock(String hash) {
        JSONRPCRequest request = new JSONRPCRequest("getblock");
        request.addParam(hash);
        request.addParam(true); // always request decoded JSON with transactions

        return client
                .requestResponseSpec(request.toString())
                .bodyToMono(BitcoinBlock.class)
                .doOnError(e -> logger.error("GET Block hash errored ========== {}", hash))
                .retryWhen(errors -> errors.delayElements(Duration.of(100, MILLIS)));
    }


    public Mono<BitcoinTransaction> getTransaction(String hash) {
        JSONRPCRequest request = new JSONRPCRequest("getrawtransaction");

        request.addParam(hash);
        request.addParam(true);

        return client
                .requestResponseSpec(request.toString())
                .bodyToMono(BitcoinTransaction.class)
                .doOnError(e -> logger.error("GET TX hash errored ========== {}, error: {}", hash, e))
                .retryWhen(errors -> errors.delayElements(Duration.of(100, MILLIS)));
    }


}

