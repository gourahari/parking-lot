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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ParkingLotManager.class);

    private List<ParkingLot> compactLots, handicappedLots, largeLots, motorcycleLots;
    private Map<ParkingToken, ParkingLot> occupiedLots = new HashMap<>();
    private ParkingLotConfig config;
    private Capacity capacity;

    @Autowired
    private PaymentService paymentService;
    private final class Capacity {
        private ParkingLotConfig config;
        private int total;
        private int handicapped;
        private int large;
        private int compact;
        private int motorcycle;
        private Capacity(ParkingLotConfig config) {
            this.config = config;
            init();
        }

        private void init() {
            total = config.getCapacity();
            compact = total;
            handicapped = (total * config.getHandicappedCapPercent()) / 100;
            compact -= handicapped;
            large = (total * config.getLargeCapPercent()) / 100;
            compact -= large;
            motorcycle = (total * config.getMotorcycleCapPercent()) / 100;
            compact -= motorcycle;
        }

        public int getTotal() {
            return total;
        }

        public int getCompact() {
            return compact;
        }

        public int getLarge() {
            return large;
        }

        public int getHandicapped() {
            return handicapped;
        }

        public int getMotorcycle() {
            return motorcycle;
        }
    }

    public void initialize(ParkingLotConfig config) {
        this.config = config;
        // Initialize the free lots cache
        capacity = new Capacity(config);
        compactLots = IntStream.range(0, capacity.compact)
                .mapToObj(e -> new CompactParkingLot(config.getCompactLotCost()))
                .collect(Collectors.toList());
        logger.info(
                "{} Compact lots cache initialized with cost: {}",
                capacity.compact, config.getCompactLotCost()
        );

        handicappedLots = IntStream.range(0, capacity.handicapped)
                .mapToObj(e -> new HandicappedParkingLot(config.getHandicappedLotCost()))
                .collect(Collectors.toList());
        logger.info(
                "{} Handicapped lots cache initialized with cost: {}",
                capacity.handicapped, config.getHandicappedLotCost()
        );

        largeLots = IntStream.range(0, capacity.large)
                .mapToObj(e -> new LargeParkingLot(config.getLargeLotCost()))
                .collect(Collectors.toList());
        logger.info(
                "{} Large lots cache initialized with cost: {}",
                capacity.large, config.getLargeLotCost()
        );

        motorcycleLots = IntStream.range(0, capacity.motorcycle)
                .mapToObj(e -> new MotorcycleParkingLot(config.getMotorcycleLotCost()))
                .collect(Collectors.toList());
        logger.info(
                "{} Motorcycle lots cache initialized with cost: {}",
                capacity.motorcycle, config.getMotorcycleLotCost()
        );
        logger.info("ParkingLotManager initialized with proper config.");
    }

    public ParkingLotNumbers getCapacity() {
        ParkingLotNumbers c = new ParkingLotNumbers();
        c.setTotal(capacity.getTotal());;
        c.setLarge(capacity.getLarge(), config.getLargeLotCost());
        c.setCompact(capacity.getCompact(), config.getCompactLotCost());
        c.setMotorcycle(capacity.getMotorcycle(), config.getMotorcycleLotCost());
        c.setHandicapped(capacity.getHandicapped(), config.getHandicappedLotCost());
        logger.info(
                "Capacity:: Total: {}, Large: {}, Compact: {}, Motorcycle: {}, Handicapped: {}",
                capacity.getTotal(), capacity.getLarge(), capacity.getCompact(),
                capacity.getMotorcycle(), capacity.getHandicapped()
        );
        return c;
    }

    public ParkingLotNumbers getAvailability() {
        ParkingLotNumbers n = new ParkingLotNumbers();
        n.setLarge(largeLots.size(), config.getLargeLotCost());
        n.setCompact(compactLots.size(), config.getCompactLotCost());
        n.setMotorcycle(motorcycleLots.size(), config.getMotorcycleLotCost());
        n.setHandicapped(handicappedLots.size(), config.getHandicappedLotCost());
        n.setTotal(capacity.getTotal() - occupiedLots.size());
        logger.info(
                "Parking lot availability:: Large: {}, Compact: {}, Motorcycle: {}, Handicapped: {}",
                largeLots.size(), compactLots.size(), motorcycleLots.size(), handicappedLots.size()
        );
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
        logger.debug("Checking if the parking lot type \"{}\" is valid.", info.getVehicleType());
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
        logger.debug("Parking lot type is valid.");
        List<ParkingLot> source = getParkingCacheByLotType(lotType);
        ParkingToken token = null;
        synchronized (source) {
            if (source.isEmpty()) {
                throw new NotAvailableException("No parking Lot available for vehicle type[" +
                        lotType + "].");
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
        logger.info("Parking lot[{}] reserved. Token id: {}", token.getLotId(), token.getTokenId());
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
