package gui.controllers;

import gui.Config;
import gui.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import model.Orchestrator.QueryOrchestrator;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller {

    public Main main;
    String ALL_BRANCHES = "All Branches";
    String ALL_VT = "All Types";
    QueryOrchestrator qo;

    // Global datetime arrays
    List<Integer> DATES = new ArrayList<>();
    List<Integer> MONTHS = new ArrayList<>();
    List<Integer> YEARS = new ArrayList<>();
    List<Integer> HOURS = new ArrayList<>();
    List<Integer> MINUTES = new ArrayList<>();

    Controller(Main main) {
        this.main = main;
        try {
            qo = new QueryOrchestrator();
        } catch (Exception e) {
            showError("Could not connect to cloud database. Please restart the application");
            throw new RuntimeException(e);
        }
        initializeDateTimeArrays();
    }

    public abstract void refreshAll();

    private void initializeDateTimeArrays() {
        for (int i = 1; i <= 31; i++) DATES.add(i);
        for (int i = 1; i <= 12; i++) MONTHS.add(i);
        for (int i = 2019; i <= 2025; i++) YEARS.add(i);
        for (int i = 0; i <= 23; i++) HOURS.add(i);
        for (int i = 0; i <= 59; i++) MINUTES.add(i);
    }

    @FXML private void switchToClerk(ActionEvent event) throws Exception {
        main.customerResInProgress = null;
        main.switchScene(Config.CLERK_RESERVATION_RENTAL_SEARCH);
    }

    @FXML private void switchToCustomer(ActionEvent event) throws Exception {
        main.switchScene(Config.CUSTOMER_CAR_SEARCH);
    }

    void showError(String msg) {
        Error e = new Error();
        e.msg = msg + "\n NOTE: This application requires an internet connection to work properly.";
        Platform.runLater(e);
    }

    private class Error implements Runnable {
        public String msg = "";
        @Override
        public void run() {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(msg);
            a.show();
        }
    }
}
