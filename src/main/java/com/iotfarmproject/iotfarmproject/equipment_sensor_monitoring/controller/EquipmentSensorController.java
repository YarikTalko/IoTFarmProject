package com.iotfarmproject.iotfarmproject.equipment_sensor_monitoring.controller;

import com.iotfarmproject.iotfarmproject.config.PostgreSQLConfig;
import com.iotfarmproject.iotfarmproject.equipment_sensor_monitoring.model.EquipmentSensorData;
import com.iotfarmproject.iotfarmproject.equipment_sensor_monitoring.service.EquipmentSensorService;
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

    private final EquipmentSensorService equipmentSensorService;

    public EquipmentSensorController(EquipmentSensorService equipmentSensorService) {
        this.equipmentSensorService = equipmentSensorService;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@RequestBody EquipmentSensorData data) throws SQLException {
        Connection conn = PostgreSQLConfig.connect("equipment_manager");
        equipmentSensorService.insertData(conn, data);
        return ResponseEntity.ok("Json message sent to PostgreSQL");
    }
}
