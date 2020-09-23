package com.example.resfeber.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resfeber.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private boolean city;
    private List<String> eventList;
    private List<CheckBox> checkBoxes;

    public FilterAdapter(List<String> eventList) {
        this.eventList = eventList;
        checkBoxes = new ArrayList<>();
    }

    @NonNull
    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_card, parent, false);
        return new FilterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterAdapter.ViewHolder holder, int position) {
        String eventString = eventList.get(position);
        holder.bind(eventString, checkBoxes);
    }

    @Override
    public int getItemCount() {
        if (eventList == null)
            return 0;
        return eventList.size();
    }

    public List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkBox)
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(String eventString, List<CheckBox> checkBoxes) {
            checkBox.setText(eventString);
            checkBoxes.add(checkBox);
        }
    }
}
