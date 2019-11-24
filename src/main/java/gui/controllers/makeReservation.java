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

    // TODO: replace error result with show error

    private String currentDLInput = "";

    @FXML private Button backToSearchButton, makeResButton;
    @FXML private Label existingCustomer, resultLine, invalidDL, vtlabel, locationlabel, startlabel, endlabel;
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
        invalidDL.setVisible(false);
        existingCustomer.setVisible(false);
        makeResButton.setDisable(false);
    }

    private boolean validateDL() {
        return dlField.getText().matches("[a-zA-Z0-9]{0,50}");
    }

    private boolean validateCell() {
        return cellField.getText().matches("[0-9]{10}");
    }

    private boolean validateName() {
        return nameField.getText().matches("[a-zA-Z\\s]{0,255}");
    }

    private boolean validateAdd() {
        return addField.getText().matches("[a-zA-Z0-9\\s]{0,255}");
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
            // do nothing here
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
            Reservation r = getResInProgress();
            r.dlicense = dlField.getText().trim();
            try {
                r = qo.makeReservation(r);
                result = String.format(result, r.confNum);
                setResInProgressTo(null);
                postSuccessRes(r);
            } catch (Exception e) {
                result = "Failed to make reservation: " + e.getMessage();
            }
        }
        resultLine.setText(result);
        resultLine.setVisible(true);
    };
}
