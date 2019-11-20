package model.Entities;

import java.sql.Timestamp;

public class TimePeriod {
    //Primary key for TimePeriod is both startDateAndTime and endDateAndTime
    Timestamp startDateAndTime;
    Timestamp endDateAndTime;

    public TimePeriod(Timestamp start, Timestamp end) {
        this.startDateAndTime = start;
        this.endDateAndTime = end;
    }
}
