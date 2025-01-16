package com.iotfarmproject.iotfarmproject.equipment_management.model;

import lombok.Data;

@Data
public class EquipmentSensorData {

    private String equipmentId;
    private String sensorType;
    private double value;
    private String unit;

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getSensorType() {
        return sensorType;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }
}

