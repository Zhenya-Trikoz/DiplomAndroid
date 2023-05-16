package com.example.bluetooth.alarmManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bluetooth.Const;

public class BroadcastReceiverAlarmManager extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String command = intent.getStringExtra("command");
        Log.d(Const.TAG, "BroadcastReceiverAlarmManager " + command);

        Intent i = new Intent(context, ServiceAlarmManager.class);

        if (command.equals(Const.COMMAND_SERVICE_FINISH_TIMETABLE_TODAY)) {
            int id = intent.getIntExtra("id", -1);
            i.putExtra("id", id);
        }
        i.putExtra("command", command);
        context.startService(i);

    }

}
