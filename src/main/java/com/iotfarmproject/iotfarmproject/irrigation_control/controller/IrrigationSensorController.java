package com.iotfarmproject.iotfarmproject.irrigation_control.controller;

import com.iotfarmproject.iotfarmproject.irrigation_control.model.IrrigationSensorData;
import com.iotfarmproject.iotfarmproject.irrigation_control.service.IrrigationSensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sensors")
public class IrrigationSensorController {

    private final IrrigationSensorService irrigationSensorService;

    public IrrigationSensorController(IrrigationSensorService irrigationSensorService) {
        this.irrigationSensorService = irrigationSensorService;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@RequestBody IrrigationSensorData irrigationSensorData) {
        irrigationSensorData.setTimestamp(LocalDateTime.now());
        irrigationSensorService.SendJsonMessage(irrigationSensorData);
        irrigationSensorService.SendDataToInfluxDB(irrigationSensorData);
        return ResponseEntity.ok("Json message sent to RabbitMQ...");
    }
}
