package com.example.bloodlink.requestedpage;
// ko ko lai request gayo list
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.bloodlink.StorageClass;
import com.example.bloodlink.donorpage.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApiClient2 {
    public static Context getCtx() {
        return ctx;
    }
    private static ApiClient2 instance;
    ArrayList<RequesterModel> requesterList = new ArrayList<>();
    private RequestQueue requestQueue;
    private static Context ctx;
    public interface RequesterResponseCallback {
        void onRequesterResponse(ArrayList<RequesterModel> requesterList);
    }


    private ApiClient2(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized ApiClient2 getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient2(context);
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
        void onSuccess(ArrayList<RequesterModel> result);
        void onError(VolleyError error);
    }


    public void getRequestors(final VolleyCallback callback) {
        SharedPreferences sharedPreferencesurl = ctx.getSharedPreferences("url_prefs",Context.MODE_PRIVATE);
        String requesterId = sharedPreferencesurl.getString("requesterId",null);
        String URL = sharedPreferencesurl.getString("URL", null);
        if( !requesterId.equals("null")) {
            String url = URL + "/api/requests"; // Replace with your actual API endpoint
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("ApiClient2", "Response: " + response.toString());
                            parseRequestorResponse(response, new RequesterResponseCallback() {
                                @Override
                                public void onRequesterResponse(ArrayList<RequesterModel> requesterList) {
                                    Log.d("callback ma xu", requesterList.toString());
                                    callback.onSuccess(requesterList);

                                }
                            });
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
        else{
            Toast.makeText(ctx, "You have not made any request", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseRequestorResponse(JSONArray response, RequesterResponseCallback callback) {
        boolean donorPresent = false;
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String requesterId = sharedPreferences.getString("requesterId", null);
        String URL = sharedPreferences.getString("URL", null);
        Log.d("requesteridkkk", " " + requesterId);
        requesterList.clear();
        try {
            final String[] name = new String[1];
            final String[] phone = new String[1];
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                JSONObject requester = jsonObject.getJSONObject("requester");
                JSONObject donorInfo = jsonObject.getJSONObject("donorInfo");
                if (requesterId.equals(requester.getString("id"))) {
                    String  disabled = jsonObject.getString("disabled");
                    Log.d("booleadDisabled is"," "+jsonObject.getString("disabled"));

                    String memberId = donorInfo.getString("id");
                    donorPresent = true;
                    String url = URL + "/api/v1/members/" + memberId; // Replace with your actual API endpoint
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

                                        JSONObject userInfo = response.getJSONObject("userInfo");
                                        Log.d("name ph", "Response: " + response.getString("firstname") + response.getString("lastname")+userInfo.getString("username")); // Log the response
                                        name[0] = response.getString("firstname") + " "+response.getString("middlename") + response.getString("lastname");
                                        Log.d("requested","disabled for "+ name[0]+ " is" + disabled);
                                        phone[0] =  userInfo.getString("username");
                                        if(disabled.equals("false")){
                                            phone[0] =   phone[0]+"0";
                                        }
                                        String bloodGroup = requester.has("bloodGroup") ? requester.getString("bloodGroup") : "N/A";
                                        double latitude = jsonObject.has("currentLatitude") ? jsonObject.getDouble("currentLatitude") : 0.0;
                                        double longitude = jsonObject.has("currentLongitude") ? jsonObject.getDouble("currentLongitude") : 0.0;
                                        RequesterModel requesterModel = new RequesterModel(name[0], " " + bloodGroup, 1, latitude, longitude,Long.parseLong(phone[0]));
                                        Log.d("phone no is ", " " + phone[0]);
                                        requesterList.add(requesterModel);
                                        callback.onRequesterResponse(requesterList); // Trigger the callback with the updated list
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("err name and ph", " " + error.toString());
                                }
                            });
                    getRequestQueue().add(jsonObjectRequest);
                }
            }
            if (!donorPresent) {
                Toast.makeText(ctx, "You have not requested Blood ", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e("ApiClient2", "JSON parsing error: ", e);
        }
    }
    private String calculateAge(String dateOfBirth) {
        return Utils.calculateAge(dateOfBirth);
    }
}