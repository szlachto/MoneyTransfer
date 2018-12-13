package com.szlachto.moneytransfer.services;

import com.szlachto.moneytransfer.model.Account;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import static com.szlachto.moneytransfer.utils.CurrencyUtil.getBigDecimalValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountServiceTest extends ServiceTest {

    @Test
    public void testGetAccountByUserName() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/account/1").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        assertEquals(200, statusCode);

        String jsonString = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(jsonString, Account.class);
        assertEquals("CaptainMarvel", account.getCustomerName());
    }

    @Test
    public void testGetAccountBalance() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/account/1/balance").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String balance = EntityUtils.toString(response.getEntity());
        BigDecimal res = getBigDecimalValue(balance);
        BigDecimal db = getBigDecimalValue(400);
        assertEquals(db, res);
    }

    @Test
    public void testDeposit() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/account/1/deposit/100").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String jsonString = EntityUtils.toString(response.getEntity());
        Account afterDeposit = mapper.readValue(jsonString, Account.class);
        assertEquals(afterDeposit.getBalance(), getBigDecimalValue(500));
    }

    @Test
    public void testWithDrawSufficientFund() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/account/2/withdraw/100").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        String jsonString = EntityUtils.toString(response.getEntity());
        Account afterDeposit = mapper.readValue(jsonString, Account.class);
        assertEquals(afterDeposit.getBalance(), getBigDecimalValue(350));
    }

    @Test
    public void testWithDrawNonSufficientFund() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/account/2/withdraw/1000.23456").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());
        assertEquals(500, statusCode);
    }
}
