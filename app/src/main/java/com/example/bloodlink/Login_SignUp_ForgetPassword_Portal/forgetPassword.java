package com.example.bloodlink.Login_SignUp_ForgetPassword_Portal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.bloodlink.databinding.ActivityForgetPasswordBinding;

public class forgetPassword extends AppCompatActivity {
    ActivityForgetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.phoneContainer.setHelperText("");
        phoneFocusListener();
    }

    private void phoneFocusListener() {
        binding.phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = validPhoneNumber();
                if (result != null) {
                    binding.phoneContainer.setHelperText(result);

                } else {
                    binding.phoneContainer.setHelperText("");
                    // Clear error text if email is valid
                }
            }
        });

    }

    private String validPhoneNumber() {
        String phoneText = binding.phoneEditText.getText().toString().trim();
        if (phoneText.length() != 10) {
            return "Minimum 10 number";
        }
        if (!phoneText.matches(".*[0-9].*")) {
            return "Must be all Digit";
        }

        return null; // Return null if email is valid
    }
    }





