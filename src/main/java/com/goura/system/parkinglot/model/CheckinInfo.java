package com.goura.system.parkinglot.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CheckinInfo {
    @NotNull(message = "licensePlate cannot be null!")
    @NotEmpty(message = "licensePlate cannot be empty!")
    String licensePlate;

    @NotNull(message = "phoneNumber cannot be null!")
    @NotEmpty(message = "phoneNumber cannot be empty!")
    String phoneNumber;

    @NotNull(message = "vehicleType cannot be null!")
    @NotEmpty(message = "vehicleType cannot be empty!")
    String vehicleType;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.trim();
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType.trim();
    }
}
