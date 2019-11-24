package model.Entities;

public class Reservation {

    //default constructor
    public Reservation(){}

    public Reservation(int confNum, String vtName, TimePeriod timePeriod, Location location, String dlicense) {
        this.confNum = confNum;
        this.vtName = vtName;
        this.timePeriod = timePeriod;
        this.location = location;
        this.dlicense = dlicense;
    }

    //confirmation number is the primary key for Reservation
    public int confNum;

    //identifies the VehicleType, timePeriod, and location that is involved with a Reservation
    public String vtName;
    public TimePeriod timePeriod;
    public Location location;

    //identifies the Customer that is involved with a Reservation
    public String dlicense;

    public int getConfNum() {
        return confNum;
    }

    public String getVtName() {
        return vtName;
    }

    public String getStart() {
        return timePeriod.getStartAsTimeDateString();
    }

    public String getEnd() {
        return timePeriod.getEndAsTimeDateString();
    }

    public String getLocationString() {
        return location.toString();
    }

    public String getDlicense() {
        return dlicense;
    }
}
