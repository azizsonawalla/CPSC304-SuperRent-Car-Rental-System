package model.Entities;

public class Vehicle {

    public enum VehicleStatus {
        RENTED,
        AVAILABLE
    }

    public Vehicle(int vid, String vlicense, String make, String model, int year, String color, int odometer, String vtname, VehicleStatus status, Location location) {
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

    public Vehicle(){

    }

    //vid (vehicle ID) must be unique
    public int vid;

    //License plate (vlicense) is the primary key for Vehicle
    public String vlicense;

    //Vehicle characteristics
    public String make;
    public String model;
    public int year;
    public String color;
    public int odometer;

    //Status of the car, true = available for rent, false = rented out
    public VehicleStatus status;

    //Identifies the VehicleType of a Vehicle
    public String vtname;

    //Identifies the Location of a Vehicle
    public Location location;
}
