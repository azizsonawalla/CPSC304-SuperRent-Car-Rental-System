package model.Entities;

import java.sql.Timestamp;

public class Card {
    long CardNo;
    public String cardName;
    public Timestamp expDate;

    public Card(long cardNo, String cardName, Timestamp expDate) {
        CardNo = cardNo;
        this.cardName = cardName;
        this.expDate = expDate;
    }
}
