package model.Entities;

public class Vehicle {
    //vid (vehicle ID) must be unique
    int vid;

    //License plate (vlicense) is the primary key for Vehicle
    public String vlicense;

    //Vehicle characteristics
    public String make;
    public String model;
    public int year;
    public String color;
    public int odometer;
    public boolean status;

    //Identifies the VehicleType of a Vehicle
    String vtname;

    //Identifies the Location of a Vehicle
    Location location;

    public Vehicle(int vid, String vlicense, String make, String model, int year, String color, int odometer, boolean status, String vtname, Location location) {
        this.vid = vid;
        this.vlicense = vlicense;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.odometer = odometer;
        this.status = status;
        this.vtname = vtname;
        this.location = location;
    }
}
