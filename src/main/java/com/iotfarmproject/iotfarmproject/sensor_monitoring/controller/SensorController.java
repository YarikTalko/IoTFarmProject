package com.iotfarmproject.iotfarmproject.sensor_monitoring.controller;

import com.iotfarmproject.iotfarmproject.sensor_monitoring.model.SensorData;
import com.iotfarmproject.iotfarmproject.sensor_monitoring.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@RequestBody SensorData sensorData) {
        sensorService.SendJsonMessage(sensorData);
        return ResponseEntity.ok("Json message sent to RabbitMQ...");
    }
}
