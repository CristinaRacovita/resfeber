package com.example.resfeber.responses;

import com.example.resfeber.model.Event;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventResponse {
    @SerializedName("events")
    private List<Event> events;

    public EventResponse(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "EventResponse{" +
                "events=" + events +
                '}';
    }
}
