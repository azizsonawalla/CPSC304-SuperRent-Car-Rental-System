package model.Orchestrator;

import model.Entities.Location;
import model.Entities.VehicleType;

public class VTSearchResult {

    public VehicleType vt;
    public Location location;
    public Integer numAvail;

    public VTSearchResult(VehicleType vt, Location location, Integer numAvail) {
        this.vt = vt;
        this.location = location;
        this.numAvail = numAvail;
    }
}
