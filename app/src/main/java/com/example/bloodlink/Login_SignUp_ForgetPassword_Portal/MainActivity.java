package com.example.bloodlink.Login_SignUp_ForgetPassword_Portal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
//import com.example.bloodlink.NotificationService;
import com.example.bloodlink.NotificationService;
import com.example.bloodlink.R;
import com.example.bloodlink.dashboard.dashboard;
import com.example.bloodlink.databinding.ActivityMainBinding;
import com.example.bloodlink.utility.NetworkChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    NetworkChangeListener networkChangeListener =new NetworkChangeListener();
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
        loadLocale();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //languge ko lagi
        loadLocale();
        // Make the activity fullscreen and draw under system bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("flow", "notification lai call hudai xa");

        binding.languagechooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage();
            }
        });
        // Ensure textView5 retains its initial text
        binding.titleTextView.setText("BloodLink");

        binding.textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,forgetPassword.class);
                startActivity(intent);
            }
        });
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferencesauth = getSharedPreferences("auth_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editorauth = sharedPreferencesauth.edit();
                editorauth.remove("acceptStatus");
                editorauth.apply();
                Intent stopIntent = new Intent(getApplicationContext(), NotificationService.class);
                stopService(stopIntent);
                Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
                SharedPreferences sharedPreferences = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String URL = "http://"+binding.ipAddress.getText().toString()+":8085";
                editor.putString("URL", URL);
                editor.apply();
                login();
            }
        });
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);

            }
        });


        phoneFocusListener();
        passwordFocusListener();

    }

    private void changeLanguage() {
        final String languages[]={"English","नेपाली"};
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(this);
        mBuilder.setTitle("Choose Language");
        mBuilder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    setLocale("");
                }
                else if(which==1){
                    setLocale("ne");//method ho with arr of languge pass

                }
                recreate();
                dialog.dismiss();
            }

        });
        mBuilder.create();
        mBuilder.show();
    }

    private void setLocale(String language) {
        //--------------------------------------------------------------
        Locale locale=new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration =new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());

        //langage change vayo------------------------------------------------------
//shared preferences
        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("app_lang",language);
        editor.apply();
    }
    //------------------------app reload huda lin locale load hos
    private void loadLocale(){
        SharedPreferences preferences=getSharedPreferences("Settings",MODE_PRIVATE);
        String language=preferences.getString("app_lang","");
        setLocale(language);
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


                String result = validPhone();
                if (result != null) {
                    binding.phoneContainer.setHelperText(result);

                } else {
                    binding.phoneContainer.setHelperText("");
                   // Clear error text if email is valid
                }
            }
        });
    }

    private String validPhone() {
        String phoneText = binding.phoneEditText.getText().toString().trim();
        if (phoneText.length() != 10) {
            return "Minimum 10 number";
        }
        if (!phoneText.matches(".*[0-9].*")) {
            return "Must be all Digit";
        }
        return null; // Return null if email is valid
    }
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
      if(!passwordText.matches(".*[A-Z].*")){
          return "Must Contain 1 Upper-case Character ";
      }
        if(!passwordText.matches(".*[a-z].*")){
            return "Must Contain 1 Lower-case Character ";
        }
        if(!passwordText.matches(".*[@#/$^&=+].*")){
            return "Must Contain 1 Special Character [@#/$^&=+] ";
        }
        return null; // Return null if email is valid
    }
    private  void login() {

        final String[] Token1 = {new String()};
        SharedPreferences sharedPreferences = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferences.getString("URL", null);

        String url = URL + "/api/v1/user/login";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("username", (binding.phoneEditText.getText().toString()));
            jsonRequest.put("password", binding.passwordEditText.getText().toString());
        } catch (JSONException e) {
           Log.d("JsonException",e.toString());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Token1[0] = response.getString("accessToken");
                    Log.d("loginresponse",response.toString());
                    //SharedPreferences to save Token to be accessed by many activities
                    SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("AuthToken", Token1[0]);
                    editor.putString("phone",binding.phoneEditText.getText().toString());
                    Log.d("phone is ",binding.phoneEditText.getText().toString() );
                    editor.apply();
                   // Token token = new Token();
                   // token.setToken(Token1[0]);
                    Intent intent=new Intent(MainActivity.this, dashboard.class);
                    Log.d("mainToken","hello"+ Token1[0]);  // This Token has null value but why??
                   // intent.putExtra("Token", Token1[0]);
                    Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    Log.v("LoginResponse",response.toString());

                } catch (JSONException e) {
                   Log.d("ResponseError",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
               // Log.d("volleyError", error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}