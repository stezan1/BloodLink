package com.example.bloodlink.utility;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.bloodlink.R;


public class NetworkChangeListener extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedToInternet(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.no_internet_dialog, null);
            builder.setView(layout_dialog);

            AppCompatButton btn_retry = layout_dialog.findViewById(R.id.btn_retry);
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);
            btn_retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
    }
}

