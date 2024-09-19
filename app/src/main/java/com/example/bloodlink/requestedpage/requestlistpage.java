package com.example.bloodlink.requestedpage;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.bloodlink.R;

import java.util.ArrayList;

public class requestlistpage extends AppCompatActivity {
    ArrayList<RequesterModel> arrRequest = new ArrayList<>();
    private RecyclerRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_requestlistpage);

        RecyclerView recyclerView = findViewById(R.id.recyclerrequestedlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerRequestAdapter(this, arrRequest);
        recyclerView.setAdapter(adapter);

        fetchRequestorData();
    }

    private void fetchRequestorData() {
        ApiClient2.getInstance(this).getRequestors(new ApiClient2.VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<RequesterModel> result) {
                arrRequest.clear();
                arrRequest.addAll(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("API_ERROR", "Error fetching data: " + error.getMessage());
            }
        });
    }
}
