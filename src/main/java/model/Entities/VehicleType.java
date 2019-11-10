package model.Entities;

public class VehicleType {
    //the name of the VehicleType (vtname) is the primary key
    public String vtname;
    public String features;

    //weekly, daily, and hourly rate for a VehicleType
    public int wrate;
    public int drate;
    public int hrate;

    //weekly, daily, and hourly insurance rate for a VehicleType
    public int wirate;
    public int dirate;
    public int hirate;

    //km rate for a VehicleType
    public int krate;
}
