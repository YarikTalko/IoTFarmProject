package com.iotfarmproject.iotfarmproject.resource_management.model;

public class ResourceDeficiencyData {

    private String resourceType;
    private double currentLevel;
    private double thresholdLevel;
    private String priority;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public double getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(double currentLevel) {
        this.currentLevel = currentLevel;
    }

    public double getThresholdLevel() {
        return thresholdLevel;
    }

    public void setThresholdLevel(double thresholdLevel) {
        this.thresholdLevel = thresholdLevel;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
