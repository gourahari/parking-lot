package com.goura.system.parkinglot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckinInfo {
    @JsonProperty(required = true)
    String licensePlate;
    @JsonProperty(required = true)
    String phoneNumber;
    @JsonProperty(required = true)
    String vehicleType;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}
