package model.Entities;

import java.sql.Timestamp;

public class TimePeriod {

    private String TIME_INFO_TEMPLATE = "%02d/%02d/%02d %02d:%02d";

    //Primary key for TimePeriod is both startDateAndTime and endDateAndTime
    public Timestamp startDateAndTime;
    public Timestamp endDateAndTime;

    public TimePeriod(Timestamp start, Timestamp end) {
        this.startDateAndTime = start;
        this.endDateAndTime = end;
    }

    public TimePeriod() {}

    public static TimePeriod getNowTo(TimePeriod timePeriod) {
        // TODO: Returns new time period that starts now and ends when timePeriod ends
        return null;
    }

    public String getStartAsTimeDateString() {
        Timestamp start = this.startDateAndTime;
        Integer sDate = start.getDate(), sMonth = start.getMonth(), sYear = start.getYear(), sHour = start.getHours(), sMin = start.getMinutes();
        return String.format(TIME_INFO_TEMPLATE, sDate, sMonth, sYear, sHour, sMin);
    }

    public String getEndAsTimeDateString() {
        Timestamp end = this.endDateAndTime;
        Integer eDate = end.getDate(), eMonth = end.getMonth(), eYear = end.getYear(), eHour = end.getHours(), eMin = end.getMinutes();
        return String.format(TIME_INFO_TEMPLATE, eDate, eMonth, eYear, eHour, eMin);
    }
}
