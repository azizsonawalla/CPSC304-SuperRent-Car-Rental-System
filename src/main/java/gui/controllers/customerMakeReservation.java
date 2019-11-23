package gui.controllers;

import gui.Config;
import gui.Main;
import model.Entities.Reservation;

public class customerMakeReservation extends makeReservation {

    public customerMakeReservation(Main main) {
        super(main);
        super.backToCarSearch = () -> {
            setResInProgressTo(null);
            main.switchScene(Config.CUSTOMER_CAR_SEARCH);
        };
    }

    @Override
    void postSuccessRes(Reservation r) {
        // Nothing to be done
    }

    @Override
    Reservation getResInProgress() {
        return main.customerResInProgress;
    }

    @Override
    void setResInProgressTo(Reservation r) {
        main.customerResInProgress = r;
    }
}
