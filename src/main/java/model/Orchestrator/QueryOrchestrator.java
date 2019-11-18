package model.Orchestrator;

import model.Database.Database;
import model.Entities.*;

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
}
