package model.Database;

import model.Entities.Customer;
import model.Entities.Rental;
import model.Entities.Reservation;
import model.Util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Interface for the SuperRent database
 */
public class Database {

    private String HOST = "jdbc:mysql://35.247.80.246/superrent";
    private String USERNAME = "root";
    private String PASSWORD = "bobobo";

    private Connection conn;

    /**
     * Constructor for Database class. Establishes a connection to the database and initializes the connection field.
     * @throws Exception if there was an error connecting to the database
     */
    public Database() throws Exception {
        Log.log("Establishing connection to database...");
        try {
            this.conn = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
            if (this.conn == null) throw new Exception("Connection object is null");
        } catch (Exception e) {
            throw new Exception("Error getting connection to database", e);
        }
        Log.log("Successfully connected to database!");
    }

    /**
     * Checks all the tables required for the system and creates them if they don't exist
     * @throws Exception if there was an error creating any of the tables
     */
    public void createTables() throws Exception {
        // TODO: implement this
        // NOTE: Use the queries set in the static Queries class, for example:
        String query = Queries.Create.CREATE_TABLE_RESERVATIONS;
        throw new Exception("Method createTables() is not implemented");
    }


    /* Reservations */

    public void addReservation(Reservation r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    public void updateReservation(Reservation r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    public void deleteReservation(Reservation r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /* Rental */

    public void addRental(Rental r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    public void updateRental(Rental r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    public void deleteRental(Rental r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /* Customer */

    public void addCustomer(Customer r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    public void updateCustomer(Customer r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    public void deleteCustomer(Customer r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }
}
