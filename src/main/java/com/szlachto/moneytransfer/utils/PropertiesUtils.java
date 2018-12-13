package com.szlachto.moneytransfer.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
    private static final Logger LOGGER = LogManager.getLogger(PropertiesUtils.class);
    private static Properties prop = new Properties();

    private PropertiesUtils() {
    }

    public static void loadConfig(String fileName) {
        try (InputStream in = new FileInputStream(fileName)) {
            LOGGER.info("Loading config file: " + fileName);
            prop.load(in);
        } catch (FileNotFoundException fne) {
            LOGGER.error("File name not found " + fileName, fne);
        } catch (IOException ioe) {
            LOGGER.error("Error when reading the config " + fileName, ioe);
        }
    }

    public static String getStringProperty(String key) {
        return prop.getProperty(key);
    }

    static {
        loadConfig("src\\main\\resources\\h2_db.properties");
    }


}