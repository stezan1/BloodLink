package com.example.bloodlink.searchdonor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bloodlink.StorageClass;
import com.example.bloodlink.becomeadonor.becomeadonor;
import com.example.bloodlink.dashboard.dashboard;
import com.example.bloodlink.databinding.ActivitySearchdonorBinding;
import com.example.bloodlink.dlist;
import com.example.bloodlink.requestedpage.requestlistpage;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class searchdonor extends AppCompatActivity {

    ActivitySearchdonorBinding binding;
    String stringBloodGroup,stringName,stringpints,stringPhone;
    private String requesterId,universalLocation;
    ArrayList<String>arrbloodGroup=  new ArrayList<>();
    ArrayList<String>arrpint=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivitySearchdonorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(binding.checkBox.isChecked()){
            binding.addressEditText.setEnabled(false);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        String phoneNo = sharedPreferences.getString("phone",null);
        binding.phone.setText(phoneNo);
        binding.patientNameContainer.setHelperText("");
        binding.addressContainer.setHelperText("");
        phoneFocusListner();
        patientNameFocusListener();
        addressFocusListener();
        arrpint.add("1");
        arrpint.add("2");
        arrpint.add("3");
        arrbloodGroup.add("A+");
        arrbloodGroup.add("AB+");
        arrbloodGroup.add("AB-");
        arrbloodGroup.add("B+");
        arrbloodGroup.add("B-");
        arrbloodGroup.add("O-");
        arrbloodGroup.add("O+");

        ArrayAdapter<String> bloodAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,arrbloodGroup);
        binding.bloodgroup.setAdapter(bloodAdapter);
        ArrayAdapter<String>pintAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,arrpint);
        binding.pintEditText.setAdapter(pintAdapter);
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                stringBloodGroup = binding.bloodgroup.getText().toString();
                stringpints = binding.pintEditText.getText().toString();
                stringName = binding.patientNameEditText.getText().toString();
                stringPhone=binding.phone.getText().toString();
                GeoCodeLocation locationAddress = new GeoCodeLocation();
                locationAddress.getAddressFromLocation(binding.addressEditText.getText().toString(), getApplicationContext(), new GeoCoderHandler());

                //geocode

//                if(binding.checkBox.isChecked())
//                {
//                    String s=binding.address.getText().toString();
//                }
//                else{
//                    String address=binding.address.getText().toString();
//                }
//                Intent intend=new Intent(searchdonor.this, dlist.class);
//                if(binding.checkBox.isChecked())
//                {
//                    String address=binding.address.getText().toString();
//                }
//                else{
//                    String address=binding.address.getText().toString();
//                }
                String patient = binding.patientNameEditText.getText().toString();
                String bloodgroup = binding.bloodgroup.getText().toString();
                String pint = binding.pintEditText.getText().toString();
                String s = binding.addressEditText.getText().toString();
                if (patient.isEmpty() && bloodgroup.isEmpty() && pint.isEmpty() && s.isEmpty()) {
                    Toast.makeText(searchdonor.this, "Please fill all  fields", Toast.LENGTH_SHORT).show();
                } else {

                }

            }

        });

    }

    private void phoneFocusListner() {
        binding.phone.addTextChangedListener(new TextWatcher() {
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
        String phoneText = binding.phone.getText().toString().trim();
        if (phoneText.length() != 10) {
            return "Minimum 10 number";
        }
        if (!phoneText.matches(".*[0-9].*")) {
            return "Must be all Digit";
        }
        return null; // Return null if email is valid
    }

    private void patientNameFocusListener() {
        binding.patientNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                String result = validpatientName();
                if (result != null) {
                    binding.patientNameContainer.setHelperText(result);

                } else {
                    binding.patientNameContainer.setHelperText("");
                    // Clear error text if email is valid
                }
            }
        });
    }
    private String validpatientName() {
        String patientNameText = binding.patientNameEditText.getText().toString().trim();
        if (patientNameText.isEmpty()) {
            return "Patient name cannot be empty";
        }
        return null; // Return null if email is valid
    }

    private void addressFocusListener() {
        binding.addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                String result = validAddress();
                if (result != null) {
                    binding.addressContainer.setHelperText(result);

                } else {
                    binding.addressContainer.setHelperText("");
                    // Clear error text if email is valid
                }
            }
        });
    }
    private String validAddress() {
        String addressText = binding.addressEditText.getText().toString().trim();
        if (addressText.isEmpty()) {
            return "Please enter your address";
        }
        return null; // Return null if email is valid
    }
    public void RequestBlood(String lat, String lon) {

        SharedPreferences sharedPreferences = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferencesauth = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
       String userId = sharedPreferencesauth.getString("userId",null);
        String URL = sharedPreferences.getString("URL", null);
        String url = URL +"/api/v1/requesters";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("bloodGroup",stringBloodGroup);
            jsonRequest.put("latitude",lat);
            jsonRequest.put("name",stringName);
            jsonRequest.put("longitude", lon);
            jsonRequest.put("phone", stringPhone);
            jsonRequest.put("pints", stringpints);
            JSONObject memberLocationObject = new JSONObject();
            JSONObject userInfoObject = new JSONObject();
            memberLocationObject.put("latitude",lat);
            userInfoObject.put("id",userId);
            memberLocationObject.put("longitude",lon);
            jsonRequest.put("memberLocation",memberLocationObject);
            jsonRequest.put("userInfo",userInfoObject);
            Log.d("requesterJsonObject is",jsonRequest.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RequesterResponse",response.toString());
                try {
                   requesterId = response.getString("id");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("requesterId",requesterId);
                    Log.d("requesterIdinsearch","r"+ requesterId);
                    editor.apply();
                    StorageClass s = new StorageClass();
                    s.setRequesterId(requesterId);
                   saveToRequestTable(requesterId);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
               if(!universalLocation.isEmpty()){ //changed by khem
                   Intent intent = new Intent(searchdonor.this,requestlistpage.class);
                   startActivity(intent);
            }
               else {
                   Toast.makeText(searchdonor.this, "Please Enter correct Address", Toast.LENGTH_SHORT).show();
               }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("volleyError", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                Intent i = getIntent();
//                String Token = i.getStringExtra("Token");
                SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                String Token = sharedPreferences.getString("AuthToken", null);
                Log.d("BeDonorTokeninheader",Token);

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+Token);

                return headers;
            }
        };

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
            try {
                String[] parts = locationAddress.split(" ");
                universalLocation = locationAddress;
                if(stringName.length() == 0){
                    Toast.makeText(searchdonor.this, "Please enter Requester Name", Toast.LENGTH_SHORT).show();
                }
                else{
                RequestBlood(parts[0], parts[1]);}
            }
            catch (NullPointerException ne){
                Toast.makeText(searchdonor.this, "Please enter Requester Name", Toast.LENGTH_SHORT).show();
            }
            catch (Exception E){
                Toast.makeText(searchdonor.this, "Please enter valid address", Toast.LENGTH_SHORT).show();

            }


        }


    }

    public void saveToRequestTable(String requesterId){
        SharedPreferences sharedPreferences = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferences.getString("URL", null);
        String url = URL +"/api/requests/send/"+requesterId;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Toast.makeText(searchdonor.this, "request sent to nearest Donors", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(searchdonor.this, requestlistpage.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("volleyError", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                String Token = sharedPreferences.getString("AuthToken", null);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+Token);

                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}



