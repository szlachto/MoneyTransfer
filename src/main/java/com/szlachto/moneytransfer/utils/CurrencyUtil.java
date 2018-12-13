package com.szlachto.moneytransfer.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyUtil {

    private static final Logger LOGGER = LogManager.getLogger(CurrencyUtil.class);
    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    public static final BigDecimal ZERO = new BigDecimal(0).setScale(SCALE, ROUNDING_MODE);

    private CurrencyUtil() {
    }

    public static BigDecimal getBigDecimalValue(double value) {
        return BigDecimal.valueOf(value).setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal getBigDecimalValue(String value) {
        return new BigDecimal(value).setScale(SCALE, ROUNDING_MODE);
    }
}
