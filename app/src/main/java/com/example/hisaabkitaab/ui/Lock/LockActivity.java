package com.example.hisaabkitaab.ui.Lock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hisaabkitaab.ui.activities.MainActivity;
import com.example.hisaabkitaab.R;
import com.example.hisaabkitaab.databinding.ActivityLockBinding;

public class LockActivity extends AppCompatActivity {
    ActivityLockBinding binding;
    private StringBuilder enteredNumbers; //store acctual numbers

    String password, savedImageUri, name; //saved data
    String currentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLockBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
        //get saved data
        savedImageUri = sharedPreferences.getString("image_uri", null);
        name = "Hi " + sharedPreferences.getString("name", null);
        password = sharedPreferences.getString("password", null);

        enteredNumbers = new StringBuilder();//initialize stringBuilder
        //setting buttons
        binding.btnEnter1.setOnClickListener(v -> updatePassword("1"));
        binding.btnEnter2.setOnClickListener(v -> updatePassword("2"));
        binding.btnEnter3.setOnClickListener(v -> updatePassword("3"));
        binding.btnEnter4.setOnClickListener(v -> updatePassword("4"));
        binding.btnEnter5.setOnClickListener(v -> updatePassword("5"));
        binding.btnEnter6.setOnClickListener(v -> updatePassword("6"));
        binding.btnEnter7.setOnClickListener(v -> updatePassword("7"));
        binding.btnEnter8.setOnClickListener(v -> updatePassword("8"));
        binding.btnEnter9.setOnClickListener(v -> updatePassword("9"));
        binding.btnEnter0.setOnClickListener(v -> updatePassword("0"));
        binding.btnEnterx.setOnClickListener(v -> cutLastDigit());

        //check if password is set
        //btnLogin handling
        if (password == "" || password == null){
            startActivity(new Intent(LockActivity.this, MainActivity.class));
            finish();
        }else{
            setSavedImage(savedImageUri,binding.loginUserLogo);
            setSavedText(name, binding.txtGreet);
            binding.btnLogin.setOnClickListener(v -> login());
        }

        //Biometric authentication
        BiometricAuthHelper biometricAuthHelper = new BiometricAuthHelper(this, new BiometricAuthHelper.BiometricAuthCallback() {
            @Override
            public void onAuthenticationError(int errorCode, String errString) {
                // Handle authentication error
                Toast.makeText(LockActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded() {
                // Authentication succeeded
                // Proceed with unlocking or other actions
                startActivity(new Intent(LockActivity.this, MainActivity.class));
                finish();
                Toast.makeText(LockActivity.this, "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                // Authentication failed
                Toast.makeText(LockActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
        biometricAuthHelper.authenticate();// Trigger authentication(automatic)

        //biometric button handling
        binding.btnBiometric.setOnClickListener(v -> {biometricAuthHelper.authenticate();});

    }



    //setting saved data to image and text
    public void setSavedImage(String savedImageUri, ImageView image){
        //setting image
        if (savedImageUri != null) {
            Uri imageUri = Uri.parse(savedImageUri);
            try {
                // Check if URI is still accessible
                getContentResolver().openInputStream(imageUri).close();
                Glide.with(this).load(imageUri).circleCrop().into(image);
            } catch (Exception e) {
                // Handle invalid URI
                image.setImageResource(R.drawable.icon_user_image);
            }
        } else {
            image.setImageResource(R.drawable.icon_user_image);
        }
    }
    public void setSavedText(String savedValue, TextView textView){
        //setting salary
        if (savedValue != null) {
            try {
                textView.setText(savedValue);
            } catch (Exception e) {
                // Handle invalid URI
                textView.setText("");
            }
        } else {
            textView.setText("");
        }
    }

    //PIN related methods
    public void updatePassword(String value){
        if (enteredNumbers.length() < 3){
            enteredNumbers.append(value);

            currentText = binding.tvPassword.getText().toString();
            binding.tvPassword.setText(currentText + "⚫");
        } else if (enteredNumbers.length() == 3) {
            //auto check password
            enteredNumbers.append(value);

            currentText = binding.tvPassword.getText().toString();
            binding.tvPassword.setText(currentText + "⚫");
            login();
        } else {
            Toast.makeText(this, "Only 4 digits are allowed.", Toast.LENGTH_SHORT).show();
        }

    }
    public void cutLastDigit(){
        if (enteredNumbers.length() > 0) {
            enteredNumbers.deleteCharAt(enteredNumbers.length() - 1);
            String currentText = binding.tvPassword.getText().toString();
            if (!currentText.isEmpty()) {
                binding.tvPassword.setText(currentText.substring(0, currentText.length() - 1));
            }
        }
    }
    public void clearPassword(){
        binding.tvPassword.setText("");
        enteredNumbers.setLength(0);
    }
    public void login(){
        String enteredPassword = enteredNumbers.toString();

        if (enteredPassword.equals(password)) {
            startActivity(new Intent(LockActivity.this, MainActivity.class));
            finish();
        }else {
            Handler handler = new Handler();
            handler.postDelayed(this::clearPassword,1000);
            binding.tvPassword.startAnimation(AnimationUtils.loadAnimation(this,R.anim.incorrect_password));
            Toast.makeText(LockActivity.this, "Please enter correct password", Toast.LENGTH_SHORT).show();

        }
    }

}

