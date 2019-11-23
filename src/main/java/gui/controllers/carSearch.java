package gui.controllers;

import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Entities.*;
import model.Orchestrator.VTSearchResult;
import model.Util.Log;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class carSearch extends Controller implements Initializable {

    // TODO: Reformat string templates for better alignment
    // TODO: Add check to "see details" and "start reservation" buttons to ensure an option is selected
    // TODO: Check size of returned options before setting 1st value in combobox
    // TODO: Change default dates

    private String SEARCH_RESULT_TEMPLATE = "  Option %d:  Vehicle Type = %-30s  Location = %-10s, %-10s  Number Available = %d\n";
    private String RESULT_DETAILS_TEMPLATE = "  Make = %s,   Model = %s,  Year = %d,  Colour = %s,  License plate = %s";

    List<VTSearchResult> currentResults;
    Lock lock = new ReentrantLock();

    private @FXML TextFlow searchResults;
    private @FXML TextFlow searchResultDetails;
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

        Timestamp start = new Timestamp(startYearValue-1900, startMonthValue-1, startDateValue, startHourValue, startMinuteValue, 0, 0);
        Timestamp end = new Timestamp(endYearValue-1900, endMonthValue-1, endDateValue, endHourValue, endMinuteValue, 0, 0);

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

            for (ComboBox c: allDateTimeComboBox) c.setValue(c.getItems().get(0));

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
                Location l = getCurrentLocationSelection();
                VehicleType vt = getCurrentVTSelection();
                TimePeriod t = getCurrentTimePeriodSelection();
                currentResults = qo.getVTSearchResultsFor(l, vt, t);
                searchResults.getChildren().clear();
                if (currentResults.size() > 0) {
                    int count = 1;
                    for (VTSearchResult r : currentResults) {
                        String str = String.format(SEARCH_RESULT_TEMPLATE, count, r.vt.vtname,
                                r.location.location, r.location.city, r.numAvail);
                        Text text = new Text(str);
                        searchResults.getChildren().add(text);
                        count++;
                    }
                    seeDetailsForOption.getItems().clear();
                    startResWithOption.getItems().clear();
                    for (int i = 1; i <= currentResults.size(); i++) {
                        seeDetailsForOption.getItems().add(i);
                        startResWithOption.getItems().add(i);
                    }
                    seeDetailsForOption.setValue(1);
                    startResWithOption.setValue(1);
                } else {
                    searchResults.getChildren().add(new Text("No results matched your search..."));
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
            VTSearchResult correspondingOption = currentResults.get(optionSelected);
            List<Vehicle> vehicles = qo.getVehiclesFor(correspondingOption);
            searchResultDetails.getChildren().clear();
            if (vehicles.size() > 0) {
                for (Vehicle v : vehicles) {
                    String str = String.format(RESULT_DETAILS_TEMPLATE, v.make, v.model, v.year, v.color, v.vlicense);
                    Text text = new Text(str);
                    searchResultDetails.getChildren().add(text);
                }
            } else {
                searchResultDetails.getChildren().add(new Text("No vehicles in this category"));
            }
        } catch (Exception e) {
            Log.log("Error showing vehicle details: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    };

    protected Runnable startReservation;
}
