package model.Entities;

public class Rental {
    //primary id of Rental
    int rid;

    //identifies the Vehicle involved in the Rental
    String vlicense;

    //identifies the Customer involved in the Rental
    long dlicense;

    //time period over which Rental occurs
    TimePeriod timePeriod;

    //odometer reading at the start of the rental
    int startOdometer;

    //Card for which rental was made
    long CardNo;
    String cardName;
    int expDate;

    //If customer is renting from a reservation, identifies Reservation involved in the Rental
    int confNo;
}
