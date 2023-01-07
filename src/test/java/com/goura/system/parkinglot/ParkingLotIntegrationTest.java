package com.goura.system.parkinglot;

import com.goura.system.parkinglot.config.ParkingLotConfig;
import com.goura.system.parkinglot.core.ParkingLotManager;
import com.goura.system.parkinglot.model.ParkingLotNumbers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.text.NumberFormat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParkingLotIntegrationTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private ParkingLotManager manager;
    @Autowired
    private ParkingLotConfig config;

    @BeforeAll
    public void setup() {
        manager.initialize(config);
    }

    @Test
    public void testGetCapacity() {
        ParkingLotNumbers n = manager.getCapacity();
        int total = config.getCapacity();
        int leftover = total, percent = 0;

        // Assert capacity
        Assertions.assertEquals(total, n.getTotal());
        Assertions.assertEquals((
                total * config.getLargeCapPercent()) / 100,
                n.getLarge().getCount()
        );
        leftover -= n.getLarge().getCount();
        Assertions.assertEquals((
                        total * config.getMotorcycleCapPercent()) / 100,
                n.getMotorcycle().getCount()
        );
        leftover -= n.getMotorcycle().getCount();
        Assertions.assertEquals((
                        total * config.getHandicappedCapPercent()) / 100,
                n.getHandicapped().getCount()
        );
        leftover -= n.getHandicapped().getCount();
        Assertions.assertEquals(leftover, n.getCompact().getCount());

        // Assert cost.
        NumberFormat format = NumberFormat.getCurrencyInstance();
        Assertions.assertEquals(format.format(config.getLargeLotCost()), n.getLarge().getCost());
        Assertions.assertEquals(format.format(config.getCompactLotCost()), n.getCompact().getCost());
        Assertions.assertEquals(format.format(config.getMotorcycleLotCost()), n.getMotorcycle().getCost());
        Assertions.assertEquals(format.format(config.getHandicappedLotCost()), n.getHandicapped().getCost());
    }
}
