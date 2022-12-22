package com.goura.system.parkinglot.core;

import com.goura.system.parkinglot.config.ParkingLotConfig;
import com.goura.system.parkinglot.config.ParkingLotType;
import com.goura.system.parkinglot.exception.NotAvailableException;
import com.goura.system.parkinglot.exception.TokenNotFoundException;
import com.goura.system.parkinglot.model.CheckinInfo;
import com.goura.system.parkinglot.model.ParkingLot;
import com.goura.system.parkinglot.model.ParkingLotNumbers;
import com.goura.system.parkinglot.model.ParkingReceipt;
import com.goura.system.parkinglot.model.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ParkingLotManager {
    private static final String CHECKOUT_MSG_FREE = "Free parking for less than 5 minutes!";
    private static final String CHECKOUT_MSG = "Thank You! Please visit again.";
    private List<ParkingLot> compactLots, handicappedLots, largeLots, motorcycleLots;
    private Map<ParkingToken, ParkingLot> occupiedLots = new HashMap<>();
    private ParkingLotConfig config;
    private Capacity capacity;

    @Autowired
    private PaymentService paymentService;
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
        // Check if tokenId is valid
        ParkingToken token = validate(tokenId);
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
        List<ParkingLot> source = getParkingCacheByLotType(lotType);
        ParkingToken token = null;
        synchronized (source) {
            if (source.isEmpty()) {
                throw new NotAvailableException("No parking Lot available for your vehicle type");
            }
            ParkingLot assigned = source.remove(0);
            token = ParkingTokenBuilder.create()
                    .setLicensePlate(info.getLicensePlate())
                    .setPhoneNumber(info.getPhoneNumber())
                    .setEntryTime(LocalDateTime.now())
                    .setType(lotType)
                    .setLotId(assigned.getId())
                    .build();
            occupiedLots.put(token, assigned);
        }
        return token;
    }

    public ParkingReceipt releaseParkingLot(String tokenId) throws Exception {
        // Check if tokenId is valid and get it.
        ParkingToken token = getParkingToken(tokenId);

        // Calculate payment
        LocalDateTime exitTime = LocalDateTime.now();

        // duration in minutes.
         long durationMinutes = ChronoUnit.MINUTES.between(token.getEntryTime(), exitTime);
        ParkingReceipt receipt = null;

        synchronized (token) {
            // Check again if token is still valid.
            validate(tokenId);
            // Free parking for duration = less than 5 minutes.
            receipt = new ParkingReceipt(token);
            receipt.setExitTime(exitTime);
            long hours = ChronoUnit.HOURS.between(token.getEntryTime(), exitTime);
            exitTime = exitTime.minusHours(hours);
            long minutes = ChronoUnit.MINUTES.between(token.getEntryTime(), exitTime);
            receipt.setDuration(
                    String.format("%d hour(s) and %d minute(s)", hours, minutes)
            );
            if (durationMinutes < 5) {
                receipt.setMessage(String.format("%s %s", CHECKOUT_MSG_FREE, CHECKOUT_MSG));
            } else {
                receipt.setMessage(CHECKOUT_MSG);
                // Proceed for payment
                double amount = calculatePayment(durationMinutes, occupiedLots.get(token));
                receipt.setAmount(amount);

                // Call payment service
                String txnId = paymentService.processPayment(amount);
                receipt.setTxnId(txnId);
            }
            // Release parking lot
            List<ParkingLot> source = getParkingCacheByLotType(token.getType());
            source.add(occupiedLots.remove(token));
        }
        return receipt;
    }

    private double calculatePayment(long durationInMinutes, ParkingLot parkingLot) {
        int numberOfHours = Double.valueOf(Math.ceil(durationInMinutes*1.0 / 60)).intValue();
        return numberOfHours * parkingLot.getCost();
    }

    private ParkingToken validate(String tokenId) throws Exception {
        ParkingToken token = new ParkingToken(tokenId);
        if (!occupiedLots.containsKey(token)) {
            throw new TokenNotFoundException("Provided token not found in the system!");
        }
        return token;
    }

    private List<ParkingLot> getParkingCacheByLotType(ParkingLotType lotType) {
        List<ParkingLot> source = null;
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
        return source;
    }

    public static void main(String[] args) {
        LocalDateTime date1 = LocalDateTime.of(2022, 12, 22, 10, 3, 10);
        LocalDateTime date2 = LocalDateTime.now();
        long hours = ChronoUnit.HOURS.between(date1, date2);
        date2 = date2.minusHours(hours);
        long minutes = ChronoUnit.MINUTES.between(date1, date2);
        System.out.println(String.format("hours: %d, minutes: %d", hours, minutes));
    }
}
