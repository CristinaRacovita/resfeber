package com.example.resfeber.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Venue implements Serializable {
    @SerializedName("name")
    private String nameVenue;
    @SerializedName("id")
    private String idVenue;
    @SerializedName("location")
    private Location location;
    private City city;
    private Country country;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getNameVenue() {
        return nameVenue;
    }

    public void setNameVenue(String nameVenue) {
        this.nameVenue = nameVenue;
    }

    public String getIdVenue() {
        return idVenue;
    }

    public void setIdVenue(String idVenue) {
        this.idVenue = idVenue;
    }

    public Venue(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "location=" + location +
                '}';
    }
}
