package gui.controllers;

import gui.Config;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Entities.*;

import java.net.URL;
import java.util.ResourceBundle;

public class clerkSubmitReturn extends Controller implements Initializable {

    private Rental rental;
    private Vehicle vehicle;
    private VehicleType vehicleType;
    private Customer customer;
    private TimePeriod t;
    private long currentCalculatedCost;

    @FXML private Button backToReservations, refreshCost, submitReturn;
    @FXML private Label invalidOdometer, rentalNumber, start, end, name, dlicense, startOdometer, result, vtname, w, wr, d, dr, h, hr, kmLimit, km, kmr, tc;
    @FXML private TextField endOdometer;
    @FXML private CheckBox tankFull;

    public clerkSubmitReturn(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
        backToReservations.setOnAction(event -> Platform.runLater(goBackToReservations));
        submitReturn.setOnAction(event -> Platform.runLater(processReturn));
        refreshCost.setOnAction(event -> Platform.runLater(refreshCostCalculation));
    }

    public void refreshAll() {
        initializeGlobals();
        Platform.runLater(fillRentalDetails);
        Platform.runLater(fillCostCalculation);
        result.setVisible(false);
        invalidOdometer.setVisible(false);
        submitReturn.setDisable(false);
        currentCalculatedCost = -1;
    }

    private void initializeGlobals() {
        if (main.clerkRentalToBeReturned == null) return;
        rental = main.clerkRentalToBeReturned;
        vehicle = qo.getVehicle(rental.vlicense);
        vehicleType = qo.getVehicleType(vehicle.vtname);
        customer = qo.getCustomer(rental.dlicense);
        t = TimePeriod.getStartToNow(rental.timePeriod);
    }

    private long getEndOdometer() {
        String endOdometerString = endOdometer.getText();
        if (endOdometerString.length() > 0) {
            long endOdometerInt;
            try {
                endOdometerInt = Long.parseLong(endOdometerString);
                if (endOdometerInt >= 0 && endOdometerInt >= main.clerkRentalToBeReturned.startOdometer) {
                    return endOdometerInt;
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        return -1;
    }

    // Tasks

    private Runnable fillRentalDetails = () -> {
        if (rental == null) return;

        rentalNumber.setText(String.format("Rental Number: %d", rental.rid));
        start.setText(String.format("Start: %s", t.getStartAsTimeDateString()));
        end.setText(String.format("End: %s (Now)", t.getEndAsTimeDateString()));
        name.setText(String.format("Customer Name: %s", customer.name));
        dlicense.setText(String.format("Customer License: %s", customer.dlicense));
        startOdometer.setText(String.format("Start Odometer: %d", rental.startOdometer));
    };

    private Runnable fillCostCalculation = () -> {
        if (rental == null) return;

        int wVal = t.getWeeks();
        int dVal = t.getDaysMinusWeeks();
        int hVal = t.getHoursMinusDaysMinusWeeks();

        vtname.setText(String.format("Vehicle Type Rented: %s", vehicle.vtname));
        kmLimit.setText(String.format("Daily KM Limit: %d km", qo.getDailyKMLimit()));

        w.setText(String.format("Number of Weeks (W) = %d",wVal));
        wr.setText(String.format("Weekly Rate (WR) = $%d (rental) + $%d (insurance)", vehicleType.wrate, vehicleType.wirate));
        d.setText(String.format("Number of Days (D) = %d",dVal));
        dr.setText(String.format("Daily Rate (DR) = $%d (rental) + $%d (insurance)", vehicleType.drate, vehicleType.dirate));
        h.setText(String.format("Number of Hours (H) = %d",hVal));
        hr.setText(String.format("Hourly Rate (HR) = $%d (rental) + $%d (insurance)", vehicleType.hrate, vehicleType.hirate));
        kmr.setText(String.format("KM Overage Rate (KMR) = $%d/km", vehicleType.krate));

        long endOdometerValue = getEndOdometer();
        if (endOdometerValue >= 0) {
            long limit = (qo.getDailyKMLimit() * t.getDays()); // TODO: double check formula and calculations
            long kmVal = Math.max(0, (endOdometerValue - rental.startOdometer) - limit);
            long tcVal = (wVal * (vehicleType.wirate + vehicleType.wrate))
                        + (dVal * (vehicleType.dirate + vehicleType.drate))
                        + (hVal * (vehicleType.hirate + vehicleType.hrate))
                        + (kmVal * vehicleType.krate);
            km.setText(String.format("Total KM over limit (KM) = %d", kmVal));
            tc.setText(String.format("Total Cost = $%d.00", tcVal));
            currentCalculatedCost = tcVal;
        } else {
            km.setText("Total KM over limit (KM) = [Enter odometer value]");
            tc.setText("Total Cost = [Enter odometer value]");
            currentCalculatedCost = -1;
        }
    };

    private Runnable refreshCostCalculation = () -> {
        invalidOdometer.setVisible(false);
        if (getEndOdometer() < 0) {
            invalidOdometer.setVisible(true);
            return;
        }
        fillCostCalculation.run();
    };

    private Runnable processReturn = () -> {
        String resultString = "Success! Return submitted. Vehicle is ready for new rental.";
        if (getEndOdometer() <= 0) {
            resultString = "Please enter valid odometer value";
        } else if (getEndOdometer() < rental.startOdometer) {
            resultString = "Please enter an odometer value greater than starting value";
        } else if (!tankFull.isSelected()) {
            resultString = "Cannot complete return if vehicle tank is not full";
        } else {
            // create return
            refreshCostCalculation.run();
            if (currentCalculatedCost < 0) {
                resultString = "Error calculating cost of rental";
            } else {
                Return r = new Return(rental.rid, t.endDateAndTime, getEndOdometer(), true, currentCalculatedCost);
                qo.submitReturn(r);
                submitReturn.setDisable(true);
            }
        }
        result.setText(resultString);
        result.setVisible(true);
    };

    private Runnable goBackToReservations = () -> {
        main.switchScene(Config.CLERK_RESERVATION_RENTAL_SEARCH);
    };

}
