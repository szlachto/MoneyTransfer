package com.szlachto.moneytransfer.services;

import com.szlachto.moneytransfer.model.Customer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static javax.ws.rs.core.Response.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerServiceTest extends ServiceTest {

    @Test
    public void testGetCustomer() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/customer/SteveRogers").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(OK.getStatusCode(), statusCode);

        String jsonString = EntityUtils.toString(response.getEntity());
        Customer customer = mapper.readValue(jsonString, Customer.class);
        assertEquals("SteveRogers", customer.getCustomerName());
    }

    @Test
    public void testCreateUser() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/customer/create").build();
        Customer user = new Customer("Thor");
        String jsonInString = mapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(OK.getStatusCode(), statusCode);

        String jsonString = EntityUtils.toString(response.getEntity());
        Customer uAfterCreation = mapper.readValue(jsonString, Customer.class);
        assertEquals("Thor", uAfterCreation.getCustomerName());
    }

    @Test
    public void testCreateExistingCustomer() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/customer/create").build();
        Customer customer = new Customer("CaptainMarvel");
        String jsonInString = mapper.writeValueAsString(customer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(BAD_REQUEST.getStatusCode(), statusCode);

    }

    @Test
    public void testDeleteCustomer() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/customer/3").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(OK.getStatusCode(), statusCode);
    }

    @Test
    public void testDeleteNonExistingCustomer() throws IOException, URISyntaxException {
        URI uri = URI_BUILDER.setPath("/customer/-1").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(NOT_FOUND.getStatusCode(), statusCode);
    }
}
