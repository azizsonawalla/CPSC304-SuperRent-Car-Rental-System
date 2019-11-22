package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.Orchestrator.QueryOrchestrator;

import java.util.ArrayList;
import java.util.Arrays;
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
    List<String> AMPM = Arrays.asList("AM", "PM");

    Controller(Main main) {
        this.main = main;
        qo = new QueryOrchestrator();
        initializeDateTimeArrays();
    }

    public abstract void refreshAll();

    private void initializeDateTimeArrays() {
        for (int i = 1; i <= 31; i++) DATES.add(i);
        for (int i = 1; i <= 12; i++) MONTHS.add(i);
        for (int i = 2019; i <= 2025; i++) YEARS.add(i);
        for (int i = 1; i <= 12; i++) HOURS.add(i);
        for (int i = 0; i <= 55; i++) MINUTES.add(i);
    }
    
    @FXML private void switchToClerk(ActionEvent event) throws Exception {
        main.customerResInProgress = null;
        main.switchScene(GUIConfig.CLERK_RESERVATION_RENTAL_SEARCH);
    }

    @FXML private void switchToCustomer(ActionEvent event) throws Exception {
        main.switchScene(GUIConfig.CUSTOMER_CAR_SEARCH);
    }
}
