package com.goura.system.parkinglot.core;

import com.goura.system.parkinglot.config.ParkingLotType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class ParkingTokenBuilderTest {

    @Test
    public void testBuildWithParkingLotIdMissing() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ParkingToken token = ParkingTokenBuilder.create()
                    .setEntryTime(new Date())
                    .setType(ParkingLotType.Large)
                    .setLicensePlate("ABC123")
                    .setPhoneNumber("1234567890")
                    .build();
        });
        Assertions.assertTrue(e.getMessage().contains("LotId"));
    }

    @Test
    public void testBuildWithParkingLotTypeMissing() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ParkingToken token = ParkingTokenBuilder.create()
                    .setEntryTime(new Date())
                    .setLicensePlate("ABC123")
                    .setPhoneNumber("1234567890")
                    .setLotId("123-456-789")
                    .build();
        });
        Assertions.assertTrue(e.getMessage().contains("ParkingLotType"));
    }

    @Test
    public void testBuildWithLicensePlateMissing() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ParkingToken token = ParkingTokenBuilder.create()
                    .setType(ParkingLotType.Motorcycle)
                    .setEntryTime(new Date())
                    .setPhoneNumber("1234567890")
                    .setLotId("123-456-789")
                    .build();
        });
        Assertions.assertTrue(e.getMessage().contains("LicensePlate"));
    }

    @Test
    public void testBuildWithPhoneNumberMissing() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ParkingToken token = ParkingTokenBuilder.create()
                    .setType(ParkingLotType.Compact)
                    .setEntryTime(new Date())
                    .setLicensePlate("ABC123")
                    .setLotId("123-456-789")
                    .build();
        });
        Assertions.assertTrue(e.getMessage().contains("PhoneNumber"));
    }

    @Test
    public void testBuildWithEntryTimeMissing() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ParkingToken token = ParkingTokenBuilder.create()
                    .setType(ParkingLotType.Large)
                    .setLicensePlate("ABC123")
                    .setPhoneNumber("1234567890")
                    .setLotId("123-456-789")
                    .build();
        });
        Assertions.assertTrue(e.getMessage().contains("EntryTime"));
    }

    @Test
    public void testBuild() {
        ParkingToken token = ParkingTokenBuilder.create()
                .setType(ParkingLotType.Handicapped)
                .setEntryTime(new Date())
                .setLicensePlate("ABC123")
                .setPhoneNumber("1234567890")
                .setLotId("123-456-789")
                .build();
    }
}
