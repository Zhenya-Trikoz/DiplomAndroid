package com.example.bluetooth.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bluetooth.Const;
import com.example.bluetooth.bluetooth.ServiceBluetooth;

public class BroadcastReceiverServiceBluetooth extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Const.ACTION_DATA_READ)) {
            String data = intent.getStringExtra(Const.EXTRA_DATA);
            Log.d(Const.TAG, "ACTION_DATA_READ data: " + data);

            Intent i = new Intent(context, ServiceBluetooth.class);
            i.putExtra("command", "Read data");
            i.putExtra("data", data);

            context.startService(i);
            // обробка отриманих даних
//        } else if (action.equals(Const.ACTION_CONNECT_DEVICE)) {
//            List<String> data = intent.getStringArrayListExtra(Const.EXTRA_DATA);
//            Log.d(Const.TAG, "ACTION_CONNECT_DEVICE data:" + data);
//
//        }
        } else if (action.equals(Const.ACTION_PASSWORD_SEND)) {
            String password = intent.getStringExtra("password");

            Intent i = new Intent(context, ServiceBluetooth.class);
            i.putExtra("command", "Send password");
            i.putExtra("password", password);
            context.startService(i);
        }
    }
}
