package gui.controllers;

import gui.Config;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import model.Entities.*;
import model.Util.Log;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class clerkReservationRentalSearch extends Controller implements Initializable {

    private Text NO_RESULTS_FOUND = new Text("No results found for your search");

    private List<Reservation> currReservationSearchRes;
    private List<Rental> currRentalSearchRes;

    @FXML private Button dailyReportsButton, rentalWithoutReservationButton, rentalWithReservationButton,
            searchReservationsButton, searchRentalsButton, startReturnButton;
    @FXML private TextField confNumField, dlField, dlFieldRental, rentalIdField;
    @FXML private TableView reservationResults, rentalResults;
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


        List<String> columnHeaders = Arrays.asList("Confirmation No.", "Customer License", "Vehicle Type", "Start", "End", "Pickup Location");
        List<String> propertyName = Arrays.asList("confNum", "dlicense", "vtName", "start", "end", "locationString");
        for (int i = 0; i < columnHeaders.size(); i++) {
            TableColumn<String, Reservation> column = new TableColumn<>(columnHeaders.get(i));
            column.setCellValueFactory(new PropertyValueFactory<>(propertyName.get(i)));
            reservationResults.getColumns().add(column);
        }

        columnHeaders = Arrays.asList("Rental ID", "Vehicle License", "Customer License", "Start", "End");
        propertyName = Arrays.asList("rid", "vlicense", "dlicense", "start", "end");
        for (int i = 0; i < columnHeaders.size(); i++) {
            TableColumn<String, Rental> column = new TableColumn<>(columnHeaders.get(i));
            column.setCellValueFactory(new PropertyValueFactory<>(propertyName.get(i)));
            rentalResults.getColumns().add(column);
        }


    }

    public void refreshAll() {
        Platform.runLater(refreshOngoingRentals);
        Platform.runLater(refreshActiveReservations);
    }

    // Tasks

    private Runnable goToDailyReports = () -> {
        Log.log("Switching to daily reports screen");
        this.main.switchScene(Config.CLERK_DAILY_REPORTS);
    };

    private Runnable refreshActiveReservations = () -> {
        Log.log("Refreshing active reservations");
        reservationResults.getItems().clear();

        reservationOptions.getItems().clear();
        rentalWithReservationButton.setDisable(true);
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
        try {
            this.currReservationSearchRes = qo.getReservationsWith(confNum, dl);
        } catch (Exception e) {
            showError("Error getting active reservation entries. Please restart the application.");
            return;
        }
        if (currReservationSearchRes.size() == 0) {
            reservationResults.setPlaceholder(NO_RESULTS_FOUND);
        } else {
            // Add items
            for (Reservation r: currReservationSearchRes) {
                reservationResults.getItems().add(r);
            }

            for (int i=1; i <= currReservationSearchRes.size(); i++) reservationOptions.getItems().add(i);
            reservationOptions.setValue(reservationOptions.getItems().get(0));
            rentalWithReservationButton.setDisable(false);
        }
    };

    private Runnable refreshOngoingRentals = () -> {
        Log.log("Refreshing active rentals");
        rentalResults.getItems().clear();

        rentalOptions.getItems().clear();
        startReturnButton.setDisable(true);
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
        try {
            currRentalSearchRes = qo.getRentalsWith(rentalId, dl);
        } catch (Exception e) {
            showError("Failed to load active rentals. Please restart application.");
            return;
        }
        if (currRentalSearchRes.size() == 0) {
            rentalResults.setPlaceholder(NO_RESULTS_FOUND);
        } else {
            // Add items
            for (Rental r: currRentalSearchRes) {
                rentalResults.getItems().add(r);
            }

            for (int i=1; i <= currRentalSearchRes.size(); i++) rentalOptions.getItems().add(i);
            rentalOptions.setValue(rentalOptions.getItems().get(0));
            startReturnButton.setDisable(false);
        }
    };

    private Runnable startRentalWithoutReservation = () -> {
        Log.log("Starting rental without reservation");
        this.main.switchScene(Config.CLERK_CAR_SEARCH);
    };

    private Runnable startRentalFromReservation = () -> {
        Log.log("Starting rental from reservation");
        this.main.clerkReservationToStart = currReservationSearchRes.get(reservationOptions.getValue() -1);
        this.main.switchScene(Config.CLERK_START_RENTAL);
    };

    private Runnable startReturnForRental = () -> {
        Log.log("starting return for rental");
        this.main.clerkRentalToBeReturned = currRentalSearchRes.get(rentalOptions.getValue() - 1);
        this.main.switchScene(Config.CLERK_SUBMIT_RETURN);
    };
}
