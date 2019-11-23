package model.Orchestrator;

import model.Database;
import model.Entities.*;
import model.Util.Log;

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
    Vehicle V1 = new Vehicle(1, "license", "Tesla", "Model S",
            2018, "black", 0, "sedan", Vehicle.VehicleStatus.AVAILABLE, L1);

    private Database db;

    public QueryOrchestrator() throws Exception {
        this.db = new Database();
    }


    /**
     * Customer car search top table
     */
    public List<VTSearchResult> getVTSearchResultsFor(Location l, VehicleType vt, TimePeriod t) throws Exception {
        //region Instructions/Placeholder code
        // this is just placeholder code
        ArrayList<VTSearchResult> list = new ArrayList<>();
        list.add(new VTSearchResult(VT1, L1, 10));
        list.add(new VTSearchResult(VT2, L1, 0));
        list.add(new VTSearchResult(VT3, L3, 15));
        list.add(new VTSearchResult(VT1, L4, 200));
        list.add(new VTSearchResult(VT2, L1, 25));
        list.add(new VTSearchResult(VT2, L4, 15));

        /*
        List<Reservation> reservations = db.getReservationsWith(t, vt, l);
         */
        // Query all tuples in location x VehicleType x Vehicle
        // Filter by location if not null
        // Filter by vt if not null
        // group by location, vt and aggregate into count

        // Query all reservations overlapping with time period
        // For each reservation, if vt + location in table, subtract 1 from count

        //endregion

        //List of VTSearchResult
        List<VTSearchResult> vtSearchResults = db.getVTSearchResultsForHelper(l, vt);
        List<Reservation> reservations = db.getReservationsWith(t, vt, l);

        for (Reservation r : reservations) {
            for (VTSearchResult vtSearchResult : vtSearchResults) {
                if (vtSearchResult.vt.vtname.equals(r.vtName) &&
                        vtSearchResult.location.city.equals(r.location.city) &&
                        vtSearchResult.location.location.equals(r.location.location))
                    vtSearchResult.numAvail--;
            }
        }

        return vtSearchResults;

    }


    /**
     * Customer car search results bottom table
     * From Vehicles table, return vehicles that have vtname == searchResult.vt.vtname, location = searchResult.location
     * and are available (status = AVAILABLE)
     * @param searchResult
     * @return
     * @throws Exception
     */
    public List<Vehicle> getVehiclesFor(VTSearchResult searchResult) throws Exception {
        return db.getVehicleWith(searchResult.vt, searchResult.location, null);
    }

    /**
     * Get customer with given driver's license number, or return null if not found
     */
    public Customer getCustomer(String dlNumber) throws Exception {
        return db.getCustomerMatching(new Customer(dlNumber));
    }

    /**
     * Add given customer object to database
     * @param c
     * @throws Exception
     */
    public void addCustomer(Customer c) throws Exception {
        db.addCustomer(c);
    }

    public Reservation makeReservation(Reservation r) throws Exception {
        db.addReservation(r);
        return r;
    }

    public List<Location> getAllLocationNames() throws Exception {
        // TODO: Implement this
        // this is just placeholder code
        ArrayList<Location> list = new ArrayList<>(Arrays.asList(L1, L2, L3, L4));
        return list;
    }

    public List<VehicleType> getAllVehicleTypes() throws Exception {
        return db.getAllVehicleTypes();
    }


    public List<Reservation> getReservationWith(int confNum, String customerDL) throws Exception{
        // if confNum == -1, then don't filter by confNum
        // if customerDL == "", then don't filer by customerDL
        Reservation r = new Reservation(confNum, null, null, null, customerDL);
        Log.log(String.valueOf(db));
        return db.getReservationMatching(r);
    }
}
