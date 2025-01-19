package com.iotfarmproject.iotfarmproject.equipment_sensor_monitoring.service;

import com.iotfarmproject.iotfarmproject.equipment_management.model.EquipmentFaultData;
import com.iotfarmproject.iotfarmproject.equipment_sensor_monitoring.model.EquipmentSensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class EquipmentSensorService {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.equipment.routing_key.name}")
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentSensorService.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EquipmentSensorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
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

            dataCheckResult = dataCheck(data);
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

    private String[] dataCheck(EquipmentSensorData data) throws SQLException {
        String status = "Normal";
        boolean isEventGenerated = false;

        EquipmentFaultData faultData = new EquipmentFaultData();

        switch (data.getSensorType()) {
            case "CPU Temperature":
                if (data.getValue() > 85 || data.getValue() < 50) {
                    faultData.setCode("00001a");
                    faultData.setDescription("The processor temperature has exceeded the allowable limits.");
                    faultData.setPriority("Medium");
                    status = "Out of limits";
                }
                break;
        }

        if (!status.equals("Normal")) {
            isEventGenerated = true;
            faultData.setEquipmentId(data.getEquipmentId());
            sendMessage(faultData);
        }

        return new String[]{status, Boolean.toString(isEventGenerated)};
    }

    private void sendMessage(EquipmentFaultData data) {
        LOGGER.info(String.format("Message sent -> %s; %s; %s; %s.",
                data.getEquipmentId(), data.getCode(),
                data.getDescription(), data.getPriority()));
        rabbitTemplate.convertAndSend(exchange, routingKey, data);
    }
}
