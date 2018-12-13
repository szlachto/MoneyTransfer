package com.szlachto.moneytransfer.dao;

import com.szlachto.moneytransfer.exception.CustomException;
import com.szlachto.moneytransfer.model.Account;
import com.szlachto.moneytransfer.model.Transaction;
import com.szlachto.moneytransfer.utils.CurrencyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.szlachto.moneytransfer.dao.TransferType.*;
import static com.szlachto.moneytransfer.utils.StatementUtils.prepareStatement;
import static java.util.Objects.isNull;

public class H2AccountDAO implements AccountDAO {
    private static final Logger LOGGER = LogManager.getLogger(H2AccountDAO.class);

    @Override
    public List<Account> getAllAccounts() {

        List<Account> accounts = new ArrayList<>();

        try (Connection conn = H2DAOFactory.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Account")) {

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Account account = new Account(rs.getLong("AccountId"), rs.getString("CustomerName"),
                                rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                        accounts.add(account);
                        LOGGER.debug("Retrieve account: " + account);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error while retrieving account by Id", e);
        }
        return accounts;
    }

    @Override
    public Account getAccountById(long accountId) {

        Account account = null;

        try (Connection conn = H2DAOFactory.getConnection()) {

            try (PreparedStatement stmt = prepareStatement(conn,
                    "SELECT * FROM Account WHERE AccountId = ?",
                    ps -> ps.setLong(1, accountId))) {

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        account = new Account(rs.getLong("AccountId"), rs.getString("CustomerName"), rs.getBigDecimal("Balance"),
                                rs.getString("CurrencyCode"));
                        LOGGER.debug("Retrieve account: " + account);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error while retrieving account by Id", e);
        }
        return account;
    }

    @Override
    public long createAccount(Account account) {

        try (Connection conn = H2DAOFactory.getConnection()) {

            try (PreparedStatement stmt = prepareStatement(conn,
                    "INSERT INTO Account (customerName, Balance, CurrencyCode) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS,
                    ps -> ps.setString(1, account.getCustomerName()),
                    ps -> ps.setBigDecimal(2, account.getBalance()),
                    ps -> ps.setString(3, account.getCurrencyCode()))) {

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    LOGGER.error("Creating account failed, no rows affected. account: " + account);
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    } else {
                        LOGGER.error("Creating account failed, no ID obtained." + account);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error Inserting account :" + account);
        }
        return 0;
    }

    @Override
    public int deleteAccountById(long accountId) {

        try (Connection conn = H2DAOFactory.getConnection()) {

            try (PreparedStatement stmt = prepareStatement(conn,
                    "DELETE FROM Account WHERE AccountId = ?",
                    ps -> ps.setLong(1, accountId))) {

                return stmt.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.error("Error Deleting Account :" + accountId);
            return -1;
        }
    }

    @Override
    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) {

        Account targetAccount = null;

        try (Connection conn = H2DAOFactory.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement stmt = prepareStatement(conn,
                    "SELECT * FROM Account WHERE AccountId = ? FOR UPDATE",
                    ps -> ps.setLong(1, accountId))) {

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        targetAccount = new Account(rs.getLong("AccountId"), rs.getString("CustomerName"),
                                rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                        LOGGER.debug("updateAccountBalance for Account: " + targetAccount);
                    }

                    if (isNull(targetAccount)) {
                        throw new CustomException("fail to lock account : " + accountId);
                    }

                    BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
                    if (balance.compareTo(CurrencyUtil.ZERO) < 0) {
                        throw new CustomException("Not sufficient Fund for account: " + accountId);
                    }

                    PreparedStatement stmt1 = prepareStatement(conn,
                            "UPDATE Account SET Balance = ? WHERE AccountId = ? ",
                            ps -> ps.setBigDecimal(1, balance),
                            ps -> ps.setLong(2, accountId));

                    int updateCount = stmt1.executeUpdate();
                    conn.commit();

                    return updateCount;

                } catch (SQLException e) {
                    conn.rollback();
                    return -1;
                }
            }

        } catch (SQLException e) {
            LOGGER.error("User Transaction Failed, rollback initiated for: " + accountId, e);
            return -1;
        }
    }

    public int transferAccountBalance(Transaction transaction) {

        Account fromAccount;
        Account toAccount;

        try (Connection conn = H2DAOFactory.getConnection()) {

            conn.setAutoCommit(false);
            fromAccount = getAccountForTransaction(conn, transaction.getFromAccountId(), TRANSFER_FROM);
            toAccount = getAccountForTransaction(conn, transaction.getToAccountId(),TRANSFER_TO);


            if (isNull(fromAccount) || isNull(toAccount)) {
                throw new CustomException("Fail to lock both accounts for write");
            }

            checkConsistenceOfCurrencyTransaction(transaction, fromAccount, toAccount);

            BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(transaction.getAmount());
            if (fromAccountLeftOver.compareTo(CurrencyUtil.ZERO) < 0) {
                throw new CustomException("Not enough Fund from source Account ");
            }
            int rowsU = updateAccountsBalance(conn, transaction, fromAccount, toAccount);

            LOGGER.debug("rows updated: " + rowsU);
            conn.commit();
            return rowsU;

        } catch (SQLException e) {
            LOGGER.error("User Transaction Failed" + transaction, e);
            return -1;
        }
    }


    private void checkConsistenceOfCurrencyTransaction(Transaction transaction, Account fromAccount, Account toAccount) {

        checkConsistencyOfCurrency(fromAccount, transaction.getCurrencyCode());

        checkConsistencyOfCurrency(fromAccount, toAccount.getCurrencyCode());
    }

    private void checkConsistencyOfCurrency(Account fromAccount, String currencyCode) {
        if (!fromAccount.getCurrencyCode().equals(currencyCode)) {
            throw new CustomException("Fail to transfer Fund, transaction ccy are different from source/destination: Account: " +
                    fromAccount + " , currencyCode" + currencyCode);
        }
    }

    private Account getAccountForTransaction(Connection conn, long accountId, TransferType transferType) {

        Account account = null;

        try (PreparedStatement stmt = prepareStatement(conn,
                "SELECT * FROM Account WHERE AccountId = ? FOR UPDATE")) {

            stmt.setLong(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    account = new Account(rs.getLong("AccountId"), rs.getString("CustomerName"),
                            rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                    LOGGER.debug(transferType.getMessage() + "Account: " + account);
                }

                if (isNull(account)) {
                    throw new CustomException("Fail to lock accounts for write");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Faild to get account for id:" + accountId, e);
        }

        return account;
    }

    private int updateAccountsBalance(Connection conn, Transaction transaction, Account accountFrom, Account accountTo) throws SQLException {

        try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE Account SET Balance = ? WHERE AccountId = ? ")) {

            updateStmt.setBigDecimal(1, accountFrom.getBalance().subtract(transaction.getAmount()));
            updateStmt.setLong(2, transaction.getFromAccountId());
            updateStmt.addBatch();

            updateStmt.setBigDecimal(1, accountTo.getBalance().add(transaction.getAmount()));
            updateStmt.setLong(2, transaction.getToAccountId());
            updateStmt.addBatch();

            int[] rowsUpdated = updateStmt.executeBatch();

            return rowsUpdated[0] + rowsUpdated[1];
        } catch (SQLException e) {
            conn.rollback();
            LOGGER.error("Exception during update accounts");
            return -1;
        }
    }
}
