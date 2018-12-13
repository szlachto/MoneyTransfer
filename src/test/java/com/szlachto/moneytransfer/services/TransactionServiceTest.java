package com.szlachto.moneytransfer.services;

import com.szlachto.moneytransfer.model.Transaction;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import static com.szlachto.moneytransfer.utils.CurrencyUtil.getBigDecimalValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionServiceTest extends ServiceTest {

    @Test
    public void testTransactionEnoughFund() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/transaction").build();
        BigDecimal amount = getBigDecimalValue(10);
        Transaction transaction = new Transaction("EUR", amount, 2, 4);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    public void testTransactionNotEnoughFund() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/transaction").build();
        BigDecimal amount = getBigDecimalValue(100000);
        Transaction transaction = new Transaction("EUR", amount, 2, 4);
        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(500, statusCode);
    }

    @Test
    public void testTransactionDifferentCcy() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/transaction").build();
        BigDecimal amount = getBigDecimalValue(100);
        Transaction transaction = new Transaction("USD", amount, 2, 4);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(500, statusCode);
    }
}
