package model.Database;

import model.Entities.*;
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

    /**
     * Add the given reservation object to the reservation table in the database
     * @param r reservation object to add
     * @throws Exception if there is an error adding the reservation, for example if the values don't meet constraints
     */
    public void addReservation(Reservation r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /**
     * Update the values of the reservation entry in the reservation table that has the same primary key as the given
     * reservation object. New values of reservation entry are values in r.
     * @param r updated values for reservation entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateReservation(Reservation r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param r object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteReservation(Reservation r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /* Rental */

    /**
     * Add the given rental object to the rental table in the database
     * @param r rental object to add
     * @throws Exception if there is an error adding the rental, for example if the values don't meet constraints
     */
    public void addRental(Rental r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /**
     * Update the values of the Rental entry in the Rental table that has the same primary key as the given
     * Rental object. New values of Rental entry are values in r.
     * @param r updated values for Rental entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateRental(Rental r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param r object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteRental(Rental r) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /* Customer */

    /**
     * Add the given Customer object to the Customer table in the database
     * @param c Customer object to add
     * @throws Exception if there is an error adding the Customer, for example if the values don't meet constraints
     */
    public void addCustomer(Customer c) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /**
     * Update the values of the Customer entry in the Customer table that has the same primary key as the given
     * Customer object. New values of Customer entry are values in c.
     * @param c updated values for Customer entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateCustomer(Customer c) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param c object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteCustomer(Customer c) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /* Vehicle Type */

    /**
     * Add the given VehicleType object to the VehicleType table in the database
     * @param vt VehicleType object to add
     * @throws Exception if there is an error adding the VehicleType, for example if the values don't meet constraints
     */
    public void addVehicleType(VehicleType vt) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /**
     * Update the values of the VehicleType entry in the VehicleType table that has the same primary key as the given
     * VehicleType object. New values of VehicleType entry are values in c.
     * @param vt updated values for VehicleType entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateVehicleType(VehicleType vt) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param vt object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteVehicleType(VehicleType vt) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /* Vehicle */

    /**
     * Add the given Vehicle object to the Vehicle table in the database
     * @param v Vehicle object to add
     * @throws Exception if there is an error adding the Vehicle, for example if the values don't meet constraints
     */
    public void addVehicle(Vehicle v) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /**
     * Update the values of the Vehicle entry in the Vehicle table that has the same primary key as the given
     * Vehicle object. New values of Vehicle entry are values in c.
     * @param v updated values for Vehicle entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateVehicle(Vehicle v) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param v object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteVehicle(Vehicle v) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }
}
