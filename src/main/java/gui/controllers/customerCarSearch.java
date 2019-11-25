package gui.controllers;

import gui.Config;
import gui.Main;
import model.Entities.Reservation;
import model.Orchestrator.VTSearchResult;
import model.Util.Log;

public class customerCarSearch extends carSearch {

    public customerCarSearch(Main main) { super(main);
        super.startReservation = () -> {
            try {
                lock.lock();
                SearchResult sr = searchResults.getSelectionModel().getSelectedItem();
                if (sr == null) {
                    showError("There is no option selected to start reservation for");
                    return;
                }
                VTSearchResult vt = sr.vtResult;
                Reservation res = new Reservation();
                res.location = vt.location;
                res.vtName = vt.vt.vtname;
                res.timePeriod = getCurrentTimePeriodSelection();
                this.main.customerResInProgress = res;
                this.main.switchScene(Config.CUSTOMER_MAKE_RES);
            } catch (Exception e) {
                Log.log("Error starting reservation: " + e.getMessage());
            } finally {
                lock.unlock();
            }
        };
    }
}
