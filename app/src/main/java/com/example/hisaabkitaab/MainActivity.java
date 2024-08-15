package com.example.hisaabkitaab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.example.hisaabkitaab.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
private ActivityMainBinding binding;
private Dialog recievePaymentDialog, addPaymentDialog, reportsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);  //setting toolbar

        //user image click
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        loadSettings();
        //getting monthly summary
        getMonthlySummary();

        //display charts
        binding.btnViewReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportsDialog = new Dialog(MainActivity.this);
                reportsDialog.setContentView(R.layout.dialog_reports);


                reportsDialog.show();
            }
        });

        //10 recent transaction
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchRecentTransaction();
        //view all transactions
        binding.btnViewAllTransactions.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ShowTransactionsActivity.class)));
        onClickReceivePayment();  //receive payment
        onClickAddPayment();  //add payment

        binding.btnViewReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog reportsDialog = new Dialog(MainActivity.this);
                reportsDialog.setContentView(R.layout.dialog_reports);
                reportsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                reportsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

                reportsDialog.show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String savedImageUri = sharedPreferences.getString("image_uri", null);
        String savedSalary = sharedPreferences.getString("salary", null);
        //setting image
        if (savedImageUri != null) {
            Uri imageUri = Uri.parse(savedImageUri);
            try {
                // Check if URI is still accessible
                getContentResolver().openInputStream(imageUri).close();
                Glide.with(this).load(imageUri).circleCrop().into(binding.userImage);
            } catch (Exception e) {
                // Handle invalid URI
                binding.userImage.setImageResource(R.drawable.icon_user_image);
            }
        } else {
            binding.userImage.setImageResource(R.drawable.icon_user_image);
        }

        //setting salary
        if (savedSalary != null) {
            try {
                // Check if URI is still accessible
                String yourSalary = "Your Salary: " + savedSalary;
                binding.yourSalary.setText(yourSalary);
            } catch (Exception e) {
                // Handle invalid URI
                binding.yourSalary.setText("₹0.0");
            }
        } else {
            binding.yourSalary.setText("₹0.0");
        }
    }

    private void getMonthlySummary(){
        DBHelper dbHelper = new DBHelper(this);
        double[] summary = dbHelper.getCurrentMonthSummary();

        double totalSpending = summary[0];
        double totalReceived = summary[1];
        double currentBalance = summary[2];
        binding.youSpent.setText("Spending: ₹" + totalSpending);
        binding.totalReceived.setText("Received: ₹" + totalReceived);
        binding.currentBalance.setText("Balance: ₹" + dbHelper.getCurrentBalance());



    }

    private void onClickReceivePayment(){
        binding.btnPaymentReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recievePaymentDialog = new Dialog(MainActivity.this);
                recievePaymentDialog.setContentView(R.layout.layout_receive_payment);
                recievePaymentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                recievePaymentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

                EditText edtAmount = recievePaymentDialog.findViewById(R.id.edt_received_amount);
                EditText edtFromWhere = recievePaymentDialog.findViewById(R.id.edt_from_where);
                EditText edtForWhich = recievePaymentDialog.findViewById(R.id.edt_received_for_which);
                CheckBox isBorrowing = recievePaymentDialog.findViewById(R.id.checkbox_is_borrow);
                Button btnAdd = recievePaymentDialog.findViewById(R.id.btn_received);

                //fetching date and time
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy 'at' h:mm a", Locale.getDefault());
                Date getDate = Calendar.getInstance().getTime();
                String date = simpleDateFormat.format(getDate);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String amount = edtAmount.getText().toString().trim();
                        String fromWhere = edtFromWhere.getText().toString().trim();
                        String forWhich = edtForWhich.getText().toString().trim();

                        //fetching transaction type
                        String type;
                        if (isBorrowing.isChecked()) {
                            type = "Borrowed from ";
                        } else{
                            type= "Received from ";
                        }

                        if (!amount.isEmpty() && !fromWhere.isEmpty()) {
                            addReceivedPayment(date, fromWhere, forWhich, type, amount);
                            recievePaymentDialog.dismiss();  // Close the dialog
                        } else {
                            // Handle the case where inputs are missing
                            // Handle the case where inputs are missing
                            Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                recievePaymentDialog.show();
            }
        });
    }

    private void onClickAddPayment(){
        binding.btnAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPaymentDialog = new Dialog(MainActivity.this);
                addPaymentDialog.setContentView(R.layout.layout_add_payment);
                addPaymentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addPaymentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

                EditText edtAmount = addPaymentDialog.findViewById(R.id.edt_paid_amount);
                EditText edtPaidTo = addPaymentDialog.findViewById(R.id.edt_paid_to);
                EditText edtForWhich = addPaymentDialog.findViewById(R.id.edt_for_which);
                CheckBox isLending = addPaymentDialog.findViewById(R.id.checkbox_is_lending);
                Button btnAdd = addPaymentDialog.findViewById(R.id.btn_add_payment);

                // Fetching date and time
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy 'at' h:mm a", Locale.getDefault());
                Date getDate = Calendar.getInstance().getTime();
                String date = simpleDateFormat.format(getDate);


                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String amount = edtAmount.getText().toString().trim();
                        String paidTo = edtPaidTo.getText().toString().trim();
                        String forWhich = edtForWhich.getText().toString().trim();

                        // Fetching transaction type
                        String type;
                        if (isLending.isChecked()){
                            type = "Given on lend to ";
                        }else {
                            type = "Paid to ";
                        }

                        if (!amount.isEmpty() && !paidTo.isEmpty()) {
                            addPayment(date, paidTo, forWhich, type, amount);
                            addPaymentDialog.dismiss();  // Close the dialog
                        } else {
                            // Handle the case where inputs are missing
                            Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                addPaymentDialog.show();
            }
        });
    }

    private void fetchRecentTransaction(){
        DBHelper db = new DBHelper(this);
        ArrayList<TransactionModel> recentTransactionsList = db.getAllTransactions(5); // Fetch transactions from database
        TransactionsAdapter adapter = new TransactionsAdapter(this, recentTransactionsList);
        binding.recyclerView.setAdapter(adapter);
        db.close(); // Close the database connection

    }






    //TransactionModel(String date, String transactor, String description, String type, String amount, String balance)
    //database methods
    public void addPayment(String date, String paidTo, String forWhich, String type, String amount){
        DBHelper db = new DBHelper(this);

        // Convert the amount to float first, then calculate the new balance
        Double amountValue = Double.parseDouble(amount);
        Double currentBalance = db.getCurrentBalance();
        Double newBalance = currentBalance - amountValue;
        Log.d("abhishek", "current balance: "+newBalance);
        // Store the new balance as a string
        String balance = String.valueOf(newBalance);

        db.addTransaction(new TransactionModel(date, paidTo, forWhich, type, amount, balance));
        db.close();
    }
    private void addReceivedPayment(String date, String fromWhere, String forWhich, String type, String amount) {
        DBHelper db = new DBHelper(this);

        // Convert the amount to float first, then calculate the new balance
        Double amountValue = Double.parseDouble(amount);
        Double currentBalance = db.getCurrentBalance();
        Double newBalance = currentBalance + amountValue;

        // Store the new balance as a string
        String balance = String.valueOf(newBalance);

        db.addTransaction(new TransactionModel(date, fromWhere, forWhich, type, amount, balance));
        db.close();
    }




}