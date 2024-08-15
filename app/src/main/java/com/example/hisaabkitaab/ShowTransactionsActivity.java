package com.example.hisaabkitaab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowTransactionsActivity extends AppCompatActivity {
RecyclerView recyclerView;
ArrayList<TransactionModel> transactionsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_transactions);

        // Setting up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DBHelper db = new DBHelper(this);
        TextView total = findViewById(R.id.txt_total_balance);
        Double balance = db.getCurrentBalance();
        Log.d("abhishek", "your balance: " + balance);
        String sign;
        if (balance <0){
            total.setTextColor(getResources().getColor(R.color.red));
            sign = " - ";
        } else if (balance>0) {
            total.setTextColor(getResources().getColor(R.color.green));
            sign =" + ";
        } else{
            total.setTextColor(getResources().getColor(R.color.red));
            sign = " ";
        }

        Double currentBalance = Math.abs(balance);
        String t = "Total Balance you have:"+sign+"₹"+ currentBalance.toString();
        total.setText(t);

        // Initializing RecyclerView
        recyclerView = findViewById(R.id.recycler_view_all_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Setting layout manager

        showTransactions(); // Display transactions
    }

    private void showTransactions() {
        DBHelper db = new DBHelper(this);
        transactionsList = db.getAllTransactions(); // Fetch transactions from database
        TransactionsAdapter adapter = new TransactionsAdapter(this, transactionsList);
        recyclerView.setAdapter(adapter);
        db.close(); // Close the database connection
    }
}