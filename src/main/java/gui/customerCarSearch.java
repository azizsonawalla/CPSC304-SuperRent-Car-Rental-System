package gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Entities.Location;
import model.Entities.TimePeriod;
import model.Entities.Vehicle;
import model.Entities.VehicleType;
import model.Orchestrator.VTSearchResult;
import model.Util.Log;

import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class customerCarSearch extends Controller implements Initializable {

    private String SEARCH_RESULT_TEMPLATE = "  Option %d:   Vehicle Type = %s,     Location = %s, %s     Number Available = %d\n";
    private String RESULT_DETAILS_TEMPLATE = "  Make = %s,   Model = %s,  Year = %d,  Colour = %s,  License plate = %s";
    private List<VTSearchResult> currentResults;
    private Lock lock = new ReentrantLock();

    private @FXML TextFlow searchResults;
    private @FXML TextFlow searchResultDetails;

    private @FXML ComboBox<String> branchSelector;
    private @FXML ComboBox<String> vtSelector;
    private @FXML ComboBox<Integer> startResWithOption;
    private @FXML ComboBox<Integer> seeDetailsForOption;

    private @FXML ComboBox<Integer> startDate;
    private @FXML ComboBox<Integer> endDate;
    private @FXML ComboBox<Integer> startMonth;
    private @FXML ComboBox<Integer> endMonth;
    private @FXML ComboBox<Integer> startYear;
    private @FXML ComboBox<Integer> endYear;
    private @FXML ComboBox<Integer> startHour;
    private @FXML ComboBox<Integer> startMinute;
    private @FXML ComboBox<String> startAM;
    private @FXML ComboBox<Integer> endHour;
    private @FXML ComboBox<Integer> endMinute;
    private @FXML ComboBox<String> endAM;

    private @FXML Button searchButton;
    private @FXML Button startReservationButton;

    // Gets all locations from DB and puts it in branch selector
    private Runnable refreshBranchList = () -> {
        try {
            lock.lock();
            List<Location> branches = qo.getAllLocationNames();
            for (Location l : branches) {
                String locationName = String.format("%s, %s", l.location, l.city);
                branchSelector.getItems().add(locationName);
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
            List<Integer> DATES = new ArrayList<>();
            for (int i = 1; i <= 31; i++) DATES.add(i);
            List<Integer> MONTHS = new ArrayList<>();
            for (int i = 1; i <= 12; i++) MONTHS.add(i);
            List<Integer> YEARS = new ArrayList<>();
            for (int i = 2019; i <= 2025; i++) YEARS.add(i);
            List<Integer> HOURS = new ArrayList<>();
            for (int i = 1; i <= 12; i++) HOURS.add(i);
            List<Integer> MINUTES = new ArrayList<>();
            for (int i = 0; i <= 55; i++) MINUTES.add(i);
            List<String> AMPM = Arrays.asList("AM", "PM");

            startDate.getItems().addAll(DATES);
            startDate.setValue(startDate.getItems().get(0));

            endDate.getItems().addAll(DATES);
            endDate.setValue(endDate.getItems().get(0));

            startMonth.getItems().addAll(MONTHS);
            startMonth.setValue(startMonth.getItems().get(0));

            endMonth.getItems().addAll(MONTHS);
            endMonth.setValue(endMonth.getItems().get(0));

            startYear.getItems().addAll(YEARS);
            startYear.setValue(startYear.getItems().get(0));

            endYear.getItems().addAll(YEARS);
            endYear.setValue(endYear.getItems().get(0));

            startHour.getItems().addAll(HOURS);
            startHour.setValue(startHour.getItems().get(0));

            startMinute.getItems().addAll(MINUTES);
            startMinute.setValue(startMinute.getItems().get(0));

            startAM.getItems().addAll(AMPM);
            startAM.setValue(startAM.getItems().get(0));

            endHour.getItems().addAll(HOURS);
            endHour.setValue(endHour.getItems().get(0));

            endMinute.getItems().addAll(MINUTES);
            endMinute.setValue(endMinute.getItems().get(0));

            endAM.getItems().addAll(AMPM);
            endAM.setValue(endAM.getItems().get(0));
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
            int optionSelected = seeDetailsForOption.getValue();
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

    private Runnable startReservation = () -> {
        try {
            lock.lock();
            int optionSelected = seeDetailsForOption.getValue();
            this.main.customerStartRes = currentResults.get(optionSelected);
            this.main.setRoot(GUIConfig.CUSTOMER_MAKE_RES);
        } catch (Exception e) {
            Log.log("Error starting reservation: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    };

    public customerCarSearch(Main main) {
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
        this.main.pool.execute(resetTimePeriod);
        // Get all branch values and put them as options in Select Branch
        this.main.pool.execute(refreshBranchList);
        // Get all vehicle types and put them as options in Select Vehicle Type
        this.main.pool.execute(refreshVehicleTypeList);
        // Update VT Search Result table with selection
        Platform.runLater(refreshVehicleTypeSearchResultTable);
    }

    private Location getCurrentLocationSelection() {
        String value = branchSelector.getValue();
        if (value.equals(ALL_BRANCHES)) return null;
        String city = value.trim().split(",")[1];
        String location = value.trim().split(",")[0];
        return new Location(city, location);
    }

    private VehicleType getCurrentVTSelection() {
        String value = vtSelector.getValue();
        if (value.equals(ALL_BRANCHES)) return null;
        return new VehicleType(value.trim(), null, -1, -1, -1, -1, -1, -1, -1);
    }

    private TimePeriod getCurrentTimePeriodSelection() {
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

        Timestamp start = new Timestamp(startYearValue, startMonthValue, startDateValue, startHourValue, startMinuteValue, 0, 0);
        Timestamp end = new Timestamp(endYearValue, endMonthValue, endDateValue, endHourValue, endMinuteValue, 0, 0);

        return new TimePeriod(start, end);
    }
}
