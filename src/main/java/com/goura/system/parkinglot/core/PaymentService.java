package com.goura.system.parkinglot.core;

import com.goura.system.parkinglot.exception.PaymentGatewayException;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

@Service
public class PaymentService {

    public String processPayment(double amount) throws PaymentGatewayException {
        int random = new Random().nextInt(10);
        if (random < 4) {
            throw new PaymentGatewayException("Error while processing the payment!");
        }
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
