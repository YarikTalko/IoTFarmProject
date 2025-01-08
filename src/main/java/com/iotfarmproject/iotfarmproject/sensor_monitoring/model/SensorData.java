package com.iotfarmproject.iotfarmproject.sensor_monitoring.model;

import lombok.Data;

import java.time.Instant;

@Data
public class SensorData {
    private String sensorId;
    private double temperature;
    private double humidity;
    private Instant timestamp;

    public String getSensorId() {
        return sensorId;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
