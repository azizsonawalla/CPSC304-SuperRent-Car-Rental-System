package model.Entities;

public class Location {
    //Primary key for Location is both city an location
    public Location(){

    }

    public Location(String city, String location){
        this.city = city;
        this.location = location;
    }

    public String city;
    public String location;

}
