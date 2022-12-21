package com.goura.system.parkinglot.controller;

import com.goura.system.parkinglot.core.ParkingLotManager;
import com.goura.system.parkinglot.core.ParkingToken;
import com.goura.system.parkinglot.exception.NotAvailableException;
import com.goura.system.parkinglot.exception.TokenNotFoundException;
import com.goura.system.parkinglot.model.CheckinInfo;
import com.goura.system.parkinglot.model.ParkingLot;
import com.goura.system.parkinglot.model.ParkingLotNumbers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@Validated
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

    @GetMapping("/token/{tokenId}")
    public ParkingToken getParkingToken(
            @PathVariable @NotNull @NotEmpty String tokenId) throws Exception {
        return manager.getParkingToken(tokenId);
    }

    @PutMapping("/reserve")
    public ParkingToken reserveParkingLot(
            @RequestBody @Valid CheckinInfo checkin) throws Exception {
        return manager.reserve(checkin);
    }

    @ExceptionHandler(NotAvailableException.class)
    public ResponseEntity handleNotAvailableException(NotAvailableException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity handleTokenNotFoundException(TokenNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleInvalidArguments(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(e.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ResponseEntity handleInvalidArguments(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleDefaultException(Exception e) {
        e.printStackTrace(System.err);
        return new ResponseEntity<>("Internal Server Error!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
