package model.Entities;

public class Reservation {
    //confirmation number is the primary key for Reservation
    int confNum;

    //identifies the VehicleType that is involved with a Reservation
    String vtName;

    //identifies the Customer that is involved with a Reservation
    String dlicense;

    //TimePeriod over which Reservation occurs
    TimePeriod timePeriod;
}
