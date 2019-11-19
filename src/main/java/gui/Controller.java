package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.Entities.TimePeriod;
import model.Orchestrator.QueryOrchestrator;

abstract class Controller {

    public Main main;
    TimePeriod DEFAULT_TIME_PERIOD;
    String ALL_BRANCHES = "All Branches";
    String ALL_VT = "All Types";
    QueryOrchestrator qo;

    Controller(Main main) {
        this.main = main;
        qo = new QueryOrchestrator();
    }

    public abstract void refreshAll();
    
    @FXML private void switchToClerk(ActionEvent event) throws Exception {
        main.setRoot(GUIConfig.CLERK_RENTAL_RES_SEARCH);
    }

    @FXML private void switchToCustomer(ActionEvent event) throws Exception {
        main.setRoot(GUIConfig.CUSTOMER_CAR_SEARCH);
    }
}
