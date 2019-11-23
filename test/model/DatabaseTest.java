package model;

import model.Entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class DatabaseTest {
    Database db;

    @BeforeEach
    void setUp() {
        try {
            db = new Database();
            db.dropTables();
            System.out.println("tables successfully dropped before test!");
            db.createTables();
            System.out.println("tables successfully created!");
        } catch (Exception e) {
            System.out.println("Setup failed");
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
            db.addLocation(l);

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
            fail();
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
            db.addLocation(l1);


            Reservation add = new Reservation(123456, "Sedan", t1, l1, "1234abcd");
            db.addReservation(add);

            Reservation update = new Reservation(123456, "SUV", t2, l1, "5678efgh");
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
            fail();
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
            db.addLocation(l1);

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
            fail();
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
            db.addLocation(l1);

            Reservation add = new Reservation(123456, "Sedan", t1, l1, "1234abcd");
            db.addReservation(add);
            db.getReservationMatching(add);
            db.deleteReservation(add);
            db.getReservationMatching(add);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
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
            db.addLocation(l1);
            db.addLocation(l2);

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
            fail();
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
            db.addLocation(l1);
            db.addLocation(l2);

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
            fail();
        }
    }

    @Test
    void addRentalTest() {

        try {
            //populate VehicleType, Card, Reservation and Customer table with data so that foreign key constraints can
            // be enforced
            VehicleType vt = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(vt);

            Customer c = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.addCustomer(c);

            //Create Rent object to add
            TimePeriod t = new TimePeriod();
            t.endDateAndTime = new Timestamp(1000000000);
            t.startDateAndTime = new Timestamp(28801000);

            Location l = new Location();
            l.city = "Vancouver";
            l.location = "123 Burrard Street";
            db.addLocation(l);

            Reservation r = new Reservation(123456, "Sedan", t, l, "1234abcd");
            db.addReservation(r);

            Timestamp expDate = new Timestamp(28801000);
            Card card = new Card(60115564485789458L,"Discover", expDate);
            db.addCard(card);

            Vehicle v = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
                    529348, "Sedan", Vehicle.VehicleStatus.AVAILABLE, l);
            db.addVehicle(v);


            Rental add = new Rental(1, "123abc", "1234abcd", t, 1000000, card,123456);
            db.addRental(add);

            Rental result = db.getRentalMatching(add);
            assertEquals(add.rid, result.rid);
            assertEquals(add.vlicense, result.vlicense);
            assertEquals(add.dlicense, result.dlicense);
            assertEquals(add.timePeriod.endDateAndTime, result.timePeriod.endDateAndTime);
            assertEquals(add.timePeriod.startDateAndTime, result.timePeriod.startDateAndTime);
            assertEquals(add.startOdometer, result.startOdometer);
            assertEquals(add.card.CardNo, result.card.CardNo);
            assertEquals(add.card.cardName, result.card.cardName);
            assertEquals(add.card.expDate, result.card.expDate);
            assertEquals(add.confNo, result.confNo);


            //empty out sample data in VehicleType and Customer tables
            db.deleteVehicleType(vt);
            db.deleteCustomer(c);
            db.deleteCard(card);
            db.deleteLocation(l);
            db.deleteVehicle(v);
            db.deleteReservation(r);
            db.deleteRental(add);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }
//    @Test
//    void updateRentalWithNulls() {
//        try {
//            //populate VehicleType, Card, Reservation and Customer table with data so that foreign key constraints can
//            // be enforced
//            VehicleType vt = new VehicleType("Sedan", "four doors, 5 seats", 300,
//                    70, 7, 100, 20, 2, 1);
//            db.addVehicleType(vt);
//            Customer c = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
//            db.addCustomer(c);
//
//            //Create Rent object to add
//            TimePeriod t = new TimePeriod();
//            t.endDateAndTime = new Timestamp(1000000000);
//            t.startDateAndTime = new Timestamp(28801000);
//
//            Location l = new Location();
//            l.city = "Vancouver";
//            l.location = "123 Burrard Street";
//            db.addLocation(l);
//
//            Reservation r = new Reservation(123456, "Sedan", t, l, "1234abcd");
//            db.addReservation(r);
//
//            Timestamp expDate = new Timestamp(28801000);
//            Card card = new Card(60115564485789458L, "Discover", expDate);
//            db.addCard(card);
//
//            Vehicle v = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
//                    529348, "Sedan", Vehicle.VehicleStatus.AVAILABLE, l);
//            db.addVehicle(v);
//
//
//            Rental add = new Rental(1, "123abc", "123abc", t, 1000000, card, 123456);
//            db.addRental(add);
//            db.updateRental(new Rental(1, null, null, null, -1, null, -1));
//
//            Rental result = db.getRentalMatching(add);
//            assertEquals(add.rid, result.rid);
//            assertEquals(add.vlicense, result.vlicense);
//            assertEquals(add.dlicense, result.dlicense);
//            assertEquals(add.timePeriod.endDateAndTime, result.timePeriod.endDateAndTime);
//            assertEquals(add.timePeriod.startDateAndTime, result.timePeriod.startDateAndTime);
//            assertEquals(add.startOdometer, result.startOdometer);
//            assertEquals(add.card.CardNo, result.card.CardNo);
//            assertEquals(add.card.cardName, result.card.cardName);
//            assertEquals(add.card.expDate, result.card.expDate);
//            assertEquals(add.confNo, result.confNo);
//
//
//            //empty out sample data in VehicleType and Customer tables
//            db.deleteVehicleType(vt);
//            db.deleteCustomer(c);
//            db.deleteCard(card);
//            db.deleteLocation(l);
//            db.deleteVehicle(v);
//            db.deleteReservation(r);
//            db.deleteRental(add);
//        } catch (Exception e){
//            e.printStackTrace();
//            fail();
//        }
//    }
//    @Test
//    void updateRentalNoNulls() {
//        try {
//            //populate VehicleType, Card, Reservation and Customer table with data so that foreign key constraints can
//            // be enforced
//            VehicleType vt1 = new VehicleType("Sedan", "four doors, 5 seats", 300,
//                    70, 7, 100, 20, 2, 1);
//            db.addVehicleType(vt1);
//            VehicleType vt2 = new VehicleType("SUV", "four doors, 5 seats", 300,
//                    70, 7, 100, 20, 2, 1);
//            db.addVehicleType(vt2);
//
//            Customer c1 = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
//            db.addCustomer(c1);
//            Customer c2 = new Customer(6048888898L, "Bella", "6363 Agronomy Road", "1234efg");
//            db.addCustomer(c1);
//
//            //Create Rent object to add
//            TimePeriod t1 = new TimePeriod();
//            t1.endDateAndTime = new Timestamp(1000000000);
//            t1.startDateAndTime = new Timestamp(28801000);
//            TimePeriod t2 = new TimePeriod();
//            t2.endDateAndTime = new Timestamp(1000000010);
//            t2.startDateAndTime = new Timestamp(28801010);
//
//            Location l1 = new Location();
//            l1.city = "Vancouver";
//            l1.location = "UBC";
//            Location l2 = new Location();
//            l1.city = "Calgary";
//            l1.location = "Downtown";
//            db.addLocation(l1);
//            db.addLocation(l2);
//
//            Reservation r1 = new Reservation(123456, vt1.vtname, t1, l1, c1.dlicense);
//            db.addReservation(r1);
//            Reservation r2 = new Reservation(123457, vt2.vtname, t2, l2, c2.dlicense);
//            db.addReservation(r2);
//
//            Timestamp expDate1 = new Timestamp(28801000);
//            Card card1 = new Card(60115564485789458L, "Discover", expDate1);
//            db.addCard(card1);
//            Timestamp expDate2 = new Timestamp(28801010);
//            Card card2 = new Card(60115564485789459L, "Visa", expDate2);
//            db.addCard(card2);
//
//            Vehicle v1 = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
//                    529348, vt1.vtname, Vehicle.VehicleStatus.AVAILABLE, l1);
//            db.addVehicle(v1);
//            Vehicle v2 = new Vehicle(12346, "123def", "Toyota", "Corolla", 2018, "silver",
//                    529248, vt2.vtname, Vehicle.VehicleStatus.AVAILABLE, l2);
//            db.addVehicle(v1);
//
//            Rental add = new Rental(1, v1.vlicense, c1.dlicense, t1, v1.odometer, card1, r1.confNum);
//            Rental update = new Rental(1, v2.vlicense, c2.dlicense, t2, v2.odometer, card2, r2.confNum);
//            db.addRental(add);
//            db.updateRental(update);
//
//            Rental result = db.getRentalMatching(add);
//            assertEquals(add.rid, result.rid);
//            assertEquals(add.vlicense, result.vlicense);
//            assertEquals(add.dlicense, result.dlicense);
//            assertEquals(add.timePeriod.endDateAndTime, result.timePeriod.endDateAndTime);
//            assertEquals(add.timePeriod.startDateAndTime, result.timePeriod.startDateAndTime);
//            assertEquals(add.startOdometer, result.startOdometer);
//            assertEquals(add.card.CardNo, result.card.CardNo);
//            assertEquals(add.card.cardName, result.card.cardName);
//            assertEquals(add.card.expDate, result.card.expDate);
//            assertEquals(add.confNo, result.confNo);
//
//
//            //empty out sample data in VehicleType and Customer tables
//            db.deleteVehicleType(vt1);
//            db.deleteCustomer(c1);
//            db.deleteCard(card1);
//            db.deleteLocation(l1);
//            db.deleteVehicle(v1);
//            db.deleteReservation(r1);
//            db.deleteRental(add);
//        } catch (Exception e){
//            e.printStackTrace();
//            fail();
//        }
//    }

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
            fail();
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
            fail();
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
            fail();
        }
    }

    @Test
    void updateCustomerNotExists() {
        try {
            Customer NotExists = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.updateCustomer(NotExists);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
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
            fail();
        }
    }

    @Test
    void deleteCustomerNotExists() {
        try {
            Customer notExists = new Customer(6048888888L, "Billy", "6363 Agronomy Road", "1234abcd");
            db.deleteCustomer(notExists);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
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
            fail();
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
            fail();
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
            fail();
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
            fail();
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
            fail();
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
            fail();
        }
    }

    @Test
    void addVehicle() {
        try {
            //populate VehicleType table with data so that foreign key constraints can be enforced
            VehicleType vt = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(vt);

            Location l = new Location();
            l.location = "123 Burrard Street"; l.city = "Vancouver";
            db.addLocation(l);

            Vehicle add = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
                    529348, "Sedan", Vehicle.VehicleStatus.AVAILABLE, l);
            db.addVehicle(add);
            Vehicle result = db.getVehicleMatching(add);
            assertEquals(add.vtname, result.vtname);

        } catch (Exception e){
            e.printStackTrace();
            fail();
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
            db.addLocation(l1);
            db.addLocation(l2);

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
            assertEquals(update.status, result.status);


        } catch (Exception e) {
            e.printStackTrace();
            fail();
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
            db.addLocation(l1);
            db.addLocation(l2);

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
            fail();
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
            db.addLocation(l);

            Vehicle add = new Vehicle(12345, "123abc", "Toyota", "Corolla", 2018, "silver",
                    529348, "Sedan", Vehicle.VehicleStatus.AVAILABLE, l);
            db.addVehicle(add);
            db.deleteVehicle(add);
            Vehicle result = db.getVehicleMatching(add);
            assertEquals(null, result);

        } catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void getVehicleWith() {
    }

    @Test
    void addCard() {
        try {
            Timestamp t = new Timestamp(900000000);
            Card add = new Card(1234123412341234L, "Visa", t);
            db.addCard(add);
            Card result = db.getCardMatching(add);
            assertEquals(add.CardNo, result.CardNo);
            assertEquals(add.cardName, result.cardName);
            assertEquals(add.expDate, result.expDate);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void deleteCard() {
        try {
            VehicleType add = new VehicleType("Sedan", "four doors, 5 seats", 300,
                    70, 7, 100, 20, 2, 1);
            db.addVehicleType(add);
            db.getVehicleTypeMatching(add);
            db.deleteVehicleType(add);
            db.getVehicleTypeMatching(add);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


}