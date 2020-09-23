package com.example.resfeber.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.resfeber.R;
import com.example.resfeber.adapters.SearchViewPagerAdapter;
import com.example.resfeber.db.MyDatabase;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ExploreFragment extends Fragment {

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager2 viewPager;
    private MyDatabase database;

    private enum ExploreMode {
        Search("Manual Search", R.drawable.ic_manual_search),
        Map("Map Search", R.drawable.ic_map_search);

        private String displayName;
        private int icon;

        ExploreMode(String displayName, int icon) {
            this.displayName = displayName;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return displayName;
        }

    }

    public ExploreFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View exploreView = inflater.inflate(R.layout.fragment_explore, container, false);
        ButterKnife.bind(this, exploreView);

        database = MyDatabase.getInstance(getApplicationContext());

        final SearchViewPagerAdapter adapter = new SearchViewPagerAdapter(this);

        SearchManualFragment searchManualFragment = new SearchManualFragment();
        SearchMapFragment searchMapFragment = new SearchMapFragment();
        adapter.addFragment(searchManualFragment);
        adapter.addFragment(searchMapFragment);

        viewPager.setAdapter(adapter);

        tabLayout.setTabTextColors(getContext().getColor(R.color.White), getContext().getColor(R.color.colorAccent));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tab.setText(ExploreMode.values()[position].displayName);
                    tab.setIcon(ExploreMode.values()[position].icon);
                }).attach();

        return exploreView;
    }
}