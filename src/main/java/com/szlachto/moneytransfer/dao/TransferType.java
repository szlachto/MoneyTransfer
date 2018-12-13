package com.szlachto.moneytransfer.dao;

public enum TransferType {
    WITHDRAW("withdraw money"), DEPOSIT("deposit money"), TRANSFER_FROM("transfer money from"), TRANSFER_TO("transfer money to");

    private final String message;

    TransferType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
