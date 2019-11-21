package model.Entities;

public class Rental {

    //primary key, id of Rental
    public int rid;

    //identifies the Vehicle involved in the Rental
    public String vlicense;

    //identifies the Customer involved in the Rental
    public String dlicense;

    //time period over which Rental occurs
    public TimePeriod timePeriod;

    //odometer reading at the start of the rental
    public int startOdometer;

    //Card for which rental was made
    public Card card;

    //If customer is renting from a reservation, identifies Reservation involved in the Rental
    public int confNo;

    public Rental(){};

    public Rental(int rid, String vLicense, String dLicense, TimePeriod timePeriod, int startOdometer, Card card,
    int confNo){
        this.rid = rid;
        this.vlicense = vLicense;
        this.dlicense = dLicense;
        this.timePeriod = timePeriod;
        this.startOdometer = startOdometer;
        this.card = card;
        this.confNo = confNo;
    }

}
