package com.example.hisaabkitaab.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hisaabkitaab.model.Transaction;
import com.example.hisaabkitaab.DataBase.DBHelper;

import java.util.ArrayList;

public class TransactionsViewModel extends ViewModel {
    private final DBHelper dbHelper;
    private final MutableLiveData<ArrayList<Transaction>> transactionsList = new MutableLiveData<>();

    public TransactionsViewModel(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        loadTransactions();
    }

    public void loadTransactions() {
        ArrayList<Transaction> transactions = dbHelper.getAllTransactions();
        transactionsList.setValue(transactions);
    }

    public LiveData<ArrayList<Transaction>> getTransactionsList() {
        return transactionsList;
    }

    public double getCurrentBalance() {
        return dbHelper.getCurrentBalance();
    }
}
