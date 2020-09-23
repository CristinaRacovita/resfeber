package com.example.resfeber.responses;

import com.google.gson.annotations.SerializedName;

public class MyResponse {
    @SerializedName("_embedded")
    private EventResponse embedded;

    public MyResponse(EventResponse embedded) {
        this.embedded = embedded;
    }

    public EventResponse getEmbedded() {
        return embedded;
    }

    public void setEmbedded(EventResponse embedded) {
        this.embedded = embedded;
    }

    @Override
    public String toString() {
        return "MyResponse{" +
                "embedded=" + embedded +
                '}';
    }
}
