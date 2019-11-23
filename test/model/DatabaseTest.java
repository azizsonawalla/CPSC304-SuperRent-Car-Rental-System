package model;

import model.Entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseTest {
    Database db;

    @BeforeEach
    void setUp() {
        try {
            db = new Database(true);
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

    //region Reservation Tests

    @Test
    void addReservation() {
        try {
            //populate VehicleType and Customer table with data so that foreign key constraints can be enforced
            VehicleType vt = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(vt);
            Customer c = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.addCustomer(c);

            //Create Reservation object to add
            TimePeriod t = new TimePeriod();
            t.endDateAndTime = new Timestamp(1000000000);
            t.startDateAndTime = new Timestamp(28801000);

            Location l = new Location();
            l.city = "Vancouver";
            l.location = "123 Burrard Street";

            Reservation add = new Reservation(123456, "Sedan", t, l, "1234abcd");
            db.addReservation(add);

            List<Reservation> result = db.getReservationMatching(add);
            assertEquals(add.confNum, result.get(0).confNum);
            assertEquals(add.vtName, result.get(0).vtName);
            assertEquals(add.timePeriod.endDateAndTime, result.get(0).timePeriod.endDateAndTime);
            assertEquals(add.timePeriod.startDateAndTime, result.get(0).timePeriod.startDateAndTime);
            assertEquals(add.location.city, result.get(0).location.city);
            assertEquals(add.location.location, result.get(0).location.location);
            assertEquals(add.dlicense, result.get(0).dlicense);

            //empty out sample data in VehicleType and Customer tables
            db.deleteVehicleType(vt);
            db.deleteCustomer(c);
            db.deleteReservation(add);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateReservationNoNulls() {
        try {
            //populate VehicleType and Customer table with data so that foreign key constraints can be enforced
            VehicleType vt1 = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            VehicleType vt2 = new VehicleType("SUV", "four doors, 5 seats, 1 sun roof", 400,
                    80, 8, 150, 30, 3, 2);
            db.addVehicleType(vt1);
            db.addVehicleType(vt2);
            Customer c1 = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            Customer c2 = new Customer(6044206969L, "Bobby", "1234 University Boulevard", "5678efgh");
            db.addCustomer(c1);
            db.addCustomer(c2);

            //Create Reservation object to add/update
            TimePeriod t1 = new TimePeriod();
            t1.endDateAndTime = new Timestamp(1000000000);
            t1.startDateAndTime = new Timestamp(28801000);
            TimePeriod t2 = new TimePeriod();
            t2.endDateAndTime = new Timestamp(900000000);
            t2.startDateAndTime = new Timestamp(38801000);

            Location l1 = new Location();
            l1.city = "Vancouver";
            l1.location = "123 Burrard Street";
            Location l2 = new Location();
            l2.city = "Calgary";
            l2.location = "456 Calgary Street";

            Reservation add = new Reservation(123456, "Sedan", t1, l1, "1234abcd");
            db.addReservation(add);

            Reservation update = new Reservation(123456, "SUV", t2, l2, "5678efgh");
            db.updateReservation(update);

            List<Reservation> result = db.getReservationMatching(add);
            assertEquals(update.confNum, result.get(0).confNum);
            assertEquals(update.vtName, result.get(0).vtName);
            assertEquals(update.timePeriod.endDateAndTime, result.get(0).timePeriod.endDateAndTime);
            assertEquals(update.timePeriod.startDateAndTime, result.get(0).timePeriod.startDateAndTime);
            assertEquals(update.location.city, result.get(0).location.city);
            assertEquals(update.location.location, result.get(0).location.location);
            assertEquals(update.dlicense, result.get(0).dlicense);

            //empty out sample data in VehicleType and Customer tables
            db.deleteVehicleType(vt1);
            db.deleteVehicleType(vt2);
            db.deleteCustomer(c1);
            db.deleteCustomer(c2);
            db.deleteReservation(add);
            db.deleteReservation(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateReservationWithNulls() {
        try {
            //populate VehicleType and Customer table with data so that foreign key constraints can be enforced
            VehicleType vt1 = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(vt1);
            Customer c1 = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.addCustomer(c1);

            //Create Reservation object to add/update
            TimePeriod t1 = new TimePeriod();
            t1.endDateAndTime = new Timestamp(1000000000);
            t1.startDateAndTime = new Timestamp(28801000);

            Location l1 = new Location();
            l1.city = "Vancouver";
            l1.location = "123 Burrard Street";

            Reservation add = new Reservation(123456, "Sedan", t1, l1, "1234abcd");
            db.addReservation(add);

            Reservation update = new Reservation(123456, null, null, null, null);
            db.updateReservation(update);

            List<Reservation> result = db.getReservationMatching(add);
            assertEquals(add.confNum, result.get(0).confNum);
            assertEquals(add.vtName, result.get(0).vtName);
            assertEquals(add.timePeriod.endDateAndTime, result.get(0).timePeriod.endDateAndTime);
            assertEquals(add.timePeriod.startDateAndTime, result.get(0).timePeriod.startDateAndTime);
            assertEquals(add.location.city, result.get(0).location.city);
            assertEquals(add.location.location, result.get(0).location.location);
            assertEquals(add.dlicense, result.get(0).dlicense);

            //empty out sample data in VehicleType and Customer tables
            db.deleteVehicleType(vt1);
            db.deleteCustomer(c1);
            db.deleteReservation(add);
            db.deleteReservation(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteReservation() {
        try {
            //populate VehicleType and Customer table with data so that foreign key constraints can be enforced
            VehicleType vt1 = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(vt1);
            Customer c1 = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.addCustomer(c1);

            //Create Reservation object to add/update
            TimePeriod t1 = new TimePeriod();
            t1.endDateAndTime = new Timestamp(1000000000);
            t1.startDateAndTime = new Timestamp(28801000);

            Location l1 = new Location();
            l1.city = "Vancouver";
            l1.location = "123 Burrard Street";

            Reservation add = new Reservation(123456, "Sedan", t1, l1, "1234abcd");
            db.addReservation(add);
            db.getReservationMatching(add);
            db.deleteReservation(add);
            db.getReservationMatching(add);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getReservationsWithNone() {
        try {
            //populate VehicleType and Customer table with data so that foreign key constraints can be enforced
            VehicleType vt1 = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            VehicleType vt2 = new VehicleType("SUV", "four doors, 5 seats, 1 sun roof", 400,
                    80, 8, 150, 30, 3, 2);
            db.addVehicleType(vt1);
            db.addVehicleType(vt2);
            Customer c1 = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            Customer c2 = new Customer(6044206969L, "Bobby", "1234 University Boulevard", "5678efgh");
            db.addCustomer(c1);
            db.addCustomer(c2);

            //Create Reservation object to add/update
            TimePeriod t1 = new TimePeriod();
            t1.endDateAndTime = new Timestamp(1000000000);
            t1.startDateAndTime = new Timestamp(28801000);
            TimePeriod t2 = new TimePeriod();
            t2.endDateAndTime = new Timestamp(900000000);
            t2.startDateAndTime = new Timestamp(38801000);

            Location l1 = new Location();
            l1.city = "Vancouver";
            l1.location = "123 Burrard Street";
            Location l2 = new Location();
            l2.city = "Calgary";
            l2.location = "456 Calgary Street";

            Reservation r1 = new Reservation(123456, "Sedan", t1, l1, "1234abcd");
            Reservation r2 = new Reservation(987654, "SUV", t2, l2, "5678efgh");
            db.addReservation(r1);
            db.addReservation(r2);

            List<Reservation> r = db.getReservationsWith(null, null, null);

            assertEquals(2, r.size());
            assertEquals(123456, r.get(0).confNum);
            assertEquals("Sedan", r.get(0).vtName);
            assertEquals(t1.startDateAndTime, r.get(0).timePeriod.startDateAndTime);
            assertEquals(t1.endDateAndTime, r.get(0).timePeriod.endDateAndTime);
            assertEquals(l1.city, r.get(0).location.city);
            assertEquals(l1.location, r.get(0).location.location);
            assertEquals("1234abcd", r.get(0).dlicense);

            assertEquals(987654, r.get(1).confNum);
            assertEquals("SUV", r.get(1).vtName);
            assertEquals(t2.startDateAndTime, r.get(1).timePeriod.startDateAndTime);
            assertEquals(t2.endDateAndTime, r.get(1).timePeriod.endDateAndTime);
            assertEquals(l2.city, r.get(1).location.city);
            assertEquals(l2.location, r.get(1).location.location);
            assertEquals("5678efgh", r.get(1).dlicense);

            db.deleteReservation(r1);
            db.deleteReservation(r2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getReservationsWithAll() {
        try {
            //populate VehicleType and Customer table with data so that foreign key constraints can be enforced
            VehicleType vt1 = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            VehicleType vt2 = new VehicleType("SUV", "four doors, 5 seats, 1 sun roof", 400,
                    80, 8, 150, 30, 3, 2);
            db.addVehicleType(vt1);
            db.addVehicleType(vt2);
            Customer c1 = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            Customer c2 = new Customer(6044206969L, "Bobby", "1234 University Boulevard", "5678efgh");
            db.addCustomer(c1);
            db.addCustomer(c2);

            //Create Reservation object to add/update
            TimePeriod t1 = new TimePeriod();
            t1.endDateAndTime = new Timestamp(1000000000);
            t1.startDateAndTime = new Timestamp(28801000);
            TimePeriod t2 = new TimePeriod();
            t2.endDateAndTime = new Timestamp(900000000);
            t2.startDateAndTime = new Timestamp(38801000);

            Location l1 = new Location();
            l1.city = "Vancouver";
            l1.location = "123 Burrard Street";
            Location l2 = new Location();
            l2.city = "Calgary";
            l2.location = "456 Calgary Street";

            Reservation r1 = new Reservation(123456, "Sedan", t1, l1, "1234abcd");
            Reservation r2 = new Reservation(987654, "SUV", t2, l2, "5678efgh");
            db.addReservation(r1);
            db.addReservation(r2);

            List<Reservation> r = db.getReservationsWith(t1, vt1, l1);

            assertEquals(1, r.size());
            assertEquals(123456, r.get(0).confNum);
            assertEquals("Sedan", r.get(0).vtName);
            assertEquals(t1.startDateAndTime, r.get(0).timePeriod.startDateAndTime);
            assertEquals(t1.endDateAndTime, r.get(0).timePeriod.endDateAndTime);
            assertEquals(l1.city, r.get(0).location.city);
            assertEquals(l1.location, r.get(0).location.location);
            assertEquals("1234abcd", r.get(0).dlicense);

            db.deleteReservation(r1);
            db.deleteReservation(r2);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //endregion

    //region Rental Tests

//    @Test
//    void addRental() {
//
//        try {
//            //populate VehicleType, Card, Reservation and Customer table with data so that foreign key constraints can
//            // be enforced
//            VehicleType vt = new VehicleType("Sedan", "four doors, 5 seats", 300,
//                    70, 7, 100, 20, 2, 1);
//            db.addVehicleType(vt);
//            Customer c = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
//            db.addCustomer(c);
//
//            //Create Reservation object to add
//            TimePeriod t = new TimePeriod();
//            t.endDateAndTime = new Timestamp(1000000000);
//            t.startDateAndTime = new Timestamp(28801000);
//
//            Location l = new Location();
//            l.city = "Vancouver";
//            l.location = "123 Burrard Street";
//
//            Reservation r = new Reservation(123456, "Sedan", t, l, "1234abcd");
//            db.addReservation(r);
//
//            Card card = new Card(60115564485789458L,"Discover", 1923);
//            db.addCard(card);
//
//            Vehicle v = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
//                    529348, "Sedan", true, l);
//
//
//            Rental add = new Rental(1, "123abc", "123abc", t, 1000000, card,123456);
//
//
//            Rental result = db.getRentalMatching(add);
//            assertEquals(add.rid, result.rid);
//            assertEquals(add.vlicense, result.vlicense);
//            assertEquals(add.dlicense, result.dlicense);
//            assertEquals(add.timePeriod.endDateAndTime, result.timePeriod.endDateAndTime);
//            assertEquals(add.timePeriod.startDateAndTime, result.timePeriod.startDateAndTime);
//            assertEquals(add.startOdometer, result.startOdometer);
//            assertEquals(add.card.cardNo, result.card.cardNo);
//            assertEquals(add.card.cardName, result.card.cardName);
//            assertEquals(add.card.expDate, result.card.expDate);
//            assertEquals(add.confNo, result.confNo);
//
//
//            //empty out sample data in VehicleType and Customer tables
//            db.deleteVehicleType(vt);
//            db.deleteCustomer(c);
//            db.deleteCard(card);  // Not ready
//            db.deleteLocation(l); // Not ready
//            db.deleteVehicle(v);
//            db.deleteReservation(r);
//
//            db.deleteRental(add);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

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

    //endregion

    //region Customer Tests

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

    //endregion

    //region VehicleType Tests

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
    void getAllVehicleTypesTest() {
        try {
            VehicleType add1 = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            VehicleType add2 = new VehicleType("SUV", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            VehicleType add3 = new VehicleType("Hatchback", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            VehicleType add4 = new VehicleType("Convertible", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);

            db.addVehicleType(add1);
            db.addVehicleType(add2);
            db.addVehicleType(add3);
            db.addVehicleType(add4);
            List<VehicleType> result = db.getAllVehicleTypes();
            assertEquals(4, result.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Vehicle Tests

    @Test
    void addVehicle() {
        try {
            //populate VehicleType table with data so that foreign key constraints can be enforced
            VehicleType vt = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(vt);

            Location l = new Location();
            l.location = "123 Burrard Street"; l.city = "Vancouver";

            Vehicle add = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
                    529348, "Sedan", Vehicle.VehicleStatus.RENTED, l);
            db.addVehicle(add);
            Vehicle result = db.getVehicleMatching(add);
            assertEquals(add.vtname, result.vtname);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void updateVehicleNoNulls() {
        try {
            //populate VehicleType table with data so that foreign key constraints can be enforced
            VehicleType vt = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(vt);
            VehicleType vt2 = new VehicleType("SUV", "four doors, 5 seats, 1 sun roof", 400,
                    80, 8, 150, 30, 3, 2);
            db.addVehicleType(vt2);

            Location l1 = new Location("Vancouver", "123 Burrard Street");
            Location l2 = new Location("Calgary", "123 Calgary Street");

            Vehicle add = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
                    529348, "Sedan", Vehicle.VehicleStatus.AVAILABLE, l1);
            db.addVehicle(add);

            Vehicle update = new Vehicle(56789, "123abc", "Audi", "Q7", 2017, "red",
                    29362, "SUV", Vehicle.VehicleStatus.RENTED, l2);
            db.updateVehicle(update);

            Vehicle result = db.getVehicleMatching(add);
            assertEquals(update.vid, result.vid);
            assertEquals(update.vlicense, result.vlicense);
            assertEquals(update.make, result.make);
            assertEquals(update.model, result.model);
            assertEquals(update.year, result.year);
            assertEquals(update.color, result.color);
            assertEquals(update.odometer, result.odometer);
            assertEquals(update.vtname, result.vtname);
            assertEquals(update.status, result.status);
            assertEquals(update.location.location, result.location.location);
            assertEquals(update.location.city, result.location.city);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateVehicleWithNulls() {
        try {
            //populate VehicleType table with data so that foreign key constraints can be enforced
            VehicleType vt = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(vt);

            Location l1 = new Location("Vancouver", "123 Burrard Street");
            Location l2 = new Location("Calgary", "123 Calgary Street");

            Vehicle add = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
                    529348, "Sedan", Vehicle.VehicleStatus.AVAILABLE, l1);
            db.addVehicle(add);

            Vehicle update = new Vehicle(-1, "123abc", null, null, -1, null,
                    -1, null, null, null);
            db.updateVehicle(update);

            Vehicle result = db.getVehicleMatching(add);
            assertEquals(add.vid, result.vid);
            assertEquals(add.vlicense, result.vlicense);
            assertEquals(add.make, result.make);
            assertEquals(add.model, result.model);
            assertEquals(add.year, result.year);
            assertEquals(add.color, result.color);
            assertEquals(add.odometer, result.odometer);
            assertEquals(add.vtname, result.vtname);
            assertEquals(add.status, result.status);
            assertEquals(add.location.location, result.location.location);
            assertEquals(add.location.city, result.location.city);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteVehicle() {
        try {
            //populate VehicleType table with data so that foreign key constraints can be enforced
            VehicleType vt = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(vt);

            Location l = new Location();
            l.location = "123 Burrard Street"; l.city = "Vancouver";

            Vehicle add = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
                    529348, "Sedan", Vehicle.VehicleStatus.AVAILABLE, l);
            db.addVehicle(add);
            db.deleteVehicle(add);
            Vehicle result = db.getVehicleMatching(add);
            assertEquals(null, result);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void getVehicleWithAll() {
        try {
            //populate VehicleType table with data so that foreign key constraints can be enforced
            VehicleType vt1 = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            VehicleType vt2 = new VehicleType("SUV", "four doors, 5 seats, 1 sun roof", 400,
                    80, 8, 150, 30, 3, 2);
            db.addVehicleType(vt1);
            db.addVehicleType(vt2);

            //Create Vehicle object to add/update
            Location l1 = new Location("Vancouver", "123 Burrard Street");
            Location l2 = new Location("Calgary", "123 Calgary Street");

            Vehicle v1 = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
                    529348, "Sedan", Vehicle.VehicleStatus.AVAILABLE, l1);
            db.addVehicle(v1);

            Vehicle v2 = new Vehicle(56789, "456def", "Audi", "Q7", 2017, "red",
                    29362, "SUV", Vehicle.VehicleStatus.RENTED, l2);
            db.addVehicle(v2);

            List<Vehicle> vehicles = db.getVehicleWith(null, null, null);

            assertEquals(2, vehicles.size());

            db.deleteVehicle(v1);
            db.deleteVehicle(v2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getVehicleWithCombo() {
        try {
            //populate VehicleType table with data so that foreign key constraints can be enforced
            VehicleType vt1 = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            VehicleType vt2 = new VehicleType("SUV", "four doors, 5 seats, 1 sun roof", 400,
                    80, 8, 150, 30, 3, 2);
            db.addVehicleType(vt1);
            db.addVehicleType(vt2);

            //Create Vehicle object to add/update
            Location l1 = new Location("Vancouver", "123 Burrard Street");
            Location l2 = new Location("Calgary", "123 Calgary Street");

            Vehicle v1 = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
                    529348, "Sedan", Vehicle.VehicleStatus.AVAILABLE, l1);
            db.addVehicle(v1);

            Vehicle v2 = new Vehicle(56789, "456def", "Audi", "Q7", 2017, "red",
                    29362, "SUV", Vehicle.VehicleStatus.RENTED, l2);
            db.addVehicle(v2);

            List<Vehicle> vehicles = db.getVehicleWith(vt1, null, null);
            assertEquals(1, vehicles.size());
            assertEquals("123abc", vehicles.get(0).vlicense);

            vehicles = db.getVehicleWith(null, l1, null);
            assertEquals(1, vehicles.size());
            assertEquals("123abc", vehicles.get(0).vlicense);

            vehicles = db.getVehicleWith(null, null, Vehicle.VehicleStatus.AVAILABLE);
            assertEquals(1, vehicles.size());
            assertEquals("123abc", vehicles.get(0).vlicense);

            vehicles = db.getVehicleWith(vt1, l1, null);
            assertEquals(1, vehicles.size());
            assertEquals("123abc", vehicles.get(0).vlicense);

            vehicles = db.getVehicleWith(vt1, null, Vehicle.VehicleStatus.AVAILABLE);
            assertEquals(1, vehicles.size());
            assertEquals("123abc", vehicles.get(0).vlicense);

            vehicles = db.getVehicleWith(null, l1, Vehicle.VehicleStatus.AVAILABLE);
            assertEquals(1, vehicles.size());
            assertEquals("123abc", vehicles.get(0).vlicense);

            vehicles = db.getVehicleWith(vt1, l1, Vehicle.VehicleStatus.AVAILABLE);
            assertEquals(1, vehicles.size());
            assertEquals("123abc", vehicles.get(0).vlicense);

            db.deleteVehicle(v1);
            db.deleteVehicle(v2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion
}