package gui.controllers;

import gui.GUIConfig;
import gui.Main;
import javafx.util.Pair;
import model.Entities.Rental;
import model.Entities.Reservation;
import model.Entities.TimePeriod;
import model.Entities.Vehicle;

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
        Vehicle autoselectedVehicle = qo.getAutoSelectedVehicle(r);  // TODO: Handle null case (no vehicle avail)
        TimePeriod nowUntilEndOfRes = TimePeriod.getNowTo(r.timePeriod);
        Rental rentalInProgress = new Rental(-1, autoselectedVehicle.vlicense, r.dlicense, nowUntilEndOfRes,
                autoselectedVehicle.odometer, null, r.confNum);
        this.main.clerkRentalInProgress = new Pair<>(r, rentalInProgress);
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
