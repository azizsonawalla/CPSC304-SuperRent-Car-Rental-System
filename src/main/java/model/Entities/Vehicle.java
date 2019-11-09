package model.Entities;

public class Vehicle {
    //vid (vehicle ID) must be unique
    int vid;

    //vlicense is the primary key for Vehicle
    int vlicense;

    //Vehicle characteristics
    String make;
    String model;
    int year;
    String color;
    int odometer;
    String status;

    //Identifies the VehicleType of a Vehicle
    String vtname;

    //Identifies the Location of a Vehicle
    Location location;
}
