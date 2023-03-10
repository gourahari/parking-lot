package com.goura.system.parkinglot;

import com.goura.system.parkinglot.config.ParkingLotConfig;
import com.goura.system.parkinglot.core.ParkingLotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotAppRunner implements CommandLineRunner {

    @Autowired
    private ParkingLotManager manager;

    @Autowired
    private ParkingLotConfig config;

    @Override
    public void run(String... args) throws Exception {
        // Initialize the parking lot manager
        manager.initialize(config);
    }
}
