package model.Database;

/**
 * A static class to store all database queries for SuperRent system
 */
public class Queries {

    public static class Create {

        public static String CREATE_TABLE_RESERVATIONS = "CREATE TABLE IF NOT EXISTS Reservations(" +
                "confNo INT, " +
                "vtName CHAR(50) NOT NULL, " +
                "dLicense INT NOT NULL, " +
                "fromDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "toDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (confNo), " +
                "FOREIGN KEY (vtName) REFERENCES VehicleType(vtName) ON DELETE CASCADE, " +
                "FOREIGN KEY (dLicense) REFERENCES Customer(dLicense) ON DELETE CASCADE);";
                
        public static String CREATE_TABLE_RENT = "CREATE TABLE IF NOT EXISTS Rent(" +
                "rId INT, " +
                "vLicense CHAR(10) NOT NULL, " +
                "dLicense INT NOT NULL, " +
                "fromDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "toDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "odometer INT NOT NULL, " +
                "cardNo INT NOT NULL, " +
                "confNo INT NOT NULL, " +
                "PRIMARY KEY (rId), " +
                "FOREIGN KEY (vLicense) REFERENCES Vehicle(vLicense), " +
                "FOREIGN KEY (cardNo) REFERENCES Card(cardNo), " +
                "FOREIGN KEY (dLicense) REFERENCES Customer(dLicense), " +
                "FOREIGN KEY (confNo) REFERENCES Reservations(confNo), " +
                "UNIQUE (confNo));";

        public static String CREATE_TABLE_VEHICLE = "CREATE TABLE IF NOT EXISTS Vehicle(" +
                "vId INT NOT NULL, " +
                "vLicense CHAR(10), " +
                "make CHAR(50), " +
                "model CHAR(50), " +
                "year YEAR, " +
                "color CHAR(50), " +
                "odometer INT, " +
                "vtName CHAR(50), " +
                "status CHAR(50), " +
                "location CHAR(50), " +
                "city CHAR(50), " +
                "PRIMARY KEY (vLicense), " +
                "FOREIGN KEY (vtName) REFERENCES VehicleType(vtName) on DELETE CASCADE, " +
                "UNIQUE (vId));";

        public static String CREATE_TABLE_VEHICLE_TYPE = "CREATE TABLE IF NOT EXISTS VehicleType(" +
                "vtName CHAR(50), " +
                "features CHAR(255), " +
                "wRate INT NOT NULL, " +
                "dRate INT NOT NULL, " +
                "hRate INT NOT NULL, " +
                "wInsRate INT NOT NULL, " +
                "dInsRate INT NOT NULL, " +
                "hInsRate INT NOT NULL, " +
                "kRate INT NOT NULL, " +
                "PRIMARY KEY (vtName));";

        public static String CREATE_TABLE_CUSTOMER = "CREATE TABLE IF NOT EXISTS Customer(" +
                "cellphone INT NOT NULL, " +
                "name CHAR(255) NOT NULL, " +
                "address CHAR(255), " +
                "dLicense INT, " +
                "PRIMARY KEY (dLicense), " +
                "UNIQUE (cellphone));";

        public static String CREATE_TABLE_RETURNS = "CREATE TABLE IF NOT EXISTS Rent(" +
                "rId INT, " +
                "vLicense CHAR(10) NOT NULL, " +
                "dLicense INT NOT NULL, " +
                "fromDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "toDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "odometer INT NOT NULL, " +
                "cardNo BIGINT NOT NULL, " +
                "confNo INT NOT NULL, " +
                "PRIMARY KEY (rId), " +
                "FOREIGN KEY (vLicense) REFERENCES Vehicle(vLicense), " +
                "FOREIGN KEY (cardNo) REFERENCES Card(cardNo), " +
                "FOREIGN KEY (dLicense) REFERENCES Customer(dLicense), " +
                "FOREIGN KEY (confNo) REFERENCES Reservations(confNo), " +
                "UNIQUE (confNo));";

        public static String CREATE_TABLE_CARD = "CREATE TABLE IF NOT EXISTS Card(" +
                "cardNo BIGINT, " +
                "cardName CHAR(50), " +
                "ExpDate INT, " +
                "PRIMARY KEY (cardNo));";

        public static String CHECK_TABLE_EXISTS = "SHOW TABLES LIKE '%?%';";
    }

    public static class Reservation {

        // TODO: Add all queries to create, update and delete reservations here

    }

    public static class Rent {

        // TODO: Add all queries to create, update and delete rentals here

        //query to add rent
        public static String ADD_RENT =
                "INSERT INTO Reservation(rId, vLicense, dLicense, fromDateTime, toDateTime, odometer, cardNo, confNo)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        //query to update reservations
        public static String UPDATE_RENT =
                "UPDATE RENT" +
                        "SET vLicense = ?, dLicense = ?, fromDateTime = ?, toDateTime = ?, odometer = ?" +
                        ", cardNo = ?, confNo = ?" +
                        "WHERE rId = ?";

        //query to delete reservations
        public static String DELETE_RESERVATION =
                "DELETE FROM RENT" +
                        "WHERE rId = ?";
    }


    public static class Customer {

        // TODO: Add all queries to create, update and delete rentals here

    }

    public static class Vehicle {

        // TODO: Add all queries to create, update and delete rentals here

    }

    public static class VehicleType {

        // TODO: Add all queries to create, update and delete vehicle types here

    }

    public static class Returns {

        // TODO: Add all queries to create, update and delete rentals here

    }

    public static class Card {

        // TODO: Add all queries to create, update and delete rentals here


    }

}

