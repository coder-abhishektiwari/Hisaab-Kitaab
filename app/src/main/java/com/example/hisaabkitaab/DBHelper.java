package com.example.hisaabkitaab;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private final Context context;

    private static final String DATABASE_NAME = "hisabkitab.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "TRANSACTIONS";
    private static final String TRANSACTION_ID = "TRANSACTION_ID";
    private static final String TRANSACTION_DATE = "TRANSACTION_DATE";
    private static final String TRANSACTOR = "TRANSACTOR";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String TRANSACTION_TYPE = "TRANSACTION_TYPE"; //paid/received/lending/borrow
    private static final String AMOUNT = "AMOUNT";
    private static final String BALANCE = "BALANCE";
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( "
                + TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TRANSACTION_DATE + " TEXT, "
                + TRANSACTOR + " TEXT, "
                + DESCRIPTION + " TEXT, "
                + TRANSACTION_TYPE + " TEXT, "
                + AMOUNT + " TEXT, "
                + BALANCE + " TEXT "
                + " )";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTransaction(TransactionModel model){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TRANSACTION_ID, model.getId());
        cv.put(TRANSACTION_DATE, model.getDate());
        cv.put(TRANSACTOR, model.getTransactor());
        cv.put(DESCRIPTION, model.getDescription());
        cv.put(TRANSACTION_TYPE, model.getType());
        cv.put(AMOUNT, model.getAmount());
        cv.put(BALANCE,model.getBalance());
        try{
            db.insert(TABLE_NAME, null, cv);
        }catch(SQLiteException e){
            Log.e("database", "Error inserting task into database", e);
            Toast.makeText(context, "Error inserting task into database", Toast.LENGTH_SHORT).show();
        }finally {
            db.close();
        }
    }
    public ArrayList<TransactionModel> getAllTransactions(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY "+ TRANSACTION_ID + " DESC";
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<TransactionModel> transactionList = new ArrayList<>();

        while (cursor.moveToNext()){
            TransactionModel model = new TransactionModel();
            model.setId(cursor.getString(0));
            model.setDate(cursor.getString(1));
            model.setTransactor(cursor.getString(2));
            model.setDescription(cursor.getString(3));
            model.setType(cursor.getString(4));
            model.setAmount(cursor.getString(5));
            model.setBalance(cursor.getString(6));

            transactionList.add(model);
        }
        db.close();
        return transactionList;
    }
    public ArrayList<TransactionModel> getAllTransactions(int limit){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY "+ TRANSACTION_ID + " DESC LIMIT " + limit;
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<TransactionModel> transactionList = new ArrayList<>();

        while (cursor.moveToNext()){
            TransactionModel model = new TransactionModel();
            model.setId(cursor.getString(0));
            model.setDate(cursor.getString(1));
            model.setTransactor(cursor.getString(2));
            model.setDescription(cursor.getString(3));
            model.setType(cursor.getString(4));
            model.setAmount(cursor.getString(5));
            model.setBalance(cursor.getString(6));

            transactionList.add(model);
        }
        db.close();
        return transactionList;
    }
    @SuppressLint("Range")
    public double[] getCurrentMonthSummary() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Get the first and last date of the current month
        String[] dateRange = getCurrentMonthDateRange();
        String firstDate = dateRange[0];
        String lastDate = dateRange[1];

        double totalSpending = 0.0;
        double totalReceived = 0.0;
        double currentBalance = 0.0;

        // Updated Spending Query
        String spendingQuery = "SELECT SUM(" + AMOUNT + ") AS totalSpending FROM " + TABLE_NAME +
                " WHERE " + TRANSACTION_DATE + " >= ? AND " + TRANSACTION_DATE + " <= ? AND (" +
                TRANSACTION_TYPE + " = 'Paid to ' OR " + TRANSACTION_TYPE + " = 'Given on lend to ')";
        Cursor spendingCursor = db.rawQuery(spendingQuery, new String[]{firstDate, lastDate});

        if (spendingCursor.moveToFirst()) {
            int columnIndex = spendingCursor.getColumnIndex("totalSpending");
            if (columnIndex != -1) {
                totalSpending = spendingCursor.getDouble(columnIndex);
                Log.d("v", "Total Spending: " + totalSpending);
            }
        } else {
            Log.d("DatabaseDebug", "No spending data found.");
        }
        spendingCursor.close();

        // Updated Received Query
        String receivedQuery = "SELECT SUM(" + AMOUNT + ") AS totalReceived FROM " + TABLE_NAME +
                " WHERE " + TRANSACTION_DATE + " >= ? AND " + TRANSACTION_DATE + " <= ? AND (" +
                TRANSACTION_TYPE + " = 'Borrowed from ' OR " + TRANSACTION_TYPE + " = 'Received from ')";
        Cursor receivedCursor = db.rawQuery(receivedQuery, new String[]{firstDate, lastDate});

        if (receivedCursor.moveToFirst()) {
            int columnIndex = receivedCursor.getColumnIndex("totalReceived");
            if (columnIndex != -1) {
                totalReceived = receivedCursor.getDouble(columnIndex);
                Log.d("DatabaseDebug", "Total Received: " + totalReceived);
            }
        } else {
            Log.d("DatabaseDebug", "No received data found.");
        }
        receivedCursor.close();

        // Query to get the current balance
        String balanceQuery = "SELECT " + BALANCE + " FROM " + TABLE_NAME +
                " ORDER BY " + TRANSACTION_ID + " DESC LIMIT 1";
        Cursor balanceCursor = db.rawQuery(balanceQuery, null);

        if (balanceCursor.moveToFirst()) {
            int columnIndex = balanceCursor.getColumnIndex(BALANCE);
            if (columnIndex != -1) {
                currentBalance = balanceCursor.getDouble(columnIndex);
                Log.d("DatabaseDebug", "Current Balance: " + currentBalance);
            }
        } else {
            Log.d("DatabaseDebug", "No balance data found.");
        }
        balanceCursor.close();

        // Return an array with totalSpending, totalReceived, and currentBalance
        return new double[]{totalSpending, totalReceived, currentBalance};
    }

    public String[] getCurrentMonthDateRange() {
        Calendar calendar = Calendar.getInstance();

        // Get the first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String firstDate = new SimpleDateFormat("d MMMM yyyy 'at' h:mm a", Locale.getDefault()).format(calendar.getTime());

        // Get the last day of the month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDate = new SimpleDateFormat("d MMMM yyyy 'at' h:mm a", Locale.getDefault()).format(calendar.getTime());

        Log.d("DateRange", "First Date: " + firstDate + " Last Date: " + lastDate);

        return new String[]{firstDate, lastDate};
    }



    public double getCurrentBalance() {
        double currentBalance = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + BALANCE + " FROM " + TABLE_NAME + " ORDER BY " + TRANSACTION_ID + " DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()){
            currentBalance = cursor.getDouble(0);
        }
        if (cursor != null){
            cursor.close();
        }
        return currentBalance;
    }
}
