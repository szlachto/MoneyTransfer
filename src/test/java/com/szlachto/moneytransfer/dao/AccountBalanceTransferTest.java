package com.szlachto.moneytransfer.dao;

import com.szlachto.moneytransfer.model.Account;
import com.szlachto.moneytransfer.model.Transaction;
import com.szlachto.moneytransfer.utils.CurrencyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountBalanceTransferTest extends AbstractH2DAOTest {
    private static Logger LOGGER = LogManager.getLogger(AccountBalanceTransferTest.class);
    private final AccountDAO accountDAO = H2_DAO_FACTORY.getAccountDAO();

    @Test
    void testTransferMoney() {
        BigDecimal transferAmount =  CurrencyUtil.getBigDecimalValue(50.01234);
        Transaction transaction = new Transaction("EUR", transferAmount, 2, 4);

        accountDAO.transferAccountBalance(transaction);

        Account accountFrom = accountDAO.getAccountById(2);
        Account accountTo = accountDAO.getAccountById(4);

        assertEquals(CurrencyUtil.getBigDecimalValue(399.9877), accountFrom.getBalance());
        assertEquals(CurrencyUtil.getBigDecimalValue(550.0123), accountTo.getBalance());

    }

    @Test
    void testTransferMoneyInMultiThread() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 20; i++) {
            executor.execute(() -> {
                Transaction transaction = new Transaction("USD",
                        CurrencyUtil.getBigDecimalValue(2), 1, 3);
                accountDAO.transferAccountBalance(transaction);
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            LOGGER.error("Error while waiting for termination", e);
        }

        assertEquals(CurrencyUtil.getBigDecimalValue(360),accountDAO.getAccountById(1).getBalance());


    }
}
