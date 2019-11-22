package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.Entities.Reservation;
import model.Util.Log;

import java.net.URL;
import java.util.ResourceBundle;

public class clerkCarSearch extends carSearch {

    @FXML private Button backToReservations;

    public clerkCarSearch(Main main) { super(main);
        super.startReservation = () -> {
            try {
                lock.lock();
                int optionSelected = seeDetailsForOption.getValue() -1;
                Reservation res = new Reservation();
                res.location = currentResults.get(optionSelected).location;
                res.vtName = currentResults.get(optionSelected).vt.vtname;
                res.timePeriod = getCurrentTimePeriodSelection();
                this.main.clerkResInProgress = res;
                this.main.switchScene(GUIConfig.CLERK_MAKE_RESERVATION);
            } catch (Exception e) {
                Log.log("Error starting reservation: " + e.getMessage());
            } finally {
                lock.unlock();
            }
        };
    }

    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        backToReservations.setOnAction(event -> Platform.runLater(goBackToReservationRentalsSearch));
    }

    private Runnable goBackToReservationRentalsSearch = () -> {
        this.main.switchScene(GUIConfig.CLERK_RESERVATION_RENTAL_SEARCH);
    };
}
