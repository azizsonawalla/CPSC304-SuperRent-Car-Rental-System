package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class clerkStartRental extends Controller implements Initializable {

    @FXML Button backButton;

    public clerkStartRental(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
        backButton.setOnAction(event -> Platform.runLater(goBackToReservationRentalsSearch));
    }

    public void refreshAll() {
        // TODO;
    }

    // Tasks

    private Runnable fillReservationRentalDetails = () -> {
        // TODO:
    };

    private Runnable fillComboBoxValues = () -> {
        // TODO: implement
        // TODO: clear before adding
    };

    private Runnable goBackToReservationRentalsSearch = () -> {
        this.main.clerkRentalInProgress = null;
        this.main.switchScene(GUIConfig.CLERK_RESERVATION_RENTAL_SEARCH);
    };

    private Runnable startRental = () -> {
        // TODO:
    };
}
