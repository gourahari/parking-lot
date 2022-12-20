package com.goura.system.parkinglot.model.impl;

import com.goura.system.parkinglot.config.ParkingLotType;
import com.goura.system.parkinglot.model.AbstractParkingLot;

public class MotorcycleParkingLot extends AbstractParkingLot {

    public MotorcycleParkingLot(double cost) {
        super(ParkingLotType.Motorcycle, cost);
    }
}
