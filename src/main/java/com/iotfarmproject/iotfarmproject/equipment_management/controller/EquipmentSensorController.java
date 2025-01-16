package com.iotfarmproject.iotfarmproject.equipment_management.controller;

import com.iotfarmproject.iotfarmproject.equipment_management.model.EquipmentSensorData;
import com.iotfarmproject.iotfarmproject.equipment_management.service.EquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentSensorController {

    private final EquipmentService equipmentService;

    public EquipmentSensorController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@RequestBody EquipmentSensorData data) throws SQLException {
        Connection conn = new EquipmentService().connect();
        equipmentService.insertDataToEquipSensorData(conn, data);
        return ResponseEntity.ok("Json message sent to PostgreSQL");
    }
}
