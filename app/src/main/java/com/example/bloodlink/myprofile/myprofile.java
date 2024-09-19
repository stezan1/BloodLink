package com.example.bloodlink.myprofile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.bloodlink.R;
import com.example.bloodlink.becomeadonor.becomeadonor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class myprofile extends AppCompatActivity {
TextView textView, txtName,txtAge,txtBloodGroup,txtLocation,txtType,lastDonatedDate;
Button button2;//declaration for button2
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getMemberById();
        setContentView(R.layout.activity_myprofile);
        txtName=findViewById(R.id.txtName);
        txtAge=findViewById(R.id.txtAgeprofile);
        txtBloodGroup=findViewById(R.id.txtBloodGroup);
       // txtPints=findViewById(R.id.txtPints);
        txtLocation=findViewById(R.id.txtLocation);
        lastDonatedDate=findViewById(R.id.lastDonatedDate);
        textView=findViewById(R.id.textView);
     //   button2=findViewById(R.id.button2);//find and assigning the value button2
String s=txtName.getText().toString();

    }
    public void getMemberById(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences sharedPreferencesurl = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferencesurl.getString("URL", null);
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId",null);
        String url = URL +"/api/v1/members";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,new Response.Listener<JSONArray>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String memberName = null,bloodGroup =null,dob=null;
                    int flag = 0;
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        JSONObject memberLocation = jsonObject.getJSONObject("memberLocation");
                       JSONObject User =  jsonObject.getJSONObject("userInfo");
                       if( userId.equals((User.getString("id")))){
                           flag = 1;
                            lastDonatedDate.setText(jsonObject.getString("lastTimeOfDonation"));
                            memberName = jsonObject.getString("firstname") +  " " +jsonObject.getString("lastname");
                            bloodGroup = jsonObject.getString("bloodGroup");
                            dob = jsonObject.getString("dateOfBirth");
                           String specificAddress;
                            specificAddress = getAddressFromLatLng(Double.parseDouble(memberLocation.getString("latitude")),Double.parseDouble(memberLocation.getString("longitude")));
                           String[] addressParts = specificAddress.split(",");
                           // Check if the address has at least two parts
                           if (addressParts.length >= 2) {
                               // Trim and combine the first two parts to get the desired address
                               specificAddress  = addressParts[0].trim() + ", " + addressParts[1].trim();

                           }
                           txtLocation.setText(specificAddress);

                           break;
                       }

                    }
                    if(flag == 1){
                        txtBloodGroup.setText(" " +bloodGroup);
                        txtName.setText(" "+memberName);
                        DateTimeFormatter formatter = null;
                            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        // Parse the string to a LocalDate object
                        LocalDate localdate = null;
                             localdate = LocalDate.parse(dob, formatter);
                        // Get the current date
                        LocalDate currentDate = null;
                            currentDate = LocalDate.now();
                        // Calculate the period between the dob and the current date
                        Period period = null;
                            period = Period.between(localdate, currentDate);
                        // Get the years, which represents the age
                             age = period.getYears();
                        txtAge.setText(" "+age);
                    }
                    else {
                        LinearLayout layout1 = findViewById(R.id.becomedonorbutton);
                        Button btn_becomeDonor = new Button(getApplicationContext());
                        btn_becomeDonor.setTextSize(20); // Text size in SP
                        btn_becomeDonor.setTextColor(Color.WHITE);
                        btn_becomeDonor.setBackgroundColor(Color.BLUE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        btn_becomeDonor.setLayoutParams(params);
                        layout1.addView(btn_becomeDonor);
                        params.setMargins(50, 20, 50, 20);
                        Toast.makeText(myprofile.this, "Please become donor to help people", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(myprofile.this,becomeadonor.class);
                        startActivity(intent);

                    }

//                    Log.d("myprofileOnsesponse","onresonse called");
//                    String id = response.getString("id").toString();
//                    String name = response.getString("firstname").toString();
//                    if(id.isEmpty()){
//                        Toast.makeText(myprofile.this, "please become a donor and help people", Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//
//                    }
                }
                catch (Exception e){
                    Log.d("exceptionInMyprofile",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("FindMemberByUseriderror",error.toString());
                Toast.makeText(myprofile.this, error.toString(), Toast.LENGTH_SHORT).show();

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
        };;

        requestQueue.add(jsonArrayRequest);

    }
    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address = "Address not found";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder addressBuilder = new StringBuilder();

                // Construct the address string from various components
                if (fetchedAddress.getSubLocality() != null) {
                    addressBuilder.append(fetchedAddress.getSubLocality()).append(", ");
                }

                if (fetchedAddress.getLocality() != null) {
                    addressBuilder.append(fetchedAddress.getLocality()).append(", ");
                }

                if (fetchedAddress.getAdminArea() != null) {
                    addressBuilder.append(fetchedAddress.getAdminArea()).append(", ");
                }

                if (fetchedAddress.getCountryName() != null) {
                    addressBuilder.append(fetchedAddress.getCountryName()).append(", ");
                }

                if (fetchedAddress.getPostalCode() != null) {
                    addressBuilder.append(fetchedAddress.getPostalCode());
                }

                address = addressBuilder.toString();

                // Clean up any trailing commas
                if (address.endsWith(", ")) {
                    address = address.substring(0, address.length() - 2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }


}