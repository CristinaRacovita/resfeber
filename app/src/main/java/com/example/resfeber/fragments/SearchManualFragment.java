package com.example.resfeber.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchManualFragment extends Fragment implements DateFragment.OnProcessData {

    @BindView(R.id.dateEditText)
    EditText dateText;
    private MyEventsAdapter mMyEventsAdapter;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private APIInterface apiInterface;
    @BindView(R.id.countryEditText)
    EditText countryText;
    @BindView(R.id.cityEditText)
    EditText cityText;
    private static String date;
    public static final String EMPTY_STRING = "";
    private Location lastLocation;

    private MyDatabase database;

    public SearchManualFragment() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View searchView = inflater.inflate(R.layout.fragment_search_manual, container, false);
        ButterKnife.bind(this, searchView);

        database = MyDatabase.getInstance(getApplicationContext());

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
                        List<Event> events = embedded.getEvents();
                        progressBar.setVisibility(View.GONE);
                        if (events != null) {
                            mMyEventsAdapter = new MyEventsAdapter(getActivity(), getContext(), events, false, database);
                            mRecyclerView.setAdapter(mMyEventsAdapter);
                        }
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
                                    List<Event> events = embedded.getEvents();
                                    progressBar.setVisibility(View.GONE);
                                    if (events != null) {
                                        mMyEventsAdapter = new MyEventsAdapter(getActivity(), getContext(), events, false, database);
                                        mRecyclerView.setAdapter(mMyEventsAdapter);
                                    }
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
    public void processDatePickerResult(int year, int month, int day, int hours, int minutes, int seconds) {
        date = "";
        if (hours < 12) {
            hours += 12;
        }
        if (month < 10 && day >= 10) {
            date = (year + "-0" + (month + 1) + "-" + day + "T" + hours + ":" + minutes + ":" + seconds + "Z");
        }
        if (day < 10 && month >= 10) {
            date = (year + "-" + (month + 1) + "-0" + day + "T" + hours + ":" + minutes + ":" + seconds + "Z");
        }
        if (day < 10 && month < 10) {
            date = (year + "-0" + (month + 1) + "-0" + day + "T" + hours + ":" + minutes + ":" + seconds + "Z");
        }
        if (day >= 10 && month >= 10) {
            date = (year + "-" + (month + 1) + "-" + day + "T" + hours + ":" + minutes + ":" + seconds + "Z");
        }

        String dateForDisplay = day + "/" + (month + 1) + "/" + year;

        dateText.setText(dateForDisplay);
    }

    @OnClick(R.id.dateEditText)
    public void pickADate(View view) {
        DateFragment dateFragment = new DateFragment(SearchManualFragment.this);
        dateFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.datePicker));
    }

    @OnClick(R.id.findEventsImage)
    public void searchSubmit(View view) {
        if (isNetworkConnected()) {
            final String COUNTRY_CODE_URL = "countryCode";
            final String CITY_URL = "city";
            final String DATE_URL = "startDateTime";

            Map<String, String> params = new LinkedHashMap<>();

            setNullEventList();
            progressBar.setVisibility(View.VISIBLE);

            if (dateText.getText().toString().equals(EMPTY_STRING) && cityText.getText().toString().equals(EMPTY_STRING) && !countryText.getText().toString().equals(EMPTY_STRING)) {
                params.put(COUNTRY_CODE_URL, getCountryCode(countryText.getText().toString()));
            }
            if (dateText.getText().toString().equals(EMPTY_STRING) && !cityText.getText().toString().equals(EMPTY_STRING) && !countryText.getText().toString().equals(EMPTY_STRING)) {
                params.put(CITY_URL, cityText.getText().toString());
                params.put(COUNTRY_CODE_URL, getCountryCode(countryText.getText().toString()));
            }
            if (dateText.getText().toString().equals(EMPTY_STRING) && !cityText.getText().toString().equals(EMPTY_STRING) && countryText.getText().toString().equals(EMPTY_STRING)) {
                params.put(CITY_URL, cityText.getText().toString());
            }
            if (!dateText.getText().toString().equals(EMPTY_STRING) && cityText.getText().toString().equals(EMPTY_STRING) && !countryText.getText().toString().equals(EMPTY_STRING)) {
                params.put(DATE_URL, date);
                params.put(COUNTRY_CODE_URL, getCountryCode(countryText.getText().toString()));
            }
            if (!dateText.getText().toString().equals(EMPTY_STRING) && !cityText.getText().toString().equals(EMPTY_STRING) && !countryText.getText().toString().equals(EMPTY_STRING)) {
                params.put(COUNTRY_CODE_URL, getCountryCode(countryText.getText().toString()));
                params.put(CITY_URL, cityText.getText().toString());
                params.put(DATE_URL, date);
            }
            if (!dateText.getText().toString().equals(EMPTY_STRING) && !cityText.getText().toString().equals(EMPTY_STRING) && countryText.getText().toString().equals(EMPTY_STRING)) {
                params.put(DATE_URL, date);
                params.put(CITY_URL, cityText.getText().toString());
            }
            if (!dateText.getText().toString().equals(EMPTY_STRING) && cityText.getText().toString().equals(EMPTY_STRING) && countryText.getText().toString().equals(EMPTY_STRING)) {
                params.put(DATE_URL, date);
            }

            Call<MyResponse> filterCall = apiInterface.doGetListFilteredEvents(params);

            filterCall.enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                    MyResponse embeddedResponse = response.body();
                    if (embeddedResponse != null) {
                        EventResponse embedded = embeddedResponse.getEmbedded();
                        if (embedded != null && !params.isEmpty()) {
                            List<Event> events = embedded.getEvents();
                            progressBar.setVisibility(View.GONE);
                            mMyEventsAdapter.refresh(events);
                        } else {
                            setNullEventList();
                        }
                    } else {
                        setNullEventList();
                    }
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {
                    call.cancel();
                }
            });
        } else {
            setNullEventList();
            Toast.makeText(getContext(), getActivity().getString(R.string.notNetwork), Toast.LENGTH_LONG).show();
        }
    }

    public void setNullEventList() {
        mMyEventsAdapter = new MyEventsAdapter(getActivity(), getContext(), null, false, database);
        mRecyclerView.setAdapter(mMyEventsAdapter);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private String getCountryCode(String countryName) {
        String[] isoCountryCodes = Locale.getISOCountries();
        for (String code : isoCountryCodes) {
            Locale locale = new Locale(EMPTY_STRING, code);
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry())) {
                return code;
            }
        }
        return EMPTY_STRING;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}