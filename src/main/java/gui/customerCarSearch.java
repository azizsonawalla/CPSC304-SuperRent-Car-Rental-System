package gui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Entities.Location;
import model.Entities.TimePeriod;
import model.Entities.VehicleType;
import model.Orchestrator.VTSearchResult;
import model.Util.Log;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class customerCarSearch extends Controller implements Initializable {

    private @FXML DatePicker startDate;
    private @FXML DatePicker endDate;
    private @FXML ComboBox<String> branchSelector;
    private @FXML ComboBox<String> vtSelector;
    private @FXML TextFlow searchResults;

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
                branchSelector.getItems().add(ALL_BRANCHES);
                branchSelector.setValue(ALL_BRANCHES);
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
                vtSelector.getItems().add(ALL_VT);
                vtSelector.setValue(ALL_VT);
            } catch (Exception e) {
                Log.log("Error refreshing vt list on customer car search screen: " + e.getMessage());
            }
            return null;
        }
    };

    // Gets all vt search results and put it in table
    private Task refreshVehicleTypeSearchResultTable = new Task() {
        @Override
        protected Object call() throws Exception {
            try {
                // TODO: complete task
                Location l = null;
                VehicleType vt = null;
                TimePeriod t = null;

                List<VTSearchResult> result = qo.getVTSearchResultsFor(l, vt, t);
                for (int i = 0; i < 100; i ++) {
                    searchResults.getChildren().add(new Text("     hellooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo     \n"));
                }
            } catch (Exception e) {
                Log.log("Error refreshing search results in table: " + e.getMessage());
            }
            return null;
        }
    };

    // Gets all vt search results and put it in table
    private Task showVehicleDetails = new Task() {
        @Override
        protected Object call() throws Exception {
            try {
                // TODO: complete task
            } catch (Exception e) {
                Log.log("Error refreshing search results in table: " + e.getMessage());
            }
            return null;
        }
    };

    public customerCarSearch(Main main) {
        super(main);
    }

    public void initialize(URL location, ResourceBundle resources) {
        refreshAll();
    }

    public void refreshAll() {
        // Get all branch values and put them as options in Select Branch
        this.main.pool.execute(refreshBranchList);
        // Get all vehicle types and put them as options in Select Vehicle Type
        this.main.pool.execute(refreshVehicleTypeList);
        // Update VT Search Result table with selection
        this.main.pool.execute(refreshVehicleTypeSearchResultTable);
    }
}
