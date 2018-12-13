package com.szlachto.moneytransfer.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementUtils {
    private StatementUtils() {
    }

    public static PreparedStatement prepareStatement(Connection connection, String sqlQuery, PreparedStatementSetter... setter) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sqlQuery);
        for (PreparedStatementSetter pss : setter) pss.setValues(ps);
        return ps;
    }

    public static PreparedStatement prepareStatement(Connection connection, String sqlQuery, int i, PreparedStatementSetter... setter) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sqlQuery,i);
        for (PreparedStatementSetter pss : setter) pss.setValues(ps);
        return ps;
    }
}
