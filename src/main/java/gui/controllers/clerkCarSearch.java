package gui.controllers;

import gui.Config;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import model.Entities.Reservation;
import model.Entities.TimePeriod;
import model.Util.Log;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class clerkCarSearch extends carSearch {

    @FXML private Button backToReservations;
    @FXML private Label rentalStartTime;
    private Timestamp fiveMinsAway;

    public clerkCarSearch(Main main) { super(main);
        super.startReservation = () -> {
            try {
                lock.lock();
                int optionSelected;
                try {
                    optionSelected = seeDetailsForOption.getValue()-1;
                } catch (Exception e) {
                    showError("There is no option selected to start reservation for");
                    return;
                }
                Reservation res = new Reservation();
                res.location = currentResults.get(optionSelected).location;
                res.vtName = currentResults.get(optionSelected).vt.vtname;
                res.timePeriod = getCurrentTimePeriodSelection();
                this.main.clerkResInProgress = res;
                this.main.switchScene(Config.CLERK_MAKE_RESERVATION);
            } catch (Exception e) {
                Log.log("Error starting reservation: " + e.getMessage());
            } finally {
                lock.unlock();
            }
        };

        super.resetTimePeriod = () -> {
            try {
                lock.lock();
                List<ComboBox> allDateTimeComboBox = Arrays.asList(endDate, endMonth, endYear, endHour, endMinute, endAM);
                List<ComboBox> allTimeComboBox = Arrays.asList(endHour, endMinute, endAM);

                for (ComboBox c: allDateTimeComboBox) c.getItems().clear();

                endDate.getItems().addAll(DATES);
                endMonth.getItems().addAll(MONTHS);
                endYear.getItems().addAll(YEARS);
                endHour.getItems().addAll(HOURS);
                endMinute.getItems().addAll(MINUTES);
                endAM.getItems().addAll(AMPM);

                for (ComboBox c: allTimeComboBox) c.setValue(c.getItems().get(0));
                Date tomorrow = new Date(System.currentTimeMillis() + 24*60*60*1000);
                Date nextWeek = new Date((long)(tomorrow.getTime() + 6*Math.pow(10,8)));
                endDate.setValue(nextWeek.getDate());
                endMonth.setValue(nextWeek.getMonth()+1);
                endYear.setValue(nextWeek.getYear() + 1900);
            } catch (Exception e) {
                Log.log("Error resetting time period: " + e.getMessage());
            } finally {
                lock.unlock();
            }
        };
    }

    @Override
    Timestamp getCurrentStartTimeSelection() {
        return fiveMinsAway;
    }

    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        backToReservations.setOnAction(event -> Platform.runLater(goBackToReservationRentalsSearch));
    }

    public void refreshAll() {
        super.refreshAll();
        fiveMinsAway = new Timestamp(System.currentTimeMillis() + 5*60*1000);
        rentalStartTime.setText(new TimePeriod(fiveMinsAway, fiveMinsAway).getStartAsTimeDateString());
    }

    private Runnable goBackToReservationRentalsSearch = () -> {
        this.main.switchScene(Config.CLERK_RESERVATION_RENTAL_SEARCH);
    };
}
