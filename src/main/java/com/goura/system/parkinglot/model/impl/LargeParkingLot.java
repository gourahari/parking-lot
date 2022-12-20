package com.goura.system.parkinglot.model.impl;

import com.goura.system.parkinglot.config.ParkingLotType;
import com.goura.system.parkinglot.model.AbstractParkingLot;

public class LargeParkingLot extends AbstractParkingLot {

    public LargeParkingLot(double cost) {
        super(ParkingLotType.Large, cost);
    }
}
