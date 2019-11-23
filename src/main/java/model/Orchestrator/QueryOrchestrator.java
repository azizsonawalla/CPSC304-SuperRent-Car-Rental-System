package model.Orchestrator;

import model.Database;
import model.Entities.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class serves as the intermediate layer between the UI controller and the Database interface class.
 * The class 'orchestrates' various SQL queries to build and return the data required by the UI
 */
public class QueryOrchestrator {

    // Some dummy results for testing. Remove later
    Location L1 = new Location("Vancouver", "Downtown");
    Location L2 = new Location("Calgary", "Downtown");
    Location L3 = new Location("Vancouver", "UBC");
    Location L4 = new Location("Toronto", "Brampton");
    VehicleType VT1 = new VehicleType("SUV");
    VehicleType VT2 = new VehicleType("Truck");
    VehicleType VT3 = new VehicleType("Hatchback");
    VehicleType VT4 = new VehicleType("Sedan");
    Vehicle V1 = new Vehicle(1, "license", "Tesla", "Model S", 2018, "black", 0, true, "sedan", L1);
    Timestamp ts = new Timestamp(2018-1900, 1-1, 1, 1, 1, 1, 1);
    TimePeriod t = new TimePeriod(ts, ts);

    private Database db;

    public QueryOrchestrator() throws Exception {
        //this.db = new Database(); // commented this out for UI testing. uncomment later
    }

    public Integer getDailyKMLimit() {
        return 100; // TODO: Double check specs for a number
    }

    public List<VTSearchResult> getVTSearchResultsFor(Location l, VehicleType vt, TimePeriod t) throws Exception {
        // TODO: Implement this
        // this is just placeholder code
        ArrayList<VTSearchResult> list = new ArrayList<>();
        list.add(new VTSearchResult(VT1, L1, 10));
        list.add(new VTSearchResult(VT2, L1, 0));
        list.add(new VTSearchResult(VT3, L3, 15));
        list.add(new VTSearchResult(VT1, L4, 200));
        list.add(new VTSearchResult(VT2, L1, 25));
        list.add(new VTSearchResult(VT2, L4, 15));
        return list;
    }

    public List<Vehicle> getVehiclesFor(VTSearchResult searchResult) throws Exception {
        // TODO: Implement this
        return Arrays.asList(V1);
    }

    /**
     * Get customer with given driver's license number, or return null if not found
     */
    public Customer getCustomer(String dlNumber) {
        // TODO: Implement this
        return new Customer(1234512345, "John Smith", "Some address", "DL12345");
    }

    public void addCustomer(Customer c) throws Exception {
        // TODO: Implement this
        throw new Exception("Not implemented");
    }

    public Reservation makeReservation(Reservation r) throws Exception {
        // TODO: Implement this
        r.confNum = 1;
        return r;
    }

    public List<Location> getAllLocationNames() throws Exception {
        // TODO: Implement this
        // this is just placeholder code
        ArrayList<Location> list = new ArrayList<>(Arrays.asList(L1, L2, L3, L4));
        return list;
    }

    public List<VehicleType> getAllVehicleTypes() throws Exception {
        // TODO: Implement this
        // this is just placeholder code
        ArrayList<VehicleType> list = new ArrayList<>(Arrays.asList(VT1, VT2, VT3, VT4));
        return list;
    }

    public List<Reservation> getReservationsWith(int confNum, String customerDL) {
        // TODO: Implement this
        // if confNum == -1, then don't filter by confNum
        // if customerDL == "", then don't filer by customerDL
        return Arrays.asList(new Reservation(1, "dummyvt", t, L1, "dummyDL" ));
    }

    public List<Rental> getRentalsWith(int rentalId, String customerDL) {
        // TODO: Implement this
        // if rentalId == -1, then don't filter by confNum
        // if customerDL == "", then don't filer by customerDL
        return Arrays.asList(new Rental(1, "dummyplates", "dummyDL", t, 0, null, 1));
    }

    /**
     * Given a reservation, select a vehicle from the database that is available now,
     * of the requested type, at the reservation location. If no such vehicle is available, return null.
     * @param selectedRes
     * @return
     */
    public Vehicle getAutoSelectedVehicle(Reservation selectedRes) {
        // TODO: Implement this;
        return new Vehicle(1, "license", "make", "model", 2020, "black", 0, true, "SUV", L1);
    }

    public Rental addRental(Rental r) {
        // TODO;
        return r;
    }

    public Vehicle getVehicle(String vlicense) {
        // TODO
        return V1;
    }

    public VehicleType getVehicleType(String vtname) {
        // TODO
        return VT1;
    }

    public void submitReturn(Return r) {
        // TODO: implement
        // TODO: mark vehicle as available
        // TODO: update vehicle odometer
    }
}
