package com.example.resfeber.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "events")
public class Event implements Serializable {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id_event")
    @SerializedName("id")
    private String idEvent;
    @ColumnInfo(name = "title_event")
    @SerializedName("name")
    private String title;
    @ColumnInfo(name = "description_event")
    @SerializedName("pleaseNote")
    private String description;
    @Ignore
    private boolean isExpanded;
    @ColumnInfo(name = "url_event")
    @SerializedName("url")
    private String urlDetails;
    @Ignore
    @SerializedName("images")
    private List<Image> images;
    private String urlImage;
    private int noOfTickets;
    @Ignore
    @SerializedName("_embedded")
    private _Embedded embedded;
    private Double lat;
    private Double lng;
    private String venueName;
    private String cityName;
    private String countryName;
    @Ignore
    private Date dates;
    private String startDate;
    @Ignore
    private List<PriceRange> priceRanges;
    private String currency;
    private double price;

    public Event() {
    }

    public Event(String idEvent, String title, String description, String urlDetails, String urlImage, int noOfTickets, Double lat, Double lng, String venueName, String cityName, String countryName, String startDate,String currency, double price) {
        this.idEvent = idEvent;
        this.title = title;
        this.description = description;
        this.urlDetails = urlDetails;
        this.urlImage = urlImage;
        this.noOfTickets = noOfTickets;
        this.lat = lat;
        this.lng = lng;
        this.venueName = venueName;
        this.cityName = cityName;
        this.countryName = countryName;
        this.startDate = startDate;
        this.currency = currency;
        this.price = price;
    }

    public List<PriceRange> getPriceRanges() {
        return priceRanges;
    }

    public void setPriceRanges(List<PriceRange> priceRanges) {
        this.priceRanges = priceRanges;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDates() {
        return dates;
    }

    public void setDates(Date dates) {
        this.dates = dates;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getUrlDetails() {
        return urlDetails;
    }

    public void setUrlDetails(String urlDetails) {
        this.urlDetails = urlDetails;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public void setNoOfTickets(int noOfTickets) {
        this.noOfTickets = noOfTickets;
    }

    public _Embedded getEmbedded() {
        return embedded;
    }

    public void setEmbedded(_Embedded embedded) {
        this.embedded = embedded;
    }

    @Override
    public String toString() {
        return "Event{" +
                "idEvent='" + idEvent + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isExpanded=" + isExpanded +
                ", urlDetails='" + urlDetails + '\'' +
                ", images=" + images +
                ", urlImage='" + urlImage + '\'' +
                ", noOfTickets=" + noOfTickets +
                '}';
    }
}
