package com.bcam.bcmonitor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
 * Monero: https://getmonero.org/resources/developer-guides/daemon-rpc.html#get_block
 *
 * Bitcoin: https://bitcoin.org/en/developer-reference#getrawtransaction
 *
 *
 *
 */
@Document()
public class BitcoinBlock extends AbstractBlock {
    //
    // @Id
    // @Indexed
    // private String hash; // in RPC byte order

    private long medianTime; // of previous 11 block
    private BigInteger chainWork;
    private int confirmations; // 1 at best chain tip, -1 not in best chain

    public BitcoinBlock() { }

    public BitcoinBlock(String hash, long height) {
        super(hash, height);
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public void setMedianTime(long medianTime) {
        this.medianTime = medianTime;
    }


    public void setChainWork(BigInteger chainWork) {
        this.chainWork = chainWork;
    }

    public long getMedianTime() {
        return medianTime;
    }

    public BigInteger getChainWork() {
        return chainWork;
    }


    @Override
    public String toString() {
        return "BitcoinBlock{ hash = " + getHash() + ", height = " + getHeight() + "}";
    }
}
