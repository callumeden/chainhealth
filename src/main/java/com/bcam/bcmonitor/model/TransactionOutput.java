package com.bcam.bcmonitor.model;

import java.util.List;

public class TransactionOutput {

    private float value;
    private int index;
    private List<String> addresses;
    private String txid;

    public float getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getTxid() {
        return txid;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<String> getAddresses() {
        return addresses;
    }
}
