package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Entities.Rental;
import model.Entities.Reservation;
import model.Entities.Return;
import model.Entities.TimePeriod;
import model.Util.Log;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class clerkReservationRentalSearch extends Controller implements Initializable {

    // TODO: Align search result strings

    private Text NO_RESULTS_FOUND = new Text("No results found for your search");
    private String RESERVATION_RESULT_TEMPLATE = "Confirmation No = %d, Customer DL = %s, Vehicle Type = %s, Start = %s, End = %s, Pickup = %s";
    private String RENTAL_RESULT_TEMPLATE = "Rental ID = %d, Vehicle License = %s, Customer DL = %s, Start = %s, End = %s";

    private List<Reservation> currReservationSearchRes;
    private List<Rental> currRentalSearchRes;

    @FXML private Button dailyReportsButton, rentalWithoutReservationButton, rentalWithReservationButton,
                            searchReservationsButton, searchRentalsButton, startReturnButton;
    @FXML private TextField confNumField, dlField, dlFieldRental, rentalIdField;
    @FXML private TextFlow reservationResults, rentalResults;
    @FXML private ComboBox<Integer> reservationOptions, rentalOptions;

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
        Log.log("Refreshing active reservations");
        reservationResults.getChildren().clear();
        reservationOptions.getItems().clear();
        // TODO: Disable start rental button
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
        this.currReservationSearchRes = qo.getReservationsWith(confNum, dl);
        if (currReservationSearchRes.size() == 0) {
            reservationResults.getChildren().add(NO_RESULTS_FOUND);
        } else {
            for (Reservation r: currReservationSearchRes) {
                String resultString = String.format(RESERVATION_RESULT_TEMPLATE, r.confNum, r.dlicense, r.vtName,
                                                    r.timePeriod.getStartAsTimeDateString(), r.timePeriod.getEndAsTimeDateString(), r.location.toString());
                reservationResults.getChildren().add(new Text(resultString));
            }
            for (int i=1; i <= currReservationSearchRes.size(); i++) reservationOptions.getItems().add(i);
            reservationOptions.setValue(reservationOptions.getItems().get(0));
            // TODO: Enable start rental button
        }
    };

    private Runnable refreshOngoingRentals = () -> {
        Log.log("Refreshing active rentals");
        rentalResults.getChildren().clear();
        rentalOptions.getItems().clear();
        // TODO: Disable start return button
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
        currRentalSearchRes = qo.getRentalsWith(rentalId, dl);
        if (currRentalSearchRes.size() == 0) {
            rentalResults.getChildren().add(NO_RESULTS_FOUND);
        } else {
            for (Rental r: currRentalSearchRes) {
                String resultString = String.format(RENTAL_RESULT_TEMPLATE, r.rid, r.vlicense, r.dlicense,
                                                    r.timePeriod.getStartAsTimeDateString(), r.timePeriod.getEndAsTimeDateString());
                rentalResults.getChildren().add(new Text(resultString));
            }
            for (int i=1; i <= currRentalSearchRes.size(); i++) rentalOptions.getItems().add(i);
            rentalOptions.setValue(rentalOptions.getItems().get(0));
            // TODO: Enable start return button
        }
    };

    private Runnable startRentalWithoutReservation = () -> {
        Log.log("Starting rental without reservation");
        this.main.switchScene(GUIConfig.CLERK_CAR_SEARCH);
    };

    private Runnable startRentalFromReservation = () -> {
        Log.log("Starting rental from reservation");
        this.main.clerkRentalInProgress = currReservationSearchRes.get(reservationOptions.getValue() -1);
        this.main.switchScene(GUIConfig.CLERK_START_RENTAL);
    };

    private Runnable startReturnForRental = () -> {
        Log.log("starting return for rental");
        Rental selectedRental = currRentalSearchRes.get(rentalOptions.getValue() - 1);
        this.main.clerkReturnInProgress = new Return(selectedRental.rid, TimePeriod.getNow(), -1, Return.TankStatus.NOT_FULL_TANK, -1);
        this.main.switchScene(GUIConfig.CLERK_SUBMIT_RETURN);
    };
}
