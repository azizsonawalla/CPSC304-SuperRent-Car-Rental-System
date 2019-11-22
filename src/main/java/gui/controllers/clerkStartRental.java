package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Entities.*;
import model.Util.Log;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class clerkStartRental extends Controller implements Initializable {

    private List<String> CREDIT_CARD_TYPES = Arrays.asList("MasterCard", "Visa");
    private Reservation res;
    private Vehicle v;
    private Customer c;

    @FXML Button backButton, startRental;
    @FXML ComboBox<String> cardType;
    @FXML ComboBox<Integer> month, year;
    @FXML Label vehicleType, start, end, name, dlicense, make, model, vlicense, colour, result;
    @FXML TextField cardNumber;

    public clerkStartRental(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
        backButton.setOnAction(event -> Platform.runLater(goBackToReservationRentalsSearch));
        startRental.setOnAction(event -> Platform.runLater(createRental));
    }

    public void refreshAll() {
        Platform.runLater(fillReservationRentalDetails);
        Platform.runLater(fillComboBoxValues);
        result.setVisible(false);
        startRental.setDisable(false);
        cardNumber.clear();
    }

    // Tasks

    private Runnable fillReservationRentalDetails = () -> {

        if (main.clerkRentalInProgress == null) return;

        res = main.clerkRentalInProgress;
        v = qo.getAutoSelectedVehicle(res);  // TODO: Handle null case (no vehicle avail)
        c = qo.getCustomer(res.dlicense);

        vehicleType.setText(String.format("Vehicle Type: %s", res.vtName));
        start.setText(String.format("Start: %s (Now)", res.timePeriod.getStartAsTimeDateString()));
        end.setText(String.format("End: %s", res.timePeriod.getEndAsTimeDateString()));
        name.setText(String.format("Customer Name: %s", c.name));
        dlicense.setText(String.format("Customer License: %s", c.dlicense));
        make.setText(String.format("Make: %s", v.make));
        model.setText(String.format("Model: %s", v.model));
        vlicense.setText(String.format("License Plate: %s", v.vlicense));
        colour.setText(String.format("Colour: %s", v.color));
    };

    private Runnable fillComboBoxValues = () -> {
        cardType.getItems().clear();
        cardType.getItems().addAll(CREDIT_CARD_TYPES);
        cardType.setValue(cardType.getItems().get(0));

        month.getItems().clear();
        month.getItems().addAll(MONTHS);
        month.setValue(month.getItems().get(0));

        year.getItems().clear();
        year.getItems().addAll(YEARS);
        year.setValue(year.getItems().get(0));
    };

    private Runnable goBackToReservationRentalsSearch = () -> {
        this.main.clerkRentalInProgress = null;
        this.main.switchScene(GUIConfig.CLERK_RESERVATION_RENTAL_SEARCH);
    };

    private Runnable createRental = () -> {
        String cardTypeValue = cardType.getValue();
        String cardNumberStringValue = cardNumber.getText().trim();
        String resultString = "Success! Rental has been started";
        Integer expiryMonth = month.getValue();
        Integer expiryYear = year.getValue();
        Timestamp expiry = new Timestamp(expiryYear-1900, expiryMonth-1, 1,0,0,0,0);
        if (expiry.getTime() < TimePeriod.getNow().getTime()) {
            resultString = "Card has expired";
        } else if (cardNumberStringValue.length() != 16) {
            resultString = "Please enter valid card number";
        } else {
            long cardNumberIntValue;
            try {
                cardNumberIntValue = Long.parseLong(cardNumberStringValue);
                Card card = new Card(cardNumberIntValue, cardTypeValue, expiry);
                TimePeriod rentalTime = TimePeriod.getNowTo(res.timePeriod);
                Rental r = new Rental(-1, v.vlicense, c.dlicense, rentalTime, v.odometer, card, res.confNum);
                qo.addRental(r);
                startRental.setDisable(true);
            } catch (Exception e) {
                resultString = "Please enter valid card number";
            }
        }
        result.setText(resultString);
        result.setVisible(true);
    };
}
