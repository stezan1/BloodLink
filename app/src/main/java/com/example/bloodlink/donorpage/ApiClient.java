package com.example.bloodlink.donorpage;
// notification icon ma click gare paxi yo class run hunxa
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ApiClient {
    private static ApiClient instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private ApiClient(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public interface VolleyCallback {
        void onSuccess(ArrayList<DonorModel> result);
        void onError(VolleyError error);
    }

    public void getDonors(final VolleyCallback callback) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferences.getString("URL", null);
        String url = URL + "/api/requests"; // Replace with your actual API endpoint
        Log.d("funct", "functionCalled");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<DonorModel> donors = parseResponse(response);
                        callback.onSuccess(donors);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });
        getRequestQueue().add(jsonArrayRequest);
    }

    private ArrayList<DonorModel> parseResponse(JSONArray response) {
        SharedPreferences sharedPreferencesauth = ctx.getSharedPreferences("auth_prefs",Context.MODE_PRIVATE);
        String memberId = sharedPreferencesauth.getString("memberId",null);
        Log.d("memberId in Api ", " "+ memberId);
        ArrayList<DonorModel> donorModels = new ArrayList<>();
        try {
            int flag = 0;
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                JSONObject requesterObj = jsonObject.getJSONObject("requester");
                JSONObject donorInfoObj = jsonObject.optJSONObject("donorInfo");
                String requestId = requesterObj.getString("id");
                boolean disabled = jsonObject.getBoolean("disabled");
                String acceptStatus =  sharedPreferencesauth.getString("acceptStatus",null);
                String donorId = donorInfoObj.getString("id").toString();
                if((donorId.equals(memberId)  && disabled == false)) {
                    flag = 1;
                    String firstName = requesterObj.optString("name", "Unknown");
                    String lastName = ""; // Last name is not present in the JSON, use empty string or handle accordingly
                    String name = firstName + " " + lastName;
                    // Extract age and blood group
                    String bloodgroup = requesterObj.optString("bloodGroup", "Unknown");
                    int pints = jsonObject.optInt("totalPintsDonated", 0);

                    // Location
                    String latitude = requesterObj.optString("latitude", "0");
                    String longitude = requesterObj.optString("longitude", "0");
                    String location = latitude + ", " + longitude;
                    // Default age since no birth date information is available
                    String age = "18";
                    DonorModel donorModel = new DonorModel(name, age, bloodgroup, pints, location,requestId);
                    donorModels.clear();//accept gareko bahek aaru lai nadekhaune
                        SharedPreferences.Editor editor = sharedPreferencesauth.edit();
                        editor.putString("acceptStatus", "Accepted");
                        editor.apply();
                    donorModels.add(donorModel);
                    break;
                }
                else if((donorId.equals(memberId) && disabled != false)) {//accept gare paxi yo code chalxa
                    flag = 1;
                    String firstName = requesterObj.optString("name", "Unknown");
                    String lastName = ""; // Last name is not present in the JSON, use empty string or handle accordingly
                    String name = firstName + " " + lastName;
                    // Extract age and blood group
                    String bloodgroup = requesterObj.optString("bloodGroup", "Unknown");
                    int pints = jsonObject.optInt("totalPintsDonated", 0);
                    if (pints > 0) {
                        // Location
                        String latitude = requesterObj.optString("latitude", "0");
                        String longitude = requesterObj.optString("longitude", "0");
                        String location = latitude + ", " + longitude;
                        // Default age since no birth date information is available
                        String age = "18";
                        DonorModel donorModel = new DonorModel(name, age, bloodgroup, pints, location, requestId);
                        donorModels.add(donorModel);

                    }
                }

            }
            if(flag == 0){
                Toast.makeText(ctx, "NO request for you today", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("Error parsing JSON response: " + e.getMessage());
        }
        return donorModels;
    }

    private String calculateAge(String dateOfBirth) {
        String age = Utils.calculateAge(dateOfBirth);
        return age;
    }
}
