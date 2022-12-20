package com.goura.system.parkinglot.controller;

import com.goura.system.parkinglot.core.ParkingLotManager;
import com.goura.system.parkinglot.exception.NotAvailableException;
import com.goura.system.parkinglot.model.CheckinInfo;
import com.goura.system.parkinglot.model.ParkingLotNumbers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ParkingLotController {
    private final ParkingLotManager manager = ParkingLotManager.getInstance();

    @GetMapping("/capacity")
    public ParkingLotNumbers getCapacity() {
        return manager.getCapacity();
    }

    @GetMapping("/available")
    public ParkingLotNumbers getAvailability() {
        return manager.getAvailability();
    }

    @PutMapping("/reserve")
    public String reserveParkingLot(@RequestBody CheckinInfo checkin) throws Exception {
        return manager.reserve(checkin);
    }

    @ExceptionHandler(NotAvailableException.class)
    public void handleNotAvailableException(NotAvailableException e) {
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleDefaultException(Exception e) {
        e.printStackTrace(System.err);
        return new ResponseEntity<>("Internal Server Error!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
