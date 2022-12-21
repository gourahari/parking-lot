package com.goura.system.parkinglot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TokenNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    public TokenNotFoundException() {
        super();
    }
    public TokenNotFoundException(String message) {
        super(message);
    }
}
