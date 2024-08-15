package com.example.hisaabkitaab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    private Context context;
    ArrayList<TransactionModel> transactionsList;

    public TransactionsAdapter(Context context, ArrayList<TransactionModel> transactionsList) {
        this.context = context;
        this.transactionsList = transactionsList;
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_transactions,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.ViewHolder holder, int position) {
        TransactionModel transactions = transactionsList.get(position);
        String des= transactions.getDescription();
        if (transactions.getDescription().equals("")){
            des = transactions.getDescription();
        }else{
            if (transactions.getType().equals("Borrowed from ") || transactions.getType().equals("Received from ")){
                if (transactions.getType().equals("Borrowed from ")){
                    des = " for " + transactions.getDescription();
                }else {
                    des = "("+transactions.getDescription()+")";
                }
            }else{
                des = " for " + transactions.getDescription();
            }
        }
        String header = transactions.getType() + transactions.getTransactor() + des;
        if (transactions.getType().equals("Borrowed from ") || transactions.getType().equals("Received from ")){
            holder.amount.setTextColor(context.getResources().getColor(R.color.green));
            String amount = "+ ₹" +transactions.getAmount();
            holder.amount.setText(amount);
        }else{
            holder.amount.setTextColor(context.getResources().getColor(R.color.black));
            String amount = "₹" +transactions.getAmount();
            holder.amount.setText(amount);
        }
        holder.transaction.setText(header);

        holder.date.setText(transactions.getDate());
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView transaction, amount, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transaction = itemView.findViewById(R.id.txt_transaction);
            amount = itemView.findViewById(R.id.txt_amount);
            date = itemView.findViewById(R.id.txt_date_time);
        }
    }
}
