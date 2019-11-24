package model.Orchestrator;

import model.Entities.Rental;
import model.Entities.Reservation;
import model.Entities.Return;

public class ReturnReportEntry {
    public Rental rent;
    public Reservation res;
    public Return ret;

    public ReturnReportEntry(Rental rent, Reservation res, Return ret) {
        this.rent = rent;
        this.res = res;
        this.ret = ret;
    }
}
