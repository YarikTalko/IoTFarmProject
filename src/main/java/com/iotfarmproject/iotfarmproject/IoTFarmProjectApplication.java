package com.iotfarmproject.iotfarmproject;

import com.iotfarmproject.iotfarmproject.equipment_management.service.EquipmentService;
import com.iotfarmproject.iotfarmproject.resource_management.service.ResourceService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
public class IoTFarmProjectApplication {

    public static void main(String[] args) throws SQLException {
        SpringApplication.run(IoTFarmProjectApplication.class, args);

//        EquipmentService equipmentService = new EquipmentService();
//        Connection conn = equipmentService.connect();
//        equipmentService.createTables(conn);
//        equipmentService.insertDataToEquipSensorData(conn);

//        ResourceService resourceService = new ResourceService();
//        Connection conn = resourceService.connect();
//        resourceService.createTable(conn);
    }

}