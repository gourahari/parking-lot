package com.goura.system.parkinglot.model.impl;

import com.goura.system.parkinglot.config.ParkingLotType;
import com.goura.system.parkinglot.model.AbstractParkingLot;

public class HandicappedParkingLot extends AbstractParkingLot {

    public HandicappedParkingLot(double cost) {
        super(ParkingLotType.Handicapped, cost);
    }
}
