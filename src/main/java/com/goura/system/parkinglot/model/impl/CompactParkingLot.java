package com.goura.system.parkinglot.model.impl;

import com.goura.system.parkinglot.config.ParkingLotType;
import com.goura.system.parkinglot.model.AbstractParkingLot;

public class CompactParkingLot extends AbstractParkingLot {

    public CompactParkingLot(double cost) {
        super(ParkingLotType.Compact, cost);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
