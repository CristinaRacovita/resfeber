package com.example.resfeber.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.resfeber.R;
import com.example.resfeber.adapters.MyEventsAdapter;
import com.example.resfeber.db.MyDatabase;
import com.example.resfeber.model.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.resfeber.fragments.SearchManualFragment.round;
import static com.facebook.FacebookSdk.getApplicationContext;

public class EventDetailsFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.res)
    TextView res;
    @BindView(R.id.titleText)
    TextView title;
    @BindView(R.id.infoText)
    TextView info;
    @BindView(R.id.urlDetails)
    TextView urlDetails;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.space0)
    FrameLayout space0;
    @BindView(R.id.url)
    TextView url;
    @BindView(R.id.space)
    FrameLayout space;
    @BindView(R.id.startDate1)
    TextView startDate;
    @BindView(R.id.eventImage2)
    ImageView eventImage;
    @BindView(R.id.priceSum)
    TextView price;
    @BindView(R.id.total)
    TextView totalTextView;

    MapView mapView;
    GoogleMap map;

    private MyDatabase database;
    private Event event;

    private FirebaseAuth firebaseAuth;

    public static final String MY_EVENTS = "myEvents";

    public EventDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View eventDetailsView = inflater.inflate(R.layout.fragment_event_details, container, false);
        ButterKnife.bind(this, eventDetailsView);

        firebaseAuth = FirebaseAuth.getInstance();

        database = MyDatabase.getInstance(getApplicationContext());

        mapView = (MapView) eventDetailsView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        Bundle bundle = getArguments();
        event = (Event) bundle.getSerializable(MyEventsAdapter.KEY);

        if (event.getTitle() != null) {
            title.setText(event.getTitle());
        }
        if (event.getDescription() != null) {
            info.setText(event.getDescription());
        } else {
            info.setVisibility(View.GONE);
            space0.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        }
        if (event.getUrlDetails() != null) {
            urlDetails.setText(event.getUrlDetails());
        } else {
            space.setVisibility(View.GONE);
            urlDetails.setVisibility(View.GONE);
            url.setVisibility(View.GONE);
        }

        Glide.with(getContext())
                .load(event.getUrlImage())
                .into(eventImage);

        final String SEPARATOR = "-";
        String[] date = event.getDates().getStart().getLocalDate().split(SEPARATOR);
        String dateForDisplay = date[2] + SEPARATOR + date[1] + SEPARATOR + date[0];
        startDate.setText(dateForDisplay);

        if (event.getPriceRanges() != null) {
            String priceText = event.getPriceRanges().get(0).getMin() + " " + event.getPriceRanges().get(0).getCurrency();
            price.setText(priceText);
            String totalText = getString(R.string.initialQuantity) + " " + event.getPriceRanges().get(0).getCurrency();
            totalTextView.setText(totalText);
        } else {
            price.setText(getString(R.string.defaultPrice));
            String totalText = getString(R.string.initialQuantity) + " " + getString(R.string.defaultCurrency);
            totalTextView.setText(totalText);
        }

        return eventDetailsView;
    }

    @OnClick(R.id.less)
    public void subtract(View view) {
        int opRes = Integer.parseInt(res.getText().toString());
        if (opRes != 0) {
            String subRes = String.valueOf(opRes - 1);
            res.setText(subRes);

            if(event.getPriceRanges()!=null) {
                String totalText = round(Integer.parseInt(subRes) * event.getPriceRanges().get(0).getMin().doubleValue(), 2) + " " + event.getPriceRanges().get(0).getCurrency();
                totalTextView.setText(totalText);
            }
            else{
                String totalText = round(Integer.parseInt(subRes) * Double.parseDouble(getString(R.string.defaultValue)), 2) + " " + getString(R.string.defaultCurrency);
                totalTextView.setText(totalText);
            }
        }
    }

    @OnClick(R.id.more)
    public void addition(View view) {
        int opRes = Integer.parseInt(res.getText().toString());
        if (opRes <= 10) {
            String addRes = String.valueOf(opRes + 1);
            res.setText(addRes);

            if(event.getPriceRanges()!=null) {
                String totalText = round(Integer.parseInt(addRes) * event.getPriceRanges().get(0).getMin().doubleValue(), 2) + " " + event.getPriceRanges().get(0).getCurrency();
                totalTextView.setText(totalText);
            }
            else{
                String totalText = Integer.parseInt(addRes) * Double.parseDouble(getString(R.string.defaultValue)) + " " + getString(R.string.defaultCurrency);
                totalTextView.setText(totalText);
            }
        }
    }

    @OnClick(R.id.buyButton)
    public void buyTicket(View view) {
        if (isNetworkConnected()) {

            if (res.getText().toString().equals(getActivity().getResources().getString(R.string.initialQuantity))) {
                Toast.makeText(getContext(), getActivity().getString(R.string.minimQuantity), Toast.LENGTH_LONG).show();
            } else {
                event.setNoOfTickets(Integer.parseInt(res.getText().toString()));
                event.setVenueName(event.getEmbedded().getVenues().get(0).getNameVenue());
                event.setLat(event.getEmbedded().getVenues().get(0).getLocation().getLat().doubleValue());
                event.setLng(event.getEmbedded().getVenues().get(0).getLocation().getLng().doubleValue());
                event.setCityName(event.getEmbedded().getVenues().get(0).getCity().getName());
                event.setCountryName(event.getEmbedded().getVenues().get(0).getCountry().getName());
                event.setStartDate(event.getDates().getStart().getLocalDate());
                if (event.getPriceRanges() != null) {
                    event.setCurrency(event.getPriceRanges().get(0).getCurrency());
                    event.setPrice(event.getPriceRanges().get(0).getMin().intValue());
                } else {
                    event.setCurrency(getString(R.string.defaultCurrency));
                    event.setPrice(Double.parseDouble(getString(R.string.defaultValue)));
                }

                database.getAppDatabase().eventDao().insertEvent(event);

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference myRef = firebaseDatabase.getReference(MY_EVENTS).child(firebaseAuth.getCurrentUser().getUid());

                myRef.setValue(database.getAppDatabase().eventDao().selectAll());

                Toast.makeText(getContext(), getActivity().getString(R.string.ticketBought), Toast.LENGTH_LONG).show();

                MyEventsFragment myEventsFragment = new MyEventsFragment();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.my_current_fragment, myEventsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        } else {
            Toast.makeText(getContext(), getActivity().getString(R.string.notNetwork), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.urlDetails)
    public void openLink(View view) {
        String url = urlDetails.getText().toString();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        intent.setData(Uri.parse(url));

        getActivity().startActivity(intent);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void addEventMarker(GoogleMap map, Context context, int vectorResId, LatLng latLng, String title) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        MarkerOptions myEvent = new MarkerOptions();
        myEvent.position(latLng);
        myEvent.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        Marker eventMarker = map.addMarker(myEvent);
        eventMarker.setTitle(title);
        eventMarker.showInfoWindow();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        map = googleMap;
        if (event.getEmbedded().getVenues().get(0).getLocation() != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getEmbedded().getVenues().get(0).getLocation().getLat().doubleValue(), event.getEmbedded().getVenues().get(0).getLocation().getLng().doubleValue()), 15));
            addEventMarker(map, getContext(), R.drawable.ic_marker, new LatLng(event.getEmbedded().getVenues().get(0).getLocation().getLat().doubleValue(), event.getEmbedded().getVenues().get(0).getLocation().getLng().doubleValue()), event.getEmbedded().getVenues().get(0).getNameVenue());
        }
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


}