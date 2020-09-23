package com.example.resfeber.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.resfeber.R;
import com.example.resfeber.activities.TicketsActivity;
import com.example.resfeber.api.APIClient;
import com.example.resfeber.api.APIInterface;
import com.example.resfeber.db.MyDatabase;
import com.example.resfeber.fragments.EventDetailsFragment;
import com.example.resfeber.model.Event;
import com.example.resfeber.model.Image;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.ViewHolder> {

    private FragmentActivity activity;
    private List<Event> events;
    private boolean canExpand;
    private Context context;
    public static final String KEY = "details";
    private final String IMAGE_SIZE = "TABLET_LANDSCAPE_LARGE";
    public final static String NO_TICKETS = "NO TICKETS";

    public MyEventsAdapter(FragmentActivity activity, Context context, List<Event> events, boolean canExpand, MyDatabase database) {
        this.activity = activity;
        this.events = events;
        this.canExpand = canExpand;
        this.context = context;
    }

    @NonNull
    @Override
    public MyEventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyEventsAdapter.ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event, position);
    }

    @Override
    public int getItemCount() {
        if (events == null) {
            return 0;
        }
        return events.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        if (holder.mapCurrent != null) {
            holder.mapCurrent.clear();
            holder.mapCurrent.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        @BindView(R.id.titleText)
        TextView title;
        @BindView(R.id.expandImage)
        ImageView expandImage;
        @BindView(R.id.infoText)
        TextView infoText;
        @BindView(R.id.map_place)
        FrameLayout mapLayout;
        @BindView(R.id.shrinkImage)
        ImageView shrinkImage;
        @BindView(R.id.eventImage)
        ImageView eventImage;
        @BindView(R.id.urlInfo)
        TextView urlDetails;
        @BindView(R.id.ticket)
        TextView ticketHere;
        @BindView(R.id.no)
        TextView noInfo;
        @BindView(R.id.ticketsNumber)
        TextView ticketsNum;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.url)
        TextView url;
        @BindView(R.id.sp1)
        FrameLayout space1;
        @BindView(R.id.sp2)
        FrameLayout space2;
        @BindView(R.id.sp3)
        FrameLayout space3;
        @BindView(R.id.space4)
        FrameLayout space4;
        @BindView(R.id.startDate)
        TextView startDate;
        @BindView(R.id.sp4)
        FrameLayout space5;
        @BindView(R.id.textView5)
        TextView startDateText;
        GoogleMap mapCurrent;
        MapView map;
        @BindView(R.id.sp5)
        FrameLayout space6;
        @BindView(R.id.priceSum)
        TextView price;
        @BindView(R.id.priceText)
        TextView priceText;

        private Event event;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            map = itemView.findViewById(R.id.map);
        }

        public void initializeMapView() {
            if (map != null) {
                map.onCreate(null);
                map.onResume();
                map.getMapAsync(this);
            }
        }

        @OnClick(R.id.urlInfo)
        public void openLink(View view) {
            String url = event.getUrlDetails();

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);

            intent.setData(Uri.parse(url));

            activity.startActivity(intent);
        }

        @OnClick(R.id.ticket)
        public void seeTickets(View view) {
            Intent ticketActivity = new Intent(activity, TicketsActivity.class);
            ticketActivity.putExtra(NO_TICKETS, event.getNoOfTickets());
            activity.startActivity(ticketActivity);
        }

        public void bind(final Event event, final int position) {
            this.event = event;

            initializeMapView();

            title.setText(event.getTitle());

            final String urlImages = "events/" + event.getIdEvent() + "/images.json?apikey=" + APIInterface.apiKey;

            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

            Call<Event> call = apiInterface.doGetImages(urlImages);
            call.enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {
                    if (response.body() != null) {
                        List<Image> imagesEvent = response.body().getImages();
                        if (imagesEvent != null) {
                            List<Image> image = imagesEvent
                                    .stream()
                                    .filter(img -> img.getUrl().contains(IMAGE_SIZE))
                                    .collect(Collectors.toList());

                            Glide.with(context)
                                    .load(image.get(0).getUrl())
                                    .into(eventImage);

                        }
                    }
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {

                }
            });

            if (canExpand) {

                boolean isExpanded = event.isExpanded();

                infoText.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                urlDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                mapLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                shrinkImage.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                expandImage.setVisibility(!isExpanded ? View.VISIBLE : View.GONE);
                ticketHere.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                ticketsNum.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                noInfo.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                url.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                description.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                space1.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                space2.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                space3.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                space4.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                space5.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                startDate.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                startDateText.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                price.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                priceText.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                space6.setVisibility(isExpanded ? View.VISIBLE : View.GONE);


                if (event.getDescription() != null) {
                    infoText.setText(event.getDescription());
                }

                ticketsNum.setText(String.valueOf(event.getNoOfTickets()));

                final String SEPARATOR = "-";
                String[] date = event.getStartDate().split(SEPARATOR);
                String dateForDisplay = date[2] + SEPARATOR + date[1] + SEPARATOR + date[0];
                startDate.setText(dateForDisplay);

                String priceDisplay = event.getPrice() + " " + event.getCurrency();
                price.setText(priceDisplay);

                expandImage.setOnClickListener(v -> {
                    boolean expanded = event.isExpanded();
                    event.setExpanded(!expanded);
                    notifyItemChanged(position);
                });

                shrinkImage.setOnClickListener(v -> {
                    boolean expanded = event.isExpanded();
                    event.setExpanded(!expanded);
                    notifyItemChanged(position);
                });
            } else {
                infoText.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                shrinkImage.setVisibility(View.GONE);
                expandImage.setVisibility(View.GONE);
                urlDetails.setVisibility(View.GONE);
                ticketHere.setVisibility(View.GONE);
                noInfo.setVisibility(View.GONE);
                ticketsNum.setVisibility(View.GONE);
                url.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                space1.setVisibility(View.GONE);
                space2.setVisibility(View.GONE);
                space3.setVisibility(View.GONE);
                space4.setVisibility(View.GONE);
                space5.setVisibility(View.GONE);
                startDate.setVisibility(View.GONE);
                startDateText.setVisibility(View.GONE);
                priceText.setVisibility(View.GONE);
                price.setVisibility(View.GONE);
                space6.setVisibility(View.GONE);

                itemView.setOnClickListener(v -> {
                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                    EventDetailsFragment fragment = new EventDetailsFragment();
                    Bundle bundle = new Bundle();
                    if (event.getImages() != null) {
                        List<Image> image = event.getImages()
                                .stream()
                                .filter(img -> img.getUrl().contains(IMAGE_SIZE))
                                .collect(Collectors.toList());
                        event.setUrlImage(image.get(0).getUrl());
                    }
                    bundle.putSerializable(KEY, event);
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.my_current_fragment, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                });

            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (canExpand) {
                MapsInitializer.initialize(getApplicationContext());
                mapCurrent = googleMap;
                mapCurrent.getUiSettings().setAllGesturesEnabled(false);
                mapCurrent.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getLat(), event.getLng()), 15));
                EventDetailsFragment.addEventMarker(mapCurrent, context, R.drawable.ic_marker, new LatLng(event.getLat(), event.getLng()), event.getVenueName());
            }
        }
    }

    public void refresh(List<Event> eventList) {
        events = eventList;
        notifyDataSetChanged();
    }
}
