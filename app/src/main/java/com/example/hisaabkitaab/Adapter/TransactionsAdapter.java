package com.example.hisaabkitaab.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hisaabkitaab.DataBase.DBHelper;
import com.example.hisaabkitaab.R;
import com.example.hisaabkitaab.model.TransactionModel;
import com.example.hisaabkitaab.ui.activities.TransactionsActivity;

import java.util.ArrayList;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<TransactionModel> transactionsList;
    private final OnTransactionDeleteListener deleteListener;

    public TransactionsAdapter(Context context, ArrayList<TransactionModel> transactionsList, OnTransactionDeleteListener deleteListener) {
        this.context = context;
        this.transactionsList = transactionsList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_transactions, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.ViewHolder holder, int position) {
        TransactionModel transaction = transactionsList.get(position);
        String descriptionText = transaction.getDescription().isEmpty() ? "" : " for " + transaction.getDescription();
        String header = transaction.getType() + transaction.getTransactor() + descriptionText;

        holder.transaction.setText(header);
        holder.date.setText(transaction.getDate());

        if (transaction.getType().contains("Borrowed") || transaction.getType().contains("Received")) {
            holder.amount.setTextColor(context.getResources().getColor(R.color.green));
            holder.amount.setText("+ ₹" + transaction.getAmount());
        } else {
            holder.amount.setTextColor(context.getResources().getColor(R.color.black));
            holder.amount.setText("₹" + transaction.getAmount());
        }

        holder.itemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteTransaction(position);
                        deleteListener.onTransactionDeleted(); // Update balance indicator
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView transaction, amount, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transaction = itemView.findViewById(R.id.txt_transaction);
            amount = itemView.findViewById(R.id.txt_amount);
            date = itemView.findViewById(R.id.txt_date_time);
        }
    }

    private void deleteTransaction(int position) {
        DBHelper db = new DBHelper(context);
        TransactionModel model = transactionsList.get(position);
        db.deleteTransaction(model.getId());
        transactionsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, transactionsList.size());
        db.close();
    }

    public interface OnTransactionDeleteListener {
        void onTransactionDeleted();
    }
}
