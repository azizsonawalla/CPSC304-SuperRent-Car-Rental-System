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

    public Rental(int rid, String vlicense, String dlicense, TimePeriod timePeriod, int startOdometer, Card card, int confNo) {
        this.rid = rid;
        this.vlicense = vlicense;
        this.dlicense = dlicense;
        this.timePeriod = timePeriod;
        this.startOdometer = startOdometer;
        this.card = card;
        this.confNo = confNo;
    }

    public int getRid() {
        return rid;
    }

    public String getVlicense() {
        return vlicense;
    }

    public String getDlicense() {
        return dlicense;
    }

    public String getStart() {
        return timePeriod.getStartAsTimeDateString();
    }

    public String getEnd() {
        return timePeriod.getEndAsTimeDateString();
    }

    public int getStartOdometer() {
        return startOdometer;
    }

    public Card getCard() {
        return card;
    }

    public int getConfNo() {
        return confNo;
    }
}
