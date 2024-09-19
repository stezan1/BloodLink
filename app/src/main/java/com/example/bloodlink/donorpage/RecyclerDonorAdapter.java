package com.example.bloodlink.donorpage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bloodlink.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecyclerDonorAdapter extends RecyclerView.Adapter<RecyclerDonorAdapter.ViewHolder> {
    private Context context;
    private ArrayList<DonorModel> arrDonor;

    RecyclerDonorAdapter(Context context, ArrayList<DonorModel> arrDonor) {
        this.context = context;
        this.arrDonor = arrDonor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donor_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonorModel donor = arrDonor.get(position);
        holder.txtName.setText(donor.getName());
        holder.txtAge.setText(donor.getAge());
        holder.txtBloodGroup.setText(donor.getBloodgroup());
        holder.txtPints.setText(String.valueOf(donor.getPints()));
        holder.txtLocation.setText(donor.getLocation());
        holder.requesterId.setText(donor.getRequestId());
        String[] parts = holder.txtLocation.getText().toString().split(",");
        String stringAddress = getAddressFromLatLng(Double.parseDouble(parts[0]),Double.parseDouble(parts[1]));
        String[] addressParts = stringAddress.split(",");
       String specificAddress  = addressParts[0].trim() + ", " + addressParts[1].trim();
        holder.address.setText(specificAddress);
        holder.acceptButton.setText(donor.getAcceptButtonText());
        if ("Accepted".equals(donor.getAcceptButtonText())) {
            holder.acceptButton.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
        } else {
            holder.acceptButton.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
        }
        SharedPreferences sharedPreferencesauth = context.getSharedPreferences("auth_prefs",Context.MODE_PRIVATE);
        String acceptedStatus = sharedPreferencesauth.getString("acceptStatus",null);
        if(acceptedStatus != null){
            if(acceptedStatus.equals("Accepted")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.acceptButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    holder.acceptButton.setText("Accepted");
                }
            }
        }


        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(donor.getLocation());
            }

            private void openGoogleMaps(String location) {
                // Extract latitude and longitude from location string
                // Example: "Lat: 28.7041, Long: 77.1025"
                String[] parts = location.split(",");
                double latitude = Double.parseDouble(parts[0].substring(parts[0].indexOf(":") + 1).trim());
                double longitude = Double.parseDouble(parts[1].substring(parts[1].indexOf(":") + 1).trim());
                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Marker+Title)");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                } else {
                    Toast.makeText(context, "Google Maps app not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferencesauth = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                String userId = sharedPreferencesauth.getString("userId",null);
                SharedPreferences sharedPreferences = context.getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
                String Acceptedstatus = sharedPreferencesauth.getString("acceptStatus", "kkkkk,ky");
               String  sharedPreferencesUserId = null;
                if(Acceptedstatus != null) {
                     sharedPreferencesUserId = Acceptedstatus.substring(Acceptedstatus.indexOf(',') + 1);
                }
                if (userId.equals(sharedPreferencesUserId) && Acceptedstatus != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Do you really want to accept the request?")
                            .setTitle("Confirm Action");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                holder.acceptButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                            }
                            SharedPreferences sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("acceptStatus", "Accepted,"+"kj");
                            editor.apply();
                            holder.acceptButton.setText("Accepted");
                            updateLastDonatedDate();
                            updateRequestTable(donor.getRequestId());
                        }
                    });
                    // Set the negative button (No) and its click listener
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    // Create and show the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    Toast.makeText(context, "Cannot donate twice in 3 months", Toast.LENGTH_SHORT).show();
                }
            }

        });

// Example method to accept the request


    }

    @Override
    public int getItemCount() {
        return arrDonor.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtAge, txtBloodGroup, txtPints, txtLocation,address,requesterId;
        ImageView imgContact;
        Button acceptButton;
        ImageButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgContact = itemView.findViewById(R.id.imageContact);
            txtName = itemView.findViewById(R.id.txtName);
            txtAge = itemView.findViewById(R.id.txtAge);
            txtBloodGroup = itemView.findViewById(R.id.txtBloodGroup);
            txtPints = itemView.findViewById(R.id.txtPints);
            requesterId = itemView.findViewById(R.id.txtRequestId);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            address = itemView.findViewById(R.id.txtAddress1);
            imageButton = itemView.findViewById(R.id.locationPin);
        }
    }
    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
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
    public void updateRequestTable(String requestId){
        Log.d("RequestIdNew is"," "+requestId);
        SharedPreferences sharedPreferencesauth = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        String userId = sharedPreferencesauth.getString("userId",null);
        Log.d("acceptedFlow","updateRequest Table running");
        SharedPreferences sharedPreferences = context.getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String URL = sharedPreferences.getString("URL", null);
        String url = URL +"/api/requests/fullFillRequest/" +requestId+ "/"+userId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("rqstfullfill",response);
                Toast.makeText(context, "Go to location and donate fast", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("volleyErroruserIdset", error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
    public void updateLastDonatedDate(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferencesurl = context.getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId",null);
        String URL = sharedPreferencesurl.getString("URL", null);
        String url = URL + "/api/v1/members/updateLastDonatedDate/"+memberId;
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        StringRequest jsonArrayRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("updateLast"," "+ response);
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
    }

}

