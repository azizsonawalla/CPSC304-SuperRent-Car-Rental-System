package model.Entities;

import java.sql.Timestamp;

public class TimePeriod {
    //Primary key for TimePeriod is both startDateAndTime and endDateAndTime
    public Timestamp fromDateTime;
    public Timestamp toDateTime;
}
