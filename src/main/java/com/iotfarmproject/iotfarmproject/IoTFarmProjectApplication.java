package com.iotfarmproject.iotfarmproject;

import com.iotfarmproject.iotfarmproject.config.PostgreSQLConfig;
import com.iotfarmproject.iotfarmproject.resource_sensor_monitoring.service.ResourceSensorService;
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

//        ResourceSensorService resourceSensorService = new ResourceSensorService();
//        Connection conn = PostgreSQLConfig.connect("resource_manager");
//        resourceSensorService.createTables(conn);
    }

}