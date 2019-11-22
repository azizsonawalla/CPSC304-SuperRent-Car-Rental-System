package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import model.Entities.Reservation;
import model.Util.Log;

public class clerkCarSearch extends carSearch {

    public clerkCarSearch(Main main) { super(main);
        super.startReservation = () -> {
            try {
                lock.lock();
                int optionSelected = seeDetailsForOption.getValue() -1;
                Reservation res = new Reservation();
                res.location = currentResults.get(optionSelected).location;
                res.vtName = currentResults.get(optionSelected).vt.vtname;
                res.timePeriod = getCurrentTimePeriodSelection();
                this.main.clerkResInProgress = res;
                this.main.switchScene(GUIConfig.CLERK_MAKE_RESERVATION);
            } catch (Exception e) {
                Log.log("Error starting reservation: " + e.getMessage());
            } finally {
                lock.unlock();
            }
        };
    }
}
