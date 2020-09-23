package com.example.resfeber.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class SearchViewPagerAdapter extends FragmentStateAdapter {

    private final int MAX_CAPACITY = 2;
    private ArrayList<Fragment> fragments = new ArrayList<>(MAX_CAPACITY);

    public SearchViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return MAX_CAPACITY;
    }
}