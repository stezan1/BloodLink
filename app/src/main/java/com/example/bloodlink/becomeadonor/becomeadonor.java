package com.example.bloodlink.becomeadonor;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bloodlink.R;
import com.example.bloodlink.dashboard.dashboard;
import com.example.bloodlink.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class becomeadonor extends AppCompatActivity {
    private String latLong;
    private String id;
    private String firstName,middleName,lastName,bloodGroup,dob,registrationDate,gender,lastDonatedDate;
    private String Address;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    DatePickerDialog.OnDateSetListener setListener;


    EditText et_firstName,et_middleName,et_lastName, rt_address,et_lastdonatedDate,et_dob,lastDate,et_address;
    Button updatebtn, cancelbtn,update,cancel;
    TextView DOB;
    AutoCompleteTextView tv_bloodGroup, tv_gender;


    ArrayList<String> arblood = new ArrayList<>();
    ArrayList<String> argender = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_becomeadonor);
        et_dob = findViewById(R.id.dob);
        et_firstName= findViewById(R.id.firstName);
        et_middleName=findViewById(R.id.middleName);
        et_lastName=findViewById(R.id.lastName);
        // bloodGroup = findViewById(R.id.bloodGroup);
        et_address = findViewById(R.id.address);
        et_lastdonatedDate = findViewById(R.id.lastDate);
        tv_gender = findViewById(R.id.gender);


        updatebtn = findViewById(R.id.update);
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cancelbtn = findViewById(R.id.cancel);
        tv_bloodGroup = findViewById(R.id.bloodGroup);

        et_dob.setInputType(InputType.TYPE_CLASS_DATETIME);
        et_lastdonatedDate.setInputType(InputType.TYPE_CLASS_DATETIME);
        et_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
               int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        becomeadonor.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                        // on below line we are setting date to our edit text.
                      String formatedDate =  String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                        // dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        et_dob.setText(formatedDate);
                    }
                },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        et_lastdonatedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        becomeadonor.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our edit text.
                        // dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        String formatedDate =  String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                        et_lastdonatedDate.setText(formatedDate);
                    }
                },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });


        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address = et_address.getText().toString();
                firstName = et_firstName.getText().toString();
                middleName = et_middleName.getText().toString();
                lastName = et_lastName.getText().toString();
                bloodGroup = tv_bloodGroup.getText().toString();
                dob = et_dob.getText().toString();
                lastDonatedDate = et_lastdonatedDate.getText().toString();
               // registrationDate =  ;
                gender = tv_gender.getText().toString();

                // Check if the checkbox is checked
//                if (checkBox.isChecked()) {
                    // Check if all EditText fields are filled
                    //  int dayOfMonth = 0;
                    // int monthOfYear=0;
                    // int year = 0;
                    //String D= dob.getText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year).toString();
                    // int s=Integer.parseInt(D);
                    String dobText = et_dob.getText().toString().trim();// Trim to remove leading/trailing spaces
                    if (isEditTextFilled(et_firstName) &&
                            isEditTextFilled(et_lastName)&&
                            isEditTextFilled(tv_bloodGroup) &&
                            isEditTextFilled(et_address) &&
                            !dobText.isEmpty() &&
                            isEditTextFilled(et_lastdonatedDate)&&
                            isEditTextFilled(tv_gender)) {
                        GeoCodeLocation locationAddress = new GeoCodeLocation();
                        locationAddress.getAddressFromLocation(et_address.getText().toString(), getApplicationContext(), new GeoCoderHandler());

                        // ------All fields are filled, show success message OR DATABASE HALNU

                        // Clear all EditText fields
//                        et_firstName.getText().clear();
//                        et_middleName.getText().clear();
//                        et_lastName.getText().clear();
//                        tv_bloodGroup.getText().clear();
//                        et_address.getText().clear();
//                        tv_gender.getText().clear();
//                        et_dob.setText("");
//                        et_lastdonatedDate.setText("");



                    } else {
                        // At least one field is empty, show an error message
                        Toast.makeText(becomeadonor.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    }
//                } else {
//                    // Checkbox is not checked, show a message
//                    Toast.makeText(becomeadonor.this, "Please check the 'Become a Donor' checkbox.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(becomeadonor.this, dashboard.class);
                startActivity(intent);
            }
        });

        arblood.add("A+");
        arblood.add("AB+");
        arblood.add("AB-");
        arblood.add("B+");
        arblood.add("B-");
        arblood.add("O-");
        arblood.add("O+");

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arblood);
        tv_bloodGroup.setAdapter(bloodAdapter);

        //---------------------gender
        argender.add("Male");
        argender.add("Female");
        argender.add("Other");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, argender);
        tv_gender.setAdapter(genderAdapter);
    }


    // Helper function to check if an EditText is filled
    private boolean isEditTextFilled(EditText editText) {
        return editText.getText() != null && !editText.getText().toString().isEmpty();
    }

    public void becomeDonor() {
        SharedPreferences sharedPreferencesauth = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        String userId = sharedPreferencesauth.getString("userId",null);
        Log.d("userId is", " " +userId);
        SharedPreferences sharedPreferences = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String latitude = sharedPreferences.getString("latitude",null);
        String longitude = sharedPreferences.getString("longitude",null);
        String URL = sharedPreferences.getString("URL", null);
        String url = URL +"/api/v1/members";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("firstname",firstName );
            jsonRequest.put("middlename", middleName);
            jsonRequest.put("lastname", lastName);
            jsonRequest.put("dateOfBirth", dob);
            jsonRequest.put("bloodGroup", bloodGroup);
            jsonRequest.put("gender", gender);
            jsonRequest.put("lastTimeOfDonation", lastDonatedDate);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                jsonRequest.put("registrationDate", LocalDate.now());
            }
            JSONObject memberLocationObject = new JSONObject();
            memberLocationObject.put("latitude",latitude);
            memberLocationObject.put("longitude",longitude);
            jsonRequest.put("memberLocation",memberLocationObject);
            Log.d("nestedRequest",jsonRequest.toString());



        } catch (JSONException e) {
           e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    id = response.getString("id");
                    Log.d("memberResponse","id is"+id);
                    setUserId(id,userId);
                } catch (JSONException e) {
                    Log.d("xceptionInResponse",e.toString());
                }

                Intent intent = new Intent(becomeadonor.this, dashboard.class);
                // This Token has null value but why??
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
            SharedPreferences sharedPreferences = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            try {
                String[] parts = locationAddress.split(" ");
                editor.putString("latitude", parts[0]);
                editor.putString("longitude", parts[1]);
                editor.apply();
                Log.d("Location1",locationAddress);
                becomeDonor();
            }
            catch (Exception E){
                Toast.makeText(becomeadonor.this, "Please enter valid address", Toast.LENGTH_SHORT).show();

            }




        }


    }
    public void setUserId(String memberId, String userId){
        SharedPreferences sharedPreferences = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferences.getString("URL", null);
        String url = URL +"/api/v1/members/setUserId/" +memberId + "/"+userId;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
Log.d("seruserIdresponse",response);
                setDonorId(memberId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("volleyErroruserIdset", error.toString());
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
    public void setDonorId(String id){
        SharedPreferences sharedPreferences = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferences.getString("URL", null);
        String url = URL +"/api/v1/donor-infos/"+ id ;
        Log.d("donorId"," "+id);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("seruserIdresponse",response);
		  // Toast.makeText(becomeadonor.this, "donor id is set to" + response, Toast.LENGTH_SHORT).show();
                Toast.makeText(becomeadonor.this, "Successful. Thank you for becoming a donor to save innocent lives", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volleyErroruserIdset", error.toString());
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


}








