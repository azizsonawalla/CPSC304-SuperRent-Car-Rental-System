package gui.controllers;

import gui.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import model.Util.Log;

import java.net.URL;
import java.util.ResourceBundle;

public class clerkReservationRentalSearch extends Controller implements Initializable {

    @FXML private Button dailyReportsButton;

    public clerkReservationRentalSearch(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
    }

    public void refreshAll() {
        // TODO;
    }

    // Tasks

    private Runnable goToDailyReports = () -> {
        // TODO: Implement this
        Log.log("Switching to daily reports screen");
    };

    private Runnable refreshActiveReservations = () -> {
        // TODO: Implement this
        Log.log("Refreshing active reservations");
    };

    private Runnable refreshOngoingRentals = () -> {
        // TODO: Implement this
        Log.log("Refreshing ongoing rentals");
    };

    private Runnable startRentalWithoutReservation = () -> {
        // TODO: Implement this
        Log.log("Starting rental without reservation");
    };

    private Runnable startRentalFromReservation = () -> {
        // TODO: Implement this
        Log.log("Starting rental from reservation");
    };

    private Runnable startReturnForRental = () -> {
        // TODO: Implement this
        Log.log("starting return for rental");
    };
}
