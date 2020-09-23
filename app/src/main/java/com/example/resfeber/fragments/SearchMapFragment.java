package com.example.resfeber.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resfeber.R;
import com.example.resfeber.adapters.MyEventsAdapter;
import com.example.resfeber.api.APIClient;
import com.example.resfeber.api.APIInterface;
import com.example.resfeber.db.MyDatabase;
import com.example.resfeber.model.Event;
import com.example.resfeber.responses.EventResponse;
import com.example.resfeber.responses.MyResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.resfeber.fragments.SearchManualFragment.round;
import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchMapFragment extends Fragment implements OnMapReadyCallback {

    private MyEventsAdapter mMyEventsAdapter;
    @BindView(R.id.recyclerView1)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar;
    private MyDatabase database;
    private List<Event> events;
    private Location lastLocation;
    private APIInterface apiInterface;

    MapView mapView;
    GoogleMap map;

    public SearchMapFragment() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View searchView = inflater.inflate(R.layout.fragment_search_map, container, false);
        ButterKnife.bind(this, searchView);

        database = MyDatabase.getInstance(getApplicationContext());

        mapView = searchView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        boolean network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!network_enabled) {
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings, ((DialogInterface.OnClickListener) (paramDialogInterface, paramInt) -> getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))))
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }

        lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLocation != null) {
            Call<MyResponse> call = apiInterface.doGetListEventsMyLocation(lastLocation.getLatitude() + "," + lastLocation.getLongitude());

            call.enqueue(new Callback<MyResponse>() {

                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                    MyResponse embeddedResponse = response.body();
                    if (embeddedResponse != null) {
                        EventResponse embedded = embeddedResponse.getEmbedded();
                        events = embedded.getEvents();
                        progressBar.setVisibility(View.GONE);
                        if (events != null) {
                            mMyEventsAdapter = new MyEventsAdapter(getActivity(), getContext(), events, false, database);
                            mRecyclerView.setAdapter(mMyEventsAdapter);
                        }
                    } else {
                        mMyEventsAdapter = new MyEventsAdapter(getActivity(), getContext(), null, false, database);
                        mRecyclerView.setAdapter(mMyEventsAdapter);
                    }
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {
                    call.cancel();
                }
            });
        }


        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null && lastLocation != null) {
                    if (round(location.getLatitude(), 2) != round(lastLocation.getLatitude(), 2) && round(location.getLongitude(), 2) != round(lastLocation.getLongitude(), 2)) {
                        Call<MyResponse> call = apiInterface.doGetListEventsMyLocation(location.getLatitude() + "," + location.getLongitude());

                        call.enqueue(new Callback<MyResponse>() {

                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                MyResponse embeddedResponse = response.body();
                                if (embeddedResponse != null) {
                                    EventResponse embedded = embeddedResponse.getEmbedded();
                                    events = embedded.getEvents();
                                    progressBar.setVisibility(View.GONE);
                                    if (events != null) {
                                        mMyEventsAdapter = new MyEventsAdapter(getActivity(), getContext(), events, false, database);
                                        mRecyclerView.setAdapter(mMyEventsAdapter);
                                    }
                                } else {
                                    mMyEventsAdapter = new MyEventsAdapter(getActivity(), getContext(), null, false, database);
                                    mRecyclerView.setAdapter(mMyEventsAdapter);
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                call.cancel();
                            }
                        });
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });

        return searchView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        map = googleMap;
        map.getUiSettings().setAllGesturesEnabled(true);
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (lastLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 15));
            EventDetailsFragment.addEventMarker(map, getContext(), R.drawable.ic_small_pin, new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), getString(R.string.currentLocation));
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null && lastLocation != null) {
                    if (round(location.getLatitude(), 2) != round(lastLocation.getLatitude(), 2) && round(location.getLongitude(), 2) != round(lastLocation.getLongitude(), 2)) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                        EventDetailsFragment.addEventMarker(map, getContext(), R.drawable.ic_small_pin, new LatLng(location.getLatitude(), location.getLongitude()), getString(R.string.currentLocation));
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });
    }

    @SuppressLint("MissingPermission")
    @OnClick(R.id.current_address)
    public void submitCurrentLocation(View view) {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (lastLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 3));
            EventDetailsFragment.addEventMarker(map, getContext(), R.drawable.ic_small_pin, new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), getString(R.string.currentLocation));
            if (events != null) {
                for (Event event : events) {
                    EventDetailsFragment.addEventMarker(map, getContext(), R.drawable.ic_small_marker, new LatLng(event.getEmbedded().getVenues().get(0).getLocation().getLat().doubleValue(), event.getEmbedded().getVenues().get(0).getLocation().getLng().doubleValue()), event.getTitle());
                }
            }
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null && lastLocation != null) {
                    if (round(location.getLatitude(), 2) != round(lastLocation.getLatitude(), 2) && round(location.getLongitude(), 2) != round(lastLocation.getLongitude(), 2)) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 3));
                        EventDetailsFragment.addEventMarker(map, getContext(), R.drawable.ic_small_pin, new LatLng(location.getLatitude(), location.getLongitude()), getString(R.string.currentLocation));
                        if (events != null) {
                            for (Event event : events) {
                                EventDetailsFragment.addEventMarker(map, getContext(), R.drawable.ic_small_marker, new LatLng(event.getEmbedded().getVenues().get(0).getLocation().getLat().doubleValue(), event.getEmbedded().getVenues().get(0).getLocation().getLng().doubleValue()), event.getTitle());
                            }
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });
    }

    @OnClick(R.id.floatingActionButton)
    public void submitSearchMap(View view) {
        LatLng centerLatLang = map.getProjection().getVisibleRegion().latLngBounds.getCenter();
        Call<MyResponse> call = apiInterface.doGetListEventsMyLocation(centerLatLang.latitude + "," + centerLatLang.longitude);
        call.enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                MyResponse embeddedResponse = response.body();
                if (embeddedResponse != null) {
                    EventResponse embedded = embeddedResponse.getEmbedded();
                    events = embedded.getEvents();
                    if (events != null) {
                        mMyEventsAdapter.refresh(events);
                    }
                } else {
                    mMyEventsAdapter.refresh(null);
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}