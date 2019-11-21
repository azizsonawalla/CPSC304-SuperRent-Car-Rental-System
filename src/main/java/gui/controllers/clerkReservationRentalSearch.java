package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Entities.Rental;
import model.Entities.Reservation;
import model.Util.Log;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class clerkReservationRentalSearch extends Controller implements Initializable {

    private Text NO_RESULTS_FOUND = new Text("No results found for your search");
    private String RESERVATION_RESULT_TEMPLATE = "Confirmation No = %d, Customer DL = %s, Vehicle Type = %s, Start = %s, End = %s, Pickup = %s";
    private String RENTAL_RESULT_TEMPLATE = "Rental ID = %d, Vehicle License = %s, Customer DL = %s, Start = %s, End = %s";

    @FXML private Button dailyReportsButton, rentalWithoutReservationButton, rentalWithReservationButton,
                            searchReservationsButton, searchRentalsButton, startReturnButton;
    @FXML private TextField confNumField, dlField, dlFieldRental, rentalIdField;
    @FXML private TextFlow reservationResults, rentalResults;

    public clerkReservationRentalSearch(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
        dailyReportsButton.setOnAction(event -> Platform.runLater(goToDailyReports));
        rentalWithoutReservationButton.setOnAction(event -> Platform.runLater(startRentalWithoutReservation));
        rentalWithReservationButton.setOnAction(event -> Platform.runLater(startRentalFromReservation));
        searchReservationsButton.setOnAction(event -> Platform.runLater(refreshActiveReservations));
        searchRentalsButton.setOnAction(event -> Platform.runLater(refreshOngoingRentals));
        startReturnButton.setOnAction(event -> Platform.runLater(startReturnForRental));
    }

    public void refreshAll() {
        Platform.runLater(refreshOngoingRentals);
        Platform.runLater(refreshActiveReservations);
    }

    // Tasks

    private Runnable goToDailyReports = () -> {
        Log.log("Switching to daily reports screen");
        this.main.switchScene(GUIConfig.CLERK_DAILY_REPORTS);
    };

    private Runnable refreshActiveReservations = () -> {
        // TODO: Implement this
        Log.log("Refreshing active reservations");
        reservationResults.getChildren().clear();
        // TODO: clear options combobox
        String confNumString = confNumField.getText().trim();
        int confNum;
        String dl = dlField.getText().trim();
        if (confNumString.equals("")) {
            confNum = -1;
        } else {
            try {
                confNum = Integer.parseInt(confNumString);
            } catch (Exception e) {
                confNum = -1;
            }
        }
        if (dl.equals("")) dl = null;
        List<Reservation> result = qo.getReservationsWith(confNum, dl);
        if (result.size() == 0) {
            reservationResults.getChildren().add(NO_RESULTS_FOUND);
        } else {
            reservationResults.getChildren().add(new Text(RESERVATION_RESULT_TEMPLATE));
            // TODO: Fill combobox
            // TODO: Dummy value won't work due to null values
        }
    };

    private Runnable refreshOngoingRentals = () -> {
        // TODO: Implement this
        Log.log("Refreshing active rentals");
        rentalResults.getChildren().clear();
        // TODO: clear options combobox
        String rentalIdString = rentalIdField.getText().trim();
        int rentalId;
        String dl = dlFieldRental.getText().trim();
        if (rentalIdString.equals("")) {
            rentalId = -1;
        } else {
            try {
                rentalId = Integer.parseInt(rentalIdString);
            } catch (Exception e) {
                rentalId = -1;
            }
        }
        if (dl.equals("")) dl = null;
        List<Rental> result = qo.getRentalsWith(rentalId, dl);
        if (result.size() == 0) {
            rentalResults.getChildren().add(NO_RESULTS_FOUND);
        } else {
            rentalResults.getChildren().add(new Text(RENTAL_RESULT_TEMPLATE));
            // TODO: Fill combobox
            // TODO: Dummy value won't work due to null values
        }
    };

    private Runnable startRentalWithoutReservation = () -> {
        Log.log("Starting rental without reservation");
        this.main.switchScene(GUIConfig.CLERK_MAKE_RESERVATION);
    };

    private Runnable startRentalFromReservation = () -> {
        Log.log("Starting rental from reservation");
        // TODO: Save selected reservation in Main
        this.main.switchScene(GUIConfig.CLERK_START_RENTAL);
    };

    private Runnable startReturnForRental = () -> {
        Log.log("starting return for rental");
        // TODO: Save selected rental in Main
        this.main.switchScene(GUIConfig.CLERK_SUBMIT_RETURN);
    };
}
