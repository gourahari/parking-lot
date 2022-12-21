package com.goura.system.parkinglot.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ParkingLotNumbers {
    private int total;
    private ParkingLotNumber handicapped;
    private ParkingLotNumber large;
    private ParkingLotNumber compact;
    private ParkingLotNumber motorcycle;

    private final class ParkingLotNumber {
        private int count;
        @JsonFormat(pattern = "")
        private String cost;
        private ParkingLotNumber(int count, double cost) {
            this.count = count;
            this.cost = NumberFormat.getCurrencyInstance().format(cost);
        }

        public int getCount() {
            return count;
        }

        public String  getCost() {
            return cost;
        }
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ParkingLotNumber getHandicapped() {
        return handicapped;
    }

    public void setHandicapped(int count, double cost) {
        this.handicapped = new ParkingLotNumber(count, cost);
    }

    public ParkingLotNumber getLarge() {
        return large;
    }

    public void setLarge(int count, double cost) {
        this.large = new ParkingLotNumber(count, cost);
    }

    public ParkingLotNumber getCompact() {
        return compact;
    }

    public void setCompact(int count, double cost) {
        this.compact = new ParkingLotNumber(count, cost);
    }

    public ParkingLotNumber getMotorcycle() {
        return motorcycle;
    }

    public void setMotorcycle(int count, double cost) {
        this.motorcycle = new ParkingLotNumber(count, cost);
    }
}
