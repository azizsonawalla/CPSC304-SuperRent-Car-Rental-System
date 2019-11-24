package gui.controllers;

import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Entities.Customer;
import model.Entities.Reservation;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class makeReservation extends Controller implements Initializable {

    private String currentDLInput = "";

    @FXML private Button backToSearchButton, makeResButton;
    @FXML private Label existingCustomer, resultLine, vtlabel, locationlabel, startlabel, endlabel;
    @FXML private TextField dlField, nameField, cellField, addField;

    public makeReservation(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        backToSearchButton.setOnAction(event -> Platform.runLater(backToCarSearch));
        dlField.focusedProperty().addListener(event -> Platform.runLater(fillCustomerInfoIfAvailable));
        makeResButton.setOnAction(event -> Platform.runLater(makeReservation));
        refreshAll();
    }

    @Override
    public void refreshAll() {
        Platform.runLater(fillReservationDetails);
        Platform.runLater(clearAllFields);
        resultLine.setVisible(false);
        existingCustomer.setVisible(false);
        makeResButton.setDisable(false);
    }

    private boolean validateDL(boolean enforceNonZero) {
        boolean res = dlField.getText().matches("[a-zA-Z0-9]{0,50}")
                        && (!enforceNonZero || dlField.getText().trim().length() > 0);
        if (!res) showError("Invalid input for driver's license. Must be 1-50 alphanumric chars only.");
        return res;
    }

    private boolean validateCell() {
        boolean res = cellField.getText().matches("[0-9]{10}");
        if (!res) showError("Invalid input for cellphone. Must be 10 digits.");
        return res;
    }

    private boolean validateName() {
        boolean res = nameField.getText().matches("[a-zA-Z\\s]{0,255}");
        if (!res) showError("Invalid input for name. Must be up to 255 chars with uppercase/lowercase alphabets and spaces only.");
        return res;
    }

    private boolean validateAdd() {
        boolean res = addField.getText().matches("[a-zA-Z0-9\\s:#,.]{0,255}");
        if (!res) showError("Invalid input for address. Must be up to 255 chars with uppercase/lowercase alphabets, spaces, and symbols :,#. only");
        return res;
    }

    private Customer getCurrentCustomer() {
        Customer customer = null;
        try {
            customer = qo.getCustomer(dlField.getText().trim());
        } catch (Exception e) {
            // do nothing here
        }
        return customer;
    }

    private Customer addCurrentCustomerEntryIfNotExists() {
        Customer c = getCurrentCustomer();
        if (getCurrentCustomer() == null) {
            if (!(validateCell() && validateDL(true) && validateName() && validateAdd())) {
                return c;
            }
            c = new Customer();
            c.dlicense = dlField.getText().trim();
            c.cellphone = Long.valueOf(cellField.getText());
            c.name = nameField.getText().trim();
            c.address = addField.getText().trim();
            try {
                qo.addCustomer(c);
            } catch (Exception e) {
                showError("Failed to add Customer as new Customer: " + e.getMessage());
                return null;
            }
        }
        return c;
    }

    abstract void postSuccessRes(Reservation r);

    abstract Reservation getResInProgress();

    abstract void setResInProgressTo(Reservation r);


    // Tasks

    Runnable backToCarSearch;

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
        Reservation r = getResInProgress();
        if (r == null) return;
        vtlabel.setText(r.vtName);
        locationlabel.setText(r.location.toString());
        startlabel.setText(r.timePeriod.getStartAsTimeDateString());
        endlabel.setText(r.timePeriod.getEndAsTimeDateString());
    };

    private Runnable fillCustomerInfoIfAvailable = () -> {
        if(currentDLInput.equals(dlField.getText().trim())) return;
        currentDLInput = dlField.getText().trim();

        cellField.setDisable(false);
        nameField.setDisable(false);
        addField.setDisable(false);
        existingCustomer.setVisible(false);

        if (!validateDL(false)) {
            Platform.runLater(clearAllButDL);
            return;
        }

        Customer customer = getCurrentCustomer();
        if (customer != null) {
            existingCustomer.setVisible(true);
            cellField.setText(Long.toString(customer.cellphone));
            cellField.setDisable(true);
            nameField.setText(customer.name);
            nameField.setDisable(true);
            addField.setText(customer.address);
            addField.setDisable(true);
        } else {
            Platform.runLater(clearAllButDL);
        }
    };

    private Runnable makeReservation = () -> {
        String result = "Success! Your confirmation number is %d";
        Customer c = addCurrentCustomerEntryIfNotExists();
        if (c == null) return;
        makeResButton.setDisable(true);
        Reservation r = getResInProgress();
        r.dlicense = c.dlicense;
        try {
            r = qo.makeReservation(r);
            result = String.format(result, r.confNum);
            setResInProgressTo(null);
            postSuccessRes(r);
        } catch (Exception e) {
           showError("There was an error creating your reservation: " + e.getMessage());
        }
        resultLine.setText(result);
        resultLine.setVisible(true);
    };
}
