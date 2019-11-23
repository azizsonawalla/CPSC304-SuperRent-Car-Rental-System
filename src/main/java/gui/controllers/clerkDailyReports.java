package gui.controllers;

import gui.Config;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Entities.Location;
import model.Entities.Rental;
import model.Entities.Reservation;
import model.Orchestrator.RentalReport;
import model.Util.Log;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class clerkDailyReports extends Controller implements Initializable {

    private String RENTAL_REPORT_TEMPLATE =
            "RENTALS REPORT FOR TODAY:\n\n" +
            "Rentals Started Today (by branch, then by vehicle type):\n    %s\n\n" +
            "Rentals by Vehicle Type:\n    %s\n\n" +
            "Rentals by Branch:\n    %s\n\n" +
            "Total rentals started today = %d\n\n";
    private String RENTALS_STARTED_TODAY_TEMPLATE =
            "\nRental ID = %d, Vehicle License = %s, Customer License = %s, Start = %s, End = %s, Vehicle Type = %s, Location = %s";
    private String RENTALS_BY_VT_TEMPLATE = "%d %s rentals";
    private String RENTALS_BY_LOCATION_TEMPLATE = "%d rentals from %s";

    private Lock lock = new ReentrantLock();

    @FXML private TextFlow reportArea;
    @FXML private Button generateReport, backToReservations;
    @FXML private CheckBox rentalsBox, returnsBox;
    @FXML private ComboBox<String> branchSelector;

    public clerkDailyReports(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
        generateReport.setOnAction(event -> Platform.runLater(refreshReport));
        backToReservations.setOnAction(event -> Platform.runLater(goBackToReservations));
    }

    public void refreshAll() {
        rentalsBox.setSelected(true);
        Platform.runLater(fillBranchSelector);
        Platform.runLater(refreshReport);
    }

    // Tasks

    private Runnable refreshReport = () -> {
        reportArea.getChildren().clear();
        if (!rentalsBox.isSelected() && !returnsBox.isSelected()) {
            showError("Please select either rentals or returns to be included in the report");
            return;
        }
        if (rentalsBox.isSelected()) {
            // insert rentals report
            String selectedLocation = branchSelector.getValue();
            Location location = null;
            try {
                location = selectedLocation.equals(ALL_BRANCHES) ? null : Location.fromString(selectedLocation);
            } catch (Exception e) {
                showError("Failed to parse selected location. Please restart the application.");
            }
            RentalReport rentalReport = qo.getDailyRentalReport(location);

            // Generate all rental entries
            String rentalsStartedToday = "";
            for (Reservation res: rentalReport.rentalsStartedToday.keySet()) {
                Rental rental = rentalReport.rentalsStartedToday.get(res);
                String entry = String.format(RENTALS_STARTED_TODAY_TEMPLATE, rental.rid, rental.vlicense,
                                            rental.dlicense, rental.timePeriod.getStartAsTimeDateString(),
                                            rental.timePeriod.getEndAsTimeDateString(), res.vtName, res.location.toString());
                rentalsStartedToday = rentalsStartedToday.concat(entry);
            }
            String rentalsByVehicleType = "";
            String rentalsByLocation = "";
            String rentalsReport = String.format(RENTAL_REPORT_TEMPLATE, rentalsStartedToday, rentalsByVehicleType, rentalsByLocation, rentalReport.totalRentalsToday);
            reportArea.getChildren().add(new Text(rentalsReport));
        }

        if (returnsBox.isSelected()) {
            // insert returns report
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
}
