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

    private @FXML TableView searchResults, searchResultDetails;
    private @FXML Button searchButton, startReservationButton;
    private @FXML ComboBox<String> branchSelector, vtSelector, startAM, endAM;
    protected @FXML ComboBox<Integer> startResWithOption, seeDetailsForOption, startDate, endDate, startMonth,
            endMonth, startYear, endYear, startHour, startMinute, endHour, endMinute;

    public carSearch(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
        searchButton.setOnAction(event -> Platform.runLater(refreshVehicleTypeSearchResultTable));
        seeDetailsForOption.setOnAction(event -> Platform.runLater(showVehicleDetails));
        startReservationButton.setOnAction(event -> Platform.runLater(startReservation));


        List<String> columnHeaders = Arrays.asList("Option No.", "Vehicle Type", "Current Location", "Number Available");
        List<String> propertyName = Arrays.asList("option", "vtName", "location", "numAvail");
        for (int i = 0; i < columnHeaders.size(); i++) {
            TableColumn<String, SearchResult> column = new TableColumn<>(columnHeaders.get(i));
            column.setCellValueFactory(new PropertyValueFactory<>(propertyName.get(i)));
            searchResults.getColumns().add(column);
        }

        columnHeaders = Arrays.asList("Make", "Model", "Type", "Colour", "Year", "License Plate", "Current Location");
        propertyName = Arrays.asList("make", "model", "vtName", "color", "year", "vlicense", "location");
        for (int i = 0; i < columnHeaders.size(); i++) {
            TableColumn<String, Vehicle> column = new TableColumn<>(columnHeaders.get(i));
            column.setCellValueFactory(new PropertyValueFactory<>(propertyName.get(i)));
            searchResultDetails.getColumns().add(column);
        }
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
        searchResultDetails.setPlaceholder(new Text("No vehicles in this category"));
        searchResults.setPlaceholder(new Text("No vehicles in this category"));
    }

    private Location getCurrentLocationSelection() throws Exception {
        String value = branchSelector.getValue();
        if (value.equals(ALL_BRANCHES)) return null;
        return Location.fromString(value);
    }

    private VehicleType getCurrentVTSelection() {
        String value = vtSelector.getValue();
        if (value.equals(ALL_BRANCHES)) return null;
        return new VehicleType(value.trim(), null, -1, -1, -1, -1, -1, -1, -1);
    }

    protected TimePeriod getCurrentTimePeriodSelection() {
        Integer startDateValue = startDate.getValue();
        Integer endDateValue = endDate.getValue();
        Integer startMonthValue = startMonth.getValue();
        Integer endMonthValue = endMonth.getValue();
        Integer startYearValue = startYear.getValue();
        Integer endYearValue = endYear.getValue();
        Integer startHourValue = startHour.getValue();
        Integer startMinuteValue = startMinute.getValue();
        String startAmValue = startAM.getValue();
        Integer endHourValue = endHour.getValue();
        Integer endMinuteValue = endMinute.getValue();
        String endAmValue = endAM.getValue();

        if (startAmValue.equals("PM")) startHourValue = (startHourValue + 12) % 24;
        if (endAmValue.equals("PM")) endHourValue += (endHourValue + 12) % 24;

        if ((startDateValue == 31 && !Arrays.asList(1, 3, 5, 7, 8, 10, 12).contains(startMonthValue))
            || (startDateValue > 28 && startMonthValue == 2)) {
            showError("Invalid month and date combination");
        }
        if ((endDateValue == 31 && !Arrays.asList(1, 3, 5, 7, 8, 10, 12).contains(endMonthValue))
                || (endDateValue > 28 && endMonthValue == 2)) {
            showError("Invalid month and date combination");
        }

        Timestamp start = new Timestamp(startYearValue-1900, startMonthValue-1, startDateValue, startHourValue, startMinuteValue, 0, 0);
        Timestamp end = new Timestamp(endYearValue-1900, endMonthValue-1, endDateValue, endHourValue, endMinuteValue, 0, 0);

        if (start.getTime() > end.getTime()) {
            showError("Start time must be before end time");
            return null;
        } else if (start.getTime() < System.currentTimeMillis() || end.getTime() < System.currentTimeMillis()) {
            showError("Please select a reservation time in the future. Cannot back-date reservation searches");
            return null;
        }

        return new TimePeriod(start, end);
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
            Log.log("Error refreshing vt list on customer car search screen: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    };

    // Sets default time period values
    private Runnable resetTimePeriod = () -> {
        try {
            lock.lock();
            List<ComboBox> allDateTimeComboBox = Arrays.asList(startDate, endDate, startMonth, endMonth,
                                                                startYear, endYear, startHour, endHour,
                                                                startMinute, endMinute, startAM, endAM);
            List<ComboBox> allTimeComboBox = Arrays.asList(startHour, startMinute, endHour, endMinute, startAM, endAM);

            for (ComboBox c: allDateTimeComboBox) c.getItems().clear();

            startDate.getItems().addAll(DATES);
            endDate.getItems().addAll(DATES);
            startMonth.getItems().addAll(MONTHS);
            endMonth.getItems().addAll(MONTHS);
            startYear.getItems().addAll(YEARS);
            endYear.getItems().addAll(YEARS);
            startHour.getItems().addAll(HOURS);
            startMinute.getItems().addAll(MINUTES);
            startAM.getItems().addAll(AMPM);
            endHour.getItems().addAll(HOURS);
            endMinute.getItems().addAll(MINUTES);
            endAM.getItems().addAll(AMPM);

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
                searchResultDetails.getItems().clear();

                startResWithOption.getItems().clear();
                seeDetailsForOption.getItems().clear();

                Location l = getCurrentLocationSelection();
                VehicleType vt = getCurrentVTSelection();
                TimePeriod t = getCurrentTimePeriodSelection();
                currentResults = t == null ? new ArrayList<>() : qo.getVTSearchResultsFor(l, vt, t);

                if (currentResults.size() > 0) {
                    // Add items
                    int count = 1;
                    for (VTSearchResult r: currentResults) {
                        SearchResult entry = new SearchResult(String.valueOf(count), r.vt.vtname,
                                                                r.location.toString(), String.valueOf(r.numAvail));
                        searchResults.getItems().add(entry);
                        count++;
                    }
                    for (int i = 1; i <= currentResults.size(); i++) {
                        seeDetailsForOption.getItems().add(i);
                        startResWithOption.getItems().add(i);
                    }
                    seeDetailsForOption.setValue(1);
                    startResWithOption.setValue(1);
                    Platform.runLater(showVehicleDetails);
                } else {
                    searchResults.setPlaceholder(new Label("No results matched your search"));
                }
            } catch (Exception e) {
                Log.log("Error refreshing search results in table: " + e.getMessage());
            } finally {
                lock.unlock();
            }
        }
    };

    // Gets all vt search results and put it in table
    private Runnable showVehicleDetails = () -> {
        try {
            lock.lock();
            int optionSelected = seeDetailsForOption.getValue() - 1;
            if (optionSelected < 0) {
                showError("There is no option selected to show details for");
                return;
            }
            VTSearchResult correspondingOption = currentResults.get(optionSelected);
            List<Vehicle> vehicles = qo.getVehiclesFor(correspondingOption);
            searchResultDetails.getItems().clear();
            if (vehicles.size() > 0) {
                // Add items
                for (Vehicle v : vehicles) {
                    searchResultDetails.getItems().add(v);
                }
            } else {
                searchResultDetails.setPlaceholder(new Text("No vehicles in this category"));
            }
        } catch (Exception e) {
            Log.log("Error showing vehicle details: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    };

    protected Runnable startReservation;

    // Table models

    public class SearchResult {
        String option, vtName, location, numAvail;

        public SearchResult(String option, String vtName, String location, String numAvail) {
            this.option = option;
            this.vtName = vtName;
            this.location = location;
            this.numAvail = numAvail;
        }

        public String getOption() {
            return option;
        }

        public String getVtName() {
            return vtName;
        }

        public String getLocation() {
            return location;
        }

        public String getNumAvail() {
            return numAvail;
        }
    }
}
