package com.example.resfeber.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Location implements Serializable {
    @SerializedName("latitude")
    private Number lat;
    @SerializedName("longitude")
    private Number lng;

    public Location(Number lat, Number lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Number getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public Number getLng() {
        return lng;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Location{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
