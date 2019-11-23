package model.Entities;

import java.sql.Timestamp;

public class Return {

    public enum TankStatus {
        FULL_TANK,
        NOT_FULL_TANK
    }

    public int rid;
    public Timestamp returnDateTime;
    public int endOdometer; // TODO: Change to long
    public TankStatus fullTank;
    public int cost; // TODO: Change to double?

    public Return(int rid, Timestamp returnDateTime, int endOdometer, TankStatus fullTank, int cost) {
        this.rid = rid;
        this.returnDateTime = returnDateTime;
        this.endOdometer = endOdometer;
        this.fullTank = fullTank;
        this.cost = cost;
    }
}
