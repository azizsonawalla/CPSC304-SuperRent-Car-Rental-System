package model.Entities;

public class Reservation {
    //confirmation number is the primary key for Reservation
    public int confNum;

    //identifies the VehicleType that is involved with a Reservation
    public String vtName;

    //identifies the Customer that is involved with a Reservation
    public String dlicense;

    //TimePeriod over which Reservation occurs
    public TimePeriod timePeriod;
}
