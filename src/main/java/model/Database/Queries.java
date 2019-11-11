package model.Database;

/**
 * A static class to store all database queries for SuperRent system
 */
public class Queries {

    public static class Create {

        public static String CREATE_TABLE_RESERVATIONS = "CREATE TABLE IF NOT EXISTS Reservations(confNo INT, " +
                "vtName CHAR(15) NOT NULL, dLicense INT NOT NULL, fromDateTime TIMESTAMP NOT NULL DEFAULT " +
                "CURRENT_TIMESTAMP, toDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (confNo), " +
                "FOREIGN KEY (vtName) REFERENCES VehicleType(vtName) ON DELETE CASCADE, FOREIGN KEY (dLicense) " +
                "REFERENCES Customer(dLicense) ON DELETE CASCADE);";

        public static String CREATE_TABLE_RENTALS = "CREATE TABLE IF NOT EXISTS Rent(rId INT, vLicense char NOT NULL," +
                " dLicense INT NOT NULL, fromDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, toDateTime " +
                "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, odometer INT NOT NULL, cardNo INT NOT NULL, confNo " +
                "INT NOT NULL, PRIMARY KEY (rId), FOREIGN KEY (vLicense) REFERENCES Vehicle(vLicense), FOREIGN KEY " +
                "(cardNo) REFERENCES Card(cardNo), FOREIGN KEY (dLicense) REFERENCES Customer(dLicense), FOREIGN KEY " +
                "(confNo) REFERENCES Reservations(confNo), UNIQUE (confNo));";

        public static String CREATE_TABLE_VEHICLES = "CREATE TABLE IF NOT EXISTS Vehicle (vId INT NOT NULL, " +
                "vLicense CHAR(7), make CHAR(20), model CHAR(20), year YEAR, color CHAR(20), odometer INT, " +
                "vtName CHAR(15), status CHAR(20), location CHAR(20), city CHAR(20), PRIMARY KEY (vLicense), " +
                "FOREIGN KEY (vtName) REFERENCES VehicleType(vtName) on DELETE CASCADE, UNIQUE (vId));";

        public static String CREATE_TABLE_VEHICLE_TYPES = "CREATE TABLE IF NOT EXISTS VehicleType (vtName CHAR(15), " +
                "features CHAR(255), wRate INT NOT NULL, dRate INT NOT NULL, hRate INT NOT NULL, " +
                "wInsRate INT NOT NULL, dInsRate INT NOT NULL, hInsRate INT NOT NULL, kRate INT NOT NULL, " +
                "PRIMARY KEY (vtName));";

        public static String CREATE_TABLE_CUSTOMERS = "CREATE TABLE IF NOT EXISTS Customer (cellphone INT NOT NULL, " +
                "name CHAR(255) NOT NULL, address CHAR(255), dLicense INT, PRIMARY KEY (dLicense), UNIQUE (cellphone));";

        public static String CREATE_TABLE_RETURNS = "CREATE TABLE IF NOT EXISTS Returns( rId INT, dateTime TIMESTAMP " +
                "NOT NULL DEFAULT CURRENT_TIMESTAMP, odometer INT, fulltank BOOL DEFAULT “0”, value INT," +
                " PRIMARY KEY (rId));";

        public static String CREATE_TABLE_CREDIT_CARDS = "CREATE TABLE IF NOT EXISTS Card(cardNo INT, " +
                "cardName CHAR(20), ExpDate DATE, PRIMARY KEY (cardNo));";

        public static String CHECK_TABLE_EXISTS = "SHOW TABLES LIKE '%?%';";
    }

    public static class Reservation {

        // TODO: Add all queries to create, update and delete reservations here

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
