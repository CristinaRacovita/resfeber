package com.example.resfeber.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class _Embedded implements Serializable {
    @SerializedName("venues")
    private List<Venue> venues;

    public _Embedded(List<Venue> venues) {
        this.venues = venues;
    }

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    @Override
    public String toString() {
        return "_Embedded{" +
                "venues=" + venues +
                '}';
    }
}
