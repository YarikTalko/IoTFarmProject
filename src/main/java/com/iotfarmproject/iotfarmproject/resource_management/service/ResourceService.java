package com.iotfarmproject.iotfarmproject.resource_management.service;

import com.iotfarmproject.iotfarmproject.resource_management.model.ResourceSensorData;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResourceService {

    private final Map<String, Object> threshold;

    public ResourceService() {
        threshold = new HashMap<>();
        threshold.put("Tractor Fuel", 10);
        threshold.put("Oil", 5);
        threshold.put("Repair Kits", 5);
        threshold.put("Fertilizers", 50);
    }

    public Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/resource_manager",
                    "user", "user");
            if (conn != null) {
                System.out.println("Connected to PostgreSQL database (Resource)");
            }
        } catch (Exception e) {
            System.out.println("PostgreSQL connection error: " + e);
        }
        return conn;
    }

    public void createTable(Connection conn) throws SQLException {
        Statement stmt;

        try {
            String query1 = String.format("CREATE TABLE IF NOT EXISTS " + "resource_monitoring" + " (" +
                    "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                    "resource_type VARCHAR(50) NOT NULL," +
                    "current_level NUMERIC(10, 2) NOT NULL," +
                    "unit VARCHAR(20)," +
                    "threshold_level NUMERIC(10, 2)," +
                    "alert_generated BOOLEAN DEFAULT FALSE," +
                    "created_at TIMESTAMP DEFAULT NOW())");

            stmt = conn.createStatement();
            stmt.executeUpdate(query1);
            System.out.println("Created (if not exist) table resource_monitoring");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void insertData(Connection conn, ResourceSensorData data) throws SQLException {
        String tableName = "resource_monitoring";

        try {
            boolean isEventGenerated;
            Timestamp created_at = new Timestamp(System.currentTimeMillis());

            double thresholdLevel = getThresholdLevel(data.get_resource_type());
            isEventGenerated = isNeedEventGenerated(data.get_current_level(), thresholdLevel);

            String query = "INSERT INTO " + tableName +
                    " (resource_type, current_level, unit, threshold_level, alert_generated, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, data.get_resource_type());
                pstmt.setDouble(2, data.get_current_level());
                pstmt.setString(3, data.get_unit());
                pstmt.setDouble(4, thresholdLevel);
                pstmt.setBoolean(5, isEventGenerated);
                pstmt.setTimestamp(6, created_at);

                System.out.println(pstmt);
                pstmt.executeUpdate();
            }
            System.out.println("Inserted equipment sensor data.");

        } catch (Exception e) {
            System.out.println("Inserting error: " + e);
        }
    }

    private double getThresholdLevel(String resource_type) {
        Object thresholdObject = threshold.get(resource_type);
        if (thresholdObject instanceof Number) {
            return ((Number) thresholdObject).doubleValue();
        } else {
            System.out.println("Object is not double type.");
            return 0;
        }
    }

    private boolean isNeedEventGenerated(double current_level, double thresholdLevel) {
        return !(current_level > thresholdLevel);
    }
}
