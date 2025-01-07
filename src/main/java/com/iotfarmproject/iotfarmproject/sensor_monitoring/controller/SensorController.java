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

    //http://localhost:8080/api/sensors/publish?message=hello%20world
    @GetMapping("/publish")
    public ResponseEntity<String> sendMessage(@RequestParam("message") String message) {
        sensorService.SendMessage(message);
        return ResponseEntity.ok("Message sent to RabbitMQ...");
    }

}

//@RestController
//@RequestMapping("/api/sensors")
//public class SensorController {
//    private final SensorService sensorService;
//
//    public SensorController(SensorService sensorService) {
//        this.sensorService = sensorService;
//    }
//
//    @PostMapping
//    public ResponseEntity<String> addSensorData(@RequestBody SensorData data) {
//        sensorService.saveSensorData(data);
//        return ResponseEntity.ok("Sensor data saved.");
//    }
//}
