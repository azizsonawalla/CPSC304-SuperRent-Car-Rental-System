package model.Orchestrator;

import model.Entities.Location;
import model.Entities.Return;
import model.Entities.VehicleType;

import java.util.List;
import java.util.Map;

public class ReturnReport {

    List<Return> returnsCreatedToday; // First ordered by branch, then by vehicle type
    Map<VehicleType, Integer> countOfReturnsByVT; // Number of returns today for each vehicle type
    Map<VehicleType, Double> revenueOfReturnsByVT; // Revenue of returns today for each vehicle type
    Map<Location, Integer> countOfReturnsByLocation; // Number of returns today for each location
    Map<Location, Double> revenueOfReturnsByLocation; // Revenue of returns today for each location
    Integer totalReturnsToday; // Sum of all returns today
    Double totalReturnsRevenueToday; // Sum of all returns revenue today
}
