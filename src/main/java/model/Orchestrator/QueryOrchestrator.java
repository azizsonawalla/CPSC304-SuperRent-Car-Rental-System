package model.Orchestrator;

import model.Database;
import model.Entities.*;

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

    private Database db;

    public void QueryOrchestrator() throws Exception {
        this.db = new Database();
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
    public Customer getCustomer(String dlNumber) throws Exception {
        // TODO: Implement this
        if (dlNumber.equals("DL12345")) return new Customer(12345, "DL12345", "John Smith", "Some address");
        return null;
    }

    public void addCustomer(Customer c) throws Exception {
        // TODO: Implement this
        throw new Exception("Not implemented");
    }

    public Reservation makeReservation(Reservation r) throws Exception {
        // TODO: Implement this
        throw new Exception("Not implemented");
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
}
