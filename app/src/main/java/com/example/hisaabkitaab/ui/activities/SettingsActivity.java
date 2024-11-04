package com.example.hisaabkitaab.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hisaabkitaab.R;
import com.example.hisaabkitaab.databinding.ActivitySettingsBinding;

import java.util.Objects;
import com.bumptech.glide.Glide;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    private static final int GALLERY_REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 200;
    private Dialog dialog;
    private SharedPreferences sharedPreferences;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.settingsToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        checkPermissions();

        //load details from shared preferences
        String savedImageUri = sharedPreferences.getString("image_uri", null);
        setSavedImage(savedImageUri,binding.userImageInSettings);
        String savedName = sharedPreferences.getString("name",null);
        setSavedEdittext(savedName,binding.edtName);
        String savedSalary = sharedPreferences.getString("salary", null);
        setSavedEdittext(savedSalary,binding.edtSalary);
        String savedMobile = sharedPreferences.getString("mobile",null);
        setSavedEdittext(savedMobile,binding.edtMobile);
        String savedPass = sharedPreferences.getString("password",null);
        setSavedEdittext(savedPass,binding.edtPassword);

        binding.userImageInSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog(); // Edit or delete image
            }
        });
        binding.btnSaveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
                if (saveDetails().length() <= 4){
                    Toast.makeText(SettingsActivity.this, "Setting saved Successfully", Toast.LENGTH_SHORT).show();
                    binding.txtPasswordError.setText("");
                    finish();
                } else {
                    Toast.makeText(SettingsActivity.this, "Only 4 digit are allowed", Toast.LENGTH_SHORT).show();
                    binding.txtPasswordError.setText("Only 4 digit are allowed");
                }

            }
        });

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
    public void setSavedEdittext(String savedValue, AppCompatEditText editText){
        if (savedValue != null) {
            try {
                editText.setText(savedValue);
            } catch (Exception e) {
                // Handle invalid URI
                editText.setText("");
            }
        } else {
            editText.setText("");
        }
    }
    private String saveDetails(){
        String name = Objects.requireNonNull(binding.edtName.getText()).toString();
        String salary = Objects.requireNonNull(binding.edtSalary.getText()).toString();
        String mobile = Objects.requireNonNull(binding.edtMobile.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (imageUri != null) {
            editor.putString("image_uri", imageUri.toString());
        }
        if (password.length() <= 4){
            editor.putString("name", name);
            editor.putString("salary", salary);
            editor.putString("mobile",mobile);
            editor.putString("password", password);
            editor.apply();
        }
        return password;
    }
    public void imageDialog() {
        dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.layout_set_image);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;

        ImageView displayImage = dialog.findViewById(R.id.display_image);
        String savedImageUri = sharedPreferences.getString("image_uri", null);
        if (savedImageUri != null) {
            Uri imageUri = Uri.parse(savedImageUri);
            displayImage.setImageURI(imageUri);
        }

        dialog.findViewById(R.id.icon_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        dialog.findViewById(R.id.icon_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("image_uri");
                editor.apply();
                binding.userImageInSettings.setImageResource(R.drawable.icon_user_image);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Grant permission
            try {
                getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            // Set image instantly in settingsImageView
            Glide.with(this).load(imageUri).circleCrop().into(binding.userImageInSettings);
            ImageView imageView = dialog.findViewById(R.id.display_image);
            imageView.setImageURI(imageUri);
        }
    }





    //permission methods
    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with functionality
            } else {
                // Permission denied, handle accordingly
            }
        }
    }


}
