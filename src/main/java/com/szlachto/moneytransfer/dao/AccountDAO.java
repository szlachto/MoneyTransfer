package com.szlachto.moneytransfer.dao;

import com.szlachto.moneytransfer.model.Account;
import com.szlachto.moneytransfer.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {
    List<Account> getAllAccounts();

    Account getAccountById(long accountId);

    long createAccount(Account account);

    int deleteAccountById(long accountId);

    int updateAccountBalance(long accountId, BigDecimal deltaAmount);

    int transferAccountBalance(Transaction transaction);
}
