package model.Orchestrator;

import model.Entities.Location;
import model.Entities.Rental;
import model.Entities.Reservation;
import model.Entities.VehicleType;

import java.util.Map;

public class RentalReport {

    public Map<Reservation, Rental> rentalsStartedToday; // First ordered by branch, then by vehicle type
    public Map<VehicleType, Integer> countOfRentalsByVT; // Number of rentals started today for each vehicle type
    public Map<Location, Integer> countOfRentalsByLocation; //Number of rentals started today for each location
    public Integer totalRentalsToday; // Sum of all rentals started today
}
