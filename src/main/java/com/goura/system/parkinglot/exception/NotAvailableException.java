package com.goura.system.parkinglot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotAvailableException extends Exception {
    private static final long serialVersionUID = 1L;
    public NotAvailableException() {
        super();
    }
    public NotAvailableException(String message) {
        super(message);
    }
}
