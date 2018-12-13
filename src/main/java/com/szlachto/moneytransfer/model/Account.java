package com.szlachto.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Objects;

public class Account {

    @JsonIgnore
    private long accountId;

    @JsonProperty(required = true)
    private String customerName;

    @JsonProperty(required = true)
    private BigDecimal balance;

    @JsonProperty(required = true)
    private String currencyCode;

    public Account() {
    }

    public Account(String userName, BigDecimal balance, String currencyCode) {
        this.customerName = userName;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public Account(long accountId, String userName, BigDecimal balance, String currencyCode) {
        this.accountId = accountId;
        this.customerName = userName;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId &&
                Objects.equals(customerName, account.customerName) &&
                Objects.equals(balance, account.balance) &&
                Objects.equals(currencyCode, account.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, customerName, balance, currencyCode);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", customerName='" + customerName + '\'' +
                ", balance=" + balance +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
