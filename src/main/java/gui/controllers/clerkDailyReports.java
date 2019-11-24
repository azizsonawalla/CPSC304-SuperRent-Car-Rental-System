package gui.controllers;

import gui.Config;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;
import model.Entities.*;
import model.Orchestrator.RentalReport;
import model.Orchestrator.ReturnReport;
import model.Util.Log;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class clerkDailyReports extends Controller implements Initializable {

    private Lock lock = new ReentrantLock();

    @FXML private TableView entries, byVT, byLocation;
    @FXML private Label entriesTitle, totalCount, totalRevenue;
    @FXML private Button generateReport, backToReservations;
    @FXML private RadioButton rentalsBox, returnsBox;
    @FXML private ComboBox<String> branchSelector;

    public clerkDailyReports(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
        generateReport.setOnAction(event -> Platform.runLater(refreshReport));
        backToReservations.setOnAction(event -> Platform.runLater(goBackToReservations));
        rentalsBox.setOnAction(event -> Platform.runLater(rentalsSelected));
        returnsBox.setOnAction(event -> Platform.runLater(returnsSelected));
    }

    public void refreshAll() {
        rentalsBox.setSelected(true);
        returnsBox.setSelected(false);
        Platform.runLater(fillBranchSelector);
        Platform.runLater(refreshReport);
    }

    // Tasks

    private Runnable rentalsSelected = () -> {
        if (rentalsBox.isSelected()) {
            returnsBox.setSelected(false);
        } else if (!returnsBox.isSelected()) {
            rentalsBox.setSelected(true);
        }
    };

    private Runnable returnsSelected = () -> {
        if (returnsBox.isSelected()) {
            rentalsBox.setSelected(false);
        } else if (!rentalsBox.isSelected()) {
            returnsBox.setSelected(true);
        }
    };

    private Runnable refreshReport = () -> {
        entries.getItems().clear();
        entries.getColumns().clear();
        byVT.getItems().clear();
        byVT.getColumns().clear();
        byLocation.getColumns().clear();
        byLocation.getItems().clear();

        // insert rentals entries
        String selectedLocation = branchSelector.getValue();
        Location location = null;
        try {
            location = selectedLocation.equals(ALL_BRANCHES) ? null : Location.fromString(selectedLocation);
        } catch (Exception e) {
            showError("Failed to parse selected location. Please restart the application.");
        }

        if (rentalsBox.isSelected()) {
            entriesTitle.setText("Rentals Started Today:");
            totalRevenue.setVisible(false);
            RentalReport rentalReport = qo.getDailyRentalReport(location);
            if (rentalReport.totalRentalsToday == 0) {
                entries.setPlaceholder(new Label("No rentals were started today"));
                return;
            }

            List<String> columnHeaders = Arrays.asList("Rental ID", "Vehicle License", "Customer License", "Rental Start", "Rental End", "Vehicle Type", "Pickup Location");
            List<String> propertyName = Arrays.asList("rid", "vlicense", "dlicense", "start", "end", "vtName", "location");
            for (int i = 0; i < columnHeaders.size(); i++) {
                TableColumn<String, RentalEntry> column = new TableColumn<>(columnHeaders.get(i));
                column.setCellValueFactory(new PropertyValueFactory<>(propertyName.get(i)));
                entries.getColumns().add(column);
            }

            // Generate all rental entries
            for (Reservation res: rentalReport.rentalsStartedToday.keySet()) {
                Rental rental = rentalReport.rentalsStartedToday.get(res);
                RentalEntry entry = new RentalEntry(String.valueOf(rental.rid), rental.vlicense, rental.dlicense,
                                                    rental.timePeriod.getStartAsTimeDateString(), rental.timePeriod.getEndAsTimeDateString(),
                                                    res.vtName, res.location.toString());
                entries.getItems().add(entry);
            }

            totalCount.setText(String.format("Total Rentals Today from %s = %d", selectedLocation, rentalReport.totalRentalsToday));

            // breakdown by VT
            List<String> columnHeaders2 = Arrays.asList("Vehicle Type", "Rentals Today");
            List<String> propertyName2 = Arrays.asList("vtName", "count");
            for (int i = 0; i < columnHeaders2.size(); i++) {
                TableColumn<String, Breakdown> column = new TableColumn<>(columnHeaders2.get(i));
                column.setCellValueFactory(new PropertyValueFactory<>(propertyName2.get(i)));
                byVT.getColumns().add(column);
            }

            for (VehicleType vt: rentalReport.countOfRentalsByVT.keySet()) {
                Integer count = rentalReport.countOfRentalsByVT.get(vt);
                Breakdown b = new Breakdown(vt.vtname, "", Integer.toString(count), "");
                byVT.getItems().add(b);
            }

            // breakdown by location
            List<String> columnHeaders3 = Arrays.asList("Location", "Rentals Today");
            List<String> propertyName3 = Arrays.asList("location", "count");
            for (int i = 0; i < columnHeaders3.size(); i++) {
                TableColumn<String, Breakdown> column = new TableColumn<>(columnHeaders3.get(i));
                column.setCellValueFactory(new PropertyValueFactory<>(propertyName3.get(i)));
                byLocation.getColumns().add(column);
            }

            for (Location l: rentalReport.countOfRentalsByLocation.keySet()) {
                Integer count = rentalReport.countOfRentalsByLocation.get(l);
                Breakdown b = new Breakdown("", l.toString(), Integer.toString(count), "");
                byLocation.getItems().add(b);
            }
            return;
        }

        if (returnsBox.isSelected()) {
            entriesTitle.setText("Returns Completed Today:");
            totalRevenue.setVisible(true);

            ReturnReport returnReport = qo.getDailyReturnReport(location);
            if (returnReport.totalReturnsToday == 0) {
                entries.setPlaceholder(new Label("No returns were completed today"));
                return;
            }

            List<String> columnHeaders = Arrays.asList("Return ID", "Vehicle License", "Customer License", "Return Time", "Vehicle Type", "Pickup Location");
            List<String> propertyName = Arrays.asList("rid", "vlicense", "dlicense", "returnTime", "vtName", "location");
            for (int i = 0; i < columnHeaders.size(); i++) {
                TableColumn<String, ReturnEntry> column = new TableColumn<>(columnHeaders.get(i));
                column.setCellValueFactory(new PropertyValueFactory<>(propertyName.get(i)));
                entries.getColumns().add(column);
            }

            // Generate all rental entries
            for (Rental rental: returnReport.returnsCreatedToday.keySet()) {
                Return r = returnReport.returnsCreatedToday.get(rental);
                Reservation res = null;
                try {
                    res = qo.getReservationsWith(rental.confNo, null).get(0);
                } catch (Exception e) {
                    showError("Error getting rental entries for today. Please restart the application");
                    return;
                }
                ReturnEntry entry = new ReturnEntry(String.valueOf(r.rid), TimePeriod.getTimestampAsTimeDateString(r.returnDateTime),
                                                    rental.dlicense, rental.vlicense, res.vtName, res.location.toString());
                entries.getItems().add(entry);
            }

            totalCount.setText(String.format("Total Returns Today at %s = %d", selectedLocation, returnReport.totalReturnsToday));
            totalRevenue.setText(String.format("Total Revenue Today from Returns at %s = $%.2f", selectedLocation, returnReport.totalReturnsRevenueToday));

            // breakdown by VT
            List<String> columnHeaders2 = Arrays.asList("Vehicle Type", "Rentals Today", "Revenue Today");
            List<String> propertyName2 = Arrays.asList("vtName", "count", "revenue");
            for (int i = 0; i < columnHeaders2.size(); i++) {
                TableColumn<String, Breakdown> column = new TableColumn<>(columnHeaders2.get(i));
                column.setCellValueFactory(new PropertyValueFactory<>(propertyName2.get(i)));
                byVT.getColumns().add(column);
            }

            for (VehicleType vt: returnReport.breakDownByVT.keySet()) {
                Pair<Integer, Double> countRevenue = returnReport.breakDownByVT.get(vt);
                Integer count = countRevenue.getKey();
                Double revenue = countRevenue.getValue();
                Breakdown b = new Breakdown(vt.vtname, "", Integer.toString(count), Double.toString(revenue));
                byVT.getItems().add(b);
            }

            // breakdown by location
            List<String> columnHeaders3 = Arrays.asList("Location", "Rentals Today", "Revenue Today");
            List<String> propertyName3 = Arrays.asList("location", "count", "revenue");
            for (int i = 0; i < columnHeaders3.size(); i++) {
                TableColumn<String, Breakdown> column = new TableColumn<>(columnHeaders3.get(i));
                column.setCellValueFactory(new PropertyValueFactory<>(propertyName3.get(i)));
                byLocation.getColumns().add(column);
            }

            for (Location l: returnReport.breakDownByLocation.keySet()) {
                Pair<Integer, Double> countRevenue = returnReport.breakDownByLocation.get(l);
                Integer count = countRevenue.getKey();
                Double revenue = countRevenue.getValue();
                Breakdown b = new Breakdown("", l.toString(), Integer.toString(count), Double.toString(revenue));
                byLocation.getItems().add(b);
            }
            return;
        }
    };

    private Runnable goBackToReservations = () -> {
        main.switchScene(Config.CLERK_RESERVATION_RENTAL_SEARCH);
    };

    private Runnable fillBranchSelector = () -> {
        try {
            lock.lock();
            branchSelector.getItems().clear();
            List<Location> branches = qo.getAllLocationNames();
            for (Location l : branches) {
                branchSelector.getItems().add(l.toString());
            }
            branchSelector.getItems().add(ALL_BRANCHES);
            branchSelector.setValue(ALL_BRANCHES);
        } catch (Exception e) {
            Log.log("Error refreshing branch list on customer car search screen: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    };

    // Models for table

    public class Breakdown {

        String location, vtName, count, revenue;

        public String getLocation() {
            return location;
        }

        public Breakdown(String vtName, String location, String count, String revenue) {
            this.vtName = vtName;
            this.location = location;
            this.count = count;
            this.revenue = "$"+revenue;
        }

        public String getVtName() {
            return vtName;
        }

        public String getCount() {
            return count;
        }

        public String getRevenue() {
            return revenue;
        }
    }

    public class ReturnEntry {

        String rid, returnTime, dlicense, vlicense, vtName, location;

        public ReturnEntry(String rid, String returnTime, String dlicense, String vlicense, String vtName, String location) {
            this.rid = rid;
            this.returnTime = returnTime;
            this.dlicense = dlicense;
            this.vlicense = vlicense;
            this.vtName = vtName;
            this.location = location;
        }

        public String getRid() {
            return rid;
        }

        public String getReturnTime() {
            return returnTime;
        }

        public String getDlicense() {
            return dlicense;
        }

        public String getVlicense() {
            return vlicense;
        }

        public String getVtName() {
            return vtName;
        }

        public String getLocation() {
            return location;
        }
    }

    public class RentalEntry {

        public RentalEntry(String rid, String vlicense, String dlicense, String start, String end, String vtName, String location) {
            this.rid = rid;
            this.vlicense = vlicense;
            this.dlicense = dlicense;
            this.start = start;
            this.end = end;
            this.vtName = vtName;
            this.location = location;
        }

        String rid, vlicense, dlicense, start, end, vtName, location;

        public String getRid() {
            return rid;
        }

        public String getVlicense() {
            return vlicense;
        }

        public String getDlicense() {
            return dlicense;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }

        public String getVtName() {
            return vtName;
        }

        public String getLocation() {
            return location;
        }
    }
}
