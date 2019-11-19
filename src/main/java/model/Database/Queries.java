package model.Database;

/**
 * A static class to store all database queries for SuperRent system
 */
public class Queries {

    public static class Create {

        public static String CREATE_TABLE_RESERVATIONS = ""; // TODO: Replace empty string with query

        public static String CREATE_TABLE_RENTALS = ""; // TODO: Replace empty string with query

        public static String CREATE_TABLE_VEHICLES = ""; // TODO: Replace empty string with query

        public static String CREATE_TABLE_VEHICLE_TYPES = ""; // TODO: Replace empty string with query

        public static String CREATE_TABLE_CUSTOMERS = ""; // TODO: Replace empty string with query

        public static String CREATE_TABLE_RETURNS = ""; // TODO: Replace empty string with query

        public static String CREATE_TABLE_CREDIT_CARDS = ""; // TODO: Replace empty string with query

        public static String CHECK_TABLE_EXISTS = ""; // TODO: Replace empty string with query
    }

    public static class Reservation {

        //query to add reservations
        public static String ADD_RESERVATION =
                "INSERT INTO Reservation(confNum, vtname, dlicense, fromDateTime, toDatetime)" +
                        "VALUES (?, ?, ?, ?, ?)";

        //query to update reservations
        public static String UPDATE_RESERVATION =
                "UPDATE Reservation" +
                        "SET vtname = ?, dlicense = ?, fromDateTime = ?, toDatetime = ?" +
                        "WHERE confNum = ?";

        //query to delete reservations
        public static String DELETE_RESERVATION =
                "DELETE FROM Reservation" +
                        "WHERE confNum = ?";
    }

    public static class Rental {

        // TODO: Add all queries to create, update and delete rentals here

    }

    public static class Customer {

        // TODO: Add all queries to create, update and delete customers here

    }

    public static class Vehicle {

        // TODO: Add all queries to create, update and delete vehicles here

    }

    public static class VehicleType {

        // TODO: Add all queries to create, update and delete vehicle types here

    }
}
