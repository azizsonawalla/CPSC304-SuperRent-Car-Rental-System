package model;

import model.Entities.*;
import model.Util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
        //Creates all the tables
        conn.prepareStatement(Queries.Create.CREATE_TABLE_CUSTOMER).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_VEHICLE_TYPE).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_LOCATION).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_RESERVATIONS).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_VEHICLE).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_CARD).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_RETURNS).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_RENT).executeUpdate();
    }

    /**
     * Drops all the tables that have been created
     * @throws Exception
     */
    public void dropTables() throws Exception {
        conn.prepareStatement(Queries.Drop.FOREIGN_KEY_CHECKS_OFF).execute();
        conn.prepareStatement(Queries.Drop.DROP_TABLE_RESERVATION).executeUpdate();
        conn.prepareStatement(Queries.Drop.DROP_TABLE_RENT).executeUpdate();
        conn.prepareStatement(Queries.Drop.DROP_TABLE_VEHICLE).executeUpdate();
        conn.prepareStatement(Queries.Drop.DROP_TABLE_VEHICLE_TYPE).executeUpdate();
        conn.prepareStatement(Queries.Drop.DROP_TABLE_CUSTOMER).executeUpdate();
        conn.prepareStatement(Queries.Drop.DROP_TABLE_RETURNS).executeUpdate();
        conn.prepareStatement(Queries.Drop.DROP_TABLE_CARD).executeUpdate();
        conn.prepareStatement(Queries.Drop.FOREIGN_KEY_CHECKS_ON).execute();
    }

    /* Reservations */

    /**
     * Add the given reservation object to the reservation table in the database
     * @param r reservation object to add
     * @throws Exception if there is an error adding the reservation, for example if the values don't meet constraints
     */
    public void addReservation(Reservation r) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Reservation.ADD_RESERVATION);

        //Set values for parameters in ps
        ps.setInt(1, r.confNum);
        ps.setString(2, r.vtName);
        ps.setString(3, r.dlicense);
        ps.setTimestamp(4, r.timePeriod.fromDateTime);
        ps.setTimestamp(5, r.timePeriod.toDateTime);
        ps.setString(6, r.location.city);
        ps.setString(7, r.location.location);

        //execute the update
        ps.executeUpdate();

        //commit changes and close prepared statement
        conn.commit();
        ps.close();

        Log.log("Reservation with confirmation number " + Integer.toString(r.confNum) + " successfully added");
    }

    /**
     * Update the values of the reservation entry in the reservation table that has the same primary key as the given
     * reservation object. New values of reservation entry are values in r.
     * @param r updated values for reservation entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateReservation(Reservation r) throws Exception {
        //Get Reservation tuple with confirmation number r.confNum
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Reservations WHERE confNum = ?");
        ps.setInt(1, r.confNum);
        ResultSet rs = ps.executeQuery();

        //Set values for parameters in psUpdate. Update if corresponding value in Reservation r is null, otherwise, keep unchanged
        ps = conn.prepareStatement(Queries.Reservation.UPDATE_RESERVATION);
        ps.setString(1, r.vtName != null? r.vtName: rs.getString("vtname"));
        ps.setString(2, r.dlicense != null? r.dlicense: rs.getString("dlicense"));
        ps.setTimestamp(3, r.timePeriod.fromDateTime != null? r.timePeriod.fromDateTime: rs.getTimestamp("fromDateTime"));
        ps.setTimestamp(4, r.timePeriod.toDateTime != null? r.timePeriod.toDateTime: rs.getTimestamp("toDateTime"));
        ps.setInt(5, r.confNum);

        //execute the update
        ps.executeUpdate();

        //commit changes and close prepared statement
        conn.commit();
        ps.close();

        Log.log("Reservation with confirmation number " + Integer.toString(r.confNum) + " successfully updated");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param r object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteReservation(Reservation r) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Reservation.DELETE_RESERVATION);
        //Set confirmation number parameter for Reservation tuple to be deleted
        ps.setInt(1, r.confNum);

        //execute the update
        ps.executeUpdate();

        //commit changes and close prepared statement
        conn.commit();
        ps.close();

        Log.log("Reservation with confirmation number " + Integer.toString(r.confNum) + " successfully deleted");
    }

    /**
     * Returns all reservations with the given VehicleType, Location and that *start within* the given TimePeriod
     * Params can be null - if param is null, results will not be filtered by that attribute. If all params are null,
     * then all existing reservations will be returned.
     * @throws Exception if there is any error getting results
     */
    public List<Reservation> getReservationsWith(TimePeriod t, VehicleType vt, Location l) throws Exception {
        //Empty string to build query
        String query;
        //marker to indicate if a condition has been added to the WHERE clause (and if AND needs to be used)
        boolean marker = false;

        if (t == null && vt == null && l == null){
            //If no filters are provided, return all the results
            query = "SELECT * FROM Reservations";
        } else {
            query = "SELECT * FROM Reservations R WHERE ";
            if (t != null) {
                //Given start time (from t) is after R.fromDateTime AND is before R.toDateTime
                //Given end time (from t) is before R.toDateTime AND is after R.fromDateTime
                query += "(R.fromDateTime < ? AND R.toDateTime > ?) OR " +
                        "(R.toDateTime > ? AND R.toDateTime < ?) ";
                marker = true;
            } if (vt != null) {
                query += marker? "R.vtname = " + vt.vtname + " " :
                        "AND R.vtname = " + vt.vtname + " ";
                marker = true;
            } if (l != null) {
                query += marker? "R.location = " + l.location + " " + "R.city = " + l.city:
                        "AND R.location = " + l.location + " " + "R.city = " + l.city;
            }
        }

        PreparedStatement ps = conn.prepareStatement(query);
        //Insert Timestamp values to prepared statement
        if (t != null){
            ps.setTimestamp(1, t.fromDateTime);
            ps.setTimestamp(2, t.fromDateTime);
            ps.setTimestamp(3, t.toDateTime);
            ps.setTimestamp(4, t.toDateTime);
        }
        ResultSet rs = ps.executeQuery();

        List<Reservation> r = new ArrayList<Reservation>();
        while (rs.next()){
            //Make a reservation object corresponding to a tuple queried from the database
            Reservation res = new Reservation();
            res.confNum = rs.getInt(1);
            res.vtName = rs.getString(2);
            res.dlicense = rs.getString(3);

            TimePeriod tm = new TimePeriod();
            tm.fromDateTime = rs.getTimestamp(4);
            tm.toDateTime = rs.getTimestamp(5);
            res.timePeriod = tm;

            Location loc = new Location();
            loc.city = rs.getString(6);
            loc.location = rs.getString(7);
            res.location = loc;

            //Add the reservation object to r
            r.add(res);
        }
        ps.close();
        return r;
    }

    /**
     * Finds and returns the reservation entry with the same primary key as the reservation object given. Other
     * attributes of the given object may be null. If no reservation found, returns null
     * @throws Exception if there is any error getting results
     */
    public Reservation getReservationMatching(Reservation r) throws Exception {
        // TODO: implement this
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Reservations WHERE confNo = " + Integer.toString(r.confNum));
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) return null;

        Reservation res = new Reservation();
        res.confNum = rs.getInt(1);
        res.vtName = rs.getString(2);
        res.dlicense = rs.getString(3);

        TimePeriod tm = new TimePeriod();
        tm.fromDateTime = rs.getTimestamp(4);
        tm.toDateTime = rs.getTimestamp(5);
        res.timePeriod = tm;

        Location loc = new Location();
        loc.city = rs.getString(6);
        loc.location = rs.getString(7);
        res.location = loc;

        ps.close();
        return res;
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

    public List<Rental> getRentalsWith(TimePeriod t, VehicleType vt, Location l) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    public Rental getRentalMatching(Rental r) throws Exception {
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

    public Customer getCustomerMatching(Customer c) throws Exception {
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
        PreparedStatement ps = conn.prepareStatement(Queries.VehicleType.ADD_VEHICLE_TYPE);

        //Set values for parameters in ps
        ps.setString(1, vt.vtname);
        ps.setString(2, vt.features);
        ps.setInt(3, vt.wrate);
        ps.setInt(4, vt.drate);
        ps.setInt(5, vt.hrate);
        ps.setInt(6, vt.wirate);
        ps.setInt(7, vt.dirate);
        ps.setInt(8, vt.hirate);
        ps.setInt(9, vt.krate);

        //execute the update
        ps.executeUpdate();

        //commit changes and close prepared statement
        ps.close();

        Log.log("VehicleType with vtname " + vt.vtname + " successfully added");
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

    public VehicleType getVehicleTypeMatching(VehicleType vt) throws Exception {
        // TODO: implement this

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM VehicleType WHERE vtName = '" + vt.vtname + "'");
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) return null;
        VehicleType res = new VehicleType(rs.getString(1), rs.getString(2), rs.getInt(3),
                rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7),
                rs.getInt(8), rs.getInt(9));
        System.out.println("VehicleType with vtname " + vt.vtname + " successfully retrieved");
        ps.close();
        return res;
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

    public List<Vehicle> getVehicleWith(VehicleType vt, Location l, boolean availableNow) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }

    public Vehicle getVehicleMatching(Vehicle v) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }
}
