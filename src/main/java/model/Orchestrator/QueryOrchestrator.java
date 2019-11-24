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
        this.db = new Database();
    }

    public Integer getDailyKMLimit() {
        return 100;
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
        // TODO: Implement this
        // r param doesn't have a confirmation number
        // DB will have to auto-generate this number
        // Return new reservation object which contains the auto-generated conf number
        // db.addReservation(r);
        return r;
    }

    public List<Location> getAllLocationNames() throws Exception {
        // ArrayList<Location> list = new ArrayList<Location>(Arrays.asList(L1, L2, L3, L4));
        return db.getAllLocations();
    }

    public List<VehicleType> getAllVehicleTypes() throws Exception {
        return db.getAllVehicleTypes();
    }


    public List<Reservation> getReservationsWith(int confNum, String customerDL) throws Exception{
        // if confNum == -1, then don't filter by confNum
        // if customerDL == "", then don't filer by customerDL
        return db.getReservationsWithHelper(new TimePeriod(TimePeriod.getNow(), TimePeriod.getNow()),
                new Reservation(confNum, null, null, null, customerDL));
    }

    public List<Rental> getRentalsWith(int rentalId, String customerDL) throws Exception {
        // if rentalId == -1, then don't filter by confNum
        // if customerDL == "", then don't filer by customerDL
        return db.getRentalsWithHelper(new TimePeriod(TimePeriod.getNow(), TimePeriod.getNow()),
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

    public Rental addRental(Rental r) {
        // TODO;
        // DB will have to autogenerate primary key for r
        // return new rental object with the primary key in it
        return r;
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
        //region Sample Data
        //        RentalReport report = new RentalReport();
        //        report.countOfRentalsByLocation = new HashMap<>();
        //        report.countOfRentalsByLocation.put(L1, 0);
        //        report.countOfRentalsByLocation.put(L2, 5);
        //        report.countOfRentalsByVT = new HashMap<>();
        //        report.countOfRentalsByVT.put(VT1, 1);
        //        report.countOfRentalsByVT.put(VT2, 3);
        //        report.rentalsStartedToday = new HashMap<>();
        //        report.rentalsStartedToday.put(new Reservation(1, "dummyvt", t, L1, "dummyDL" ),
        //                new Rental(1, "dummyplates", "dummyDL", t, 0, null, 1));
        //        report.totalRentalsToday = 1;
        //endregion

        Timestamp now = TimePeriod.getNow();
        Timestamp todayMidnight = new Timestamp(now.getYear(), now.getMonth(), now.getDate(),0, 0, 0, 0);
        Timestamp today1159 = new Timestamp(now.getYear(), now.getMonth(), now.getDate(), 23, 59, 59, 0);
        TimePeriod today = new TimePeriod(todayMidnight, today1159);
        List<Rental> rentals = db.getRentalsStartedToday(today);
        List<Reservation> reservations = db.getReservationsWith(null, null, null);

        List<Pair<Reservation, Rental>> rentalsStartedToday = new ArrayList<>();

        for (Rental rental : rentals) {
            for (Reservation reservation: reservations) {
                if (rental.confNo == reservation.confNum){
                    rentalsStartedToday.add(new Pair<>(reservation, rental));
                }
            }
        }

        Collections.sort(rentalsStartedToday, new Comparator<Pair<Reservation, Rental>>() {
            @Override
            public int compare(Pair<Reservation, Rental> o1, Pair<Reservation, Rental> o2) {
                //Compare the locations
                int locationComparison = o1.getKey().location.toString().compareTo(o2.getKey().location.toString());
                //If the locations are not the same, return the location comparison
                if (locationComparison != 0) return locationComparison;

                //If the locations are the same, compare by vehicle
                int vtTypeComparison = o1.getKey().vtName.compareTo(o2.getKey().vtName);
                return vtTypeComparison;
            }
        });

        Map<String, Integer> countOfRentalsByVT = new HashMap<>();
        Map<Location, Integer> countOfRentalsByLocation = new HashMap<>();
        for (Pair<Reservation, Rental> RR : rentalsStartedToday){
            if (countOfRentalsByVT.get(RR.getKey().vtName) != null) {
                countOfRentalsByVT.put(RR.getKey().vtName , countOfRentalsByVT.get(RR.getKey().vtName) + 1);
            } else {
                countOfRentalsByVT.put(RR.getKey().vtName , 0);
            }

            if (countOfRentalsByLocation.get(new Location(RR.getKey().location.city, RR.getKey().location.location)) != null) {
                countOfRentalsByLocation.put(new Location(RR.getKey().location.city, RR.getKey().location.location),
                        countOfRentalsByLocation.get(new Location(RR.getKey().location.city, RR.getKey().location.location)) + 1);
            } else {
                countOfRentalsByLocation.put(new Location(RR.getKey().location.city, RR.getKey().location.location), 0);
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

    public ReturnReport getDailyReturnReport(Location l) {
        // TODO
        //region Sample Data
//        ReturnReport report = new ReturnReport();
//        report.breakDownByLocation = new HashMap<>();
//        report.breakDownByLocation.put(L1, new Pair<>(0, 0.0));
//        report.breakDownByLocation.put(L3, new Pair<>(5, 100.6));
//        report.breakDownByVT = new HashMap<>();
//        report.breakDownByVT.put(VT1, new Pair<>(1, 0.0));
//        report.breakDownByVT.put(VT2, new Pair<>(4, 5.0));
//        report.returnsCreatedToday = new HashMap<>();
//        report.returnsCreatedToday.put( new Rental(1, "dummyplates", "dummyDL", t, 0, null, 1),
//                                        new Return(1, new Timestamp(System.currentTimeMillis()), 1, Return.TankStatus.FULL_TANK, 566));
//        report.totalReturnsRevenueToday = 200.5;
//        report.totalReturnsToday = 1;
        //endregion

        Timestamp now = TimePeriod.getNow();
        Timestamp todayMidnight = new Timestamp(now.getYear(), now.getMonth(), now.getDate(),0, 0, 0, 0);
        Timestamp today1159 = new Timestamp(now.getYear(), now.getMonth(), now.getDate(), 23, 59, 59, 0);
        TimePeriod today = new TimePeriod(todayMidnight, today1159);



        ReturnReport report = new ReturnReport();

        return report;
    }
}
