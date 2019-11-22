package model.Entities;

import java.sql.Timestamp;

public class Return {

    public int rid;
    public Timestamp returnDateTime;
    public int endOdometer;
    public boolean fulltank;
    public int cost;

    public Return(int rid, Timestamp returnDateTime, int endOdometer, boolean fulltank, int cost) {
        this.rid = rid;
        this.returnDateTime = returnDateTime;
        this.endOdometer = endOdometer;
        this.fulltank = fulltank;
        this.cost = cost;
    }
}
