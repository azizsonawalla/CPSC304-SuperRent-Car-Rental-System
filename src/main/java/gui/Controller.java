package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Controller {

    private Main main;
    @FXML private Button switchToClerkButton;

    public Controller(Main main) {
        this.main = main;
    }
    
    @FXML
    private void switchToClerk(ActionEvent event) throws Exception {
        main.setRoot("views/clerkReservationRentalSearch.fxml");
    }
}
