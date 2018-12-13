package com.szlachto.moneytransfer.service;

import com.szlachto.moneytransfer.dao.DAOFactory;
import com.szlachto.moneytransfer.model.Account;
import com.szlachto.moneytransfer.utils.CurrencyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.util.Objects.isNull;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {
    private static final DAOFactory DAO_FACTORY = DAOFactory.getDAOFactory(DAOFactory.H2);
    private static final Logger LOGGER = LogManager.getLogger(AccountService.class);

    @GET
    @Path("/{accountId}")
    public Account getAccount(@PathParam("accountId") long accountId) {
        return DAO_FACTORY.getAccountDAO().getAccountById(accountId);
    }

    @GET
    @Path("/all")
    public List<Account> getAllAccounts() {
        return DAO_FACTORY.getAccountDAO().getAllAccounts();
    }

    @GET
    @Path("/{accountId}/balance")
    public BigDecimal getBalance(@PathParam("accountId") long accountId) {
        final Account account = DAO_FACTORY.getAccountDAO().getAccountById(accountId);

        if (isNull(account)) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }
        return account.getBalance();
    }

    @PUT
    @Path("/{accountId}/deposit/{amount}")
    public Account deposit(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) {

        if (amount.compareTo(CurrencyUtil.ZERO) <= 0) {
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }

        DAO_FACTORY.getAccountDAO().updateAccountBalance(accountId, amount.setScale(4, RoundingMode.HALF_EVEN));
        return DAO_FACTORY.getAccountDAO().getAccountById(accountId);
    }

    @PUT
    @Path("/{accountId}/withdraw/{amount}")
    public Account withdraw(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) {

        if (amount.compareTo(CurrencyUtil.ZERO) <= 0) {
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        BigDecimal delta = amount.negate();
        LOGGER.debug("Withdraw service: delta change to account  " + delta + " Account ID = " + accountId);
        DAO_FACTORY.getAccountDAO().updateAccountBalance(accountId, delta.setScale(4, RoundingMode.HALF_EVEN));
        return DAO_FACTORY.getAccountDAO().getAccountById(accountId);
    }


}

