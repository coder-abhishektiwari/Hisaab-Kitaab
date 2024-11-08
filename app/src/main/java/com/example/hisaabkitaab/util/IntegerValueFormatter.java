package com.example.hisaabkitaab.util;

import com.github.mikephil.charting.formatter.ValueFormatter;


public class IntegerValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return String.valueOf((int) value); // Convert float to integer and return as string
    }
}

