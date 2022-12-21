package com.goura.system.parkinglot.model;

import com.goura.system.parkinglot.config.ParkingLotType;
import java.util.UUID;

public class AbstractParkingLot implements ParkingLot {
    private String id;
    private ParkingLotType type;
    private double cost; // Per hour cost for this parking lot.

    protected AbstractParkingLot(ParkingLotType type, double cost) {
        id = UUID.randomUUID().toString();
        this.type = type;
        this.cost = cost;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return String.format(MSG, id, type);
    }
}
