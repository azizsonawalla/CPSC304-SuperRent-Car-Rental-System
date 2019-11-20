package model;

/**
 * A static class to store all database queries for SuperRent system
 */
public class Queries {

    public static class Create {

        public static String CREATE_TABLE_RESERVATIONS = "CREATE TABLE IF NOT EXISTS Reservations(" +
                "confNo INT, " +
                "vtName CHAR(50) NOT NULL, " +
                "dLicense CHAR(50) NOT NULL, " +
                "fromDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "toDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "city CHAR(50) NOT NULL, " +
                "location CHAR(50) NOT NULL, " +
                "PRIMARY KEY (confNo), " +
                "FOREIGN KEY (vtName) REFERENCES VehicleType(vtName) ON DELETE CASCADE, " +
                "FOREIGN KEY (city, location) REFERENCES Location(city, location) ON DELETE CASCADE, " +
                "FOREIGN KEY (dLicense) REFERENCES Customer(dLicense) ON DELETE CASCADE);";
                
        public static String CREATE_TABLE_RENT = "CREATE TABLE IF NOT EXISTS Rent(" +
                "rId INT, " +
                "vLicense CHAR(10) NOT NULL, " +
                "dLicense CHAR(50) NOT NULL, " +
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
                "cellphone BIGINT NOT NULL, " +
                "name CHAR(255) NOT NULL, " +
                "address CHAR(255), " +
                "dLicense CHAR(50), " +
                "PRIMARY KEY (dLicense), " +
                "UNIQUE (cellphone));";

        public static String CREATE_TABLE_RETURNS = "CREATE TABLE IF NOT EXISTS Returns(" +
                "rId INT, " +
                "vLicense CHAR(10) NOT NULL, " +
                "dLicense CHAR(50) NOT NULL, " +
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

        public static String CREATE_TABLE_LOCATION = "CREATE TABLE IF NOT EXISTS Location(" +
                "city CHAR(50), " +
                "location CHAR(50), " +
                "PRIMARY KEY (city, location));";

        public static String CHECK_TABLE_EXISTS = "SHOW TABLES LIKE '%?%';";
    }

    public static class Drop {
        public static String FOREIGN_KEY_CHECKS_OFF = "SET FOREIGN_KEY_CHECKS = 0";
        public static String FOREIGN_KEY_CHECKS_ON = "SET FOREIGN_KEY_CHECKS = 1";
        public static String DROP_TABLE_RESERVATION = "DROP TABLE Reservations";
        public static String DROP_TABLE_RENT = "DROP TABLE Rent";
        public static String DROP_TABLE_VEHICLE = "DROP TABLE Vehicle";
        public static String DROP_TABLE_VEHICLE_TYPE = "DROP TABLE VehicleType";
        public static String DROP_TABLE_CUSTOMER = "DROP TABLE Customer";
        public static String DROP_TABLE_RETURNS = "DROP TABLE Returns";
        public static String DROP_TABLE_CARD = "DROP TABLE Card";
    }

    public static class Reservation {

        //query to add reservations
        public static String ADD_RESERVATION =
                "INSERT INTO Reservations(confNo, vtname, dLicense, fromDateTime, toDateTime, city, location) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        //query to update reservations
        public static String UPDATE_RESERVATION =
                "UPDATE Reservations " +
                        "SET vtname = ?, dlicense = ?, fromDateTime = ?, toDatetime = ?, city = ?, location = ?" +
                        "WHERE confNum = ?";

        //query to delete reservations
        public static String DELETE_RESERVATION =
                "DELETE FROM Reservations " +
                        "WHERE confNum = ?";

    }

    public static class Rent {

        // TODO: Add all queries to create, update and delete rentals here

        String insertQueryStatement = "INSERT INTO Rent " +
                "VALUES (?,?,?,?,?,?,?,?)";
        String deleteQueryStatement = "DELETE FROM Rent " +
                "WHERE rId = (?)";

    }

    public static class Customer {

        public static String ADD_CUSTOMER = "INSERT INTO Customer(cellphone, name, address, dLicense) " +
                "VALUES (?,?,?,?)";

        public static String UPDATE_CUSTOMER =
                "UPDATE Customer " +
                        "SET cellphone = ?, name = ?, address = ? " +
                        "WHERE dLicense = ?";

        public static String DELETE_CUSTOMER = "DELETE FROM Customer " +
                "WHERE dLicense = ?";

    }

    public static class Vehicle {

        String insertQueryStatement = "INSERT INTO Vehicle " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        String deleteQueryStatement = "DELETE FROM Vehicle " +
                "WHERE vLicense = (?)";

    }

    public static class VehicleType {

        public static String ADD_VEHICLE_TYPE = "INSERT INTO VehicleType(vtName, features, wRate, dRate, hRate, wInsRate, dInsRate, hInsRate, kRate)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        public static String UPDATE_VEHICLE_TYPE = "UPDATE VehicleType " +
                "SET features = ?, wRate = ?, dRate = ?, " +
                "hRate = ?, wInsRate = ?, dInsRate = ?, hInsRate = ?, kRate = ? " +
                "WHERE vtName = ?";

        public static String DELETE_VEHICLE_TYPE = "DELETE FROM VehicleType " +
                "WHERE vtName = ?";
    }

    public static class Returns {

        // TODO: Add all queries to create, update and delete rentals here
        String insertQueryStatement = "INSERT INTO Returns " +
                "VALUES (?,?,?,?,?)";
        String deleteQueryStatement = "DELETE FROM Returns " +
                "WHERE rId = (?)";
    }

    public static class Card {

        // TODO: Add all queries to create, update and delete rentals here
        private static String insertQueryStatement = "INSERT INTO Card " +
                "VALUES (?,?,?)";
        private static String deleteQueryStatement = "DELETE FROM Card " +
                "WHERE cardNo = (?)";

    }

    public static class CustomerTransactions {
        //TODO: Find the number of vehicles for any combination of: car type, location, and time interval

        //TODO: Decide on/generate confirmation number

    }

    public static class ClerkTransactions {
        //TODO: Generate daily rentals report

        //TODO: Generate daily rentals report for branch

        //TODO: Generate daily returns report

        //TODO: Generate daily returns report for branch
    }
}

