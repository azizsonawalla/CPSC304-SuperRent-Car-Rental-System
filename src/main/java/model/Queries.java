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

        //Add a new reservation tuple to the Reservations table. confNo autoincrements.
        /*
        Used in:
            1) Make a reservation
         */
        public static String ADD_RESERVATION =
                "INSERT INTO Reservations(vtName, dLicense, fromDateTime, toDateTime, city, location) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        //Update existing tuple in reservations table given a confNo.
        public static String UPDATE_RESERVATION =
                "UPDATE Reservations " +
                        "SET vtname = ?, dLicense = ?, fromDateTime = ?, toDatetime = ?, city = ?, location = ? " +
                        "WHERE confNo = ?";

        //Delete existing tuple from reservations table given a confNo.
        public static String DELETE_RESERVATION =
                "DELETE FROM Reservations " +
                        "WHERE confNo = ?";

        //Query reservation tuple from Reservation with a given confNo
        public static String GET_RESERVATION =
                "SELECT * " +
                        "FROM Reservation " +
                        "WHERE confNo = ?";

        //Query active reservations (any reservation that has not been started as long as the end time of the reservation
        //is after the current time)
        public static String GET_ACTIVE_RESERVATIONS =
                "SELECT * " +
                        "FROM Reservations " +
                        "WHERE confNo = ? AND dLicense = ? AND ? <= toDateTime " +
                        "AND confNo NOT IN (SELECT r.confNo FROM Rent r)";

        //Get reservations that overlap with the given time period at a given location for a given vehicle type
        /*
        Used in:
            1) View the number of available vehicles for a specific car type, location, and time interval
            2) Generate "Daily Rentals" report
            3) Generate "Daily Rentals for Branch" report
            4) Generate "Daily Returns" report
            5) Generate "Daily Returns for Branch" report
         */
        public static String GET_RESERVATIONS_WITH =
                "SELECT * " +
                        "FROM Reservations " +
                        "WHERE (? <= R.toDateTime) AND (? >= R.fromDateTime) " +
                        "AND R.vtName = ? AND R.location = ? AND R.city = ?";

    }

    public static class Rent {

        //Add a new rent tuple to the Rent table. rId autoincrements.
        /*
        Used In:
            1) Renting a Vehicle
         */
        public static String ADD_RENTAL =
                "INSERT INTO Rent(vLicense, dLicense, fromDateTime, toDateTime, odometer, cardNo, confNo)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        //Update existing tuple in Rent table given a rId.
        public static String UPDATE_RENTAL =
                "UPDATE Rent" +
                        "SET vLicense = ?, dLicense = ?, fromDateTime = ?, toDateTime = ?, odometer = ?" +
                        ", cardNo = ?, confNo = ?" +
                        "WHERE rId = ?";

        //Delete existing tuple in Rent table given a rId.
        public static String DELETE_RENTAL =
                "DELETE FROM Rent " +
                        "WHERE rId = ?";


        //Query Rent tuple with a given rId
        public static String GET_RENTAL =
                "SELECT * " +
                        "FROM Rent " +
                        "WHERE rId = ?";

        //Query active rentals (any rent that has not been completed and returned as long as the end time is after the
        //current time)
        public static String GET_ACTIVE_RENTALS =
                "SELECT * " +
                        "FROM Rent " +
                        "WHERE rId = ? AND dLicense = ? AND ? <= toDateTime " +
                        "AND rId NOT IN (SELECT rId FROM Returns)";

        //Query rentals that were made in a given time period
        /*
        Used In:
            1) Generate "Daily Rentals" report
            2) Generate "Daily Rentals for Branch" report
         */
        public static String GET_RENTALS_TODAY =
                "SELECT * " +
                        "FROM Rent R " +
                        "WHERE ? <= R.fromDateTime AND ? >= R.fromDateTime";

        //Get rentals that overlap with the given time period at a given location for a given vehicle type
        /*
        Used in:
            1) Generate "Daily Returns" report
            2) Generate "Daily Returns for Branch" report
         */
        public static String GET_RENTALS_WITH =
                "SELECT * " +
                        "FROM Rent R, Reservations V " +
                        "WHERE R.confNo = V.confNo AND (? <= R.toDateTime) AND (? >= R.fromDateTime) " +
                        "AND V.vtName = ? AND V.location = ? AND V.city = ?";

        }

    public static class Customer {

        //Add a new customer to the Customer table.
        public static String ADD_CUSTOMER =
                "INSERT INTO Customer(cellphone, name, address, dLicense) " +
                        "VALUES (?,?,?,?)";

        //Update an existing customer tuple in the Customer table.
        public static String UPDATE_CUSTOMER =
                "UPDATE Customer " +
                        "SET cellphone = ?, name = ?, address = ? " +
                        "WHERE dLicense = ?";

        //Delete an existing customer tuple from the Customer table.
        public static String DELETE_CUSTOMER = "DELETE FROM Customer " +
                "WHERE dLicense = ?";

    }

    public static class Vehicle {

        //Add a new vehicle to the Vehicle table.
        public static String ADD_VEHICLE = "INSERT INTO Vehicle(vId, vLicense, make, model, year, color, odometer, " +
                "vtName, status, location, city) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        //Update an existing vehicle tuple in the Vehicle table.
        /*
        Used in:
            1) Returning a Vehicle
         */
        public static String UPDATE_VEHICLE = "UPDATE Vehicle " +
                "SET vId = ?, make = ?, model = ?, year = ?, color = ?, odometer = ?, status = ? " +
                "WHERE vLicense = ?";

        //Delete an existing vehicle tuple from the Vehicle table.
        public static String DELETE_VEHICLE = "DELETE FROM Vehicle " +
                "WHERE vLicense = (?)";

        //Get vehicles with a given type at a given location and of a given status
        public static String GET_VEHICLES_WITH =
                "SELECT * " +
                        "FROM Vehicle " +
                        "WHERE vtName = ? AND location = ? AND city = ? AND status = ?";

        //Get the total number of vehicles at a given location that are a given vehicle type
        /*
        Used in:
            1) View the number of available vehicles for a specific car type, location, and time interval
         */
        public static String GET_NUM_VEHICLES_WITH =
                "SELECT V.vtName, V.location, V.city, COUNT(*) " +
                        "FROM Vehicle V " +
                        "WHERE vtName = ? AND location = ? AND city = ? " +
                        "GROUP BY V.vtName, V.location, V.city";

        //Query vehicle tuple with a given vLicense
        /*
        Used in:
            1) Returning a Vehicle
         */
        public static String GET_VEHICLE =
                "SELECT * " +
                        "FROM Vehicle " +
                        "WHERE vLicense = ?";


    }

    public static class VehicleType {

        //Add a new vehicle type to the VehicleType table.
        public static String ADD_VEHICLE_TYPE =
                "INSERT INTO VehicleType(vtName, features, wRate, dRate, hRate, wInsRate, dInsRate, hInsRate, kRate)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        //Update an existing vehicle type tuple in the VehicleType table.
        public static String UPDATE_VEHICLE_TYPE =
                "UPDATE VehicleType " +
                        "SET features = ?, wRate = ?, dRate = ?, " +
                        "hRate = ?, wInsRate = ?, dInsRate = ?, hInsRate = ?, kRate = ? " +
                        "WHERE vtName = ?";

        //Delete an existing vehicle type tuple from the VehicleType table.
        public static String DELETE_VEHICLE_TYPE =
                "DELETE FROM VehicleType " +
                        "WHERE vtName = ?";

        //Query all vehicle type tuples
        public static String QUERY_ALL =
                "SELECT * FROM VehicleType";

    }

    public static class Returns {

        //Add a new return to the Returns table.
        /*
        Used in:
            1) Returning a Vehicle
         */
        public static String ADD_RETURN =
                "INSERT INTO Returns(rId, dateTime, odometer, fullTank, value) " +
                        "VALUES (?, ?, ?, ?, ?)";

        //Update an existing return tuple in the Returns table.
        public static String UPDATE_RETURN =
                "UPDATE Returns" +
                        "SET dateTime = ?, odometer = ?, fullTank = ?, value = ?" +
                        "WHERE rId = ?";

        //Delete an existing return tuple from the Returns table.
        public static String DELETE_RETURN =
                "DELETE FROM Returns " +
                        "WHERE rId = ?";

        //Query all return tuples with a given rId
        public static String GET_RETURN =
                "SELECT * " +
                        "FROM Returns " +
                        "WHERE rId = ?";

        //Query all tuples joining Returns with Rent by rId for a given rId
        /*
        Used in:
            1) Returning a Vehicle
         */
        public static String JOIN_RENTAL =
                "SELECT * " +
                        "FROM Returns RT, Rent R " +
                        "WHERE RT.rId = R.rId AND RT.rId = ?";

        //Query all tuples natural joining returns, rent, and reservations within a given time period, of a given vehicle
        //type, and at a given branch
        /*
        Used in:
            1) Generate "Daily Returns" report
            2) Generate "Daily Returns for Branch" report
         */
        public static String GET_RETURNS_WITH =
                "SELECT * " +
                        "FROM Returns R, Rent N, Reservations V " +
                        "WHERE R.rId = N.rId AND N.confNo = V.confNo AND (R.dateTime >= ?) AND (R.dateTime <= ?) AND " +
                        "AND V.vtName = ? AND AND V.location = ? AND V.city = ?";
    }

    public static class Card {

        //Add a new card to the Card table.
        public static String ADD_CARD =
                "INSERT INTO Card(cardNo, cardName, expDate) " +
                        "VALUES (?, ?, ?)";

        //Update an existing card tuple in the Card table.
        public static String UPDATE_CARD =
                "UPDATE Card" +
                        "SET cardName = ?, expDate = ? " +
                        "WHERE cardNo = ?";

        //Delete an existing card tuple from the Card table.
        public static String DELETE_CARD =
                "DELETE FROM Card " +
                        "WHERE cardNo = ?";

        //Query a card with a given cardNo from the Card table
        public static String GET_CARD =
                "SELECT * " +
                        "FROM Card " +
                        "WHERE cardNo = ?";
    }

    public static class Branch {

        //Add a new branch to the Branch table.
        public static String ADD_BRANCH =
                "INSERT INTO Branch(city, location) " +
                        "VALUES(?, ?)";

        //Update an existing branch tuple in the Branch table.
        public static String UPDATE_BRANCH =
                "UPDATE Branch" +
                        "SET city = , location = ? " +
                        "WHERE city = ? AND location = ?";

        //Delete an existing branch tuple from the Branch table.
        public static String DELETE_BRANCH =
                "DELETE FROM Branch " +
                        "WHERE city = ? AND location = ?";

        //Query a branch from the Branch table with a given city and location
        public static String GET_BRANCH =
                        "SELECT * FROM Branch " +
                        "WHERE city = ? AND location = ?";

        //Query all branch tuples from the Branch table
        public static String GET_ALL_BRANCHES =
                "SELECT * " +
                        "FROM Branch";
    }
}