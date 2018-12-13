package com.szlachto.moneytransfer.dao;

import com.szlachto.moneytransfer.model.Customer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerDAOTest extends AbstractH2DAOTest {

    private static final Logger LOGGER = LogManager.getLogger(CustomerDAOTest.class);
    private final CustomerDAO customerDAO = H2_DAO_FACTORY.getCustomerDAO();

    @Test
    void testGetCustomerById() {
        Customer customer = customerDAO.getCustomerById(1);
        assertEquals(new Customer(1, "CaptainMarvel"), customer);
    }

    @Test
    void testGetCustomerByName() {
        Customer customer = customerDAO.getCustomerByName("TonyStark");
        assertEquals("TonyStark", customer.getCustomerName());
    }

    @Test
    void testGetNonExistingCustomerById() {
        Customer customer = customerDAO.getCustomerById(123);
        assertNull(customer);
    }

    @Test
    void testGetNonExistingCustomerByName() {
        Customer customer = customerDAO.getCustomerByName("Hulk");
        assertNull(customer);
    }

    @Test
    void testCreateCustomer() {
        Customer customer = new Customer("BlackWidow");
        long id = customerDAO.insertCustomer(customer);
        Customer customerFromDB = customerDAO.getCustomerById(id);
        assertEquals(customer.getCustomerName(), customerFromDB.getCustomerName());
    }

    @Test()
    void testCreateExistingCustomer() {
        Customer customer = new Customer("SteveRogers");
        assertEquals(-1, customerDAO.insertCustomer(customer));
    }

    @Test
    void testDeletingCustomer() {
        int rowCount = customerDAO.deleteCustomerById(2);
        assertEquals(1, rowCount);
        assertNull(customerDAO.getCustomerById(2));
    }

    @Test
    void testGetAllCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        assertTrue(customers.size() > 1);
    }
}