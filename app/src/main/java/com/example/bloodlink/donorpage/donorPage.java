package com.example.bloodlink.donorpage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import com.example.bloodlink.R;

import java.util.ArrayList;

public class donorPage extends AppCompatActivity {
    ArrayList<DonorModel> arrDonor = new ArrayList<>();
    RecyclerDonorAdapter adapter;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_donor_page);

        RecyclerView recyclerView = findViewById(R.id.recyclerdonor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter
        adapter = new RecyclerDonorAdapter(this, arrDonor);
        recyclerView.setAdapter(adapter);

        fetchDonorData();

    }
    private void fetchDonorData() {
        ApiClient.getInstance(this).getDonors(new ApiClient.VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<DonorModel> result) {
                arrDonor.addAll(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                // Log detailed error message
                Log.e("donorPage", "Error fetching donors: " + error.getMessage(), error);
                // Show a toast or alert indicating the failure to the user
                Toast.makeText(donorPage.this, "Error fetching donors. Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
