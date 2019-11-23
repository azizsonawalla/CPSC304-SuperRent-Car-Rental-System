package model.Entities;

import java.sql.Timestamp;

public class Return {

    public int rid;
    public Timestamp returnDateTime;
    public long endOdometer;
    public boolean fulltank;
    public long cost; // TODO: Change to double?

    public Return(int rid, Timestamp returnDateTime, long endOdometer, boolean fulltank, long cost) {
        this.rid = rid;
        this.returnDateTime = returnDateTime;
        this.endOdometer = endOdometer;
        this.fulltank = fulltank;
        this.cost = cost;
    }
}
