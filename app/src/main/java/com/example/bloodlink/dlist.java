package com.example.bloodlink;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodlink.databinding.ActivityDlistBinding;
import com.example.bloodlink.requestedpage.requestlistpage;
import com.example.bloodlink.searchdonor.searchdonor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dlist extends AppCompatActivity {
ActivityDlistBinding binding;

    Button backbtn;
    Button reqbtn;

    double requestingDeviceLatitude;
    double requestingDeviceLongitude;
    String mydevID;
    String myfcm;

    double mydevIDLocationKm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlist);

        reqbtn=findViewById(R.id.button);
        Intent i = getIntent();
        if (i != null) {
            mydevID = i.getStringExtra("devId");
            myfcm = i.getStringExtra("fcm");
            requestingDeviceLatitude = i.getDoubleExtra("lat", 0.0); // 0.0 is the default value if the extra is not found
            requestingDeviceLongitude = i.getDoubleExtra("lon", 0.0);
        }
        reqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent i=new Intent(dlist.this, requestlistpage.class);
                Intent i = new Intent(dlist.this, requestlistpage.class);
                //intent.putExtra("patient", getIntent().getStringExtra("patient"));
                i.putExtra("bloodgroup", getIntent().getStringExtra("bloodgroup"));
                i.putExtra("pints", getIntent().getStringExtra("pints"));
                i.putExtra("address", getIntent().getStringExtra("address"));


                startActivity(i);
            }
        });
        backbtn = findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(dlist.this, searchdonor.class);
                startActivity(i);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("locations");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TableLayout tableLayout = findViewById(R.id.tableLayout);
                tableLayout.removeAllViews();

                TableRow headerRow = new TableRow(dlist.this);

                TextView deviceIdHeaderTextView = new TextView(dlist.this);
                deviceIdHeaderTextView.setText("Device ID");
                deviceIdHeaderTextView.setPadding(10, 0, 10, 0);

                TextView distanceHeaderTextView = new TextView(dlist.this);
                distanceHeaderTextView.setText("Distance (km)");
                distanceHeaderTextView.setPadding(10, 0, 10, 0);

                TextView differenceHeaderTextView = new TextView(dlist.this);
                differenceHeaderTextView.setText("Difference (km)");
                differenceHeaderTextView.setPadding(10, 0, 10, 0);

                headerRow.addView(deviceIdHeaderTextView);
                headerRow.addView(distanceHeaderTextView);
                headerRow.addView(differenceHeaderTextView);

                tableLayout.addView(headerRow);
                System.out.println(requestingDeviceLatitude);
                System.out.println(requestingDeviceLongitude);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nodeKey = snapshot.getKey();
                    LocationData locationData = snapshot.getValue(LocationData.class);

                    double distance = calculateDistance(
                            requestingDeviceLatitude,
                            requestingDeviceLongitude,
                            locationData.getLatitude(),
                            locationData.getLongitude()
                    );

                    if (nodeKey.equals(mydevID)) {
                        mydevIDLocationKm = distance;
                    }
                    
                    double myDeviceDistance = mydevIDLocationKm;
                    double difference = distance - myDeviceDistance;


                    TableRow row = new TableRow(dlist.this);

                    TextView nodeKeyTextView = new TextView(dlist.this);
                    nodeKeyTextView.setText(nodeKey);
                    nodeKeyTextView.setPadding(10, 0, 10, 0);

                    TextView distanceTextView = new TextView(dlist.this);
                    distanceTextView.setText(String.format("%.2f", distance));
                    distanceTextView.setPadding(10, 0, 10, 0);

                    TextView differenceTextView = new TextView(dlist.this);
                    differenceTextView.setText(String.format("%.2f", difference));
                    differenceTextView.setPadding(10, 0, 10, 0);

                    row.addView(nodeKeyTextView);
                    row.addView(distanceTextView);
                    row.addView(differenceTextView);

                    tableLayout.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in kilometers
        return distance;
    }
}
