package model.Orchestrator;

import javafx.util.Pair;
import model.Entities.Location;
import model.Entities.Rental;
import model.Entities.Reservation;

import java.util.List;
import java.util.Map;

public class RentalReport {

    public List<Pair<Reservation, Rental>> rentalsStartedToday; // First ordered by branch, then by vehicle type
    public Map<String, Integer> countOfRentalsByVT; // Number of rentals started today for each vehicle type
    public Map<Location, Integer> countOfRentalsByLocation; //Number of rentals started today for each location
    public Integer totalRentalsToday; // Sum of all rentals started today
}
