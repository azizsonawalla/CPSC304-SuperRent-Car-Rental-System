package model;

/**
 * A static class to store all database queries for SuperRent system
 */
public class Queries {

    public static class Create {

        public static String CREATE_TABLE_RESERVATIONS = "CREATE TABLE IF NOT EXISTS Reservations(" +
                "confNo INT NOT NULL AUTO_INCREMENT, " +
                "vtName CHAR(50) NOT NULL, " +
                "dLicense CHAR(50) NOT NULL, " +
                "fromDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "toDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "city CHAR(50) NOT NULL, " +
                "location CHAR(50) NOT NULL, " +
                "PRIMARY KEY (confNo), " +
                "FOREIGN KEY (city, location) REFERENCES Branch(city, location) on DELETE CASCADE, " +
                "FOREIGN KEY (vtName) REFERENCES VehicleType(vtName) ON DELETE CASCADE, " +
                "FOREIGN KEY (dLicense) REFERENCES Customer(dLicense) ON DELETE CASCADE)";

        public static String CREATE_TABLE_RENT = "CREATE TABLE IF NOT EXISTS Rent(" +
                "rId INT NOT NULL AUTO_INCREMENT, " +
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
                "UNIQUE (confNo))";

        public static String CREATE_TABLE_VEHICLE = "CREATE TABLE IF NOT EXISTS Vehicle(" +
                "vId INT NOT NULL, " +
                "vLicense CHAR(10), " +
                "make CHAR(50) NOT NULL, " +
                "model CHAR(50) NOT NULL, " +
                "year YEAR NOT NULL, " +
                "color CHAR(50) NOT NULL, " +
                "odometer INT NOT NULL, " +
                "vtName CHAR(50) NOT NULL, " +
                "status BOOL NOT NULL, " +
                "location CHAR(50) NOT NULL, " +
                "city CHAR(50) NOT NULL, " +
                "PRIMARY KEY (vLicense), " +
                "FOREIGN KEY (vtName) REFERENCES VehicleType(vtName) on DELETE CASCADE, " +
                "FOREIGN KEY (city, location) REFERENCES Branch(city, location) on DELETE CASCADE, " +
                "UNIQUE (vId))";

        public static String CREATE_TABLE_VEHICLE_TYPE = "CREATE TABLE IF NOT EXISTS VehicleType(" +
                "vtName CHAR(50), " +
                "features CHAR(255) NOT NULL, " +
                "wRate DOUBLE NOT NULL, " +
                "dRate DOUBLE NOT NULL, " +
                "hRate DOUBLE NOT NULL, " +
                "wInsRate DOUBLE NOT NULL, " +
                "dInsRate DOUBLE NOT NULL, " +
                "hInsRate DOUBLE NOT NULL, " +
                "kRate DOUBLE NOT NULL, " +
                "PRIMARY KEY (vtName))";

        public static String CREATE_TABLE_CUSTOMER = "CREATE TABLE IF NOT EXISTS Customer(" +
                "cellphone BIGINT NOT NULL, " +
                "name CHAR(255) NOT NULL, " +
                "address CHAR(255) NOT NULL, " +
                "dLicense CHAR(50) NOT NULL, " +
                "PRIMARY KEY (dLicense), " +
                "UNIQUE (cellphone))";

        public static String CREATE_TABLE_RETURNS = "CREATE TABLE IF NOT EXISTS Returns(" +
                "rId INT NOT NULL, " +
                "dateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "odometer INT NOT NULL, " +
                "fullTank BOOL NOT NULL," +
                "value DOUBLE NOT NULL NOT NULL," +
                "PRIMARY KEY (rId)," +
                "FOREIGN KEY (rId) REFERENCES Rent(rId) on DELETE CASCADE)";

        public static String CREATE_TABLE_CARD = "CREATE TABLE IF NOT EXISTS Card(" +
                "cardNo BIGINT NOT NULL, " +
                "cardName CHAR(50) NOT NULL, " +
                "expDate TIMESTAMP NOT NULL, " +
                "PRIMARY KEY (cardNo))";

        public static String CREATE_TABLE_BRANCH = "CREATE TABLE IF NOT EXISTS Branch(" +
                "city CHAR(50) NOT NULL, " +
                "location CHAR(50) NOT NULL, " +
                "PRIMARY KEY (city, location))";

        public static String CHECK_TABLE_EXISTS = "SHOW TABLES LIKE '%?%'";
    }

    public static class Drop {
        public static String FOREIGN_KEY_CHECKS_OFF = "SET FOREIGN_KEY_CHECKS = 0";
        public static String FOREIGN_KEY_CHECKS_ON = "SET FOREIGN_KEY_CHECKS = 1";
        public static String DROP_TABLE_RESERVATION = "DROP TABLE IF EXISTS Reservations";
        public static String DROP_TABLE_RENT = "DROP TABLE IF EXISTS Rent";
        public static String DROP_TABLE_VEHICLE = "DROP TABLE IF EXISTS Vehicle";
        public static String DROP_TABLE_VEHICLE_TYPE = "DROP TABLE IF EXISTS VehicleType";
        public static String DROP_TABLE_CUSTOMER = "DROP TABLE IF EXISTS Customer";
        public static String DROP_TABLE_RETURNS = "DROP TABLE IF EXISTS Returns;";
        public static String DROP_TABLE_CARD = "DROP TABLE IF EXISTS Card;";
        public static String DROP_TABLE_Branch = "DROP TABLE IF EXISTS Branch;";

    }

    public static class Reservation {

        //query to add reservations
        public static String ADD_RESERVATION =
                "INSERT INTO Reservations(vtName, dLicense, fromDateTime, toDateTime, city, location) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        //query to update reservations
        public static String UPDATE_RESERVATION =
                "UPDATE Reservations " +
                        "SET vtname = ?, dLicense = ?, fromDateTime = ?, toDatetime = ?, city = ?, location = ? " +
                        "WHERE confNo = ?";

        //query to delete reservations
        public static String DELETE_RESERVATION =
                "DELETE FROM Reservations " +
                        "WHERE confNo = ?";

        //get reservations matching
        public static String GET_RESERVATION =
                "SELECT * " +
                        "FROM Reservation " +
                        "WHERE confNo = ?";

        public static String GET_ACTIVE_RESERVATIONS =
                "SELECT * " +
                        "FROM Reservations " +
                        "WHERE confNo = ? AND dLicense = ? AND ? <= toDateTime " +
                        "AND confNo NOT IN (SELECT r.confNo FROM Rent r)";

        public static String GET_RECENT_RESERVATIONS =
                        "SELECT * " +
                        "FROM Reservations " +
                        "WHERE confNo = (SELECT MAX(confNo) " +
                                        "FROM Reservations)";

    }

    public static class Rent {


        public static String ADD_RENTAL =
                "INSERT INTO Rent(vLicense, dLicense, fromDateTime, toDateTime, odometer, cardNo, confNo)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        public static String UPDATE_RENTAL =
                "UPDATE Rent" +
                        "SET vLicense = ?, dLicense = ?, fromDateTime = ?, toDateTime = ?, odometer = ?" +
                        ", cardNo = ?, confNo = ?" +
                        "WHERE rId = ?";

        public static String DELETE_RENTAL =
                "DELETE FROM Rent " +
                        "WHERE rId = ?";

        public static String GET_RENTAL =
                "SELECT * " +
                        "FROM Rent " +
                        "WHERE rId = ?";

        public static String GET_ACTIVE_RENTALS =
                "SELECT * " +
                        "FROM Rent " +
                        "WHERE rId = ? AND dLicense = ? AND ? <= toDateTime " +
                        "AND rId NOT IN (SELECT rId FROM Returns)";

        public static String GET_RENTALS_TODAY =
                "SELECT * " +
                        "FROM Rent R " +
                        "WHERE ? <= R.fromDateTime AND ? >= R.fromDateTime";

        public static String GET_RECENT_RENTALS =
                "SELECT * " +
                        "FROM Rent " +
                        "WHERE rId = (SELECT MAX(rId) " +
                                      "FROM Rent)";

        }

    public static class Customer {

        public static String ADD_CUSTOMER =
                "INSERT INTO Customer(cellphone, name, address, dLicense) " +
                        "VALUES (?,?,?,?)";

        public static String UPDATE_CUSTOMER =
                "UPDATE Customer " +
                        "SET cellphone = ?, name = ?, address = ? " +
                        "WHERE dLicense = ?";

        public static String DELETE_CUSTOMER = "DELETE FROM Customer " +
                "WHERE dLicense = ?";

    }

    public static class Vehicle {

        public static String ADD_VEHICLE = "INSERT INTO Vehicle(vId, vLicense, make, model, year, color, odometer, " +
                "vtName, status, location, city) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        public static String UPDATE_VEHICLE = "UPDATE Vehicle " +
                "SET vId = ?, make = ?, model = ?, year = ?, color = ?, odometer = ?, status = ? " +
                "WHERE vLicense = ?";

        public static String DELETE_VEHICLE = "DELETE FROM Vehicle " +
                "WHERE vLicense = (?)";

        public static String GET_VEHICLES_WITH =
                "SELECT * " +
                        "FROM Vehicle " +
                        "WHERE vtName = ? AND location = ? AND city = ? AND status = ?";

        public static String GET_NUM_VEHICLES_WITH =
                "SELECT V.vtName, V.location, V.city, COUNT(*) " +
                        "FROM Vehicle V " +
                        "WHERE vtName = ? AND location = ? AND city = ? " +
                        "GROUP BY V.vtName, V.location, V.city";

    }

    public static class VehicleType {

        public static String ADD_VEHICLE_TYPE =
                "INSERT INTO VehicleType(vtName, features, wRate, dRate, hRate, wInsRate, dInsRate, hInsRate, kRate)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        public static String UPDATE_VEHICLE_TYPE =
                "UPDATE VehicleType " +
                        "SET features = ?, wRate = ?, dRate = ?, " +
                        "hRate = ?, wInsRate = ?, dInsRate = ?, hInsRate = ?, kRate = ? " +
                        "WHERE vtName = ?";

        public static String DELETE_VEHICLE_TYPE =
                "DELETE FROM VehicleType " +
                        "WHERE vtName = ?";

        public static String QUERY_ALL =
                "SELECT * FROM VehicleType";
    }

    public static class Returns {

        public static String ADD_RETURN =
                "INSERT INTO Returns(rId, dateTime, odometer, fullTank, value) " +
                        "VALUES (?, ?, ?, ?, ?)";

        public static String UPDATE_RETURN =
                "UPDATE Returns" +
                        "SET dateTime = ?, odometer = ?, fullTank = ?, value = ?" +
                        "WHERE rId = ?";

        public static String DELETE_RETURN =
                "DELETE FROM Returns " +
                        "WHERE rId = ?";

        public static String GET_RETURN =
                "SELECT * " +
                        "FROM Returns " +
                        "WHERE rId = ?";

        public static String JOIN_RENTAL =
                "SELECT * " +
                        "FROM Returns RT, Rent R " +
                        "WHERE RT.rId = R.rId AND RT.rId = ?";
    }

    public static class Card {

        public static String ADD_CARD =
                "INSERT INTO Card(cardNo, cardName, expDate) " +
                        "VALUES (?, ?, ?)";

        public static String UPDATE_CARD =
                "UPDATE Card" +
                        "SET cardName = ?, expDate = ? " +
                        "WHERE cardNo = ?";

        public static String DELETE_CARD =
                "DELETE FROM Card " +
                        "WHERE cardNo = ?";

        public static String GET_CARD =
                "SELECT * " +
                        "FROM Card " +
                        "WHERE cardNo = ?";
    }

    public static class Branch {

        public static String ADD_BRANCH =
                "INSERT INTO Branch(city, location) " +
                        "VALUES(?, ?)";

        public static String UPDATE_BRANCH =
                "UPDATE Branch" +
                        "SET city = , location = ? " +
                        "WHERE city = ? AND location = ?";

        public static String DELETE_BRANCH =
                "DELETE FROM Branch " +
                        "WHERE city = ? AND location = ?";

        public static String GET_BRANCH =
                        "SELECT * FROM Branch " +
                        "WHERE city = ? AND location = ?";

        public static String GET_ALL_BRANCHES =
                "SELECT * " +
                        "FROM Branch";
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