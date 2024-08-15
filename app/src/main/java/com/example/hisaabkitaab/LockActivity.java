package com.example.hisaabkitaab;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hisaabkitaab.databinding.ActivityLockBinding;

import java.util.Objects;

public class LockActivity extends AppCompatActivity {
    ActivityLockBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLockBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs",MODE_PRIVATE);

        String savedImageUri = sharedPreferences.getString("image_uri", null);
        String name = "Hi " + sharedPreferences.getString("name", null);
        String password = sharedPreferences.getString("password", null);
        if (Objects.equals(password, "")){
            startActivity(new Intent(LockActivity.this,MainActivity.class));
            finish();
        }else{
            setSavedImage(savedImageUri,binding.loginUserLogo);
            setSavedText(name, binding.txtGreet);
            binding.btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pass = binding.edtEnterPassword.getText().toString().trim();

                    Log.d("pass", "password is: "+password);
                    Log.d("pass","you entered: "+pass);
                    if (binding.edtEnterPassword.getText().toString().trim().equals(password)) {
                        startActivity(new Intent(LockActivity.this, MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(LockActivity.this, "Please enter correct password", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

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




}

