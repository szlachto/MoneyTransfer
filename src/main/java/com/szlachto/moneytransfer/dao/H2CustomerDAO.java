package com.szlachto.moneytransfer.dao;

import com.szlachto.moneytransfer.model.Customer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.szlachto.moneytransfer.utils.StatementUtils.prepareStatement;

public class H2CustomerDAO implements CustomerDAO {
    private static final Logger LOGGER = LogManager.getLogger(H2CustomerDAO.class);

    @Override
    public long insertCustomer(final Customer customer) {

        try (Connection conn = H2DAOFactory.getConnection()) {

            try (PreparedStatement stmt = prepareStatement(conn,
                    "INSERT INTO Customer (CustomerName) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS,
                    ps -> ps.setString(1, customer.getCustomerName()))) {

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    LOGGER.error("Creating customer failed, no rows affected. Customer: " + customer);
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    } else {
                        LOGGER.error("Creating customer failed, no ID obtained." + customer);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error Inserting customer :" + customer, e);
        }
        return -1;
    }

    @Override
    public int deleteCustomerById(long customerId) {
        try (Connection conn = H2DAOFactory.getConnection()) {

            try (PreparedStatement stmt = prepareStatement(conn,
                    "DELETE FROM Customer WHERE CustomerId = ?",
                    ps -> ps.setLong(1, customerId))) {
                return stmt.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.error("Error Deleting customer :" + customerId);
            return -1;
        }
    }

    @Override
    public Customer getCustomerByName(String name) {

        Customer customer = null;

        try (Connection conn = H2DAOFactory.getConnection()) {

            try (PreparedStatement stmt = prepareStatement(conn,
                    "SELECT * FROM Customer WHERE CustomerName = ?",
                    ps -> ps.setString(1, name))) {

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        customer = new Customer(rs.getLong("CustomerId"), rs.getString("CustomerName"));
                        LOGGER.debug("Retrieve Customer: " + customer);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error while retrieving customer by Id", e);
        }
        return customer;
    }

    @Override
    public Customer getCustomerById(long userId) {
        Customer customer = null;
        try (Connection conn = H2DAOFactory.getConnection()) {

            try (PreparedStatement stmt = prepareStatement(conn,
                    "SELECT * FROM Customer WHERE CustomerId = ?",
                    ps -> ps.setLong(1, userId))) {

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        customer = new Customer(rs.getLong("CustomerId"), rs.getString("CustomerName"));
                        LOGGER.debug("Retrieve Customer: " + customer);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error while retrieving customer by Id", e);
        }
        return customer;
    }


    @Override
    public List<Customer> getAllCustomers() {

        List<Customer> customers = new ArrayList<>();

        try (Connection conn = H2DAOFactory.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Customer")) {

                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        Customer customer = new Customer(rs.getLong("CustomerId"), rs.getString("CustomerName"));
                        customers.add(customer);
                        LOGGER.debug("Retrieve Customer: " + customer);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error while retrieving customer by Id", e);
        }
        return customers;
    }

}