package com.iotfarmproject.iotfarmproject.resource_sensor_monitoring.service;

import com.iotfarmproject.iotfarmproject.resource_management.model.ResourceDeficiencyData;
import com.iotfarmproject.iotfarmproject.resource_sensor_monitoring.model.ResourceSensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResourceSensorService {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.resource.routing_key.name}")
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceSensorService.class);

    private final RabbitTemplate rabbitTemplate;

    private final Map<String, Object> threshold;
    private final Map<String, Object> priority;

    @Autowired
    public ResourceSensorService(RabbitTemplate rabbitTemplate) {
        threshold = new HashMap<>();
        threshold.put("Tractor Fuel", 10);
        threshold.put("Oil", 5);
        threshold.put("Repair Kits", 5);
        threshold.put("Fertilizers", 50);

        priority = new HashMap<>();
        priority.put("Tractor Fuel", "High");
        priority.put("Oil", "Low");
        priority.put("Repair Kits", "Medium");
        priority.put("Fertilizers", "High");

        this.rabbitTemplate = rabbitTemplate;
    }

    public void createTables(Connection conn) throws SQLException {
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

            String query2 = String.format("CREATE TABLE IF NOT EXISTS " + "resource_tasks" + " (" +
                    "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                    "resource_type VARCHAR(50) NOT NULL," +
                    "current_level NUMERIC(10, 2) NOT NULL," +
                    "threshold_level NUMERIC(10, 2)," +
                    "status VARCHAR(50) NOT NULL," +
                    "priority VARCHAR(50) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT NOW()," +
                    "updated_at TIMESTAMP DEFAULT NOW())");

            stmt = conn.createStatement();
            stmt.executeUpdate(query1);
            System.out.println("Created (if not exist) table resource_monitoring");
            stmt.executeUpdate(query2);
            System.out.println("Created (if not exist) table resource_tasks");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void insertData(Connection conn, ResourceSensorData data) throws SQLException {
        String tableName = "resource_monitoring";
        ResourceDeficiencyData resourceDeficiencyData = new ResourceDeficiencyData();

        try {
            boolean isEventGenerated;
            Timestamp created_at = new Timestamp(System.currentTimeMillis());

            double thresholdLevel = getThresholdLevel(data.get_resource_type());

            if (data.get_current_level() < thresholdLevel) {
                isEventGenerated = true;
                resourceDeficiencyData.setResourceType(data.get_resource_type());
                resourceDeficiencyData.setCurrentLevel(data.get_current_level());
                resourceDeficiencyData.setThresholdLevel(thresholdLevel);
                resourceDeficiencyData.setPriority(getPriority(data.get_resource_type()));
                sendMessage(resourceDeficiencyData);
            } else {
                isEventGenerated = false;
            }

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
            System.out.println("Inserted resource sensor data.");

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

    private String getPriority(String resource_type) {
        return (String) priority.get(resource_type);
    }

    private void sendMessage(ResourceDeficiencyData data) {
        LOGGER.info(String.format("Message sent -> %s; %.2f; %.2f.; %s.",
                data.getResourceType(), data.getCurrentLevel(),
                data.getThresholdLevel(), data.getPriority()));
        rabbitTemplate.convertAndSend(exchange, routingKey, data);
    }
}
