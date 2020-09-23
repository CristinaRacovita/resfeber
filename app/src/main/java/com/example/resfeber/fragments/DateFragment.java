package com.example.resfeber.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import com.example.resfeber.R;

import java.util.Calendar;

public class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnProcessData processDatePickerResult;
    private int hours;
    private int minutes;
    private int seconds;

    public DateFragment(OnProcessData processData) {
        processDatePickerResult = processData;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        hours = c.get(Calendar.HOUR);
        minutes = c.get(Calendar.MINUTE);
        seconds = c.get(Calendar.SECOND);

        return new DatePickerDialog(getActivity(), R.style.TimePickerTheme, this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        processDatePickerResult.processDatePickerResult(year, month, day, hours, minutes, seconds);
    }

    public interface OnProcessData {
        void processDatePickerResult(int year, int month, int day, int hours, int minutes, int seconds);
    }
}