package model;

import model.Entities.Location;
import model.Entities.Reservation;
import model.Entities.TimePeriod;
import model.Entities.VehicleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseTest {
    Database db;

    @BeforeEach
    void setUp() {
        try {
            db = new Database();
            db.createTables();
            System.out.println("tables successfully created!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        try {
            db.dropTables();
            System.out.println("tables successfully dropped!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void createTables() {
    }

    @Test
    void addReservation() {
        try {
            TimePeriod t = new TimePeriod();
            t.toDateTime = new Timestamp(1000000000);
            t.fromDateTime = new Timestamp(28801000);

            Location l = new Location();
            l.city = "Vancouver";
            l.location = "123 Burrard Street";

            Reservation add = new Reservation(123456, "Sedan", t, l, "123abc");
            db.addReservation(add);

            Reservation result = db.getReservationMatching(add);
            assertEquals(add, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateReservation() {
        TimePeriod t = new TimePeriod();
        t.toDateTime = new Timestamp(900);
        t.fromDateTime = new Timestamp(800);

        Location l;
    }

    @Test
    void deleteReservation() {
    }

    @Test
    void getReservationsWith() {
    }

    @Test
    void getReservationMatching() {
    }

    @Test
    void addRental() {
    }

    @Test
    void updateRental() {
    }

    @Test
    void deleteRental() {
    }

    @Test
    void getRentalsWith() {
    }

    @Test
    void getRentalMatching() {
    }

    @Test
    void addCustomer() {
    }

    @Test
    void updateCustomer() {
    }

    @Test
    void deleteCustomer() {
    }

    @Test
    void getCustomerMatching() {
    }

    @Test
    void addVehicleType() {
        try {
            VehicleType add = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(add);
            VehicleType result = db.getVehicleTypeMatching(add);
            assertEquals(add.vtname, result.vtname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateVehicleTypeNoNulls() {
        try {
            VehicleType add = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(add);
            VehicleType update = new VehicleType("Sedan", "four doors, 5 seats, 1 sunroof", 370,
                    80, 8, 170, 27, 3 ,2);
            db.updateVehicleType(update);
            VehicleType result = db.getVehicleTypeMatching(add);
            assertEquals(result.features, update.features);
            assertEquals(result.wrate, update.wrate);
            assertEquals(result.drate, update.drate);
            assertEquals(result.hrate, update.hrate);
            assertEquals(result.wirate, update.wirate);
            assertEquals(result.dirate, update.dirate);
            assertEquals(result.hirate, update.hirate);
            assertEquals(result.krate, update.krate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateVehicleTypeWithNulls() {
        try {
            VehicleType add = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(add);
            VehicleType update = new VehicleType("Sedan", null, -1,
                    -1, -1, 170, 27, 3 ,-1);
            db.updateVehicleType(update);
            VehicleType result = db.getVehicleTypeMatching(add);
            assertEquals(result.features, add.features);
            assertEquals(result.wrate, add.wrate);
            assertEquals(result.drate, add.drate);
            assertEquals(result.hrate, add.hrate);
            assertEquals(result.wirate, update.wirate);
            assertEquals(result.dirate, update.dirate);
            assertEquals(result.hirate, update.hirate);
            assertEquals(result.krate, add.krate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateVehicleTypeNotExists() {
        try {
            VehicleType notExists = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.updateVehicleType(notExists);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteVehicleType() {
        try {
            VehicleType add = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(add);
            db.getVehicleTypeMatching(add);
            db.deleteVehicleType(add);
            db.getVehicleTypeMatching(add);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteVehicleTypeNotExists() {
        try {
            VehicleType notExists = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.deleteVehicleType(notExists);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getVehicleTypeMatching() {
    }

    @Test
    void addVehicle() {
    }

    @Test
    void updateVehicle() {
    }

    @Test
    void deleteVehicle() {
    }

    @Test
    void getVehicleWith() {
    }

    @Test
    void getVehicleMatching() {
    }
}