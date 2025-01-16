package com.iotfarmproject.iotfarmproject.irrigation_control.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IrrigationSensorData {
    private String sensorId;
    private double temperature;
    private double humidity;
    private LocalDateTime timestamp;

    public String getSensorId() {
        return sensorId;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
