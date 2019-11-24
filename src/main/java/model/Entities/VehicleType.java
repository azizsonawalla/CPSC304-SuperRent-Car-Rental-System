package model.Entities;

public class VehicleType {

    public VehicleType(String vtname, String features, double wrate, double drate, double hrate, double wirate, double dirate, double hirate, double krate) {
        this.vtname = vtname;
        this.features = features;
        this.wrate = wrate;
        this.drate = drate;
        this.hrate = hrate;
        this.wirate = wirate;
        this.dirate = dirate;
        this.hirate = hirate;
        this.krate = krate;
    }

    public VehicleType(){}

    public VehicleType(String vtname) {
        this.vtname = vtname;
    }

    //the name of the VehicleType (vtname) is the primary key
    public String vtname;
    public String features;

    //weekly, daily, and hourly rate for a VehicleType
    public double wrate;
    public double drate;
    public double hrate;

    //weekly, daily, and hourly insurance rate for a VehicleType
    public double wirate;
    public double dirate;
    public double hirate;

    //km rate for a VehicleType
    public double krate;


}
