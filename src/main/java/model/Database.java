package model;

import model.Entities.*;
import model.Util.Log;

import java.sql.*;
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
        conn.prepareStatement(Queries.Create.CREATE_TABLE_BRANCH).executeUpdate();
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
        conn.prepareStatement(Queries.Drop.DROP_TABLE_Branch).executeUpdate();
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
        ps.setTimestamp(4, r.timePeriod.startDateAndTime);
        ps.setTimestamp(5, r.timePeriod.endDateAndTime);
        ps.setString(6, r.location.city);
        ps.setString(7, r.location.location);

        //execute the update
        ps.executeUpdate();

        //commit changes (automatic) and close prepared statement
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
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Reservations WHERE confNo = ?");
        ps.setInt(1, r.confNum);
        ResultSet rs = ps.executeQuery();
        rs.next();

        //Set values for parameters in psUpdate. Update if corresponding value in Reservation r is null, otherwise, keep unchanged
        ps = conn.prepareStatement(Queries.Reservation.UPDATE_RESERVATION);
        ps.setString(1, r.vtName != null? r.vtName: rs.getString("vtname"));
        ps.setString(2, r.dlicense != null? r.dlicense: rs.getString("dLicense"));
        ps.setTimestamp(3, r.timePeriod != null? r.timePeriod.startDateAndTime: rs.getTimestamp("startDateAndTime"));
        ps.setTimestamp(4, r.timePeriod != null? r.timePeriod.endDateAndTime: rs.getTimestamp("endDateAndTime"));
        ps.setString(5, r.location != null? r.location.city: rs.getString("city"));
        ps.setString(6, r.location != null? r.location.location: rs.getString("location"));
        ps.setInt(7, r.confNum);

        //execute the update
        ps.executeUpdate();

        //commit changes (automatic) and close prepared statement
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

        //commit changes (automatic) and close prepared statement
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
                //Given start time (from t) is at the same time or after R.startDateAndTime AND is before R.endDateAndTime
                //Given end time (from t) is at the same time before R.endDateAndTime AND is after R.startDateAndTime
                query += "((R.startDateAndTime <= ? AND R.endDateAndTime > ?) OR " +
                        "(R.endDateAndTime >= ? AND R.endDateAndTime < ?)) ";
                marker = true;
            } if (vt != null) {
                query += marker? "AND R.vtname = '" + vt.vtname + "' " :
                        "R.vtname = '" + vt.vtname + "' ";
                marker = true;
            } if (l != null) {
                query += marker? "AND R.location = '" + l.location + "' " + "AND R.city = '" + l.city + "'":
                        "R.location = '" + l.location + "' " + "AND R.city = '" + l.city + "'";
            }
        }
        PreparedStatement ps = conn.prepareStatement(query);
        //Insert Timestamp values to prepared statement
        if (t != null){
            ps.setTimestamp(1, t.startDateAndTime);
            ps.setTimestamp(2, t.startDateAndTime);
            ps.setTimestamp(3, t.endDateAndTime);
            ps.setTimestamp(4, t.endDateAndTime);
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
            tm.startDateAndTime = rs.getTimestamp(4);
            tm.endDateAndTime = rs.getTimestamp(5);
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

        if (!rs.next()) {
            System.out.println("NOTE: Reservation " + r.confNum + " does not exist");
            ps.close();
            return null;
        }

        Reservation res = new Reservation();
        res.confNum = rs.getInt(1);
        res.vtName = rs.getString(2);
        res.dlicense = rs.getString(3);

        TimePeriod tm = new TimePeriod();
        tm.startDateAndTime = rs.getTimestamp(4);
        tm.endDateAndTime = rs.getTimestamp(5);
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


        PreparedStatement ps = conn.prepareStatement(Queries.Rent.ADD_RENTAL);

        //Set values for parameters in ps
        ps.setInt(1, r.rid);
        ps.setString(2, r.vlicense);
        ps.setString(3, r.dlicense);
        ps.setTimestamp(4, r.timePeriod.startDateAndTime);
        ps.setTimestamp(5, r.timePeriod.endDateAndTime);
        ps.setInt(6, r.startOdometer);
        ps.setLong(7, r.card.CardNo);
        ps.setLong(8, r.confNo);

        //execute the update
        ps.executeUpdate();

        //commit changes (automatic) and close prepared statement
        ps.close();

        Log.log("Rental with rent id " + Integer.toString(r.rid) + " successfully added");
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
        PreparedStatement ps = conn.prepareStatement(Queries.Customer.ADD_CUSTOMER);

        //Set values for parameters in ps
        ps.setLong(1, c.cellphone);
        ps.setString(2, c.name);
        ps.setString(3, c.address);
        ps.setString(4, c.dlicense);

        //execute the update
        ps.executeUpdate();

        //commit changes and close prepared statement
        ps.close();

        Log.log("Customer with dlicense " + c.dlicense + " successfully added");
    }

    /**
     * Update the values of the Customer entry in the Customer table that has the same primary key as the given
     * Customer object. New values of Customer entry are values in c.
     * @param c updated values for Customer entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateCustomer(Customer c) throws Exception {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Customer WHERE dLicense = ?");
        ps.setString(1, c.dlicense);
        ResultSet rs = ps.executeQuery();
        rs.next();

        //Set values for parameters in psUpdate. Update if corresponding value in Reservation r is null, otherwise, keep unchanged
        ps = conn.prepareStatement(Queries.Customer.UPDATE_CUSTOMER);
        ps.setLong(1, c.cellphone != -1? c.cellphone: rs.getLong("cellphone"));
        ps.setString(2, c.name != null? c.name: rs.getString("name"));
        ps.setString(3, c.address != null? c.address: rs.getString("address"));
        ps.setString(4, c.dlicense);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Customer " + c.dlicense + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Customer with dlicense " + c.dlicense + " successfully updated");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param c object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteCustomer(Customer c) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Customer.DELETE_CUSTOMER);
        //Set confirmation number parameter for Reservation tuple to be deleted
        ps.setString(1, c.dlicense);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Customer " + c.dlicense + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Customer with dlicense " + c.dlicense + " successfully deleted");
    }

    public Customer getCustomerMatching(Customer c) throws Exception {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Customer WHERE dLicense = '" + c.dlicense + "'");
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("NOTE: Customer " + c.dlicense + " does not exist");
            ps.close();
            return null;
        }

        Customer res = new Customer(rs.getLong(1), rs.getString(2), rs.getString(3),
                rs.getString(4));
        System.out.println("Customer with dLicense " + c.dlicense + " successfully retrieved");
        ps.close();
        return res;
    }

    /* Vehicle Type */

    /**
     * Add the given VehicleType object to the VehicleType table in the database
     * @param vt VehicleType object to add
     * @throws Exception if there is an error adding the VehicleType, for example if the values don't meet constraints
     */
    public void addVehicleType(VehicleType vt) throws SQLException {
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
    public void updateVehicleType(VehicleType vt) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM VehicleType WHERE vtName = ?");
        ps.setString(1, vt.vtname);
        ResultSet rs = ps.executeQuery();
        rs.next();

        //Set values for parameters in psUpdate. Update if corresponding value in Reservation r is null, otherwise, keep unchanged
        ps = conn.prepareStatement(Queries.VehicleType.UPDATE_VEHICLE_TYPE);
        ps.setString(1, vt.features != null? vt.features: rs.getString("features"));
        ps.setInt(2, vt.wrate != -1? vt.wrate: rs.getInt("wRate"));
        ps.setInt(3, vt.drate != -1? vt.drate: rs.getInt("dRate"));
        ps.setInt(4, vt.hrate != -1? vt.hrate: rs.getInt("hRate"));
        ps.setInt(5, vt.wirate != -1? vt.wirate: rs.getInt("wInsRate"));
        ps.setInt(6, vt.dirate != -1? vt.dirate: rs.getInt("dInsRate"));
        ps.setInt(7, vt.hirate != -1? vt.hirate: rs.getInt("hInsRate"));
        ps.setInt(8, vt.krate != -1? vt.krate: rs.getInt("kRate"));
        ps.setString(9, vt.vtname);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: VehicleType " + vt.vtname + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("VehicleType with vtname " + vt.vtname + " successfully updated");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param vt object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteVehicleType(VehicleType vt) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.VehicleType.DELETE_VEHICLE_TYPE);
        //Set confirmation number parameter for Reservation tuple to be deleted
        ps.setString(1, vt.vtname);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: VehicleType " + vt.vtname + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("VehicleType with vtname " + vt.vtname + " successfully deleted");
    }

    public VehicleType getVehicleTypeMatching(VehicleType vt) throws SQLException {
        // TODO: implement this

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM VehicleType WHERE vtName = '" + vt.vtname + "'");
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("NOTE: VehicleType " + vt.vtname + " does not exist");
            ps.close();
            return null;
        }

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

    }

    /**
     * Update the values of the Vehicle entry in the Vehicle table that has the same primary key as the given
     * Vehicle object. New values of Vehicle entry are values in c.
     * @param v updated values for Vehicle entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateVehicle(Vehicle v) throws Exception {
        // TODO: implement this
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

    /* Card */

    /**
     * Add the given Card object to the Card table in the database
     * @param c Card object to add
     * @throws Exception if there is an error adding the Card, for example if the values don't meet constraints
     */
    public void addCard(Card c) throws Exception {
        // TODO: implement this

    }

    /**
     * Update the values of the Card entry in the Card table that has the same primary key as the given
     * Card object. New values of Card entry are values in c.
     * @param c updated values for Card entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateCard(Card c) throws Exception {
        // TODO: implement this
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param c object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteCard(Card c) throws Exception {
        // TODO: implement this
        throw new Exception("Method not implemented");
    }


//    public void generateLocationCardData() throws Exception {
//        PreparedStatement ps = conn.prepareStatement(Queries.Location.ADD_LOCATION);
//
//        //Set values for parameters in ps
//        ps.setString(1, "Vancouver");
//        ps.setString(2, "");
//    }
}
