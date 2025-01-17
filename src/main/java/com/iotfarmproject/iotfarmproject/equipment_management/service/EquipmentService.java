package com.iotfarmproject.iotfarmproject.equipment_management.service;

import com.iotfarmproject.iotfarmproject.equipment_management.model.EquipmentSensorData;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class EquipmentService {

    public Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/equipment_manager",
                    "user", "user");
            if (conn != null) {
                System.out.println("Connected to PostgreSQL database (Equipment)");
            }
        } catch (Exception e) {
            System.out.println("PostgreSQL connection error: " + e);
        }
        return conn;
    }

    public void createTables(Connection conn) throws SQLException {
        Statement stmt;

        try {
            String query1 = String.format("CREATE TABLE IF NOT EXISTS " + "equipment_sensor_data" + " (" +
                    "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                    "equipment_id VARCHAR(50) NOT NULL," +
                    "sensor_type VARCHAR(50) NOT NULL," +
                    "value NUMERIC(10, 2) NOT NULL," +
                    "unit VARCHAR(20)," +
                    "status VARCHAR(20)," +
                    "event_generated BOOLEAN DEFAULT FALSE," +
                    "created_at TIMESTAMP DEFAULT NOW())");

            String query2 = String.format("CREATE TABLE IF NOT EXISTS " + "equipment_tasks" + " (" +
                    "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
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

    public void insertData(Connection conn, EquipmentSensorData data) throws SQLException {
        String tableName = "equipment_sensor_data";

        try {
            String status; // = "Перевищення межі";
            boolean isEventGenerated = false;
            String[] dataCheckResult;
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            dataCheckResult = dataCheck(conn, data);
            status = dataCheckResult[0];
            isEventGenerated = Boolean.parseBoolean(dataCheckResult[1]);

            String query = "INSERT INTO " + tableName +
                    " (equipment_id, sensor_type, value, unit, status, event_generated, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, data.getEquipmentId());
                pstmt.setString(2, data.getSensorType());
                pstmt.setDouble(3, data.getValue());
                pstmt.setString(4, data.getUnit());
                pstmt.setString(5, status);
                pstmt.setBoolean(6, isEventGenerated);
                pstmt.setTimestamp(7, timestamp);

                System.out.println(pstmt);
                pstmt.executeUpdate();
            }
            System.out.println("Inserted equipment sensor data.");

        } catch (Exception e) {
            System.out.println("Inserting error: " + e);
        }
    }

    private String[] dataCheck(Connection conn, EquipmentSensorData data) throws SQLException {
        String dbName = "equipment_tasks";
        String equipment_status = "Normal";
        boolean isEventGenerated = false;

        try {
            String fault_code = "", fault_description = "";
            String assigned_to = "", status = "", priority = "";
            Timestamp created_at, updated_at;

            switch (data.getSensorType()) {
                case "CPU Temperature":
                    if (data.getValue() > 85 || data.getValue() < 50) {
                        fault_code = "00001a";
                        fault_description = "The processor temperature has exceeded the allowable limits.";
                        priority = "Medium";
                        equipment_status = "Out of limits";
                    }
                    break;
            }

            if (!fault_code.equals("")) {
                isEventGenerated = true;
                status = "Pending";
                created_at = new Timestamp(System.currentTimeMillis());
                updated_at = created_at;

                String query = "INSERT INTO " + dbName +
                        " (equipment_id, fault_code, fault_description, assigned_to, status, priority, " +
                        "created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, data.getEquipmentId());
                    pstmt.setString(2, fault_code);
                    pstmt.setString(3, fault_description);
                    pstmt.setString(4, assigned_to);
                    pstmt.setString(5, status);
                    pstmt.setString(6, priority);
                    pstmt.setTimestamp(7, created_at);
                    pstmt.setTimestamp(8, updated_at);

                    System.out.println(pstmt);
                    pstmt.executeUpdate();
                }
                System.out.println("Inserted equipment tasks data.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            equipment_status = "Error";
        }
        return new String[]{equipment_status, Boolean.toString(isEventGenerated)};
    }
}
