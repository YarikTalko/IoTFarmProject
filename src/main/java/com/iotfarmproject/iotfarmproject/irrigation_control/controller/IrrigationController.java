package com.iotfarmproject.iotfarmproject.irrigation_control.controller;

import com.iotfarmproject.iotfarmproject.irrigation_control.service.IrrigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/irrigation")
public class IrrigationController {

//    private final IrrigationService irrigationService;
//
//    @Autowired
//    public IrrigationController(IrrigationService irrigationService) {
//        this.irrigationService = irrigationService;
//    }
//
//    @PostMapping("/start")
//    public ResponseEntity<String> startIrrigation() {
//        irrigationService.startIrrigation();
//        return ResponseEntity.ok("Irrigation started.");
//    }
//
//    @PostMapping("/stop")
//    public ResponseEntity<String> stopIrrigation() {
//        irrigationService.stopIrrigation();
//        return ResponseEntity.ok("Irrigation stopped.");
//    }
}
