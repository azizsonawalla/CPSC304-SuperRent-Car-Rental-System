package model.Entities;

public class Customer {

    //Customer cellphone must be unique
    public long cellphone;

    //Customer drivers license, primary key for Customer
    public String dlicense;

    public String name;
    public String address;

    public Customer(){}

    public Customer(long cellphone, String name, String address, String dlicense) {
        this.cellphone = cellphone;
        this.dlicense = dlicense;
        this.name = name;
        this.address = address;
    }

    public Customer(String dlicense){
        this.dlicense = dlicense;
    }
}


