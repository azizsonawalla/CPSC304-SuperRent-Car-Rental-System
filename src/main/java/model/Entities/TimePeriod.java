package model.Entities;

import java.sql.Timestamp;

public class TimePeriod {

    private static String TIME_INFO_TEMPLATE = "%02d/%02d/%02d %02d:%02d";
    private long HOUR_IN_MS = 60 * 60 * 1000;
    private long DAY_IN_MS = 24 * HOUR_IN_MS;
    private long WEEK_IN_MS = 7 * DAY_IN_MS;

    //Primary key for TimePeriod is both startDateAndTime and endDateAndTime
    public Timestamp startDateAndTime;
    public Timestamp endDateAndTime;

    public TimePeriod(Timestamp start, Timestamp end) {
        this.startDateAndTime = start;
        this.endDateAndTime = end;
    }

    public TimePeriod() {}

    public String getStartAsTimeDateString() {
        Timestamp start = this.startDateAndTime;
        return getTimestampAsTimeDateString(start);
    }

    public String getEndAsTimeDateString() {
        Timestamp end = this.endDateAndTime;
        return getTimestampAsTimeDateString(end);
    }

    public static String getTimestampAsTimeDateString(Timestamp t) {
        Integer d = t.getDate(), m = t.getMonth(), y = t.getYear(), h = t.getHours(), min = t.getMinutes();
        return String.format(TIME_INFO_TEMPLATE, d, m+1, y+1900, h, min);
    }

    public long getDurationInMs() {
        return endDateAndTime.getTime() - startDateAndTime.getTime();
    }

    public int getWeeks() {
        long dur = getDurationInMs();
        return (int) (dur / WEEK_IN_MS);
    }

    public int getDays() {
        long dur = getDurationInMs();
        return (int) (dur / DAY_IN_MS);
    }

    public int getDaysMinusWeeks() {
        long dur = getDurationInMs() - (getWeeks() * WEEK_IN_MS);
        return (int) (dur / DAY_IN_MS);
    }

    public int getHoursMinusDaysMinusWeeks() {
        long dur = getDurationInMs() - (getWeeks() * WEEK_IN_MS) - (getDaysMinusWeeks() * DAY_IN_MS);
        return (int) (dur / HOUR_IN_MS);
    }

    // Returns new time period that starts now and ends when timePeriod ends
    public static TimePeriod getNowTo(TimePeriod timePeriod) {
        return new TimePeriod(new Timestamp(System.currentTimeMillis()), timePeriod.endDateAndTime);
    }

    // Returns new time period that starts with timePeriod and ends now
    public static TimePeriod getStartToNow(TimePeriod timePeriod) {
        return new TimePeriod(timePeriod.startDateAndTime, new Timestamp(System.currentTimeMillis()));
    }

    public static Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }
}
