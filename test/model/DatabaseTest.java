package model;

import model.Entities.Customer;
import model.Entities.VehicleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

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
//        try {
//            TimePeriod t = new TimePeriod();
//            t.toDateTime = new Timestamp(1000000000);
//            t.fromDateTime = new Timestamp(28801000);
//
//            Location l = new Location();
//            l.city = "Vancouver";
//            l.location = "123 Burrard Street";
//
//            Reservation add = new Reservation(123456, "Sedan", t, l, "123abc");
//            db.addReservation(add);
//
//            Reservation result = db.getReservationMatching(add);
//            assertEquals(add, result);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    void updateReservation() {

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
        try {
            Customer add = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.addCustomer(add);
            Customer result = db.getCustomerMatching(add);
            assertEquals(add.cellphone, result.cellphone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateCustomerNoNulls() {
        try {
            Customer add = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.addCustomer(add);
            Customer update = new Customer(6044206969L, "Bobby", "1234 University Boulevard", "1234abcd");
            db.updateCustomer(update);
            Customer result = db.getCustomerMatching(add);
            assertEquals(result.cellphone, update.cellphone);
            assertEquals(result.dlicense, update.dlicense);
            assertEquals(result.name, update.name);
            assertEquals(result.address, update.address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateCustomerWithNulls() {
        try {
            Customer add = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.addCustomer(add);
            Customer update = new Customer(-1, "Bobby", null, "1234abcd");
            db.updateCustomer(update);
            Customer result = db.getCustomerMatching(add);
            assertEquals(result.cellphone, add.cellphone);
            assertEquals(result.dlicense, update.dlicense);
            assertEquals(result.name, update.name);
            assertEquals(result.address, add.address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateCustomerNotExists() {
        try {
            Customer NotExists = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.updateCustomer(NotExists);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteCustomer() {
        try {
            Customer add = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.addCustomer(add);
            db.getCustomerMatching(add);
            db.deleteCustomer(add);
            db.getCustomerMatching(add);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteCustomerNotExists() {
        try {
            Customer notExists = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.deleteCustomer(notExists);
        } catch (Exception e) {
            e.printStackTrace();
        }
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