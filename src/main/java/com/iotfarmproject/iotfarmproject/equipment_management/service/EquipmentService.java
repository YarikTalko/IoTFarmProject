package com.iotfarmproject.iotfarmproject.equipment_management.service;

import com.iotfarmproject.iotfarmproject.equipment_management.model.EquipmentFaultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Service
public class EquipmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentService.class);

    //// TODO: Add Inserting Error Data

    @RabbitListener(queues = "${rabbitmq.equipment.queue.name}")
    public void consumeMessage(EquipmentFaultData data) {
        LOGGER.info(String.format("Received message (EM) -> %s; %s; %s; %s.",
                data.getEquipmentId(), data.getCode(),
                data.getDescription(), data.getPriority()));
    }

    private void insertErrorData(Connection conn, EquipmentFaultData data) throws SQLException {
        String tableName = "equipment_tasks";

        try {
            String assigned_to = "", status = "Pending";

            Timestamp created_at = new Timestamp(System.currentTimeMillis());
            Timestamp updated_at = created_at;

            String query = "INSERT INTO " + tableName +
                    " (equipment_id, fault_code, fault_description, assigned_to, status, priority, " +
                    "created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, data.getEquipmentId());
                pstmt.setString(2, data.getCode());
                pstmt.setString(3, data.getDescription());
                pstmt.setString(4, assigned_to);
                pstmt.setString(5, status);
                pstmt.setString(6, data.getPriority());
                pstmt.setTimestamp(7, created_at);
                pstmt.setTimestamp(8, updated_at);

                System.out.println(pstmt);
                pstmt.executeUpdate();
            }
            System.out.println("Inserted equipment tasks data.");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
