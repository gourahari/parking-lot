package com.goura.system.parkinglot.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goura.system.parkinglot.config.ParkingLotType;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class ParkingToken {
    private String tokenId;
    private ParkingLotType type;
    private String licensePlate;
    private String phoneNumber;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date entryTime;
    @JsonIgnore
    private String lotId;

    protected ParkingToken(String tokenId) {
        this.tokenId = tokenId;
    }

    protected ParkingToken(ParkingTokenBuilder builder) {
        this.tokenId = UUID.randomUUID().toString();
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

    public String getTokenId() {
        return tokenId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingToken)) return false;
        ParkingToken that = (ParkingToken) o;
        return Objects.equals(tokenId, that.tokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenId);
    }
}
