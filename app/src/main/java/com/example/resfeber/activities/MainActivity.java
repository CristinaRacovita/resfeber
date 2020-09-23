package com.example.resfeber.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.resfeber.R;
import com.example.resfeber.db.MyDatabase;
import com.example.resfeber.fragments.EventDetailsFragment;
import com.example.resfeber.fragments.ExploreFragment;
import com.example.resfeber.fragments.MyEventsFragment;
import com.example.resfeber.fragments.ProfileFragment;
import com.example.resfeber.model.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    private MyDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 40);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 41);

            return;
        }

        database = MyDatabase.getInstance(getApplicationContext());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    DatabaseReference myRef = firebaseDatabase.getReference(EventDetailsFragment.MY_EVENTS).child(firebaseAuth.getCurrentUser().getUid());

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<Event> events = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Event event = dataSnapshot.getValue(Event.class);
                                events.add(event);
                            }
                            database.getAppDatabase().eventDao().insertAll(events);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        };

        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_menu:
                    ProfileFragment profileFragment = new ProfileFragment();
                    makeTransaction(profileFragment);
                    break;
                case R.id.action_my_events:
                    MyEventsFragment myEventsFragment = new MyEventsFragment();
                    makeTransaction(myEventsFragment);
                    break;
                case R.id.action_explore:
                    ExploreFragment exploreFragment = new ExploreFragment();
                    makeTransaction(exploreFragment);
                    break;
            }
            return true;
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        MyEventsFragment myEventsFragment = new MyEventsFragment();
        makeTransaction(myEventsFragment);
    }

    public void makeTransaction(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.my_current_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.my_current_fragment);
        if (!(fragment instanceof EventDetailsFragment)) {
            finish();
        } else {
            ExploreFragment exploreFragment = new ExploreFragment();
            makeTransaction(exploreFragment);
        }
    }
}