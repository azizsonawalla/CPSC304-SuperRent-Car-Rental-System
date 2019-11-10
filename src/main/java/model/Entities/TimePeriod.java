package model.Entities;

import java.sql.Date;
import java.sql.Time;

public class TimePeriod {
    //Primary key for TimePeriod is both startDateAndTime and endDateAndTime
    public Date startDate;
    public Time startTime;
    public Date endDate;
    public Time endTime;
}
