package com.szlachto.moneytransfer.dao;

import com.szlachto.moneytransfer.utils.PropertiesUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2DAOFactory extends DAOFactory {
    private static final Logger LOGGER = LogManager.getLogger(H2DAOFactory.class);
    private static final String H2_DRIVER = PropertiesUtils.getStringProperty("h2_driver");
    private static final String H2_CONNECTION_URL = PropertiesUtils.getStringProperty("h2_connection_url");
    private static final String H2_USER = PropertiesUtils.getStringProperty("h2_user");
    private static final String H2_PASSWORD = PropertiesUtils.getStringProperty("h2_password");

    public H2DAOFactory() {
        DbUtils.loadDriver(H2_DRIVER);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(H2_CONNECTION_URL, H2_USER, H2_PASSWORD);

    }

    @Override
    public CustomerDAO getCustomerDAO() {
        return new H2CustomerDAO();
    }

    @Override
    public AccountDAO getAccountDAO() {
        return new H2AccountDAO();
    }

    @Override
    public void populateTestData() {
        LOGGER.info("Populating Customer data and Account data for testing");
        try (Connection conn = H2DAOFactory.getConnection()) {
            RunScript.execute(conn, new FileReader("src/test/resources/demo.sql"));
        } catch (SQLException e) {
            LOGGER.error("Error populating user data: ", e);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error finding test script file ", e);
        }
    }
}
