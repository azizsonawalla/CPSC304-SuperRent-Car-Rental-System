package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Entities.Customer;
import model.Entities.Reservation;
import sun.misc.Regexp;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class customerMakeReservation extends Controller implements Initializable {

    String LOCATION_INFO_TEMPLATE = "%s, %s";
    String TIME_INFO_TEMPLATE = "On %02d/%02d/%02d at %02d:%02d";

    @FXML private Button backToSearchButton, makeResButton;
    @FXML private Label existingCustomer, resultLine, invalidDL, vtlabel, locationlabel, startlabel, endlabel;
    @FXML private TextField dlField, nameField, cellField, addField;

    public customerMakeReservation(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        backToSearchButton.setOnAction(event -> Platform.runLater(backToCustomerCarSearch));
        dlField.focusedProperty().addListener(event -> Platform.runLater(fillCustomerInfoIfAvailable));
        makeResButton.setOnAction(event -> Platform.runLater(makeReservation));
        refreshAll();
    }

    @Override
    public void refreshAll() {
        Platform.runLater(fillReservationDetails);
        Platform.runLater(clearAllFields);
        resultLine.setVisible(false);
        invalidDL.setVisible(false);
        existingCustomer.setVisible(false);
    }

    private boolean validateDL() {
        return dlField.getText().matches("[a-zA-Z0-9]{0,15}");
    }

    private boolean validateCell() {
        return cellField.getText().matches("[0-9]{10}");
    }

    private boolean validateName() {
        return nameField.getText().matches("[a-zA-Z\\s]{0,30}");
    }

    private boolean validateAdd() {
        return dlField.getText().matches("[a-zA-Z0-9\\s]{0,50}");
    }

    // Tasks

    private Runnable backToCustomerCarSearch = () -> {
        main.customerResInProgress = null;
        main.setRoot(GUIConfig.CUSTOMER_CAR_SEARCH);
    };

    private Runnable clearAllFields = () -> {
        dlField.clear();
        nameField.clear();
        cellField.clear();
        addField.clear();
    };

    private Runnable clearAllButDL = () -> {
        nameField.clear();
        cellField.clear();
        addField.clear();
    };

    private Runnable fillReservationDetails = () -> {
        Reservation r = main.customerResInProgress;
        if (r == null) return;
        vtlabel.setText(r.vtName);
        locationlabel.setText(String.format(LOCATION_INFO_TEMPLATE, r.location.location, r.location.city));
        Timestamp start = r.timePeriod.startDateAndTime;
        Timestamp end = r.timePeriod.endDateAndTime;
        Integer sDate = start.getDate(), sMonth = start.getMonth(), sYear = start.getYear(), sHour = start.getHours(), sMin = start.getMinutes();
        Integer eDate = end.getDate(), eMonth = end.getMonth(), eYear = end.getYear(), eHour = end.getHours(), eMin = end.getMinutes();
        startlabel.setText(String.format(TIME_INFO_TEMPLATE, sDate, sMonth, sYear, sHour, sMin));
        endlabel.setText(String.format(TIME_INFO_TEMPLATE, eDate, eMonth, eYear, eHour, eMin));
    };

    private Runnable fillCustomerInfoIfAvailable = () -> {
        existingCustomer.setVisible(false);
        invalidDL.setVisible(false);
        if (!validateDL()) {
            invalidDL.setVisible(true);
            Platform.runLater(clearAllButDL);
            return;
        }
        Customer customer = null;
        try {
            customer = qo.getCustomer(dlField.getText().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (customer != null) {
            existingCustomer.setVisible(true);
            cellField.setText(Long.toString(customer.cellphone));
            nameField.setText(customer.name);
            addField.setText(customer.address);
        } else {
            Platform.runLater(clearAllButDL);
        }
    };

    private Runnable makeReservation = () -> {
        // TODO;
        String result = "Success! Your confirmation number is %d";
        if (dlField.getText().length() == 0 || !validateDL()) {
            result = "Please enter valid driver's license number";
        } else if (cellField.getText().length() == 0 || !validateCell()) {
            result = "Please enter valid cellphone number";
        } else if (addField.getText().length() == 0 || !validateAdd()) {
            result = "Please enter valid address";
        } else if (nameField.getText().length() == 0 || !validateName()) {
            result = "Please enter valid name";
        } else {
            makeResButton.setDisable(true);
            Reservation r = main.customerResInProgress;
            r.dlicense = dlField.getText().trim();
            try {
                r = qo.makeReservation(r);
                result = String.format(result, r.confNum);
            } catch (Exception e) {
                result = "Failed to make reservation: " + e.getMessage();
            }
        }
        resultLine.setText(result);
        resultLine.setVisible(true);
    };
}
