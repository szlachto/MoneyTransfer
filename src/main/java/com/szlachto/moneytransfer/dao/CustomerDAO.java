package com.szlachto.moneytransfer.dao;

import com.szlachto.moneytransfer.model.Customer;

import java.util.List;

public interface CustomerDAO {
    long insertCustomer(Customer customer);

    int deleteCustomerById(long customerId);

    Customer getCustomerByName(String name);

    Customer getCustomerById(long id);

    List<Customer> getAllCustomers();
}
