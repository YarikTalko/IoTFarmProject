package com.iotfarmproject.iotfarmproject.sensor_monitoring.service;

import com.iotfarmproject.iotfarmproject.sensor_monitoring.model.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Service
public class SensorService {

    @Value("${rabbitmq.sensors.exchange.name}")
    private String exchange;

    //    @Value("${rabbitmq.sensors.routing_key}")
    @Value("${rabbitmq.sensors.json.routing_key}")
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorService.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public SensorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void SendJsonMessage(SensorData sensorData) {
        LOGGER.info(String.format("Json message sent -> %s; %.2f; %.2f; %s.",
                sensorData.getSensorId(), sensorData.getTemperature(),
                sensorData.getHumidity(), sensorData.getTimestamp().toString()));
        rabbitTemplate.convertAndSend(exchange, routingKey, sensorData);
    }

//    private final InfluxDBClient influxDBClient;
//    private final RabbitTemplate rabbitTemplate;

//    public SensorService(InfluxDBClient influxDBClient, RabbitTemplate rabbitTemplate) {
//        this.influxDBClient = influxDBClient;
//        this.rabbitTemplate = rabbitTemplate;
//    }

//    public void saveSensorData(SensorData data) {
//        // Збереження даних у InfluxDB
//        Point point = Point.measurement("sensor_data")
//                .addField("id", data.getSensorId())
//                .addField("temperature", data.getTemperature())
//                .addField("humidity", data.getHumidity())
//                .time(data.getTimestamp().toEpochMilli(), WritePrecision.MS);
//        influxDBClient.getWriteApiBlocking().writePoint(point);
//
//        // Відправка події до RabbitMQ
//        String message = String.format("SensorId: %s, Temperature: %.2f, Humidity: %.2f",
//                data.getSensorId(), data.getTemperature(), data.getHumidity());
//        try {
//            rabbitTemplate.convertAndSend("sensor_data_queue", message);
//            System.out.println("Message sent: " + message);
//            System.out.println("Sensor data event sent: " + message);
//        } catch (Exception e) {
//            System.out.println("Error sending message: " + e.getMessage());
//        }
//    }
}

