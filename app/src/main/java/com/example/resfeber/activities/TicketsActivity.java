package com.example.resfeber.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resfeber.R;
import com.example.resfeber.adapters.MyEventsAdapter;
import com.example.resfeber.adapters.TicketAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketsActivity extends AppCompatActivity {

    @BindView(R.id.ticketsRecyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);
        ButterKnife.bind(this);

        int noOfTickets = getIntent().getIntExtra(MyEventsAdapter.NO_TICKETS, 0);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        TicketAdapter mTicketAdapter = new TicketAdapter(noOfTickets);

        mRecyclerView.setAdapter(mTicketAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}