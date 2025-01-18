package com.iotfarmproject.iotfarmproject.equipment_sensor_monitoring.controller;

import com.iotfarmproject.iotfarmproject.equipment_sensor_monitoring.model.EquipmentSensorData;
import com.iotfarmproject.iotfarmproject.equipment_sensor_monitoring.service.EquipmentSensorService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final RabbitTemplate rabbitTemplate;

    public EquipmentSensorController(EquipmentSensorService equipmentSensorService) {
        this.equipmentSensorService = equipmentSensorService;
        this.rabbitTemplate = new RabbitTemplate();
    }

    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@RequestBody EquipmentSensorData data) throws SQLException {
        Connection conn = new EquipmentSensorService(rabbitTemplate).connect();
        equipmentSensorService.insertData(conn, data);
        return ResponseEntity.ok("Json message sent to PostgreSQL");
    }
}
