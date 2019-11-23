package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.Orchestrator.QueryOrchestrator;

public abstract class Controller {

    public Main main;
    String ALL_BRANCHES = "All Branches";
    String ALL_VT = "All Types";
    QueryOrchestrator qo;

    Controller(Main main) {
        this.main = main;
        try {
            qo = new QueryOrchestrator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void refreshAll();
    
    @FXML private void switchToClerk(ActionEvent event) throws Exception {
        main.customerResInProgress = null;
        main.switchScene(GUIConfig.CLERK_RESERVATION_RENTAL_SEARCH);
    }

    @FXML private void switchToCustomer(ActionEvent event) throws Exception {
        main.switchScene(GUIConfig.CUSTOMER_CAR_SEARCH);
    }
}
