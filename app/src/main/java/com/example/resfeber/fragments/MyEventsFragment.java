package com.example.resfeber.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resfeber.R;
import com.example.resfeber.adapters.MyEventsAdapter;
import com.example.resfeber.db.MyDatabase;
import com.example.resfeber.model.Event;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyEventsFragment extends Fragment {

    private MyEventsAdapter mMyEventsAdapter;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar;
    private MyDatabase database;


    public MyEventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = MyDatabase.getInstance(getApplicationContext());
        List<Event> events = database.getAppDatabase().eventDao().selectAll();

        View myEventsView;

        if (events.isEmpty()) {
            myEventsView = inflater.inflate(R.layout.fragment_empty_my_events, container, false);
        } else {
            myEventsView = inflater.inflate(R.layout.fragment_my_events, container, false);

            ButterKnife.bind(this, myEventsView);

            Toolbar toolbar = myEventsView.findViewById(R.id.toolbar);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mMyEventsAdapter = new MyEventsAdapter(getActivity(), getContext(), events, true, database);

            progressBar.setVisibility(View.GONE);

            mRecyclerView.setAdapter(mMyEventsAdapter);

            ImageView filterImage = toolbar.findViewById(R.id.filter);
            filterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FilterDialogFragment filterDialogFragment = new FilterDialogFragment(events, mMyEventsAdapter);
                    filterDialogFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.filter));
                }
            });

            EditText searchEditText = toolbar.findViewById(R.id.search);
            searchEditText.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            List<Event> events = database.getAppDatabase().eventDao().selectWithTitle(searchEditText.getText().toString());
                            progressBar.setVisibility(View.VISIBLE);
                            mMyEventsAdapter.refresh(null);

                            new Handler().postDelayed(() -> {
                                progressBar.setVisibility(View.GONE);
                                mMyEventsAdapter.refresh(events);

                            }, 1000);

                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        return myEventsView;
    }
}