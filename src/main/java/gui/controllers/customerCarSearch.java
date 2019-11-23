package gui.controllers;

import gui.Config;
import gui.Main;
import model.Entities.Reservation;
import model.Util.Log;

public class customerCarSearch extends carSearch {

    public customerCarSearch(Main main) { super(main);
        super.startReservation = () -> {
            try {
                lock.lock();
                int optionSelected = seeDetailsForOption.getValue() -1;
                Reservation res = new Reservation();
                res.location = currentResults.get(optionSelected).location;
                res.vtName = currentResults.get(optionSelected).vt.vtname;
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
