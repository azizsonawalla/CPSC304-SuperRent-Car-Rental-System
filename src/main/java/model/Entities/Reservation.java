package model.Entities;

public class Reservation {
    //confirmation number is the primary key for Reservation
    public int confNum;

    //identifies the VehicleType, timePeriod, and location that is involved with a Reservation
    public String vtName;
    public TimePeriod timePeriod;
    public Location location;

    //identifies the Customer that is involved with a Reservation
    public String dlicense;
}
