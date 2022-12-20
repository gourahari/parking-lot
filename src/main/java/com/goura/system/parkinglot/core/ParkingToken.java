package com.goura.system.parkinglot.core;

import com.goura.system.parkinglot.config.ParkingLotType;

import java.util.Date;

public class ParkingToken {
    private ParkingLotType type;
    private String licensePlate;
    private String phoneNumber;
    private Date entryTime;
    private String lotId;
    protected ParkingToken(ParkingTokenBuilder builder) {
        this.type = builder.type;
        this.phoneNumber = builder.phoneNumber;
        this.licensePlate = builder.licensePlate;
        this.entryTime = builder.entryTime;
        this.lotId = builder.lotId;
    }

    public ParkingLotType getType() {
        return type;
    }

    public String getLotId() {
        return lotId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getEntryTime() {
        return entryTime;
    }
}
