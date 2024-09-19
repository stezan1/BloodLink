package com.example.bloodlink;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bloodlink.Login_SignUp_ForgetPassword_Portal.MainActivity;
import com.example.bloodlink.utility.NotificationActionReceiver;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;
import okio.ByteString;

public class NotificationService extends Service {

    private static final String TAG = "NotificationService";
    private static final String CHANNEL_ID = "BloodLinkChannel";
    private WebSocket webSocket;
    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler(); // Handler for scheduling reconnections
    private final Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("flow","connectWebSocket in run");
            connectWebSocket();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", null);
        if(memberId != null){
            Log.d("flow","memberId is "+ memberId);
            createNotificationChannel();
            connectWebSocket();

        }
        else{
            Log.d("flow", "memberId null xa");
        }
    }

    private void connectWebSocket() {
        resetNotificationCount();
        Log.d("flow"," web socket connecting");
        // Retrieve the MemberId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferencesurl = getSharedPreferences("url_prefs", Context.MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", null);
        Log.d("flow"," memberId in webSocket is"+memberId);
        String URL = sharedPreferencesurl.getString("URL","http://192.168.1.76:8085");
        int startIndex = URL.indexOf("//");

        String extractedPart = "";
        if (startIndex != -1) {
            extractedPart = URL.substring(startIndex); // Extracts from the '//' onward
        }

        if (memberId != null) {
            // Append MemberId to the WebSocket URL
            String wsUrl = "ws:" + extractedPart+"/ws?memberId=" + memberId;

            Request request = new Request.Builder()
                    .url(wsUrl)
                    .build();
            WebSocketListener listener = new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    Log.d(TAG, "WebSocket connection opened");
                    webSocket.send("Hello from client with MemberId: " + memberId);
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    Log.d(TAG, "Received message: " + text);
                    showNotification(text);
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    Log.d(TAG, "Received bytes: " + bytes.size());
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    Log.d(TAG, "WebSocket closing: " + reason);
                    webSocket.close(code, reason);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    Log.d(TAG, "WebSocket closed: " + reason);
                    reconnectWebSocket(); // Reconnect on close
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    Log.e(TAG, "WebSocket error", t);
                    reconnectWebSocket(); // Reconnect on failure
                }
            };

            webSocket = client.newWebSocket(request, listener);
        } else {
            Log.e("memidnull", "MemberId is null. Cannot connect to WebSocket.");
        }
    }

    private void reconnectWebSocket() {
        handler.postDelayed(reconnectRunnable, 50000); // Delay of 30 seconds before reconnecting
    }

    private void createNotificationChannel() {
            Log.d("flow","create hudai xa notification");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "BloodLink Notifications";
            String description = "Channel for BloodLink notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void showNotification(String message) {
        Log.d("flow"," notificatio is showing begingin" + message);
        Log.d("flow","count is " + getNotificationCount());
        int commaIndex = message.indexOf(",");
        if (getNotificationCount() >= 3) {
            Log.d(TAG, "Notification limit reached. No new notifications will be shown.");
            return;
        }

        // Intent for Accept action
        Intent acceptIntent = new Intent(this, NotificationActionReceiver.class);
        acceptIntent.setAction("ACTION_ACCEPT");
        acceptIntent.putExtra("message", message);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent for Reject action
        Intent rejectIntent = new Intent(this, NotificationActionReceiver.class);
        rejectIntent.setAction("ACTION_REJECT");
        rejectIntent.putExtra("message", message);
        PendingIntent rejectPendingIntent = PendingIntent.getBroadcast(this, 1, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.blood) // Replace with your own icon
                .setContentTitle(message.substring(commaIndex + 1).trim() + " needs " + message.substring(6,9) + " blood")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.round_check_24, "Accept", acceptPendingIntent)
                .addAction(R.drawable.ic_reject, "Reject", rejectPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = (int) System.currentTimeMillis();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        incrementNotificationCount();
        notificationManager.notify(notificationId, builder.build());
    }


    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service Running")
                .setContentText("Foreground service is active")
                .setSmallIcon(R.drawable.blood) // Use your own icon
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Service destroyed");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        NotificationService getService() {
            return NotificationService.this;
        }
    }
    private static final int MAX_NOTIFICATIONS = 3;
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String NOTIFICATION_COUNT_KEY = "notification_count";

    private void incrementNotificationCount() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int count = prefs.getInt(NOTIFICATION_COUNT_KEY, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(NOTIFICATION_COUNT_KEY, count + 1);
        editor.apply();
    }

    private void resetNotificationCount() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(NOTIFICATION_COUNT_KEY, 0);
        editor.apply();
    }

    private int getNotificationCount() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(NOTIFICATION_COUNT_KEY, 0);
    }

}
