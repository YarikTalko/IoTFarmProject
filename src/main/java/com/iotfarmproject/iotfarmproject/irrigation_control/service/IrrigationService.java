package com.iotfarmproject.iotfarmproject.irrigation_control.service;

import com.iotfarmproject.iotfarmproject.sensor_monitoring.model.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class IrrigationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IrrigationService.class);

    @RabbitListener(queues = "${rabbitmq.sensors.queue.json.name}")
    public void consumeJsonMessage(SensorData sensorData) {
        LOGGER.info(String.format("Received JSON Message (RL) -> %s; %.2f; %.2f; %s.",
                sensorData.getSensorId(), sensorData.getTemperature(),
                sensorData.getHumidity(), sensorData.getTimestamp().toString()));
    }

//    @RabbitListener(queues = "sensor_data_queue")
//    public void handleSensorEvent(String message) {
//        System.out.println("Received sensor data: " + message);
//        if (message.equals("START")) {
//            // Логіка старту зрошування
//        } else if (message.equals("STOP")) {
//            // Логіка зупинки
//        }
//    }

//    public void startIrrigation() {
//        System.out.println("Irrigation started.");
//        // Додайте логіку старту поливу
//    }
//
//    public void stopIrrigation() {
//        System.out.println("Irrigation stopped.");
//        // Додайте логіку зупинки поливу
//    }
}
