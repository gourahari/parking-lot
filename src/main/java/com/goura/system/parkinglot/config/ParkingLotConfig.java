package com.goura.system.parkinglot.config;

public class ParkingLotConfig {
    private interface Default {
        int CAPACITY = 20000;

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

    private static final ParkingLotConfig INSTANCE = new ParkingLotConfig();
    private int capacity;
    private int handicappedCapPercent;
    private int motorcycleCapPercent;
    private int largeCapPercent;
    private double compactLotCost;
    private double handicappedLotCost;
    private double motorcycleLotCost;
    private double largeLotCost;

    private ParkingLotConfig() {
        this.capacity = Default.CAPACITY;

        this.handicappedCapPercent = Default.HANDICAPPED_CAP_PERCENT;
        this.motorcycleCapPercent = Default.MOTORCYCLE_CAP_PERCENT;
        this.largeCapPercent = Default.LARGE_CAP_PERCENT;

        this.compactLotCost = Default.COMPACT_LOT_COST;
        this.largeLotCost = Default.LARGE_LOT_COST;
        this.handicappedLotCost = Default.HANDICAPPED_LOT_COST;
        this.motorcycleLotCost = Default.MOTORCYCLE_LOT_COST;
    }

    public static ParkingLotConfig getInstance() {
        return INSTANCE;
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
