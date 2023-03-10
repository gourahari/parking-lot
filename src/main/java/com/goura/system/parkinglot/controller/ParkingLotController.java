package com.goura.system.parkinglot.controller;

import com.goura.system.parkinglot.core.ParkingLotManager;
import com.goura.system.parkinglot.core.ParkingToken;
import com.goura.system.parkinglot.exception.NotAvailableException;
import com.goura.system.parkinglot.exception.PaymentGatewayException;
import com.goura.system.parkinglot.exception.TokenNotFoundException;
import com.goura.system.parkinglot.model.CheckinInfo;
import com.goura.system.parkinglot.model.ParkingLot;
import com.goura.system.parkinglot.model.ParkingLotNumbers;
import com.goura.system.parkinglot.model.ParkingReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final Logger logger = LoggerFactory.getLogger(ParkingLotController.class);

    @Autowired
    private ParkingLotManager manager;

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

    @PostMapping("/token/{tokenId}/release")
    public ParkingReceipt releaseParkingLot(
            @PathVariable @NotNull @NotEmpty String tokenId) throws Exception {
        return manager.releaseParkingLot(tokenId);
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

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity handlePaymentGatewayException(PaymentGatewayException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleDefaultException(Exception e) {
        return new ResponseEntity<>("Internal Server Error!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
