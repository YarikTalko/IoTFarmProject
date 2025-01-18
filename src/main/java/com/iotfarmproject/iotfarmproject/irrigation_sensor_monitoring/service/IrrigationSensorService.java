package com.iotfarmproject.iotfarmproject.irrigation_sensor_monitoring.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.client.WriteApiBlocking;
import com.iotfarmproject.iotfarmproject.irrigation_sensor_monitoring.model.IrrigationSensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class IrrigationSensorService {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.sensors.routing_key.name}")
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(IrrigationSensorService.class);

    private final RabbitTemplate rabbitTemplate;
    private final WriteApiBlocking writeApi;

    @Autowired
    public IrrigationSensorService(RabbitTemplate rabbitTemplate, InfluxDBClient influxDBClient) {
        this.rabbitTemplate = rabbitTemplate;
        this.writeApi = influxDBClient.getWriteApiBlocking();
    }

    public void sendMessage(IrrigationSensorData irrigationSensorData) {
        LOGGER.info(String.format("Json message sent -> %s; %.2f; %.2f; %s.",
                irrigationSensorData.getSensorId(), irrigationSensorData.getTemperature(),
                irrigationSensorData.getHumidity(), irrigationSensorData.getTimestamp().toString()));
        rabbitTemplate.convertAndSend(exchange, routingKey, irrigationSensorData);
    }

    public void sendDataToInfluxDB(IrrigationSensorData irrigationSensorData) {
        Point point = Point.measurement("sensors_data")
                .addTag("sensor_id", irrigationSensorData.getSensorId())
                .addField("temperature", irrigationSensorData.getTemperature())
                .addField("humidity", irrigationSensorData.getHumidity())
                .time(irrigationSensorData.getTimestamp()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .truncatedTo(ChronoUnit.SECONDS), WritePrecision.S);
        writeApi.writePoint(point);
    }
}

