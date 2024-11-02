package com.example.hisaabkitaab.HomeContents;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.hisaabkitaab.CustomDateValueFormatter;
import com.example.hisaabkitaab.DBHelper;
import com.example.hisaabkitaab.IntegerValueFormatter;
import com.example.hisaabkitaab.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ChartHelper {
    private Context context;
    Dialog reportsDialog;
    LineChart expenseChart, incomeChart;
    PieChart pieChart;

    public ChartHelper(Context context, Dialog reportsDialog, LineChart expenseChart, LineChart incomeChart, PieChart pieChart) {
        this.context = context;
        this.reportsDialog = reportsDialog;
        this.expenseChart = expenseChart;
        this.incomeChart = incomeChart;
        this.pieChart = pieChart;
    }
    public Map<String, Float> getTransactionTypeData() {
        return getTransactionTypeData(context);
    }


    public void filterChartData(String selectedTimeRange) {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        // Adjust the start date based on selected time range
        switch (selectedTimeRange) {
            case "Last 7 Days":
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case "Last 30 Days":
                calendar.add(Calendar.DAY_OF_YEAR, -30);
                break;
            case "Last 6 Months":
                calendar.add(Calendar.MONTH, -6);
                break;
            case "Last Year":
                calendar.add(Calendar.YEAR, -1);
                break;
        }
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        // Fetch filtered data and update the chart

        DBHelper dbHelper = new DBHelper(context);
        List<Entry> expenseEntries = dbHelper.getTransactionsBetweenDates(startDate, endDate, "Paid to ", "Given on lend to ");
        List<Entry> incomeEntries = dbHelper.getTransactionsBetweenDates(startDate, endDate, "Borrowed from ", "Received from ");
        // Update the charts with filtered data
        updateCharts(expenseEntries, incomeEntries);

    }
    public Map<String, Float> getTransactionTypeData(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, Float> typeData = new HashMap<>();

        Cursor cursor = db.rawQuery("SELECT TRANSACTION_TYPE, SUM(AMOUNT) AS TOTAL_AMOUNT FROM TRANSACTIONS GROUP BY TRANSACTION_TYPE", null);
        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor.getColumnIndexOrThrow("TRANSACTION_TYPE"));
                float totalAmount = cursor.getFloat(cursor.getColumnIndexOrThrow("TOTAL_AMOUNT"));
                typeData.put(type, totalAmount);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return typeData;
    }


    public void lineChart(LineChart chart, List<Entry> entries, String label, String transactionType_1, String transactionType_2){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT TRANSACTION_DATE, AMOUNT FROM TRANSACTIONS WHERE TRANSACTION_TYPE = ? OR TRANSACTION_TYPE = ?",
                new String[]{transactionType_1, transactionType_2}
        );

        if (cursor.getCount() == 0) {
            Log.d("ChartDebug", "No data found for the given transaction types.");
            return;
        }
        long timestamp = 0;
        while (cursor.moveToNext()) {
            String transactionDate = cursor.getString(cursor.getColumnIndexOrThrow("TRANSACTION_DATE"));
            float amount = cursor.getFloat(cursor.getColumnIndexOrThrow("AMOUNT"));


            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy 'at' h:mm a", Locale.getDefault());
                Date date = dateFormat.parse(transactionDate);
                timestamp = date.getTime();
            } catch (ParseException e) {
                Log.e("ChartDebug", "Error parsing date: " + transactionDate, e);
                continue; // Skip this data point if parsing fails
            }
            entries.add(new Entry(timestamp, amount));
        }

        cursor.close();
        db.close();

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(context.getResources().getColor(R.color.blue));
        dataSet.setValueTextColor(context.getResources().getColor(R.color.black));

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new CustomDateValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new IntegerValueFormatter());

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setValueFormatter(new IntegerValueFormatter());
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }
    public void pieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : getTransactionTypeData().entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Transaction Types");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(context.getResources().getColor(R.color.black));
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Transaction Types");
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
    public void updateCharts(List<Entry> expenseEntries, List<Entry> incomeEntries) {
        if (reportsDialog != null) {
            lineChart(expenseChart, expenseEntries, "Expense", "Paid to ", "Given on lend to ");
            lineChart(incomeChart, incomeEntries, "Income", "Borrowed from ", "Received from ");
        } else {
            // Handle the case where reportsDialog is null (e.g.,show an error message)
            Log.e("ChartDebug", "reportsDialog is null in updateCharts");
        }
    }

}
