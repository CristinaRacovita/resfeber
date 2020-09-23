package com.example.resfeber.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resfeber.R;
import com.example.resfeber.adapters.FilterAdapter;
import com.example.resfeber.adapters.MyEventsAdapter;
import com.example.resfeber.model.Event;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterDialogFragment extends DialogFragment {
    @BindView(R.id.cityRecyclerView)
    RecyclerView mRecyclerViewCity;
    @BindView(R.id.countryRecyclerView)
    RecyclerView mRecyclerViewCountry;
    @BindView(R.id.apply)
    Button applyButton;

    private List<Event> events;
    private MyEventsAdapter myEventsAdapter;
    private FilterAdapter filterAdapterCity;
    private FilterAdapter filterAdapterCountry;

    public FilterDialogFragment(List<Event> events, MyEventsAdapter myEventsAdapter) {
        this.events = events;
        this.myEventsAdapter = myEventsAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View filterFragment = inflater.inflate(R.layout.fragment_filter, container, false);

        ButterKnife.bind(this, filterFragment);

        mRecyclerViewCity.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewCountry.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> cities = new ArrayList<>();
        List<String> countries = new ArrayList<>();

        for (Event event : events) {
            if (!cities.contains(event.getCityName())) {
                cities.add(event.getCityName());
            }
            if (!countries.contains(event.getCountryName())) {
                countries.add(event.getCountryName());
            }
        }

        filterAdapterCountry = new FilterAdapter(countries);
        mRecyclerViewCountry.setAdapter(filterAdapterCountry);

        filterAdapterCity = new FilterAdapter(cities);
        mRecyclerViewCity.setAdapter(filterAdapterCity);

        return filterFragment;
    }

    @OnClick(R.id.apply)
    public void submitFilters(View view) {
        List<CheckBox> checkBoxesCity = filterAdapterCity.getCheckBoxes();
        List<CheckBox> checkBoxesCountry = filterAdapterCountry.getCheckBoxes();

        List<Event> eventsRes = new ArrayList<>();

        boolean city = false;
        boolean country = false;

        for (CheckBox checkBox : checkBoxesCity) {
            if (checkBox.isChecked()) {
                city = true;
                break;
            }
        }

        for (CheckBox checkBox : checkBoxesCountry) {
            if (checkBox.isChecked()) {
                country = true;
                break;
            }
        }

        for (Event event : events) {
            if (city && country) {
                for (int i = 0; i < checkBoxesCity.size(); i++) {
                    for (int j = 0; j < checkBoxesCountry.size(); j++) {
                        if (checkBoxesCity.get(i).isChecked() && checkBoxesCountry.get(j).isChecked()) {
                            if (checkBoxesCity.get(i).getText().toString().equals(event.getCityName()) && checkBoxesCountry.get(j).getText().toString().equals(event.getCountryName())) {
                                if (!eventsRes.contains(event)) {
                                    eventsRes.add(event);
                                }
                            }
                        }
                    }
                }
            } else if (!city && !country) {
                eventsRes = events;
            } else {
                checkBoxesCity.addAll(checkBoxesCountry);
                for (CheckBox checkBox : checkBoxesCity) {
                    if (checkBox.isChecked()) {
                        if (checkBox.getText().toString().equals(event.getCityName()) || checkBox.getText().toString().equals(event.getCountryName())) {
                            if (!eventsRes.contains(event)) {
                                eventsRes.add(event);
                            }
                        }
                    }
                }
            }

        }

        dismiss();
        myEventsAdapter.refresh(null);

        List<Event> finalEventsRes = eventsRes;

        new Handler().postDelayed(() -> {
            myEventsAdapter.refresh(finalEventsRes);
        }, 500);
    }
}
