package com.example.bluetooth.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.bluetooth.constFields.ConstFields;

public class BroadcastReceiverServiceBluetooth extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConstFields.ACTION_DATA_READ)) {
            String data = intent.getStringExtra(ConstFields.EXTRA_DATA);

            Intent i = new Intent(context, ServiceBluetooth.class);
            i.putExtra("command", ConstFields.ACTION_DATA_READ);
            i.putExtra("data", data);
            context.startService(i);

        } else if (action.equals(ConstFields.ACTION_DISCONNECT)) {
            Intent i = new Intent(context, ServiceBluetooth.class);
            i.putExtra("command", ConstFields.ACTION_DISCONNECT);
            context.startService(i);

        } else if (action.equals(ConstFields.ACTION_DATA_SEND)) {
            Intent i = new Intent(context, ServiceBluetooth.class);
            i.putExtra("command", ConstFields.ACTION_DATA_SEND);
            context.startService(i);

        } else if (action.equals(ConstFields.ACTION_PASSWORD_SEND)) {
            String password = intent.getStringExtra("password");

            Intent i = new Intent(context, ServiceBluetooth.class);
            i.putExtra("command", ConstFields.ACTION_PASSWORD_SEND);
            i.putExtra("password", password);
            context.startService(i);

        } else if (action.equals(ConstFields.ACTION_ADDITION_SEND)) {
            String feed = intent.getStringExtra("feed");

            Intent i = new Intent(context, ServiceBluetooth.class);
            i.putExtra("command", ConstFields.ACTION_ADDITION_SEND);
            i.putExtra("feed", feed);
            context.startService(i);

        } else if (action.equals(ConstFields.ACTION_TIME_SEND)) {
            Intent i = new Intent(context, ServiceBluetooth.class);
            i.putExtra("command", ConstFields.ACTION_TIME_SEND);
            context.startService(i);

        }else if (action.equals(ConstFields.ACTION_MEASURE_WEIGHT_SEND)) {
            Intent i = new Intent(context, ServiceBluetooth.class);
            i.putExtra("command", ConstFields.ACTION_MEASURE_WEIGHT_SEND);
            context.startService(i);
        }
    }
}
