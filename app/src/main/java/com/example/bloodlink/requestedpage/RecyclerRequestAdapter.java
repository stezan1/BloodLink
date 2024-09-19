package com.example.bloodlink.requestedpage;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodlink.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerRequestAdapter extends RecyclerView.Adapter<RecyclerRequestAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RequesterModel> arrRequest;

    public RecyclerRequestAdapter(Context context, ArrayList<RequesterModel> arrRequest) {
        this.context = context;
        this.arrRequest = arrRequest;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.requested_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequesterModel requester = arrRequest.get(position);
        holder.txtName.setText(requester.getName());
        holder.txtBloodGroup.setText(requester.getBloodGroup());
        holder.txtPints.setText(String.valueOf(requester.getPints()));
        holder.txtLocation.setText(requester.getLatitude() + "," + requester.getLongitude());
        // Set click listener for the image button
        String address = getAddressFromLatLng(requester.getLatitude(),requester.getLongitude());
        Log.d("Addreddkhemis", " "+address);
        String[] addressParts = address.split(",");
        String phNo = requester.getPhone().toString();
        Log.d("phnoinLise"," "+phNo);
         if(phNo.length()>=11){
             Log.d("flow","accept farya raixa");
             phNo = phNo.substring(0,10);
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                 holder.btn_requested.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                 holder.btn_requested.setText("Accepted");
             }
             phNo = phNo.substring(phNo.indexOf("0")+1);
         }
        holder.txtphone.setText(" "+phNo);
        String specificAddress  = addressParts[0].trim() + "," + addressParts[1].trim();
        Log.d("speAddreddkhemis", " "+specificAddress);
        holder.txtAddress.setText(" " +specificAddress);
        holder.btn_requested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall(holder.txtphone.getText().toString());
            }
        });
        holder.imageButton.setOnClickListener(v -> openGoogleMaps( requester.getLongitude()+ "," + requester.getLatitude()));
    }
    @Override
    public int getItemCount() {
        return arrRequest.size();
    }

//    private void openGoogleMaps(double latitude, double longitude) {
//        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Location)");
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//
//        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
//            context.startActivity(mapIntent);
//        }
//    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtBloodGroup, txtPints, txtLocation,txtAddress,txtphone,btn_requested;
        ImageView imgContact;
        ImageButton imageButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgContact = itemView.findViewById(R.id.imageContact);
            txtName = itemView.findViewById(R.id.txtName);
            txtBloodGroup = itemView.findViewById(R.id.txtBloodGroup);
            txtPints = itemView.findViewById(R.id.txtPints);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            imageButton = itemView.findViewById(R.id.locationPin);
            txtAddress = itemView.findViewById(R.id.txtAddress1);
            txtphone = itemView.findViewById(R.id.txtPhone);
            btn_requested = itemView.findViewById(R.id.buttonRequested);
        }
    }
    private void openGoogleMaps(String location) {

        // Extract latitude and longitude from location string
        // Example: "Lat: 28.7041, Long: 77.1025"
        String[] parts = location.split(",");
        double latitude = Double.parseDouble(parts[0].substring(parts[0].indexOf(":") + 1).trim());
        double longitude = Double.parseDouble(parts[1].substring(parts[1].indexOf(":") + 1).trim());
        Log.d("lat and long is ", " "+ latitude + "k" + longitude);

        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Marker+Title)");

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            Toast.makeText(context, "Please Install google Maps", Toast.LENGTH_SHORT).show();
        }
    }
    private String getAddressFromLatLng( double longitude,double latitude) {
        Log.d("latlong is" , " "+latitude + longitude);
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
    public void makePhoneCall(String phoneNumber) {

        if(phoneNumber.length()>=11){
            phoneNumber = phoneNumber.substring(0,10);
        }
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
        }
    }
}
