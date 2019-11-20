package model.Entities;

public class Customer {

    public Customer(){

    }

    public Customer(long cellphone, String name, String address, String dlicense) {
        this.cellphone = cellphone;
        this.dlicense = dlicense;
        this.name = name;
        this.address = address;
    }

    //Customer cellphone must be unique
    public long cellphone;

    //Customer drivers license, primary key for Customer
    public String dlicense;

    public String name;
    public String address;
}


