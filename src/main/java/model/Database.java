package model;

import model.Entities.*;
import model.Orchestrator.VTSearchResult;
import model.Util.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for the SuperRent database
 */
public class Database {

    private String HOST = "jdbc:mysql://35.247.80.246/superrent";
    private String TEST_HOST = "jdbc:mysql://35.247.80.246/superrenttest";
    private String USERNAME = "root";
    private String PASSWORD = "bobobobo";

    private Connection conn;

    /**
     * Constructor for Database class. Establishes a connection to the database and initializes the connection field.
     * @throws Exception if there was an error connecting to the database
     */
    public Database(boolean testmode) throws Exception {
        Log.log("Establishing connection to database...");
        try {
            this.conn = DriverManager.getConnection(testmode ? TEST_HOST : HOST, USERNAME, PASSWORD);
            if (this.conn == null) throw new Exception("Connection object is null");
            createTables();
        } catch (Exception e) {
            throw new Exception(e);
        }
        Log.log("Successfully connected to database!");
    }

    public Database() throws Exception {
        this(false);
    }

    //region CreateDropTables

    /**
     * Checks all the tables required for the system and creates them if they don't exist
     * @throws Exception if there was an error creating any of the tables
     */
    public void createTables() throws Exception{
        //Creates all the tables
        conn.prepareStatement(Queries.Create.CREATE_TABLE_CUSTOMER).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_VEHICLE_TYPE).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_BRANCH).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_CARD).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_VEHICLE).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_RESERVATIONS).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_RENT).executeUpdate();
        conn.prepareStatement(Queries.Create.CREATE_TABLE_RETURNS).executeUpdate();
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

    //endregion

    //region Reservations

    /**
     * Add the given reservation object to the reservation table in the database
     * @param r reservation object to add
     * @throws Exception if there is an error adding the reservation, for example if the values don't meet constraints
     */
    public Reservation addReservation(Reservation r) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Reservation.ADD_RESERVATION, Statement.RETURN_GENERATED_KEYS);

        //Set values for parameters in ps
        ps.setString(1, r.vtName);
        ps.setString(2, r.dlicense);
        ps.setTimestamp(3, r.timePeriod.startDateAndTime);
        ps.setTimestamp(4, r.timePeriod.endDateAndTime);
        ps.setString(5, r.location.city);
        ps.setString(6, r.location.location);

        //execute the update
        ps.executeUpdate();
        int confNo;
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            confNo = rs.getInt(1);
        } else {
            throw new Exception("There was an error retrieving the confNo of the reservation created");
        }
        r.confNum = confNo;
        //commit changes (automatic) and close prepared statement
        ps.close();

        Log.log("Reservation successfully added");

        return r;
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
        //advance cursor to first tuple. If there is no first tuple, return
        if(!rs.next()) {
            Log.log("No Reservation with confirmation number " + Integer.toString(r.confNum) + " found");
            return;
        }

        //Set values for parameters in psUpdate. Update if corresponding value in Reservation r is not null, otherwise, keep unchanged
        ps = conn.prepareStatement(Queries.Reservation.UPDATE_RESERVATION);
        ps.setString(1, r.vtName != null? r.vtName: rs.getString("vtname"));
        ps.setString(2, r.dlicense != null? r.dlicense: rs.getString("dLicense"));
        ps.setTimestamp(3, r.timePeriod != null? r.timePeriod.startDateAndTime: rs.getTimestamp("fromDateTime"));
        ps.setTimestamp(4, r.timePeriod != null? r.timePeriod.endDateAndTime: rs.getTimestamp("toDateTime"));
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
        int updates = ps.executeUpdate();

        //commit changes (automatic) and close prepared statement
        ps.close();

        if (updates == 0) Log.log("No Reservation with confirmation number " + Integer.toString(r.confNum) + " found");
        else Log.log("Reservation with confirmation number " + Integer.toString(r.confNum) + " successfully deleted");
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

        //TODO: move this query to Util.Queries
        if (t == null && vt == null && l == null){
            //If no filters are provided, return all the results
            query = "SELECT * FROM Reservations";
        } else {
            query = "SELECT * FROM Reservations R WHERE ";
            if (t != null) {
                //Given start time (from t) is at the same time or after R.startDateAndTime AND is before R.endDateAndTime
                //Given end time (from t) is at the same time before R.endDateAndTime AND is after R.startDateAndTime
                query += "((R.fromDateTime <= ? AND R.toDateTime > ?) OR " +
                        "(R.toDateTime >= ? AND R.fromDateTime < ?)) ";
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

        List<Reservation> r = new ArrayList<>();

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

            Location loc = new Location("Vancouver", "UBC");
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
        PreparedStatement ps = conn.prepareStatement(Queries.Reservation.GET_RESERVATION);
        ps.setInt(1, r.confNum);
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

        Location loc = new Location("Vancouver", "UBC");
        loc.city = rs.getString(6);
        loc.location = rs.getString(7);
        res.location = loc;

        ps.close();
        return res;
    }

    /**
     * Returns all active Reservations. Active reservations are reservations that have an end time greater than the
     * current time in milliseconds, and also don't have any rental associated with them. If r.confNum is not null,
     * filters results matching r.confNum. If r.dlicense is not null, filters results matching r.dlicense.
     * @param r
     * @return
     * @throws Exception
     */
    public List<Reservation> getActiveReservationsMatching(Reservation r) throws Exception {
        String query = Queries.Reservation.GET_ACTIVE_RESERVATIONS;

        if (r.confNum == -1) {
            query = query.replace("confNo = ? AND ", "");
        } else {
            query = query.replace("confNo = ? AND", "confNo = " + r.confNum + " AND");
        }

        if (r.dlicense == null) {
            query = query.replace("dLicense = ? AND ", "");
        } else {
            query = query.replace("dLicense = ? AND ", "dLicense = '" + r.dlicense + "' AND ");
        }

        Log.log(query);

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        ResultSet rs = ps.executeQuery();

        List<Reservation> reservations = new ArrayList<>();

        while (rs.next()){
            Reservation res = new Reservation();
            res.confNum = rs.getInt(1);
            res.vtName = rs.getString(2);
            res.dlicense = rs.getString(3);

            TimePeriod tm = new TimePeriod();
            tm.startDateAndTime = rs.getTimestamp(4);
            tm.endDateAndTime = rs.getTimestamp(5);
            res.timePeriod = tm;

            Location loc = new Location("Vancouver", "UBC");
            loc.city = rs.getString(6);
            loc.location = rs.getString(7);
            res.location = loc;
            reservations.add(res);
        }
        return reservations;
    }

    //endregion

    //region Rental

    /**
     * Add the given rental object to the rental table in the database
     * @param r rental object to add
     * @throws Exception if there is an error adding the rental, for example if the values don't meet constraints
     */
    public Rental addRental(Rental r) throws Exception {

        // Check if Card associated with rental exists. If not, add it first
        Card c = getCardMatching(r.card);
        if (c == null) {
            addCard(r.card);
        }

        PreparedStatement ps = conn.prepareStatement(Queries.Rent.ADD_RENTAL, Statement.RETURN_GENERATED_KEYS);

        //Set values for parameters in ps
        ps.setString(1, r.vlicense);
        ps.setString(2, r.dlicense);
        ps.setTimestamp(3, r.timePeriod.startDateAndTime);
        ps.setTimestamp(4, r.timePeriod.endDateAndTime);
        ps.setInt(5, r.startOdometer);
        ps.setLong(6, r.card.CardNo);
        ps.setLong(7, r.confNo);

        //execute the update
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        int rid;
        if (rs.next()) {
            rid = rs.getInt(1);
        } else {
            throw new Exception("Failed to retrieve id of return created");
        }
        r.rid = rid;

        //commit changes (automatic) and close prepared statement
        ps.close();

        //Log.log("Rental with rent id " + r.rid + " successfully added");
        return r;

    }

    /**
     * Update the values of the Rental entry in the Rental table that has the same primary key as the given
     * Rental object. New values of Rental entry are values in r.
     * @param r updated values for Rental entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateRental(Rental r) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Rent.GET_RENTAL);
        ps.setInt(1, r.rid);
        ResultSet rs = ps.executeQuery();
        rs.next();

        //Set values for parameters in psUpdate. Update if corresponding value in Reservation r is null, otherwise, keep unchanged
        ps = conn.prepareStatement(Queries.Rent.UPDATE_RENTAL);
        ps.setString(1, r.vlicense != null? r.vlicense: rs.getString("vLicense"));
        ps.setString(2, r.dlicense != null? r.dlicense: rs.getString("dLicense"));
        ps.setTimestamp(3,r.timePeriod.startDateAndTime != null? r.timePeriod.startDateAndTime: rs.getTimestamp("fromDateTime"));
        ps.setTimestamp(4,r.timePeriod.endDateAndTime != null? r.timePeriod.endDateAndTime: rs.getTimestamp("toDateTime"));
        ps.setInt(5,r.startOdometer != -1? r.startOdometer: rs.getInt("odometer"));
        ps.setLong(6, r.card.CardNo != -1? r.card.CardNo: rs.getLong("cardNo"));
        ps.setInt(7,r.confNo != -1? r.confNo: rs.getInt("confNo"));
        ps.setInt(8, r.rid);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Rental " + r.rid + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Rental with rid " + r.rid + " successfully updated");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param r object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteRental(Rental r) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Rent.DELETE_RENTAL);
        //Set confirmation number parameter for Reservation tuple to be deleted
        ps.setInt(1, r.rid);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Rental " + r.rid + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Rental with rid " + r.rid + " successfully deleted");
    }

    public List<Rental> getRentalsWith(TimePeriod t, VehicleType vt, Location l) throws Exception {
        //Empty string to build query
        String query;
        //marker to indicate if a condition has been added to the WHERE clause (and if AND needs to be used)
        boolean marker = false;

        if (t == null && vt == null && l == null){
            //If no filters are provided, return all the results
            query = "SELECT * FROM Rent ";
        } else {
            query = "SELECT * FROM Rent R, Reservations V WHERE R.confNo = V.confNo AND ";
            if (t != null) {
                //Given start time (from t) is at the same time or after R.startDateAndTime AND is before R.endDateAndTime
                //Given end time (from t) is at the same time before R.endDateAndTime AND is after R.startDateAndTime
                query += "((R.fromDateTime <= ? AND R.toDateTime > ?) OR " +
                        "(R.toDateTime >= ? AND R.toDateTime < ?)) ";
                marker = true;
            } if (vt != null) {
                query += marker? "AND V.vtName = '" + vt.vtname + "' " :
                        "V.vtname = '" + vt.vtname + "' ";
                marker = true;
            } if (l != null) {
                query += marker? "AND V.location = '" + l.location + "' " + "AND V.city = '" + l.city + "'":
                        "V.location = '" + l.location + "' " + "AND V.city = '" + l.city + "'";
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

        List<Rental> rentalList = new ArrayList<>();

        while (rs.next()){
            //Make a Rental object corresponding to a tuple queried from the database
            int rid = rs.getInt("rId");
            String vlicense = rs.getString("vLicense");
            String dlicense = rs.getString("dLicense");

            TimePeriod tm = new TimePeriod();
            tm.startDateAndTime = rs.getTimestamp("fromDateTime");
            tm.endDateAndTime = rs.getTimestamp("toDateTime");

            int startOdometer = rs.getInt("odometer");

            Card card = getCardMatching(new Card(rs.getLong("cardNo"), null, null));
            int confNo = rs.getInt("confNo");

            Rental rental = new Rental(rid, vlicense, dlicense, tm, startOdometer, card, confNo);

            //Add the reservation object to r
            rentalList.add(rental);
        }
        ps.close();
        return rentalList;
    }

    public Rental getRentalMatching(Rental r) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Rent.GET_RENTAL);
        ps.setInt(1, r.rid);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("NOTE: Rental " + r.rid + " does not exist");
            ps.close();
            return null;
        }

        int rid = rs.getInt("rId");
        String vlicense = rs.getString("vLicense");
        String dlicense = rs.getString("dLicense");

        TimePeriod tm = new TimePeriod();
        tm.startDateAndTime = rs.getTimestamp("fromDateTime");
        tm.endDateAndTime = rs.getTimestamp("toDateTime");

        int startOdometer = rs.getInt("odometer");

        Card card = getCardMatching(new Card(rs.getLong("cardNo"), null, null));
        int confNo = rs.getInt("confNo");

        Rental rental = new Rental(rid, vlicense, dlicense, tm, startOdometer, card, confNo);

        ps.close();
        return rental;

    }

    /* Return */
    public List<Rental> getRentalsWithHelper(TimePeriod timePeriod, Rental rental) throws Exception {
        String query = Queries.Rent.GET_ACTIVE_RENTALS;

        if (rental.rid == -1) {
            query = query.replace("rId = ? AND ", "");
        } else {
            query = query.replace("rId = ? AND", "rId = " + rental.rid + " AND");
        }

        if (rental.dlicense == null) {
            query = query.replace("dLicense = ? AND ", "");
        } else {
            query = query.replace("dLicense = ? AND ", "dLicense = '" + rental.dlicense + "' AND ");
        }

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setTimestamp(1, timePeriod.startDateAndTime);
        ResultSet rs = ps.executeQuery();

        List<Rental> rentals = new ArrayList<>();

        while (rs.next()){
            int rid = rs.getInt("rId");
            String vlicense = rs.getString("vLicense");
            String dlicense = rs.getString("dLicense");

            TimePeriod tm = new TimePeriod();
            tm.startDateAndTime = rs.getTimestamp("fromDateTime");
            tm.endDateAndTime = rs.getTimestamp("toDateTime");

            int startOdometer = rs.getInt("odometer");

            Card card = getCardMatching(new Card(rs.getLong("cardNo"), "", null));
            int confNo = rs.getInt("confNo");

            rentals.add(new Rental(rid, vlicense, dlicense, tm, startOdometer, card, confNo));
        }

        return rentals;
    }

    public List<Rental> getRentalsStartedToday(TimePeriod today) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Rent.GET_RENTALS_TODAY);
        ps.setTimestamp(1, today.startDateAndTime);
        ps.setTimestamp(2, today.endDateAndTime);
        ResultSet rs = ps.executeQuery();

        List<Rental> rentals = new ArrayList<>();

        while (rs.next()){
            int rid = rs.getInt("rId");
            String vlicense = rs.getString("vLicense");
            String dlicense = rs.getString("dLicense");

            TimePeriod tm = new TimePeriod();
            tm.startDateAndTime = rs.getTimestamp("fromDateTime");
            tm.endDateAndTime = rs.getTimestamp("toDateTime");

            int startOdometer = rs.getInt("odometer");

            Card card = getCardMatching(new Card(rs.getLong("cardNo"), "", null));
            int confNo = rs.getInt("confNo");

            rentals.add(new Rental(rid, vlicense, dlicense, tm, startOdometer, card, confNo));
        }

        return rentals;
    }

    //endregion

    //region Return
    /**
     * Add the given Return object to the return table in the database
     * @param r return object to add
     * @throws Exception if there is an error adding the rental, for example if the values don't meet constraints
     */
    public void addReturn(Return r) throws Exception {

        PreparedStatement ps = conn.prepareStatement(Queries.Returns.ADD_RETURN);

        //Set values for parameters in ps
        ps.setInt(1, r.rid);
        ps.setTimestamp(2, r.returnDateTime);
        ps.setInt(3, r.endOdometer);
        ps.setBoolean(4, r.fullTank == Return.TankStatus.FULL_TANK);
        ps.setDouble(5, r.cost);

        //execute the update
        ps.executeUpdate();

        //commit changes (automatic) and close prepared statement
        ps.close();

        Log.log("Return with rent id " + r.rid + " successfully added");

    }

    public List<Return> getReturnsWith(TimePeriod t, VehicleType vt, Location l) throws Exception {
        //Empty string to build query
        String query;
        //marker to indicate if a condition has been added to the WHERE clause (and if AND needs to be used)
        boolean marker = false;

        if (t == null && vt == null && l == null){
            //If no filters are provided, return all the results
            query = "SELECT * FROM Returns R ";
        } else {
            query = "SELECT * FROM Returns R, Rent N, Reservations V WHERE R.rId = N.rId AND N.confNo = V.confNo AND ";
            if (t != null) {
                //Given start time (from t) is at the same time or after R.startDateAndTime AND is before R.endDateAndTime
                //Given end time (from t) is at the same time before R.endDateAndTime AND is after R.startDateAndTime
                query += "(R.dateTime >= ? ) AND (R.dateTime <= ? )";
                marker = true;
            } if (vt != null) {
                query += marker? "AND V.vtName = '" + vt.vtname + "' " :
                        "V.vtname = '" + vt.vtname + "' ";
                marker = true;
            } if (l != null) {
                query += marker? "AND V.location = '" + l.location + "' " + "AND V.city = '" + l.city + "'":
                        "V.location = '" + l.location + "' " + "AND V.city = '" + l.city + "'";
            }
        }
        PreparedStatement ps = conn.prepareStatement(query);
        //Insert Timestamp values to prepared statement
        if (t != null){
            ps.setTimestamp(1, t.startDateAndTime);
            ps.setTimestamp(2, t.endDateAndTime);

        }
        ResultSet rs = ps.executeQuery();

        List<Return> returnList = new ArrayList<>();

        while (rs.next()){
            //Make a Rental object corresponding to a tuple queried from the database
            int rid = rs.getInt("rId");
            Timestamp returnDateTime = rs.getTimestamp("dateTime");
            int endOdometer = rs.getInt("R.odometer");
            Return.TankStatus tank = rs.getBoolean("fullTank")? Return.TankStatus.FULL_TANK : Return.TankStatus.NOT_FULL_TANK;
            double cost = rs.getDouble("value");

            Return ret = new Return(rid, returnDateTime, endOdometer,tank, cost);

            //Add the reservation object to r
            returnList.add(ret);
        }
        ps.close();
        return returnList;
    }


    /**
     * Update the values of the Return entry in the Return table that has the same primary key as the given
     * Return object. New values of Rental entry are values in r.
     * @param r updated values for Return entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateReturn(Return r) throws Exception { // TODO: FIX if we need to use.
        PreparedStatement ps = conn.prepareStatement(Queries.Returns.GET_RETURN);
        ps.setInt(1, r.rid);
        ResultSet rs = ps.executeQuery();
        rs.next();

        //Set values for parameters in psUpdate. Update if corresponding value in Reservation r is null, otherwise, keep unchanged
        ps = conn.prepareStatement(Queries.Returns.UPDATE_RETURN);
        ps.setTimestamp(1, r.returnDateTime != null? r.returnDateTime: rs.getTimestamp("DateTime"));
        ps.setInt(2, r.endOdometer != -1? r.endOdometer: rs.getInt("odometer"));
        if (r.fullTank == Return.TankStatus.FULL_TANK) ps.setBoolean(3, true);
        else if (r.fullTank == Return.TankStatus.NOT_FULL_TANK) ps.setBoolean(3, false);
        else ps.setBoolean(3, rs.getBoolean("status"));
        ps.setDouble(4,r.cost!= -1.? r.cost : rs.getDouble("value"));
        ps.setInt(5, r.rid);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Return " + r.rid + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Return with rid " + r.rid + " successfully updated");
    }

    /**
     * Delete entry from return corresponding return that matches the primary key of the given object
     * @param r object with same primary key as entry to delete from Return
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteReturn(Return r) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Returns.DELETE_RETURN);
        //Set confirmation number parameter for Return tuple to be deleted
        ps.setInt(1, r.rid);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Return " + r.rid + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Return with rid " + r.rid + " successfully deleted");
    }


    public Return getReturnMatching(Return r) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Returns.GET_RETURN);
        ps.setInt(1, r.rid);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("NOTE: Return " + r.rid + " does not exist");
            ps.close();
            return null;
        }

        int rid = rs.getInt("rId");
        Timestamp dateTime = rs.getTimestamp("dateTime");
        int endOdometer = rs.getInt("odometer");
        Boolean fullTank = rs.getBoolean("fullTank");
        Return.TankStatus tankEnum = fullTank? Return.TankStatus.FULL_TANK : Return.TankStatus.NOT_FULL_TANK;
        int cost = rs.getInt("value");


        Return ret = new Return(rid, dateTime, endOdometer, tankEnum, cost);

        ps.close();
        return ret;

    }

    /* Customer */
    public String getReturnedVehicle(Return r) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(Queries.Returns.JOIN_RENTAL);
        ps.setInt(1, r.rid);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("vLicense");
    }

    //endregion

    //region Customer

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
        ps.setDouble(3, vt.wrate);
        ps.setDouble(4, vt.drate);
        ps.setDouble(5, vt.hrate);
        ps.setDouble(6, vt.wirate);
        ps.setDouble(7, vt.dirate);
        ps.setDouble(8, vt.hirate);
        ps.setDouble(9, vt.krate);

        //execute the update
        ps.executeUpdate();

        //commit changes and close prepared statement
        ps.close();
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
        ps.setDouble(2, vt.wrate != -1? vt.wrate: rs.getInt("wRate"));
        ps.setDouble(3, vt.drate != -1? vt.drate: rs.getInt("dRate"));
        ps.setDouble(4, vt.hrate != -1? vt.hrate: rs.getInt("hRate"));
        ps.setDouble(5, vt.wirate != -1? vt.wirate: rs.getInt("wInsRate"));
        ps.setDouble(6, vt.dirate != -1? vt.dirate: rs.getInt("dInsRate"));
        ps.setDouble(7, vt.hirate != -1? vt.hirate: rs.getInt("hInsRate"));
        ps.setDouble(8, vt.krate != -1? vt.krate: rs.getInt("kRate"));
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
    }

    public VehicleType getVehicleTypeMatching(VehicleType vt) throws SQLException {

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

        //Handle situation where vehicle type does not have features
        if (res.features == null) res.features = "No features";

        ps.close();
        return res;
    }

    public List<VehicleType> getAllVehicleTypes() throws SQLException {//TODO FIX TO BE GET ALL
        ResultSet rs = conn.prepareStatement(Queries.VehicleType.QUERY_ALL).executeQuery();
        List<VehicleType> vehicleTypes = new ArrayList<>();
        while (rs.next()){
            vehicleTypes.add(new VehicleType(rs.getString(1), rs.getString(2), rs.getInt(3),
                    rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7),
                    rs.getInt(8), rs.getInt(9)));
        }
        return vehicleTypes;
    }

    //endregion

    //region Vehicle

    /**
     * Add the given Vehicle object to the Vehicle table in the database
     * @param v Vehicle object to add
     * @throws Exception if there is an error adding the Vehicle, for example if the values don't meet constraints
     */
    public void addVehicle(Vehicle v) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Vehicle.ADD_VEHICLE);

        //Set values for parameters in ps
        ps.setInt(1, v.vid);
        ps.setString(2, v.vlicense);
        ps.setString(3, v.make);
        ps.setString(4, v.model);
        ps.setInt(5, v.year);
        ps.setString(6, v.color);
        ps.setInt(7, v.odometer);
        ps.setString(8, v.vtname);
        if (v.status == Vehicle.VehicleStatus.AVAILABLE) ps.setBoolean(9, true);
        else ps.setBoolean(9, false);
        ps.setString(10, v.location.location);
        ps.setString(11, v.location.city);

        //execute the update
        ps.executeUpdate();

        //commit changes and close prepared statement
        ps.close();

        Log.log("Vehicle with vlicense " + v.vlicense + " successfully added");

    }

    /**
     * Update the values of the Vehicle entry in the Vehicle table that has the same primary key as the given
     * Vehicle object. New values of Vehicle entry are values in c.
     * @param v updated values for Vehicle entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateVehicle(Vehicle v) throws Exception {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Vehicle WHERE vLicense = ?");
        ps.setString(1, v.vlicense);
        ResultSet rs = ps.executeQuery();
        rs.next();

        //Set values for parameters in psUpdate. Update if corresponding value in Reservation r is null, otherwise, keep unchanged
        ps = conn.prepareStatement(Queries.Vehicle.UPDATE_VEHICLE);
        ps.setInt(1, v.vid != -1? v.vid: rs.getInt("vId"));
        ps.setString(2, v.make != null? v.make: rs.getString("make"));
        ps.setString(3, v.model != null? v.model: rs.getString("model"));
        ps.setInt(4, v.year != -1? v.year: rs.getInt("year"));
        ps.setString(5, v.color != null? v.color: rs.getString("color"));
        ps.setInt(6, v.odometer != -1? v.odometer: rs.getInt("odometer"));
        ps.setBoolean(7, v.status != null? v.status == Vehicle.VehicleStatus.AVAILABLE : rs.getBoolean("status"));
        ps.setString(8, v.vlicense);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Vehicle " + v.vlicense + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Vehicle with vlicense " + v.vtname + " successfully updated");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param v object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteVehicle(Vehicle v) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Vehicle.DELETE_VEHICLE);
        //Set confirmation number parameter for Reservation tuple to be deleted
        ps.setString(1, v.vlicense);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Vehicle " + v.vlicense + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Vehicle with vlicense " + v.vlicense + " successfully deleted");
    }

    public List<Vehicle> getVehicleWith(VehicleType vt, Location l, Vehicle.VehicleStatus availableNow) throws Exception {
        String query = Queries.Vehicle.GET_VEHICLES_WITH;

        if (vt == null && l == null && availableNow == null){
            //If no filters are provided, return all the vehicles in the Vehicle table
            query = query.replace(" WHERE vtName = ? AND location = ? AND city = ? AND status = ?", "");
        } else {
            //If no VehicleType is provided or if vtName can be anything, remove VehicleType filter from WHERE clause.
            //If VehicleType provided, filter by vt.vtname
            if (vt == null) query = query.replace("vtName = ? AND ", "");
            else query = query.replace("vtName = ?", "vtName = '" + vt.vtname + "'");
            //If no location is provided, remove location filter from WHERE clause.
            //If location provided, filter by l.location and l.city
            if (l == null) query = query.replace("location = ? AND city = ? AND ", "");
            else query = query.replace("location = ? AND city = ?", "location = '" + l.location + "' AND city = '" + l.city + "'");
            //If no VehicleStatus is provided, remove VehicleStatus filter from WHERE clause.
            //If VehicleStatus provided, filter by VehicleStatus (AVAILABLE or RENTED)
            if (availableNow == null) query = query.replace("AND status = ?", "");
            else query = query.replace("status = ?", "status = " + (availableNow == Vehicle.VehicleStatus.AVAILABLE));
        }

        //Prepare statement and execute query
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        List<Vehicle> vehicles = new ArrayList<>();

        while (rs.next()){
            //Make a Vehicle object corresponding to a tuple queried from the database
            Vehicle v = new Vehicle(rs.getInt("vId"),
                                    rs.getString("vLicense"),
                                    rs.getString("make"),
                                    rs.getString("model"),
                                    rs.getInt("year"),
                                    rs.getString("color"),
                                    rs.getInt("odometer"),
                                    rs.getString("vtName"),
                                    (rs.getBoolean("status")) ?
                                            Vehicle.VehicleStatus.AVAILABLE: Vehicle.VehicleStatus.RENTED,
                                    new Location(rs.getString("city"), rs.getString("location")));

            //Add the vehicle object to vehicles
            vehicles.add(v);
        }
        //close the prepared statement and return the results
        ps.close();
        return vehicles;

    }

    public Vehicle getVehicleMatching(Vehicle v) throws Exception {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Vehicle WHERE vLicense = '" + v.vlicense + "'");
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("NOTE: Vehicle " + v.vlicense + " does not exist");
            ps.close();
            return null;
        }

        Vehicle res = new Vehicle(rs.getInt("vId"),
                rs.getString("vLicense"),
                rs.getString("make"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getString("color"),
                rs.getInt("odometer"),
                rs.getString("vtName"),
                (rs.getBoolean("status")) ? Vehicle.VehicleStatus.AVAILABLE: Vehicle.VehicleStatus.RENTED,
                new Location(rs.getString("city"), rs.getString("location")));

        ps.close();
        return res;
    }

    public List<VTSearchResult> getVTSearchResultsForHelper(Location l, VehicleType vt) throws Exception{

        //This query returns the TOTAL number of cars that are a vehicle type and/or at a location
        String query = Queries.Vehicle.GET_NUM_VEHICLES_WITH;


        if (vt == null && l == null){
            //If no location/vehicle type filter is provided, group all vehicles by vehicle types and location and
            // return the count
            query = query.replace(" WHERE vtName = ? AND location = ? AND city = ?", "");
        } else {
            //If no VehicleType is provided, remove VehicleType filter from WHERE clause.
            //If VehicleType provided, filter by vt.vtname
            if (vt == null) {
                query = query.replace("vtName = ? AND ", "");
            } else {
                query = query.replace("vtName = ?", "vtName = '" + vt.vtname + "'");
            }
            //If no location is provided, remove location filter from WHERE clause.
            //If location provided, filter by l.location and l.city
            if (l == null) {
                query = query.replace("AND location = ? AND city = ?", "");
            } else {
                query = query.replace("location = ? AND city = ?", "location = '" + l.location +
                        "' AND city = '" + l.city + "'");
            }
        }

        //Prepare statement and execute query
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        List<VTSearchResult> vtSearchResults = new ArrayList<>();

        //Iterate through resultSet
        while (rs.next()){
            //Make a VTSearchResult object based on current tuple.
            VTSearchResult vtsr = new VTSearchResult(
                    getVehicleTypeMatching(new VehicleType(rs.getString("vtName"))),
                    new Location(rs.getString("city"), rs.getString("location")),
                    rs.getInt(4));

            //Add the VTSearchResult object to vehicles
            vtSearchResults.add(vtsr);
        }
        //close the prepared statement and return the results
        ps.close();
        return vtSearchResults;
    }

    //endregion


    /* Card */

    /**
     * Add the given Card object to the Card table in the database
     * @param c Card object to add
     * @throws Exception if there is an error adding the Card, for example if the values don't meet constraints
     */
    public void addCard(Card c) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Card.ADD_CARD);

        //Set values for parameters in ps
        ps.setLong(1, c.CardNo);
        ps.setString(2, c.cardName);
        ps.setTimestamp(3, c.expDate);


        //execute the update
        ps.executeUpdate();

        //commit changes and close prepared statement
        ps.close();

        Log.log("Card with cardNo " + c.CardNo + " successfully added");

    }

    /**
     * Update the values of the Card entry in the Card table that has the same primary key as the given
     * Card object. New values of Card entry are values in c.
     * @param c updated values for Card entry
     * @throws Exception if there is an error updating entry, for example if entry doesn't exist already
     */
    public void updateCard(Card c) throws Exception {
        //Get Card tuple with confirmation number c.cardNo
        PreparedStatement ps = conn.prepareStatement(Queries.Card.GET_CARD);
        ps.setLong(1, c.CardNo);
        ResultSet rs = ps.executeQuery();
        rs.next();

        //Set values for parameters in psUpdate. Update if corresponding value in Card c is null, otherwise, keep unchanged
        ps = conn.prepareStatement(Queries.Card.UPDATE_CARD);
        ps.setString(1, c.cardName != null? c.cardName: rs.getString("cardName"));
        ps.setTimestamp(2, c.expDate != null? c.expDate: rs.getTimestamp("expDate"));
        ps.setLong(3, c.CardNo);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Card with CardNo " + c.CardNo + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Card with cardNo " + c.CardNo + " successfully updated");
    }

    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param c object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteCard(Card c) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Card.DELETE_CARD);
        //Set card number parameter for Reservation tuple to be deleted
        ps.setLong(1, c.CardNo);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Card with CardNo " + c.CardNo + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Card with cardNo " + c.CardNo + " successfully deleted");
    }

    /**
     * Finds and returns the card entry with the same primary key as the card object given. Other
     * attributes of the given object may be null. If no card found, returns null
     * @throws Exception if there is any error getting results
     */
    public Card getCardMatching(Card c) throws Exception {
        long cardNo = c.CardNo;
        PreparedStatement ps = conn.prepareStatement(Queries.Card.GET_CARD);
        ps.setLong(1, cardNo);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("NOTE: Card " + cardNo + " does not exist");
            ps.close();
            return null;
        }

        long CardNo = rs.getLong("cardNo");
        String cardName = rs.getString("cardName");
        Timestamp expDate = rs.getTimestamp("expDate");
        Card cardFound = new Card(CardNo, cardName, expDate);

        ps.close();
        return cardFound;
    }


    /* Location */

    /**
     * Add the given Location object to the Location table in the database
     * @param l Location object to add
     * @throws Exception if there is an error adding the Card, for example if the values don't meet constraints
     */
    public void addLocation(Location l) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Branch.ADD_BRANCH);

        //Set values for parameters in ps
        ps.setString(1, l.city);
        ps.setString(2, l.location);

        //execute the update
        ps.executeUpdate();

        //commit changes and close prepared statement
        ps.close();

        Log.log("Branch at " + l.city +  ", " + l.location + " successfully added");
    }

    /**
     * Finds and returns the location entry with the same primary key as the card object given. Other
     * attributes of the given object may be null. If no location found, returns null
     * @throws Exception if there is any error getting results
     */
    public Location getLocationMatching(Location l) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Branch.GET_BRANCH);
        ps.setString(1, l.city);
        ps.setString(2, l.location);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("NOTE: Branch at" + l.toString() + " does not exist");
            ps.close();
            return null;
        }

        Location locationFound = new Location(rs.getString("city"), rs.getString("location"));

        ps.close();
        return locationFound;
    }


    /**
     * Delete entry from table corresponding table that matches the primary key of the given object
     * @param l object with same primary key as entry to delete from table
     * @throws Exception if there is an error deleting entry, for example if entry doesn't exist already
     */
    public void deleteLocation(Location l) throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Branch.DELETE_BRANCH);
        //Set card number parameter for Reservation tuple to be deleted
        ps.setString(1, l.city);
        ps.setString(2, l.location);

        //execute the update
        int rowCount = ps.executeUpdate();
        if (rowCount == 0) {
            System.out.println("NOTE: Branch at " + l.city +  ", " + l.location + " does not exist");
            ps.close();
            return;
        }

        //commit changes and close prepared statement
        ps.close();

        Log.log("Branch at " + l.city +  ", " + l.location + " successfully deleted");
    }

    /**
     * Returns all Location in the database
     * @return
     * @throws Exception
     */
    public List<Location> getAllLocations() throws Exception {
        PreparedStatement ps = conn.prepareStatement(Queries.Branch.GET_ALL_BRANCHES);
        ResultSet rs = ps.executeQuery();
        List<Location> locations = new ArrayList<>();

        while (rs.next()){
            locations.add(new Location(rs.getString("city"), rs.getString("location")));
        }

        ps.close();
        return locations;
    }


    //endregion



//    public void generateLocationCardData() throws Exception {
//        PreparedStatement ps = conn.prepareStatement(Queries.Location.ADD_LOCATION);
//
//        //Set values for parameters in ps
//        ps.setString(1, "Vancouver");
//        ps.setString(2, "");
//    }
}
