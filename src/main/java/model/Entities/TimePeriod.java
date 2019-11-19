package model.Entities;

import java.util.Date;

public class TimePeriod {
    //Primary key for TimePeriod is both startDateAndTime and endDateAndTime
    Date startDateAndTime;
    Date endDateAndTime;

    public TimePeriod(Date start, Date end) {
        this.startDateAndTime = start;
        this.endDateAndTime = end;
    }
}
