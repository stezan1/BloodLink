package com.example.bloodlink.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.bloodlink.NotificationService;
import com.example.bloodlink.R;
import com.example.bloodlink.StorageClass;
import com.example.bloodlink.becomeadonor.becomeadonor;
import com.example.bloodlink.databinding.ActivityDashboardBinding;
import com.example.bloodlink.donorpage.RecyclerDonorAdapter;
import com.example.bloodlink.donorpage.donorPage;
import com.example.bloodlink.myprofile.myprofile;
import com.example.bloodlink.requestedpage.requestlistpage;
import com.example.bloodlink.searchdonor.searchdonor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class dashboard extends AppCompatActivity {
    ActivityDashboardBinding binding;
    ImageButton notify;
    Button requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("memberId"); // Removes the "memberId" key-value pair
        editor.apply();
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setIdInSharedPreferences();
        setContentView(binding.getRoot());
        requests = findViewById(R.id.requests);

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard.this, requestlistpage.class);
                startActivity(intent);
            }
        });

        notify = findViewById(R.id.notification);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                String memberId = sharedPreferences.getString("memberId", null);
                if (memberId != null){
                    Intent i = new Intent(dashboard.this, donorPage.class);
                startActivity(i);
            }
                else {
                    Toast.makeText(dashboard.this, "You are no a registered donor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageSlider imageSlider = binding.imageSlider;
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.bl1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.b2, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);


        binding.requested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(dashboard.this, searchdonor.class);
                startActivity(intent);
            }
        });
        binding.person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMember();
            }
        });

    }

    public void setIdInSharedPreferences() {
        Log.d("kkk", "functionCalled");
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        String requesterId = sharedPreferences.getString("requesterId", null);
        StorageClass s = new StorageClass();
        s.setRequesterId(requesterId);
        String phone = sharedPreferences.getString("phone", null);
        SharedPreferences sharedPreferencesurl = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferencesurl.getString("URL", null);
        String url = URL + "/api/v1/user/getId/" + phone;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest jsonArrayRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId", response);
                        editor.putString("acceptStatus", "Accepted,"+response);
                        editor.apply();
                        Log.d("userIdInDash", response);
                        editor.apply();
                        getRequesterIdByUserId(response);
                        getMemberIdByUserId();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("errorResponse", "Error: " + error.toString());
                        // Handle error here
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
        Log.d("ll", "requested queed");

    }

    public void getMemberIdByUserId() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences sharedPreferencesurl = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferencesurl.getString("URL", null);
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        String url = URL + "/api/v1/members";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String memberName = null, bloodGroup = null, dob = null;
                   int flag = 0;
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        JSONObject User = jsonObject.getJSONObject("userInfo");
                        if (userId.equals((User.getString("id")))) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("memberId", jsonObject.getString("id"));
                            Log.d("memberIdSetInDashboard", " " + jsonObject.getString("id"));
                            editor.apply();
                            if(jsonObject.getString("id")==null){
                                Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                                stopService(intent);
                            }
                            break;
                        }

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
                } catch (Exception e) {
                    Log.d("exceptionIndash", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DashError", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                Intent i = getIntent();
//                String Token = i.getStringExtra("Token");
                SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                String Token = sharedPreferences.getString("AuthToken", null);
                Log.d("BeDonorTokeninheader", Token);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Token);

                return headers;
            }
        };
        ;

        requestQueue.add(jsonArrayRequest);

    }

    public void getRequesterIdByUserId(String userId) {
        SharedPreferences sharedPreferencesurl = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferencesurl.getString("URL", null);
        String url = URL + "/api/v1/requesters/" + userId;  // Replace with your actual URL

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences.Editor editor = sharedPreferencesurl.edit();
                        Log.d("requeserIdResponse", response);
                        editor.putString("requesterId", response);
                        editor.apply();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors
                        Log.e("Error", error.toString());
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void isMember() {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences sharedPreferencesurl = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferencesurl.getString("URL", null);
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        String url = URL + "/api/v1/members";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String memberName = null, bloodGroup = null, dob = null;
                   int  flag = 0;
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        JSONObject User = jsonObject.getJSONObject("userInfo");
                        if (userId.equals((User.getString("id")))) {
                            flag = 1;
                            break;
                        }

                    }
                    if (flag == 1) {
                        Intent intent = new Intent(dashboard.this,myprofile.class);
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(dashboard.this,becomeadonor.class);
                        startActivity(intent);

                    }

                } catch (Exception e) {
                    Log.d("exceptionInMyprofile", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("personBtnErr", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                Intent i = getIntent();
//                String Token = i.getStringExtra("Token");
                SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                String Token = sharedPreferences.getString("AuthToken", null);
                Log.d("BeDonorTokeninheader", Token);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Token);

                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }
}


