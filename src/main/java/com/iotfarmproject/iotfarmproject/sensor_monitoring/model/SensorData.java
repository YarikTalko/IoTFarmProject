package com.iotfarmproject.iotfarmproject.sensor_monitoring.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

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

    // Гетери та сетери
}
