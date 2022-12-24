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
public class ParkingLotManagerAspect {

    private static final Logger logger = LoggerFactory.getLogger(ParkingLotManagerAspect.class);

    @Before("execution(* com.goura.system.parkinglot.core.ParkingLotManager.reserve(..)) and args(info)")
    public void beforeAdvice(JoinPoint joinPoint, CheckinInfo info) {
        logger.info("Reserving a parking lot for a {} vehicle.", info.getVehicleType());
    }

    @AfterReturning(
            value = "execution(* com.goura.system.parkinglot.core.ParkingLotManager.reserve(..)) and args(info)",
            returning = "token"
    )
    public void returnAdvice(JoinPoint joinPoint, CheckinInfo info, ParkingToken token) {
        logger.info("Successfully reserved a parking lot for a {} vehicle. Token id: {}.",
                info.getVehicleType(),
                token.getTokenId());
    }

    @Before("execution(* com.goura.system.parkinglot.core.ParkingLotManager.release*(..)) and args(tokenId)")
    public void beforeAdvice(JoinPoint joinPoint, String tokenId) {
        logger.info("Releasing parking lot with token id: {}", tokenId);
    }

    @AfterReturning(
            value = "execution(* com.goura.system.parkinglot.core.ParkingLotManager.release*(..)) and args(tokenId)",
            returning = "receipt"
    )
    public void beforeAdvice(JoinPoint joinPoint, String tokenId, ParkingReceipt receipt) {
        logger.info("Parking lot with token id: {} successfully released. {}", tokenId, receipt.getMessage());
    }

}
