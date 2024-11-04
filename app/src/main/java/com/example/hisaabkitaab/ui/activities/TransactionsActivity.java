package com.example.hisaabkitaab.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.hisaabkitaab.Adapter.TransactionsAdapter;
import com.example.hisaabkitaab.DataBase.DBHelper;
import com.example.hisaabkitaab.R;
import com.example.hisaabkitaab.model.TransactionModel;

import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity implements TransactionsAdapter.OnTransactionDeleteListener {
    private RecyclerView recyclerView;
    private ArrayList<TransactionModel> transactionsList;
    private TransactionsAdapter adapter;
    private TextView totalBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_transactions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        totalBalance = findViewById(R.id.txt_total_balance);
        recyclerView = findViewById(R.id.recycler_view_all_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTransactions();
    }

    private void loadTransactions() {
        DBHelper db = new DBHelper(this);
        transactionsList = db.getAllTransactions();
        adapter = new TransactionsAdapter(this, transactionsList, this);
        recyclerView.setAdapter(adapter);
        db.close();
        updateBalanceIndicator();
    }

    @Override
    public void onTransactionDeleted() {
        updateBalanceIndicator();
    }

    public void updateBalanceIndicator() {
        DBHelper db = new DBHelper(this);
        double balance = db.getCurrentBalance();
        db.close();

        totalBalance.setTextColor(getResources().getColor(balance < 0 ? R.color.red : R.color.green));
        totalBalance.setText(String.format("Total Balance you have: %s ₹%.2f",
                balance < 0 ? "-" : "+", Math.abs(balance)));
    }
}
