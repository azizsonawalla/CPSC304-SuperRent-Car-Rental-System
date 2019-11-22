package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import model.Entities.Reservation;

public class clerkMakeReservation extends makeReservation {

    public clerkMakeReservation(Main main) {
        super(main);
        super.backToCarSearch = () -> {
            setResInProgressTo(null);
            main.switchScene(GUIConfig.CLERK_CAR_SEARCH);
        };
    }

    @Override
    void postSuccessRes(Reservation r) {
        this.main.clerkRentalInProgress = r;
        this.main.switchScene(GUIConfig.CLERK_START_RENTAL);
    }

    @Override
    Reservation getResInProgress() {
        return main.clerkResInProgress;
    }

    @Override
    void setResInProgressTo(Reservation r) {
        main.clerkResInProgress = r;
    }
}
