package model.Entities;

public class Card {
    public long CardNo;
    public String cardName;
    public int expDate;

    public Card(){}

    public Card(long cardNumber, String cardName, int expDate){
        this.CardNo = cardNumber;
        this.cardName = cardName;
        this.expDate = expDate;
    }
}
