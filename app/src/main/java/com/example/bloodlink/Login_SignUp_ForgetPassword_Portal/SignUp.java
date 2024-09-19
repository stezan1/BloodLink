package com.example.bloodlink.Login_SignUp_ForgetPassword_Portal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bloodlink.R;
import com.example.bloodlink.becomeadonor.becomeadonor;
import com.example.bloodlink.dashboard.dashboard;
import com.example.bloodlink.databinding.ActivitySignUpBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {
ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.signUpTxt.setText("Sign Up");

       // binding.emailContainer.setHelperText("");
        binding.passwordContainer.setHelperText("");
        binding.conformPasswordContainer.setHelperText("");
        binding.phoneContainer.setHelperText("");
//        emailFocusListener();
        passwordFocusListener();
        conformPasswordFocusListener();
        phoneFocusListener();
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.phoneEditText.getText().toString().length()!= 10){
                    Toast.makeText(SignUp.this, "phone no must be of 10 digit", Toast.LENGTH_SHORT).show();
                }
                else if(binding.passwordEditText.getText().toString().equals(binding.conformPasswordEditText.getText().toString())) {
                    Signup();
                }
                else {
                    Toast.makeText(SignUp.this, "confirm password did not match Enter again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //----------------------------------frontend validation--------------------------------
//    private void emailFocusListener() {
//        binding.emailEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//
//                String result = validEmail();
//                if (result != null) {
//                    binding.emailContainer.setHelperText(result);
//
//                } else {
//                    binding.emailContainer.setHelperText("");
//                    // Clear error text if email is valid
//                }
//            }
//        });
//    }

//    private String validEmail() {
//        String emailText = binding.emailEditText.getText().toString().trim();
//        if (emailText.isEmpty()) {
//            return "Email cannot be empty";
//        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
//            return "Invalid Email Address";
//        }
//        return null; // Return null if email is valid
//    }

    private void passwordFocusListener() {
        binding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = validPassword();
                if (result != null) {
                    binding.passwordContainer.setHelperText(result);

                } else {
                    binding.passwordContainer.setHelperText("");
                    // Clear error text if email is valid
                }
            }
        });
    }

    private String validPassword() {
        String passwordText = binding.passwordEditText.getText().toString().trim();
        if(passwordText.length()<8){
            return "Minimum 8 Character Password";
        }

        return null; // Return null if email is valid
    }
    private void conformPasswordFocusListener() {
        binding.conformPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = validconformPassword();
                if (result != null) {
                    binding.conformPasswordContainer.setHelperText(result);

                } else {
                    binding.conformPasswordContainer.setHelperText("");
                    // Clear error text if email is valid
                }
            }
        });
    }

    private String validconformPassword() {
        String getPasswordText=binding.passwordEditText.getText().toString().trim();
        String conformPasswordText = binding.conformPasswordEditText.getText().toString().trim();
        if(conformPasswordText.length()<8){
            return "Minimum 8 Character Password";
        }
//        if(!conformPasswordText.matches(".*[A-Z].*")){
//            return "Must Contain 1 Upper-case Character ";
//        }
//        if(!conformPasswordText.matches(".*[a-z].*")){
//            return "Must Contain 1 Lower-case Character ";
//        }
//        if(!conformPasswordText.matches(".*[@#/$^&=+].*")){
//            return "Must Contain 1 Special Character [@#/$^&=+] ";
//        }
//        if(!getPasswordText.equals(conformPasswordText)){
//            return "Password Doesnot match ";
//        }
        return null; // Return null if email is valid
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
    public void Signup(){
        SharedPreferences sharedPreferences = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferences.getString("URL", null);
        String url = URL + "/api/v1/user/signup";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("username",binding.phoneEditText.getText());
            jsonRequest.put("password", binding.passwordEditText.getText());
            Log.d("phone",binding.phoneEditText.toString());
            Log.d("password",binding.passwordEditText.toString());

        } catch (JSONException e) {
            Log.d("JsonExceptionSignup",e.toString());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(),"Signup successful",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error",error.toString());
                Toast.makeText(getApplicationContext(), "Invalid Credential Enter again", Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue.add(jsonObjectRequest);
    }
    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
           Log.d("Location1",locationAddress);
        }
    }
    //----------------------------------------end validation-------------------------------
}