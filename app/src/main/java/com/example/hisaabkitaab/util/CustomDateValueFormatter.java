package com.example.hisaabkitaab.util;

import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomDateValueFormatter extends ValueFormatter {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault()); // Or your desired format

    @Override
    public String getFormattedValue(float value) {
        long timestamp = (long) (value); // Convert x-axis value to timestamp
        Date date = new Date(timestamp); // Create Date object from timestamp
        return dateFormat.format(date); // Format the date
    }
}