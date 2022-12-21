package com.goura.system.parkinglot.core;

import com.goura.system.parkinglot.config.ParkingLotConfig;
import com.goura.system.parkinglot.config.ParkingLotType;
import com.goura.system.parkinglot.exception.NotAvailableException;
import com.goura.system.parkinglot.exception.TokenNotFoundException;
import com.goura.system.parkinglot.model.CheckinInfo;
import com.goura.system.parkinglot.model.ParkingLot;
import com.goura.system.parkinglot.model.ParkingLotNumbers;
import com.goura.system.parkinglot.model.impl.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParkingLotManager {
    private static final ParkingLotManager INSTANCE = new ParkingLotManager();
    private List<ParkingLot> compactLots, handicappedLots, largeLots, motorcycleLots;
    private Map<ParkingToken, ParkingLot> occupiedLots = new HashMap<>();
    private ParkingLotConfig config;
    private Capacity capacity;

    private final class Capacity {
        private ParkingLotConfig config;
        private int totalCapacity;
        private int handicappedCapacity;
        private int largeCapacity;
        private int compactCapacity;
        private int motorcycleCapacity;
        private Capacity(ParkingLotConfig config) {
            this.config = config;
            init();
        }

        private void init() {
            totalCapacity = config.getCapacity();
            compactCapacity = totalCapacity;
            handicappedCapacity = (totalCapacity * config.getHandicappedCapPercent()) / 100;
            compactCapacity -= handicappedCapacity;
            largeCapacity = (totalCapacity * config.getLargeCapPercent()) / 100;
            compactCapacity -= largeCapacity;
            motorcycleCapacity = (totalCapacity * config.getMotorcycleCapPercent()) / 100;
            compactCapacity -= motorcycleCapacity;
        }

        public int getTotalCapacity() {
            return totalCapacity;
        }

        public int getCompactCapacity() {
            return compactCapacity;
        }

        public int getLargeCapacity() {
            return largeCapacity;
        }

        public int getHandicappedCapacity() {
            return handicappedCapacity;
        }

        public int getMotorcycleCapacity() {
            return motorcycleCapacity;
        }
    }
    private ParkingLotManager() {
    }

    public static ParkingLotManager getInstance() {
        return INSTANCE;
    }

    public void initialize(ParkingLotConfig config) {
        this.config = config;
        // Initialize the free lots cache
        capacity = new Capacity(config);
        compactLots = IntStream.range(0, capacity.compactCapacity)
                .mapToObj(e -> new CompactParkingLot(config.getCompactLotCost()))
                .collect(Collectors.toList());

        handicappedLots = IntStream.range(0, capacity.handicappedCapacity)
                .mapToObj(e -> new HandicappedParkingLot(config.getHandicappedLotCost()))
                .collect(Collectors.toList());

        largeLots = IntStream.range(0, capacity.largeCapacity)
                .mapToObj(e -> new LargeParkingLot(config.getLargeLotCost()))
                .collect(Collectors.toList());

        motorcycleLots = IntStream.range(0, capacity.motorcycleCapacity)
                .mapToObj(e -> new MotorcycleParkingLot(config.getMotorcycleLotCost()))
                .collect(Collectors.toList());
    }

    public ParkingLotNumbers getCapacity() {
        ParkingLotNumbers c = new ParkingLotNumbers();
        c.setTotal(capacity.getTotalCapacity());;
        c.setLarge(capacity.getLargeCapacity(), config.getLargeLotCost());
        c.setCompact(capacity.getCompactCapacity(), config.getCompactLotCost());
        c.setMotorcycle(capacity.getMotorcycleCapacity(), config.getMotorcycleLotCost());
        c.setHandicapped(capacity.getHandicappedCapacity(), config.getHandicappedLotCost());
        return c;
    }

    public ParkingLotNumbers getAvailability() {
        ParkingLotNumbers n = new ParkingLotNumbers();
        n.setLarge(largeLots.size(), config.getLargeLotCost());
        n.setCompact(compactLots.size(), config.getCompactLotCost());
        n.setMotorcycle(motorcycleLots.size(), config.getMotorcycleLotCost());
        n.setHandicapped(handicappedLots.size(), config.getHandicappedLotCost());
        n.setTotal(capacity.getTotalCapacity() - occupiedLots.size());
        return n;
    }

    public ParkingToken getParkingToken(String tokenId) throws Exception {
        ParkingToken token = new ParkingToken(tokenId);
        if (!occupiedLots.containsKey(token)) {
            throw new TokenNotFoundException("Provided token not found in the system!");
        }
        return occupiedLots.keySet().stream()
                .filter(e -> e.equals(token))
                .findFirst()
                .get();
    }

    public ParkingToken reserve(CheckinInfo info) throws Exception {
        // Validate vehicleType against ParkingLotType enum values.
        ParkingLotType lotType = null;
        try {
            lotType = ParkingLotType.valueOf(info.getVehicleType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Wrong value for vehicleType. Please use one of these: " +
                            Arrays.stream(ParkingLotType.values())
                                    .map(ParkingLotType::toString)
                                    .collect(Collectors.joining(", "))
            );
        };
        List<ParkingLot> source = null;
        ParkingToken token = null;
        switch (lotType) {
            case Large:
                source = largeLots;
                break;
            case Compact:
                source = compactLots;
                break;
            case Motorcycle:
                source = motorcycleLots;
                break;
            case Handicapped:
                source = handicappedLots;
                break;
            default:
                throw new IllegalArgumentException("Wrong Parking Lot Type!");
        }
        synchronized (source) {
            if (source.isEmpty()) {
                throw new NotAvailableException("No parking Lot available for your vehicle type");
            }
            ParkingLot assigned = source.remove(0);
            token = ParkingTokenBuilder.create()
                    .setLicensePlate(info.getLicensePlate())
                    .setPhoneNumber(info.getPhoneNumber())
                    .setEntryTime(new Date())
                    .setType(lotType)
                    .setLotId(assigned.getId())
                    .build();
            occupiedLots.put(token, assigned);
        }
        return token;
    }

    public static void main(String[] args) {
        System.out.println(
        IntStream.range(0, 10)
                .mapToObj(String::valueOf)
                .parallel()
                .collect(Collectors.joining(",")));
    }
}
