package com.iotfarmproject.iotfarmproject.resource_sensor_monitoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResourceSensorData {

    @JsonProperty("resource_type")
    private String resource_type;
    @JsonProperty("current_level")
    private double current_level;
    @JsonProperty("unit")
    private String unit;

    public String get_resource_type() {
        return resource_type;
    }

    public double get_current_level() {
        return current_level;
    }

    public String get_unit() {
        return unit;
    }
}
