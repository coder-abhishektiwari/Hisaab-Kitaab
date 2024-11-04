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

public class ReceivePaymentDialogFragment extends DialogFragment {

    public interface ReceivePaymentListener {
        void onReceivePayment(String date, String fromWhere, String forWhich, String transactionType, String amount);
    }

    private ReceivePaymentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ReceivePaymentListener) {
            listener = (ReceivePaymentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ReceivePaymentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_receive_payment, null);

        EditText edtAmount = view.findViewById(R.id.edt_received_amount);
        EditText edtFromWhere = view.findViewById(R.id.edt_from_where);
        EditText edtForWhich = view.findViewById(R.id.edt_received_for_which);
        CheckBox isBorrowing = view.findViewById(R.id.checkbox_is_borrow);
        Button btnAdd = view.findViewById(R.id.btn_received);

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
                Log.d("DatabaseDebug", "button clicked");
                //fetching transaction type
                String type;
                if (isBorrowing.isChecked()) {
                    type = "Borrowed from ";
                } else{
                    type= "Received from ";
                }

                if (!amount.isEmpty() && !fromWhere.isEmpty()) {
                    listener.onReceivePayment(date, fromWhere, forWhich, type, amount);
                    dismiss();
                } else {
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
