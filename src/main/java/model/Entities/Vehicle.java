package model.Entities;

public class Vehicle {
    //vid (vehicle ID) must be unique
    int vid;

    //License plate (vlicense) is the primary key for Vehicle
    String vlicense;

    //Vehicle characteristics
    String make;
    String model;
    int year;
    String color;
    int odometer;
    boolean status;

    //Identifies the VehicleType of a Vehicle
    String vtname;

    //Identifies the Location of a Vehicle
    Location location;
}
