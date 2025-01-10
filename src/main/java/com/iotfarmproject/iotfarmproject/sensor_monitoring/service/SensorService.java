package com.iotfarmproject.iotfarmproject.sensor_monitoring.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.client.WriteApiBlocking;
import com.iotfarmproject.iotfarmproject.sensor_monitoring.model.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
public class SensorService {

    @Value("${rabbitmq.sensors.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.sensors.json.routing_key}")
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorService.class);

    private final RabbitTemplate rabbitTemplate;
    private final WriteApiBlocking writeApi;

    @Autowired
    public SensorService(RabbitTemplate rabbitTemplate, InfluxDBClient influxDBClient) {
        this.rabbitTemplate = rabbitTemplate;
        this.writeApi = influxDBClient.getWriteApiBlocking();
    }

    public void SendJsonMessage(SensorData sensorData) {
        LOGGER.info(String.format("Json message sent -> %s; %.2f; %.2f; %s.",
                sensorData.getSensorId(), sensorData.getTemperature(),
                sensorData.getHumidity(), sensorData.getTimestamp().toString()));
        rabbitTemplate.convertAndSend(exchange, routingKey, sensorData);
    }

    public void SendDataToInfluxDB(SensorData sensorData) {
        Point point = Point.measurement("sensors_data")
                .addTag("sensor_id", sensorData.getSensorId())
                .addField("temperature", sensorData.getTemperature())
                .addField("humidity", sensorData.getHumidity())
                .time(sensorData.getTimestamp()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .truncatedTo(ChronoUnit.SECONDS), WritePrecision.S);
        writeApi.writePoint(point);
    }
}

