package com.goura.system.parkinglot.aop;

import com.goura.system.parkinglot.core.ParkingToken;
import com.goura.system.parkinglot.model.CheckinInfo;
import com.goura.system.parkinglot.model.ParkingReceipt;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ParkingLotControllerAspect {

    private static final Logger logger = LoggerFactory.getLogger(ParkingLotControllerAspect.class);

    @Before("execution(* com.goura.system.parkinglot.controller.ParkingLotController.handle*(..)) and args(e)")
    public void beforeAdvice(JoinPoint joinPoint, Exception e) {
        logger.error(e.getMessage());
    }
}
