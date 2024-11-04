//package com.example.hisaabkitaab.ViewModel;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.example.hisaabkitaab.model.TransactionModel;
//
//import java.util.List;
//
//public class TransactionsViewModel extends ViewModel {
//    private final MutableLiveData<List<TransactionModel>> transactionsList = new MutableLiveData<>();
//    private final MutableLiveData<Integer> balance = new MutableLiveData<>();
//
//    public LiveData<List<TransactionModel>> getTransactionsList() {
//        return transactionsList;
//    }
//
//    public LiveData<Integer> getBalance() {
//        return balance;
//    }
//
//    public void setTransactionsList(List<TransactionModel> transactions) {
//        transactionsList.setValue(transactions);
//        updateBalance();
//    }
//
//    public void deleteTransaction(int position) {
//        List<TransactionModel> currentList = transactionsList.getValue();
//        if (currentList != null && position < currentList.size()) {
//            currentList.remove(position);
//            transactionsList.setValue(currentList);
//            updateBalance();
//        }
//    }
//
//    private void updateBalance() {
//        int newBalance = 0;
//        // Calculate balance based on transactions
//        for (TransactionModel transaction : transactionsList.getValue()) {
//            newBalance += Integer.parseInt(transaction.getAmount()); // Example calculation
//        }
//        balance.setValue(newBalance);
//    }
//}
