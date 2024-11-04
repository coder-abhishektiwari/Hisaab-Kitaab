package com.example.hisaabkitaab.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.hisaabkitaab.R;
import com.example.hisaabkitaab.ui.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPaymentDialogFragment extends DialogFragment {

    public interface AddPaymentListener {
        void onAddPayment(String date, String paidTo, String forWhich, String type, String amount);
    }

    private AddPaymentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddPaymentListener) {
            listener = (AddPaymentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AddPaymentListener");
        }
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_payment, null);

        EditText edtAmount = view.findViewById(R.id.edt_paid_amount);
        EditText edtPaidTo = view.findViewById(R.id.edt_paid_to);
        EditText edtForWhich = view.findViewById(R.id.edt_for_which);
        CheckBox isLending = view.findViewById(R.id.checkbox_is_lending);
        Button btnAdd = view.findViewById(R.id.btn_add_payment2);

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
                    listener.onAddPayment(date, paidTo, forWhich, type, amount);
                    dismiss();  // Close the dialog
                } else {
                    // Handle the case where inputs are missing
                    Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_SHORT).show();
                }
            }
        });


        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set background in onStart() after the dialog is fully created
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }
    }
}
