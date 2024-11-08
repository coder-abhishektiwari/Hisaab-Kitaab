package com.example.hisaabkitaab.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.example.hisaabkitaab.DataBase.DBHelper;
import com.example.hisaabkitaab.helper.ChartHelper;
import com.example.hisaabkitaab.R;
import com.example.hisaabkitaab.model.Transaction;
import com.example.hisaabkitaab.Adapter.TransactionsAdapter;
import com.example.hisaabkitaab.databinding.ActivityMainBinding;
import com.example.hisaabkitaab.ui.fragments.AddPaymentDialogFragment;
import com.example.hisaabkitaab.ui.fragments.ReceivePaymentDialogFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity implements AddPaymentDialogFragment.AddPaymentListener,
        ReceivePaymentDialogFragment.ReceivePaymentListener{

    private ActivityMainBinding binding;
    private Dialog reportsDialog;
    private SharedPreferences sharedPreferences;
    private Double totalSpending;
    private String savedBudget;
    private Double previousTotalSpending = null;
    private String previousSavedBudget = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE); // Initialize here

        // Load initial data
        refreshActivity();

        // Set up click listeners
        setupToolbarClick();
        setupBudgetDialog();
        setupViewReportsDialog();
        setupViewAllTransactions();
        setupAddTransactionButton();
    }


    private void setupToolbarClick() {
        binding.toolbar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
    }  // Method to set up toolbar click listener
    private void setupBudgetDialog() {
        binding.cvBudget.setOnClickListener(v -> {
            Dialog budgetDialog = new Dialog(MainActivity.this);
            budgetDialog.setContentView(R.layout.dialog_budget);
            budgetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            budgetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

            Slider budgetSlider = budgetDialog.findViewById(R.id.budgetSlider);
            savedBudget = sharedPreferences.getString("budget", null);
            if (savedBudget != null) {
                budgetSlider.setValue(Float.parseFloat(savedBudget));
            }

            budgetSlider.addOnChangeListener((slider, value, fromUser) -> {
                String budgetString = String.valueOf((int) value);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("budget", budgetString);
                editor.apply();
                loadSettings();
            });
            budgetDialog.show();
        });
    }  // Method to set up budget dialog
    private void setupViewReportsDialog() {
        binding.btnViewReports.setOnClickListener(v -> {
            reportsDialog = new Dialog(MainActivity.this);
            reportsDialog.setContentView(R.layout.dialog_reports);
            reportsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            reportsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

            // Setting up charts
            setupChartsInDialog(reportsDialog);
            reportsDialog.show();
        });
    }// Method to set up reports dialog
    private void setupChartsInDialog(Dialog reportsDialog) {
        LineChart expenseChart = reportsDialog.findViewById(R.id.expenseChart);
        LineChart incomeChart = reportsDialog.findViewById(R.id.incomeChart);
        PieChart profitLoss = reportsDialog.findViewById(R.id.profitloss);
        ChartHelper chartHelper = new ChartHelper(this, reportsDialog, expenseChart, incomeChart, profitLoss);

        // Time range selection
        Spinner spinner = reportsDialog.findViewById(R.id.timeRangeSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTimeRange = parent.getItemAtPosition(position).toString();
                chartHelper.filterChartData(selectedTimeRange);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected
            }
        });

        // Setting charts
        chartHelper.lineChart(expenseChart, new ArrayList<>(), "expense", "Paid to ", "Given on lend to ");
        chartHelper.lineChart(incomeChart, new ArrayList<>(), "income", "Borrowed from ", "Received from ");
        chartHelper.pieChart();
    }// Method to set up charts in reports dialog
    private void setupViewAllTransactions() {
        binding.btnViewAllTransactions.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TransactionsActivity.class)));
    } // Method to set up view all transactions button
    private void setupAddTransactionButton() {
        binding.btnAddPayment.setOnClickListener(v -> showAddPaymentDialog());
        binding.btnPaymentReceived.setOnClickListener(v -> showReceivePaymentDialog());
    } // Method to set up add payment button

    private void loadSettings() {
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String savedImageUri = sharedPreferences.getString("image_uri", null);
        String savedSalary = sharedPreferences.getString("salary", null);
        savedBudget = sharedPreferences.getString("budget", null);

        loadUserImage(savedImageUri);
        loadSalary(savedSalary);
        loadBudget(savedBudget);
        updateBudgetProgress();
    }
    private void loadUserImage(String imageUri) {
        if (imageUri != null) {
            Uri uri = Uri.parse(imageUri);
            try {
                getContentResolver().openInputStream(uri).close();
                Glide.with(this).load(uri).circleCrop().into(binding.userImage);
            } catch (Exception e) {
                binding.userImage.setImageResource(R.drawable.icon_user_image);
            }
        } else {
            binding.userImage.setImageResource(R.drawable.icon_user_image);
        }
    }
    private void loadSalary(String salary) {
        if (salary != null) {
            binding.yourSalary.setText("Your Salary: ₹" + salary);
        } else {
            binding.yourSalary.setText("₹0.0");
        }
    }
    private void loadBudget(String budget) {
        if (budget != null) {
            binding.txtBudget.setText("₹" + budget);
            binding.budgetProgress.setMax((int) Double.parseDouble(budget));
        } else {
            binding.txtBudget.setText("₹0.0");
        }
    }
    private void updateBudgetProgress() {
        // Check if totalSpending or savedBudget has changed before updating
        if ((previousTotalSpending != null && previousTotalSpending.equals(totalSpending)) &&
                (previousSavedBudget != null && previousSavedBudget.equals(savedBudget))) {
            return; // No need to update if values haven't changed
        }

        if (totalSpending != null) {
            double budget = savedBudget != null ? Double.parseDouble(savedBudget) : 0;
            int progress = (int) totalSpending.doubleValue();
            binding.budgetProgress.setProgress(progress);

            String budgetStatus;
            int statusColor;
            if (totalSpending < budget) {
                budgetStatus = String.format("You spent ₹%.2f from your budget", totalSpending);
                statusColor = getResources().getColor(R.color.black);
                binding.budgetProgress.setIndicatorColor(getResources().getColor(R.color.colorPrimary));
            } else if (totalSpending > budget) {
                double over = totalSpending - budget;
                budgetStatus = String.format("You have crossed your budget over ₹%.2f", over);
                statusColor = getResources().getColor(R.color.red);
                binding.budgetProgress.setIndicatorColor(getResources().getColor(R.color.red));
            } else {
                budgetStatus = "You have reached your budget";
                statusColor = getResources().getColor(R.color.black);
                binding.budgetProgress.setIndicatorColor(getResources().getColor(R.color.colorPrimary));
            }
            binding.txtBudgetStatus.setText(budgetStatus);
            binding.txtBudgetStatus.setTextColor(statusColor);

        } else {
            binding.budgetProgress.setProgress(0);
            binding.txtBudgetStatus.setText("You spent ₹0 from your budget");
            binding.txtBudgetStatus.setTextColor(getResources().getColor(R.color.black));
            binding.budgetProgress.setIndicatorColor(getResources().getColor(R.color.colorPrimary));
        }

        // Store the current values as previous values for future comparison
        previousTotalSpending = totalSpending;
        previousSavedBudget = savedBudget;
    }
    private void fetchRecentTransaction() {
        DBHelper db = new DBHelper(this);
        // Implementing the delete listener inline
        TransactionsAdapter.OnTransactionDeleteListener deleteListener = () -> refreshActivity();
        ArrayList<Transaction> recentTransactionsList = db.getAllTransactions(5);
        TransactionsAdapter adapter = new TransactionsAdapter(this, recentTransactionsList,deleteListener);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        db.close();
    }
    private void getMonthlySummary() {
        DBHelper dbHelper = new DBHelper(this);
        double[] summary = dbHelper.getCurrentMonthSummary();

        totalSpending = summary[0];
        double totalReceived = summary[1];
        double currentBalance = summary[2];

        binding.youSpent.setText("Spending: ₹" + totalSpending);
        binding.totalReceived.setText("Received: ₹" + totalReceived);
        binding.currentBalance.setText("Balance: ₹" + dbHelper.getCurrentBalance());
    }
    public void refreshActivity() {
        fetchRecentTransaction();
        getMonthlySummary();
        loadSettings();
    }

    @Override
    public void onAddPayment(String date, String paidTo, String forWhich, String type, String amount) {
        addPayment(date, paidTo, forWhich, type, amount);
        refreshActivity();
    }
    public void addPayment(String date, String paidTo, String forWhich, String type, String amount){
        DBHelper db = new DBHelper(this);

        // Convert the amount to float first, then calculate the new balance
        Double amountValue = Double.parseDouble(amount);
        Double currentBalance = db.getCurrentBalance();
        Double newBalance = currentBalance - amountValue;
        // Store the new balance as a string
        String balance = String.valueOf(newBalance);

        db.addTransaction(new Transaction(date, paidTo, forWhich, type, amount, balance));
        db.close();
    }
    private void showAddPaymentDialog() {
        AddPaymentDialogFragment dialog = new AddPaymentDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddPaymentDialog");
    }

    @Override
    public void onReceivePayment(String date, String fromWhere, String forWhich, String type, String amount) {
        addReceivedPayment(date, fromWhere, forWhich, type, amount);
        refreshActivity();
    }
    private void addReceivedPayment(String date, String fromWhere, String forWhich, String type, String amount) {
        DBHelper db = new DBHelper(this);

        // Convert the amount to float first, then calculate the new balance
        Double amountValue = Double.parseDouble(amount);
        Double currentBalance = db.getCurrentBalance();
        Double newBalance = currentBalance + amountValue;

        // Store the new balance as a string
        String balance = String.valueOf(newBalance);

        db.addTransaction(new Transaction(date, fromWhere, forWhich, type, amount, balance));
        db.close();
    }
    private void showReceivePaymentDialog() {
        ReceivePaymentDialogFragment dialog = new ReceivePaymentDialogFragment();
        dialog.show(getSupportFragmentManager(),"RecievePaymentDialog");
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshActivity();

    }

}

