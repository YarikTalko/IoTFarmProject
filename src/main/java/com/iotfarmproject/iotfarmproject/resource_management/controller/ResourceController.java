package com.iotfarmproject.iotfarmproject.resource_management.controller;

import com.iotfarmproject.iotfarmproject.resource_management.model.ResourceSensorData;
import com.iotfarmproject.iotfarmproject.resource_management.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@RequestBody ResourceSensorData data) throws SQLException {
        System.out.println("Received data: " + data);
        Connection conn = new ResourceService().connect();
        resourceService.insertData(conn, data);
        return ResponseEntity.ok("Json message sent to PostgreSQL");
    }
}
