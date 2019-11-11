package model.Entities;

public class VehicleType {
    //the name of the VehicleType (vtname) is the primary key
    String vtname;
    String features;

    //weekly, daily, and hourly rate for a VehicleType
    int wrate;
    int drate;
    int hrate;

    //weekly, daily, and hourly insurance rate for a VehicleType
    int wirate;
    int dirate;
    int hirate;

    //km rate for a VehicleType
    int krate;
}
