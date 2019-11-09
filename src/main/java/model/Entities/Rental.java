package model.Entities;

public class Rental {
    //primary id of Rental
    int rid;

    //identifies the Vehicle involved in the Rental
    int vlicense;

    //identifies the Customer involved in the Rental
    long dlicense;

    //time period over which Rental occurs
    TimePeriod timePeriod;

    int odometer;

    //Card for which rental was made
    int CardNo;
    String CardName;
    int expDate;

    //If customer is renting from a reservation, identifies Reservation involved in the Rental
    int confNo;
}
