package com.goura.system.parkinglot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotConfig {
    private interface Default {
        int CAPACITY = 20;

        // Percentage occupancy
        int HANDICAPPED_CAP_PERCENT = 5;
        int MOTORCYCLE_CAP_PERCENT = 2;
        int LARGE_CAP_PERCENT = 15;

        // Cost per hour
        double COMPACT_LOT_COST = 3.00;
        double HANDICAPPED_LOT_COST = 2.50;
        double MOTORCYCLE_LOT_COST = 1.25;
        double LARGE_LOT_COST = 5.00;
    }

    @Value("${capacity.total}")
    private int capacity;
    @Value("${capacity.percent.large}")
    private int largeCapPercent;
    @Value("${capacity.percent.motorcycle}")
    private int motorcycleCapPercent;
    @Value("${capacity.percent.handicapped}")
    private int handicappedCapPercent;

    @Value("${parking.cost.large}")
    private double largeLotCost;
    @Value("${parking.cost.compact}")
    private double compactLotCost;
    @Value("${parking.cost.motorcycle}")
    private double motorcycleLotCost;
    @Value("${parking.cost.handicapped}")
    private double handicappedLotCost;

    public ParkingLotConfig() {
        // Initialize with default values.
        this.capacity = Default.CAPACITY;

        this.handicappedCapPercent = Default.HANDICAPPED_CAP_PERCENT;
        this.motorcycleCapPercent = Default.MOTORCYCLE_CAP_PERCENT;
        this.largeCapPercent = Default.LARGE_CAP_PERCENT;

        this.compactLotCost = Default.COMPACT_LOT_COST;
        this.largeLotCost = Default.LARGE_LOT_COST;
        this.handicappedLotCost = Default.HANDICAPPED_LOT_COST;
        this.motorcycleLotCost = Default.MOTORCYCLE_LOT_COST;
    }

    public void setCapacity(int capacity) {
        System.out.println("Capacity: " + capacity);
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getHandicappedCapPercent() {
        return handicappedCapPercent;
    }

    public int getLargeCapPercent() {
        return largeCapPercent;
    }

    public int getMotorcycleCapPercent() {
        return motorcycleCapPercent;
    }

    public double getCompactLotCost() {
        return compactLotCost;
    }

    public double getHandicappedLotCost() {
        return handicappedLotCost;
    }

    public double getLargeLotCost() {
        return largeLotCost;
    }

    public double getMotorcycleLotCost() {
        return motorcycleLotCost;
    }
}
