package com.goura.system.parkinglot;

import com.goura.system.parkinglot.config.ParkingLotConfig;
import com.goura.system.parkinglot.core.ParkingLotManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParkingLotApplication {

	public static void main(String[] args) {
		// Initialize the config object;
		ParkingLotConfig config = ParkingLotConfig.getInstance();

		// Initialize the parking lot manager
		ParkingLotManager manager = ParkingLotManager.getInstance();
		manager.initialize(config);

		SpringApplication.run(ParkingLotApplication.class, args);
	}

}
