package com.szlachto.moneytransfer.dao;

import com.szlachto.moneytransfer.model.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.szlachto.moneytransfer.utils.CurrencyUtil.getBigDecimalValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountDAOTest extends AbstractH2DAOTest {
    private final static Logger LOGGER = LogManager.getLogger(AccountDAOTest.class);
    private final AccountDAO accountDAO = H2_DAO_FACTORY.getAccountDAO();

    @Test
    void testGetAllAccounts() {
        List<Account> accounts = accountDAO.getAllAccounts();
        assertTrue(accounts.size() > 1);
    }

    @Test
    void testWithdrawFromAccount() {
        BigDecimal deltaWithdraw = getBigDecimalValue(-50);
        BigDecimal afterWithdraw = getBigDecimalValue(350);
        int rowsUpdated = accountDAO.updateAccountBalance(1, deltaWithdraw);
        assertEquals(1, rowsUpdated);
        assertEquals(afterWithdraw, accountDAO.getAccountById(1).getBalance());
    }

    @Test
    void testDepositToAccount() {
        BigDecimal deltaDeposit = getBigDecimalValue(50);
        BigDecimal afterDeposit = getBigDecimalValue(500);
        int rowsUpdated = accountDAO.updateAccountBalance(2, deltaDeposit);
        assertEquals(1, rowsUpdated);
        assertEquals(afterDeposit, accountDAO.getAccountById(2).getBalance());
    }

    @Test
    void testConcurrentUpdateAccountBalance() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        BigDecimal deposit = getBigDecimalValue(50);

        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                Account account = accountDAO.getAccountById(1);
                BigDecimal balanceBeforeDeposit = account.getBalance();
                accountDAO.updateAccountBalance(1, deposit);
                assertEquals(balanceBeforeDeposit.add(deposit), accountDAO.getAccountById(1).getBalance());
            });

        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            LOGGER.error("Error while waiting for termination", e);
        }

    }
}
