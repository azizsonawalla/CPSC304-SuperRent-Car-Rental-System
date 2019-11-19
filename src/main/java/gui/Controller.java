package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

class Controller {

    private Main main;
    @FXML private Button switchToClerkButton;

    Controller(Main main) {
        this.main = main;
    }
    
    @FXML private void switchToClerk(ActionEvent event) throws Exception {
        main.setRoot(GUIConfig.CLERK_RENTAL_RES_SEARCH);
    }

    @FXML private void switchToCustomer(ActionEvent event) throws Exception {
        main.setRoot(GUIConfig.CUSTOMER_CAR_SEARCH);
    }
}
