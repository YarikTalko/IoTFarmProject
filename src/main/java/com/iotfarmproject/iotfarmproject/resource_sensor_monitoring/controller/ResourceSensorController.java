package com.iotfarmproject.iotfarmproject.resource_sensor_monitoring.controller;

import com.iotfarmproject.iotfarmproject.resource_sensor_monitoring.model.ResourceSensorData;
import com.iotfarmproject.iotfarmproject.resource_sensor_monitoring.service.ResourceSensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/resources")
public class ResourceSensorController {

    private final ResourceSensorService resourceSensorService;

    public ResourceSensorController(ResourceSensorService resourceSensorService) {
        this.resourceSensorService = resourceSensorService;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@RequestBody ResourceSensorData data) throws SQLException {
        System.out.println("Received data: " + data);
        Connection conn = new ResourceSensorService().connect();
        resourceSensorService.insertData(conn, data);
        return ResponseEntity.ok("Json message sent to PostgreSQL");
    }
}
