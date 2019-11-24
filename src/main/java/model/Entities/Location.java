package model.Entities;

public class Location {
    //Primary key for Location is both city an location

    public String city;
    public String location;

    public Location(String city, String location) {
        this.city = city;
        this.location = location;
    }

    public Location() {}

    public String toString() {
        return String.format("%s, %s", location.trim(), city.trim());
    }

    public static Location fromString(String input) throws Exception {
        String[] parts = input.trim().split(",");
        return new Location(parts[1].trim(), parts[0].trim());
    }
}
