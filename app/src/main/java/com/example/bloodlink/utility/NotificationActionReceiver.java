package com.example.bloodlink.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String message = intent.getStringExtra("message");

        if ("ACTION_ACCEPT".equals(action)) {
            // Handle Accept action
            Log.d("NotificationAction", "Accepted: " + message);
            // Perform actions like updating the server or database
        } else if ("ACTION_REJECT".equals(action)) {
            // Handle Reject action
            Log.d("NotificationAction", "Rejected: " + message);
            // Perform actions like updating the server or database
        }
    }
}
