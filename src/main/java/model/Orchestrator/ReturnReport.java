package model.Orchestrator;

import javafx.util.Pair;
import model.Entities.*;

import java.util.List;
import java.util.Map;

public class ReturnReport {


    public List<ReturnReportEntry> returnsCreatedToday; // First ordered by branch, then by vehicle type
    public Map<String, Pair<Integer, Double>> breakDownByVT; // Number of returns and revenue today for each vehicle type
    public Map<Location, Pair<Integer, Double>> breakDownByLocation; // Number of returns and revenue today for each location
    public Integer totalReturnsToday; // Sum of all returns today
    public Double totalReturnsRevenueToday; // Sum of all returns revenue today
}

