package com.iotfarmproject.iotfarmproject.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConfig {

    public static Connection connect(String dbName) throws SQLException {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbName,
                    "user", "user");
            if (conn != null) {
                System.out.println("Connected to PostgreSQL database (" + dbName + ")");
            }
        } catch (Exception e) {
            System.out.println("PostgreSQL connection error: " + e);
        }
        return conn;
    }
}
