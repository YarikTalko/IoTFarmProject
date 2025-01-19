package com.iotfarmproject.iotfarmproject.resource_management.service;

import com.iotfarmproject.iotfarmproject.config.PostgreSQLConfig;
import com.iotfarmproject.iotfarmproject.resource_management.model.ResourceDeficiencyData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Service
public class ResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceService.class);

    @RabbitListener(queues = "${rabbitmq.resource.queue.name}")
    public void consumeMessage(ResourceDeficiencyData data) throws SQLException {
        LOGGER.info(String.format("Received message (RM) -> %s; %.2f; %.2f; %s.",
                data.getResourceType(), data.getCurrentLevel(),
                data.getThresholdLevel(), data.getPriority()));
        Connection conn = PostgreSQLConfig.connect("resource_manager");
        insertDeficiencyData(conn, data);
    }

    private void insertDeficiencyData(Connection conn, ResourceDeficiencyData data) throws SQLException {
        String tableName = "resource_tasks";

        try {
            String status = "Pending";

            Timestamp created_at = new Timestamp(System.currentTimeMillis());
            Timestamp updated_at = created_at;

            String query = "INSERT INTO " + tableName +
                    " (resource_type, current_level, threshold_level, status, priority, created_at, " +
                    "updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, data.getResourceType());
                pstmt.setDouble(2, data.getCurrentLevel());
                pstmt.setDouble(3, data.getThresholdLevel());
                pstmt.setString(4, status);
                pstmt.setString(5, data.getPriority());
                pstmt.setTimestamp(6, created_at);
                pstmt.setTimestamp(7, updated_at);

                System.out.println(pstmt);
                pstmt.executeUpdate();
            }
            System.out.println("Inserted resource tasks data.");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
