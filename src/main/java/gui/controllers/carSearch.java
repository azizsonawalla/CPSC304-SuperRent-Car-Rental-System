package gui.controllers;

import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import model.Entities.*;
import model.Orchestrator.VTSearchResult;
import model.Util.Log;

import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class carSearch extends Controller implements Initializable {

    List<VTSearchResult> currentResults;
    Lock lock = new ReentrantLock();

    @FXML TableView<SearchResult> searchResults;
    private @FXML TableView searchResultDetails;
    private @FXML Button searchButton, startReservationButton;
    protected @FXML ComboBox<String> branchSelector, vtSelector;
    protected @FXML ComboBox<Integer> startDate, endDate, startMonth,
            endMonth, startYear, endYear, startHour, startMinute, endHour, endMinute;

    public carSearch(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
        searchButton.setOnAction(event -> Platform.runLater(refreshVehicleTypeSearchResultTable));
        searchResults.getSelectionModel().selectedItemProperty().addListener(event -> Platform.runLater(showVehicleDetails));
        startReservationButton.setOnAction(event -> Platform.runLater(startReservation));
    }

    public void refreshAll() {
        // Reset time period
        Platform.runLater(resetTimePeriod);
        // Get all branch values and put them as options in Select Branch
        Platform.runLater(refreshBranchList);
        // Get all vehicle types and put them as options in Select Vehicle Type
        Platform.runLater(refreshVehicleTypeList);
        // Update VT Search Result table with selection
        Platform.runLater(refreshVehicleTypeSearchResultTable);
        searchResultDetails.setPlaceholder(new Text("Loading..."));
        searchResults.setPlaceholder(new Text("Loading..."));
    }

    private Location getCurrentLocationSelection() throws Exception {
        String value = branchSelector.getValue();
        if (value.equals(ALL_BRANCHES)) return null;
        return Location.fromString(value);
    }

    private VehicleType getCurrentVTSelection() {
        String value = vtSelector.getValue();
        if (value.equals(ALL_VT)) return null;
        return new VehicleType(value.trim(), null, -1, -1, -1, -1, -1, -1, -1);
    }

    protected TimePeriod getCurrentTimePeriodSelection() {
        Timestamp start = getCurrentStartTimeSelection();
        Timestamp end = getCurrentEndTimeSelection();

        Log.log("start = " + start.toString());
        Log.log("end = " + end.toString());

        if (start.getTime() > end.getTime()) {
            showError("Start time must be before end time");
            return null;
        } else if (start.getTime() < System.currentTimeMillis() || end.getTime() < System.currentTimeMillis()) {
            showError("Please select a reservation time in the future. Cannot back-date reservation searches");
            return null;
        }

        return new TimePeriod(start, end);
    }

    private Timestamp getCurrentEndTimeSelection() {
        Integer endDateValue = endDate.getValue();
        Integer endMonthValue = endMonth.getValue();
        Integer endYearValue = endYear.getValue();
        Integer endHourValue = endHour.getValue();
        Integer endMinuteValue = endMinute.getValue();

        if ((endDateValue == 31 && !Arrays.asList(1, 3, 5, 7, 8, 10, 12).contains(endMonthValue))
                || (endDateValue > 28 && endMonthValue == 2)) {
            showError("Invalid month and date combination for end date. Please change selection.");
        }

        return new Timestamp(endYearValue-1900, endMonthValue-1, endDateValue, endHourValue, endMinuteValue, 0, 0);
    }

    Timestamp getCurrentStartTimeSelection() {
        Integer startDateValue = startDate.getValue();
        Integer startMonthValue = startMonth.getValue();
        Integer startYearValue = startYear.getValue();
        Integer startHourValue = startHour.getValue();
        Integer startMinuteValue = startMinute.getValue();

        if ((startDateValue == 31 && !Arrays.asList(1, 3, 5, 7, 8, 10, 12).contains(startMonthValue))
                || (startDateValue > 28 && startMonthValue == 2)) {
            showError("Invalid month and date combination for start date. Please change selection.");
        }

        return new Timestamp(startYearValue-1900, startMonthValue-1, startDateValue, startHourValue, startMinuteValue, 0, 0);
    }

    // Gets all locations from DB and puts it in branch selector
    private Runnable refreshBranchList = () -> {
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
            showError("Failed to get list of all branches. Please restart the application.");
            Log.log("Error refreshing branch list on customer car search screen: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    };

    // Gets all vehicle types from DB and puts it in vt selector
    private Runnable refreshVehicleTypeList = () -> {
        try {
            lock.lock();
            vtSelector.getItems().clear();
            List<VehicleType> vehicleTypes = qo.getAllVehicleTypes();
            for (VehicleType vt : vehicleTypes) {
                vtSelector.getItems().add(vt.vtname);
            }
            vtSelector.getItems().add(ALL_VT);
            vtSelector.setValue(ALL_VT);
        } catch (Exception e) {
            showError("Failed to get list of all vehicle types. Please restart the application.");
            Log.log("Error refreshing vt list on customer car search screen: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    };

    // Sets default time period values
    Runnable resetTimePeriod = () -> {
        try {
            lock.lock();
            List<ComboBox> allDateTimeComboBox = Arrays.asList(startDate, endDate, startMonth, endMonth,
                                                                startYear, endYear, startHour, endHour,
                                                                startMinute, endMinute);
            List<ComboBox> allTimeComboBox = Arrays.asList(startHour, startMinute, endHour, endMinute);

            for (ComboBox c: allDateTimeComboBox) c.getItems().clear();

            startDate.getItems().addAll(DATES);
            endDate.getItems().addAll(DATES);
            startMonth.getItems().addAll(MONTHS);
            endMonth.getItems().addAll(MONTHS);
            startYear.getItems().addAll(YEARS);
            endYear.getItems().addAll(YEARS);
            startHour.getItems().addAll(HOURS);
            startMinute.getItems().addAll(MINUTES);
            endHour.getItems().addAll(HOURS);
            endMinute.getItems().addAll(MINUTES);

            for (ComboBox c: allTimeComboBox) c.setValue(c.getItems().get(0));
            Date tomorrow = new Date(System.currentTimeMillis() + 24*60*60*1000);
            Date nextWeek = new Date((long)(tomorrow.getTime() + 6*Math.pow(10,8)));
            startDate.setValue(tomorrow.getDate());
            endDate.setValue(nextWeek.getDate());
            startMonth.setValue(tomorrow.getMonth()+1);
            endMonth.setValue(nextWeek.getMonth()+1);
            startYear.setValue(tomorrow.getYear() + 1900);
            endYear.setValue(nextWeek.getYear() + 1900);

        } catch (Exception e) {
            showError("Failed reset time selection. Please restart the application.");
            Log.log("Error resetting time period: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    };

    // Gets all vt search results and put it in table
    private Runnable refreshVehicleTypeSearchResultTable = new Runnable() {
        @Override
        public void run() {
            try {
                lock.lock();
                searchResults.getItems().clear();
                searchResults.getColumns().clear();

                Location l = getCurrentLocationSelection();
                VehicleType vt = getCurrentVTSelection();
                TimePeriod t = getCurrentTimePeriodSelection();
                currentResults = t == null ? new ArrayList<>() : qo.getVTSearchResultsFor(l, vt, t);

                if (currentResults.size() > 0) {
                    List<String> columnHeaders = Arrays.asList("Option No.", "Vehicle Type", "Features", "Current Location", "No. Avail.", "$/week", "$/day", "$/hr", "$/km", "$ ins./week", "$ ins./day", "$ ins./hour");
                    List<String> propertyName = Arrays.asList("option", "vtName", "features", "location", "numAvail", "wrate", "drate", "hrate", "krate", "wirate", "dirate", "hirate");
                    for (int i = 0; i < columnHeaders.size(); i++) {
                        TableColumn<SearchResult, String> column = new TableColumn<>(columnHeaders.get(i));
                        column.setCellValueFactory(new PropertyValueFactory<>(propertyName.get(i)));
                        searchResults.getColumns().add(column);
                    }

                    // Add items
                    int count = 1;
                    for (VTSearchResult r: currentResults) {
                        SearchResult entry = new SearchResult(count, r.vt, r.location.toString(), r.numAvail, r);
                        searchResults.getItems().add(entry);
                        count++;
                    }
                    Platform.runLater(showVehicleDetails);
                } else {
                    searchResults.setPlaceholder(new Label("No results matched your search"));
                }
            } catch (Exception e) {
                showError("Error refreshing search results in table: " + e.getMessage());
                Log.log("Error refreshing search results in table: " + e.getMessage());
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    };

    // Gets all vt search results and put it in table
    private Runnable showVehicleDetails = () -> {
        try {
            lock.lock();
            searchResultDetails.getColumns().clear();
            searchResultDetails.getItems().clear();

            SearchResult sr = searchResults.getSelectionModel().getSelectedItem();
            if (sr == null) {
                searchResultDetails.setPlaceholder(new Text("Select option from above to see details"));
                return;
            }
            List<Vehicle> vehicles = qo.getVehiclesFor(sr.vtResult);
            searchResultDetails.getItems().clear();
            if (vehicles.size() > 0) {
                List<String> columnHeaders = Arrays.asList("Make", "Model", "Type", "Colour", "Year", "License Plate", "Current Location");
                List<String> propertyName = Arrays.asList("make", "model", "vtname", "color", "year", "vlicense", "location");
                for (int i = 0; i < columnHeaders.size(); i++) {
                    TableColumn<String, Vehicle> column = new TableColumn<>(columnHeaders.get(i));
                    column.setCellValueFactory(new PropertyValueFactory<>(propertyName.get(i)));
                    searchResultDetails.getColumns().add(column);
                }
                // Add items
                for (Vehicle v : vehicles) {
                    searchResultDetails.getItems().add(v);
                }
            } else {
                searchResultDetails.setPlaceholder(new Text("No vehicles in this category"));
            }
        } catch (Exception e) {
            showError("Error refreshing vehicle details: " + e.getMessage());
            Log.log("Error showing vehicle details: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    };

    protected Runnable startReservation;

    // Table models

    public class SearchResult {
        String option;
        VehicleType vt;
        String location;
        String numAvail;
        VTSearchResult vtResult;

        public SearchResult(int option, VehicleType vt, String location, int numAvail, VTSearchResult vtResult) {
            this.option = String.valueOf(option);
            this.vt = vt;
            this.location = location;
            this.numAvail = String.valueOf(numAvail);
            this.vtResult = vtResult;
        }

        public String getOption() {
            return option;
        }

        public String getVtName() {
            return vt.vtname;
        }

        public String getLocation() {
            return location;
        }

        public String getNumAvail() {
            return numAvail;
        }

        public String getFeatures() {
            return vt.features;
        }

        public String getWrate() {
            return "$" + vt.wrate;
        }

        public String getWirate() {
            return "$" + vt.wirate;
        }

        public String getHrate() {
            return "$" + vt.hrate;
        }

        public String getHirate() {
            return "$" + vt.hirate;
        }

        public String getDrate() {
            return "$" + vt.drate;
        }

        public String getDirate() {
            return "$" + vt.dirate;
        }

        public String getKrate() {
            return "$" + vt.krate;
        }
    }
}
