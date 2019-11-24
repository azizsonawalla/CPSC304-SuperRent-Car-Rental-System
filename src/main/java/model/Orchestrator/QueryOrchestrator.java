package model.Orchestrator;

import javafx.util.Pair;
import model.Database;
import model.Entities.*;

import java.sql.Timestamp;
import java.util.*;

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
    Timestamp ts = new Timestamp(2018-1900, 1-1, 1, 1, 1, 1, 1);
    TimePeriod t = new TimePeriod(ts, ts);

    private Database db;

    public QueryOrchestrator() throws Exception {
        this.db = new Database(false);
    }

    public Integer getDailyKMLimit() {
        return 100;
    }

    /**
     * Customer car search top table
     */
    public List<VTSearchResult> getVTSearchResultsFor(Location l, VehicleType vt, TimePeriod t) throws Exception {
        // TODO: Filter results with 0 available out
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
        // r param doesn't have a confirmation number
        // DB will have to auto-generate this number
        // Return new reservation object which contains the auto-generated conf number
        return db.addReservation(r);

    }

    public List<Location> getAllLocationNames() throws Exception {
        return db.getAllLocations();
    }

    public List<VehicleType> getAllVehicleTypes() throws Exception {
        return db.getAllVehicleTypes();
    }


    public List<Reservation> getReservationsWith(int confNum, String customerDL) throws Exception{
        // if confNum == -1, then don't filter by confNum
        // if customerDL == "", then don't filer by customerDL
        Timestamp now = TimePeriod.getNow();
        return db.getReservationsWithHelper(new TimePeriod(now, now),
                new Reservation(confNum, null, null, null, customerDL));
    }

    public List<Rental> getRentalsWith(int rentalId, String customerDL) throws Exception {
        // if rentalId == -1, then don't filter by confNum
        // if customerDL == "", then don't filer by customerDL
        Timestamp now = TimePeriod.getNow();
        return db.getRentalsWithHelper(new TimePeriod(now, now),
                new Rental(rentalId, null, customerDL, null, -1, null, -1));
    }

    /**
     * Given a reservation, select a vehicle from the database that is available now,
     * of the requested type, at the reservation location. If no such vehicle is available, return null.
     * @param selectedRes
     * @return
     */
    public Vehicle getAutoSelectedVehicle(Reservation selectedRes) throws Exception {
        List<Vehicle> vehicles = db.getVehicleWith(new VehicleType(selectedRes.vtName), selectedRes.location, Vehicle.VehicleStatus.AVAILABLE);
        if (vehicles.size() == 0) return null;
        else return vehicles.get(0);
    }

    public Rental addRental(Rental r) throws Exception{
        // DB will have to autogenerate primary key for r
        // return new rental object with the primary key in it
        return db.addRental(r);
    }

    public Vehicle getVehicle(String vlicense) throws Exception {
        return db.getVehicleMatching(new Vehicle(vlicense));
    }

    public VehicleType getVehicleType(String vtname) throws Exception{
        return db.getVehicleTypeMatching(new VehicleType(vtname));
    }

    public void submitReturn(Return r) throws Exception{
        db.addReturn(r);
        Vehicle v = db.getVehicleMatching(new Vehicle(db.getReturnedVehicle(r)));
        v.status = Vehicle.VehicleStatus.AVAILABLE;
        v.odometer = r.endOdometer;
        db.updateVehicle(v);
    }

    public RentalReport getDailyRentalReport(Location l) throws Exception {

        Timestamp now = TimePeriod.getNow();
        Timestamp todayMidnight = new Timestamp(now.getYear(), now.getMonth(), now.getDate(),0, 0, 0, 0);
        Timestamp today1159 = new Timestamp(now.getYear(), now.getMonth(), now.getDate(), 23, 59, 59, 0);
        TimePeriod today = new TimePeriod(todayMidnight, today1159);
        List<Rental> rentals = db.getRentalsStartedToday(today);
        List<Reservation> reservations = db.getReservationsWith(null, null, l);

        List<Pair<Reservation, Rental>> rentalsStartedToday = new ArrayList<>();

        for (Rental rental : rentals) {
            for (Reservation reservation: reservations) {
                if (rental.confNo == reservation.confNum){
                    rentalsStartedToday.add(new Pair<>(reservation, rental));
                }
            }
        }

        Collections.sort(rentalsStartedToday, (o1, o2) -> {
            //Compare the locations
            int locationComparison = o1.getKey().location.toString().compareTo(o2.getKey().location.toString());
            //If the locations are not the same, return the location comparison
            if (locationComparison != 0) return locationComparison;

            //If the locations are the same, compare by vehicle
            int vtTypeComparison = o1.getKey().vtName.compareTo(o2.getKey().vtName);
            return vtTypeComparison;
        });

        Map<String, Integer> countOfRentalsByVT = new HashMap<>();
        Map<Location, Integer> countOfRentalsByLocation = new HashMap<>();
        for (Pair<Reservation, Rental> RR : rentalsStartedToday){
            if (countOfRentalsByVT.get(RR.getKey().vtName) != null) {
                countOfRentalsByVT.put(RR.getKey().vtName , countOfRentalsByVT.get(RR.getKey().vtName) + 1);
            } else {
                countOfRentalsByVT.put(RR.getKey().vtName , 1);
            }

            if (countOfRentalsByLocation.get(new Location(RR.getKey().location.city, RR.getKey().location.location)) != null) {
                countOfRentalsByLocation.put(new Location(RR.getKey().location.city, RR.getKey().location.location),
                        countOfRentalsByLocation.get(new Location(RR.getKey().location.city, RR.getKey().location.location)) + 1);
            } else {
                countOfRentalsByLocation.put(new Location(RR.getKey().location.city, RR.getKey().location.location), 1);
            }
        }

        Integer totalRentalsToday = rentalsStartedToday.size();

        RentalReport report = new RentalReport();
        report.rentalsStartedToday = rentalsStartedToday;
        report.countOfRentalsByVT = countOfRentalsByVT;
        report.countOfRentalsByLocation = countOfRentalsByLocation;
        report.totalRentalsToday = totalRentalsToday;

        return report;
    }

    public ReturnReport getDailyReturnReport(Location l) throws Exception {

        Timestamp now = TimePeriod.getNow();
        Timestamp todayMidnight = new Timestamp(now.getYear(), now.getMonth(), now.getDate(),0, 0, 0, 0);
        Timestamp today1159 = new Timestamp(now.getYear(), now.getMonth(), now.getDate(), 23, 59, 59, 0);
        TimePeriod today = new TimePeriod(todayMidnight, today1159);
        List<Return> returns = db.getReturnsWith(today, null, null);
        List<Rental> rentals = db.getRentalsWith(null, null, null);
        List<Reservation> reservations = db.getReservationsWith(null, null, l);

        List<ReturnReportEntry> returnsCreatedToday = new ArrayList<>();

        for (Return ret : returns) {
            for (Reservation reservation: reservations) {
                for (Rental rental : rentals) {
                    if (ret.rid == rental.rid && rental.confNo == reservation.confNum) {
                        returnsCreatedToday.add(new ReturnReportEntry(rental, reservation, ret));
                    }
                }
            }
        }

        Collections.sort(returnsCreatedToday, (o1, o2) -> {
            //Compare the locations
            int locationComparison = o1.res.location.toString().compareTo(o2.res.location.toString());
            //If the locations are not the same, return the location comparison
            if (locationComparison != 0) return locationComparison;

            //If the locations are the same, compare by vehicle
            int vtTypeComparison = o1.res.vtName.compareTo(o2.res.vtName);
            return vtTypeComparison;
        });

        Map<String, Pair<Integer, Double>> breakDownByVT = new HashMap<>();
        Map<Location, Pair<Integer, Double>> breakDownByLocation = new HashMap<>();
        Double totalReturnsRevenueToday = (double)0;

        for (ReturnReportEntry rre: returnsCreatedToday) {
            String vtname = rre.res.vtName;
            Pair<Integer, Double> value = breakDownByVT.get(vtname);
            if (value != null) {
                Integer i = value.getKey() + 1;
                Double d = value.getValue() + rre.ret.cost;
                breakDownByVT.put(vtname, new Pair<>(i, d));
            } else {
                breakDownByVT.put(vtname, new Pair<>(1, rre.ret.cost));
            }

            Location location = new Location(rre.res.location.city, rre.res.location.location);
            Pair<Integer, Double> value1 = breakDownByLocation.get(location);
            if (value1 != null) {
                Integer i = value.getKey() + 1;
                Double d = value.getValue() + rre.ret.cost;
                breakDownByLocation.put(location, new Pair<>(i, d));
            } else {
                breakDownByLocation.put(location, new Pair<>(1, rre.ret.cost));
            }

            totalReturnsRevenueToday += rre.ret.cost;
        }

        Integer totalReturnsToday = returnsCreatedToday.size();

        ReturnReport report = new ReturnReport();
        report.returnsCreatedToday = returnsCreatedToday;
        report.breakDownByVT = breakDownByVT;
        report.breakDownByLocation = breakDownByLocation;
        report.totalReturnsToday = totalReturnsToday;
        report.totalReturnsRevenueToday = totalReturnsRevenueToday;

        return report;
    }
}
