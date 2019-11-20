package model;

import model.Entities.Location;
import model.Entities.Reservation;
import model.Entities.TimePeriod;
import model.Entities.VehicleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void updateVehicleType() {

    }

    @Test
    void deleteVehicleType() {
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