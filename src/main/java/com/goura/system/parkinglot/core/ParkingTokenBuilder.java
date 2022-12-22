package com.goura.system.parkinglot.core;

import com.goura.system.parkinglot.config.ParkingLotType;

import java.time.LocalDateTime;
import java.util.Date;

public class ParkingTokenBuilder {
    private static final String ERROR_MSG = "Please set %s!";
    private int mask = 0; // To check if all the fields are set.
    protected ParkingLotType type;
    protected String licensePlate;
    protected String phoneNumber;
    protected LocalDateTime entryTime;
    protected String lotId;

    private ParkingTokenBuilder() {
    }
    public static ParkingTokenBuilder create() {
        return new ParkingTokenBuilder();
    }

    public ParkingTokenBuilder setType(ParkingLotType type) {
        this.type = type;
        mask = mask | 1;
        return this;
    }

    public ParkingTokenBuilder setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
        mask = mask | 2;
        return this;
    }

    public ParkingTokenBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        mask = mask | 4;
        return this;
    }

    public ParkingTokenBuilder setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
        mask = mask | 8;
        return this;
    }

    public ParkingTokenBuilder setLotId(String lotId) {
        this.lotId = lotId;
        mask = mask | 16;
        return this;
    }

    public ParkingToken build() {
        if (mask != 31) {
            int diff = 31 - mask;
            String errorMessage = null;
            if ((diff | 1) == 1) {
                errorMessage = String.format(ERROR_MSG, "ParkingLotType");
            } else if ((diff | 2) == 2) {
                errorMessage = String.format(ERROR_MSG, "LicensePlate");
            } else if ((diff | 4) == 4) {
                errorMessage = String.format(ERROR_MSG, "PhoneNumber");
            } else if ((diff | 8) == 8) {
                errorMessage = String.format(ERROR_MSG, "EntryTime");
            } else if ((diff | 16) == 16) {
                errorMessage = String.format(ERROR_MSG, "LotId");
            }
            throw new IllegalArgumentException(errorMessage);
        }
        return new ParkingToken(this);
    }
}
