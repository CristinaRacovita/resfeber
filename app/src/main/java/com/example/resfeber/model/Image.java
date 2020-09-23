package com.example.resfeber.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Image implements Serializable {
    @SerializedName("url")
    private String url;

    public Image(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                '}';
    }
}
