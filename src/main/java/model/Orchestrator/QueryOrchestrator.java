package model.Orchestrator;

import model.Database.Database;
import model.Entities.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class serves as the intermediate layer between the UI controller and the Database interface class.
 * The class 'orchestrates' various SQL queries to build and return the data required by the UI
 */
public class QueryOrchestrator {

    private Database db;

    public void QueryOrchestrator() throws Exception {
        this.db = new Database();
    }

    public List<VTSearchResult> getVTSearchResultsFor(Location l, VehicleType vt, TimePeriod t) throws Exception {
        // TODO: Implement this
        throw new Exception("Not implemented");
    }

    public List<Vehicle> getVehiclesFor(VTSearchResult searchResult) throws Exception {
        // TODO: Implement this
        throw new Exception("Not implemented");
    }

    /**
     * Get customer with given driver's license number, or return null if not found
     */
    public Customer getCustomer(String dlNumber) throws Exception {
        // TODO: Implement this
        throw new Exception("Not implemented");
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
        ArrayList<Location> list = new ArrayList<Location>();
        list.add(new Location("Vancouver", "Downtown"));
        list.add(new Location("Calgary", "Downtown"));
        list.add(new Location("Vancouver", "UBC"));
        list.add(new Location("Toronto", "Brampton"));
        return list;
    }

    public List<VehicleType> getAllVehicleTypes() throws Exception {
        // TODO: Implement this
        // this is just placeholder code
        ArrayList<VehicleType> list = new ArrayList<VehicleType>();
        list.add(new VehicleType("SUV"));
        list.add(new VehicleType("Truck"));
        list.add(new VehicleType("Sedan"));
        list.add(new VehicleType("Hatchback"));
        return list;
    }
}
