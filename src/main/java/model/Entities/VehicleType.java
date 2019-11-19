package model.Entities;

public class VehicleType {
    //the name of the VehicleType (vtname) is the primary key
    public String vtname;
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

    public VehicleType(String name) {
        this.vtname = name;
    }

    public VehicleType(String vtname, String features, int wrate, int drate, int hrate, int wirate, int dirate, int hirate, int krate) {
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
}
