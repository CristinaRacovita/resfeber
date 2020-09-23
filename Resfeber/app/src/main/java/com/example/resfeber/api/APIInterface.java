package com.example.resfeber.api;

import com.example.resfeber.model.Event;
import com.example.resfeber.responses.MyResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface APIInterface {

    String apiKey = "4IlaotpOIcINPRQWn2HhTjIKPYUqqaol";

    @GET("events?apikey=" + apiKey + "&locale=*")
    Call<MyResponse> doGetListEventsMyLocation(@Query(value = "latlong", encoded = true) String latlng);

    @GET("events?apikey=" + apiKey + "&locale=*")
    Call<MyResponse> doGetListFilteredEvents(@QueryMap Map<String, String> params);

    @GET
    Call<Event> doGetImages(@Url String url);
}
