package model.Entities;

public class Card {
    public long cardNo;
    public String cardName;
    public int expDate;

    public Card(){}

    public Card(long cardNumber, String cardName, int expDate){
        this.cardNo = cardNumber;
        this.cardName = cardName;
        this.expDate = expDate;
    }
}
