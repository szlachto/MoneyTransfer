package com.szlachto.moneytransfer.dao;

import com.szlachto.moneytransfer.utils.PropertiesUtils;
import org.junit.jupiter.api.BeforeAll;

public class AbstractH2DAOTest {
    static final DAOFactory H2_DAO_FACTORY = DAOFactory.getDAOFactory(DAOFactory.H2);

    @BeforeAll
    public static void setup() {
        PropertiesUtils.loadConfig("src\\test\\resources\\h2_db.properties");
        H2_DAO_FACTORY.populateTestData();
    }
}
