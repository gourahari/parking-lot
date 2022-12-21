package com.goura.system.parkinglot.exception;

public class PaymentGatewayException extends Exception {
    private static final long serialVersionUID = 1L;
    public PaymentGatewayException() {
        super();
    }
    public PaymentGatewayException(String message) {
        super(message);
    }
}
