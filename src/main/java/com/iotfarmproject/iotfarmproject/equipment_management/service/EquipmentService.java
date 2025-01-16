package com.iotfarmproject.iotfarmproject.equipment_management.service;

import java.sql.*;

public class EquipmentService {

    public Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/equipment_manager",
                    "user", "user");
            if (conn != null) {
                System.out.println("Connected to PostgreSQL database");
            }
        } catch (Exception e) {
            System.out.println("PostgreSQL connection error: " + e);
        }
        return conn;
    }

    public void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        try {
            String query1 = String.format("CREATE TABLE IF NOT EXISTS " + "equipment_sensor_data" + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "equipment_id VARCHAR(50) NOT NULL," +
                    "sensor_type VARCHAR(50) NOT NULL," +
                    "value NUMERIC(10, 2) NOT NULL," +
                    "unit VARCHAR(20)," +
                    "status VARCHAR(20)," +
                    "event_generated BOOLEAN DEFAULT FALSE," +
                    "created_at TIMESTAMP DEFAULT NOW())");

            String query2 = String.format("CREATE TABLE IF NOT EXISTS " + "equipment_tasks" + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "equipment_id VARCHAR(50) NOT NULL," +
                    "fault_code VARCHAR(50)," +
                    "fault_description TEXT," +
                    "assigned_to VARCHAR(100)," +
                    "status VARCHAR(20) DEFAULT 'Pending'," +
                    "priority VARCHAR(20)," +
                    "created_at TIMESTAMP DEFAULT NOW()," +
                    "updated_at TIMESTAMP DEFAULT NOW())");

            stmt = conn.createStatement();
            stmt.executeUpdate(query1);
            System.out.println("Created (if not exist) table equipment_sensor_data");
            stmt.executeUpdate(query2);
            System.out.println("Created (if not exist) table equipment_tasks");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void insertDataToEquipSensorData(Connection conn) throws SQLException {
        String dbName = "equipment_sensor_data";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            String query = String.format("INSERT INTO " + dbName +
                            " VALUES(" + "%d, '%s', '%s', %d, '%s', '%s', %b)",
                    1, "drone1", "CPU temperature", 90, "°C", "Перевищення межі", false, timestamp);

//                    "id SERIAL PRIMARY KEY," +
//                    "equipment_id VARCHAR(50) NOT NULL," +
//                    "sensor_type VARCHAR(50) NOT NULL," +
//                    "value NUMERIC(10, 2) NOT NULL," +
//                    "unit VARCHAR(20)," +
//                    "status VARCHAR(20)," +
//                    "event_generated BOOLEAN DEFAULT FALSE," +
//                    "created_at TIMESTAMP DEFAULT NOW())");

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Inserted equipment sensor data.");

        } catch (Exception e) {
            System.out.println("Inserting error: " + e);
        }
    }
}
