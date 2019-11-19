package gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import model.Entities.Location;
import model.Entities.TimePeriod;
import model.Entities.VehicleType;
import model.Orchestrator.QueryOrchestrator;
import model.Util.Log;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class customerCarSearch extends Controller implements Initializable {

    private TimePeriod DEFAULT_TIME_PERIOD;
    private QueryOrchestrator qo;

    private @FXML DatePicker startDate;
    private @FXML DatePicker endDate;
    private @FXML ComboBox<String> branchSelector;
    private @FXML ComboBox<String> vtSelector;

    // Gets all locations from DB and puts it in branch selector
    private Task refreshBranchList = new Task() {
        @Override
        protected Object call() throws Exception {
            try {
                List<Location> branches = qo.getAllLocationNames();
                for (Location l : branches) {
                    String locationName = String.format("%s, %s", l.location, l.city);
                    branchSelector.getItems().add(locationName);
                }
                branchSelector.getItems().add("All Branches");
                branchSelector.setValue("All Branches");
            } catch (Exception e) {
                Log.log("Error refreshing branch list on customer car search screen: " + e.getMessage());
            }
            return null;
        }
    };

    // Gets all vehicle types from DB and puts it in vt selector
    private Task refreshVehicleTypeList = new Task() {
        @Override
        protected Object call() throws Exception {
            try {
                List<VehicleType> vehicleTypes = qo.getAllVehicleTypes();
                for (VehicleType vt : vehicleTypes) {
                    vtSelector.getItems().add(vt.vtname);
                }
                vtSelector.getItems().add("All Types");
                vtSelector.setValue("All Types");
            } catch (Exception e) {
                Log.log("Error refreshing vt list on customer car search screen: " + e.getMessage());
            }
            return null;
        }
    };

    public customerCarSearch(Main main) {
        super(main);
        qo = new QueryOrchestrator();
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
    }

    public void refreshAll() {
        // Get all branch values and put them as options in Select Branch
        Platform.runLater(refreshBranchList);
        // Get all vehicle types and put them as options in Select Vehicle Type
        Platform.runLater(refreshVehicleTypeList);
        // TODO
    }
}
