package com.example.bluetooth.bluetooth;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bluetooth.constFields.ConstFields;
import com.example.bluetooth.csv.DataRead_Write;
import com.example.bluetooth.timeTable.ListTimeTable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class ServiceBluetooth extends Service {

    private BluetoothDevice device;
    private BluetoothGattCharacteristic characteristicGatt;
    public BluetoothGatt bluetoothGatt;
    private BluetoothGattCallback myGattCallback;

    private String a;
    private List<ListTimeTable> listTimeTables; //Список годувань
    private BroadcastReceiverServiceBluetooth broadcastReceiverServiceBluetooth;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();
        myGattCallback = new BluetoothGattCallback(this);

        broadcastReceiverServiceBluetooth = new BroadcastReceiverServiceBluetooth();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstFields.ACTION_DISCONNECT);
        filter.addAction(ConstFields.ACTION_DATA_READ);
        filter.addAction(ConstFields.ACTION_DATA_SEND);
        filter.addAction(ConstFields.ACTION_PASSWORD_SEND);
        filter.addAction(ConstFields.ACTION_ADDITION_SEND);
        filter.addAction(ConstFields.ACTION_TIME_SEND);
        filter.addAction(ConstFields.ACTION_MEASURE_WEIGHT_SEND);
        registerReceiver(broadcastReceiverServiceBluetooth, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            Log.d(ConstFields.TAG, "ServiceBluetooth start command: " + command);

            switch (command) {
                case ConstFields.ACTION_CONNECT_DEVICE:
                    device = intent.getParcelableExtra("bluetooth_device");
                    if (device != null) {
                        Log.d(ConstFields.TAG, "device: " + device);
                        bluetoothGatt = device.connectGatt(this, true, myGattCallback);
                    }
                    break;
                case ConstFields.ACTION_DISCONNECT:
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                    Log.d(ConstFields.TAG, "Bluetooth disconnect");
                    break;
                case ConstFields.ACTION_DATA_SEND:
                    listTimeTables = DataRead_Write.dataReadListTimeTable(getApplicationContext());

                    String com = "D";

                    List<String> textPackage = new ArrayList<>();
                    String end = "~";

                    for (int i = 0; i < listTimeTables.size(); i++) {

                        String[] s = listTimeTables.get(i).getTime().split(":");
                        String time = "" + (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                        if (Integer.parseInt(time) <= 9) {
                            time = "000" + time;
                        } else if (Integer.parseInt(time) <= 99) {
                            time = "00" + time;
                        } else if (Integer.parseInt(time) <= 999) {
                            time = "0" + time;
                        }
                        String sizePortion = getStringLess(Integer.parseInt(listTimeTables.get(i).getSizePortion()));

                        String repetitionPackage = "0";
                        if (listTimeTables.get(i).isRepetition()) {
                            repetitionPackage = "1";
                        }
                        String buff = com
                                + getWeekDayAsInt(listTimeTables.get(i).getWeekDay())
                                + time
                                + sizePortion
                                + repetitionPackage
                                + end;
//                        Log.d(ConstFields.TAG, "buff: " + buff);
                        textPackage.add(buff);
                    }

                    for (String s : textPackage) {
                        updateData(s);
                        try {
                            Thread.sleep(120);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    updateData("C1~");
                    break;
                case ConstFields.ACTION_DATA_READ:
                    a += intent.getStringExtra("data");
                    String b = a;

                    Log.d(ConstFields.TAG, "data Service: " + b.length());
                    break;
                case ConstFields.ACTION_PASSWORD_SEND:
                    String password = "P" + intent.getStringExtra("password") + "~";
                    Log.d(ConstFields.TAG, "Password send: " + intent.getStringExtra("password"));
                    updateData(password);
                    break;
                case ConstFields.ACTION_ADDITION_SEND:
                    String addition = "C3" + intent.getStringExtra("feed") + "~";
                    updateData(addition);
                    break;
                case ConstFields.ACTION_MEASURE_WEIGHT_SEND:
                    String definition = "C2~";
                    updateData(definition);
                    break;
                case ConstFields.ACTION_TIME_SEND:

                    Calendar calendar = Calendar.getInstance();

                    String year = getStringLess(calendar.get(Calendar.YEAR));
                    String month = getStringLess(calendar.get(Calendar.MONTH) + 1);
                    String day = getStringLess(calendar.get(Calendar.DAY_OF_MONTH));
                    String hour = getStringLess(calendar.get(Calendar.HOUR_OF_DAY));
                    String minute = getStringLess(calendar.get(Calendar.MINUTE));
                    String second = getStringLess(calendar.get(Calendar.SECOND));
                    String time = "T" + year + month + day + hour + minute + second + "~";
                    updateData(time);
                    break;
            }
        }
        return START_NOT_STICKY;
    }


    public void updateData(String data) {
        byte[] value = data.getBytes(StandardCharsets.UTF_8);
        characteristicGatt = bluetoothGatt
                .getService(UUID.fromString(ConstFields.UUID_SERVICE))
                .getCharacteristic(UUID.fromString(ConstFields.CHARACTERISTIC_UUID));
        characteristicGatt.setValue(value);
        bluetoothGatt.writeCharacteristic(characteristicGatt);
    }

    public String getStringLess(int number) {
        String s;
        if (number <= 9) {
            s = "0" + number;
        } else {
            s = String.valueOf(number);
        }
        return s;
    }

    public int getWeekDayAsInt(String weekDay) {

        switch (weekDay) {
            case "Пн":
                return 0;
            case "Вт":
                return 1;
            case "Ср":
                return 2;
            case "Чт":
                return 3;
            case "Пт":
                return 4;
            case "Сб":
                return 5;
            case "Нд":
                return 6;
        }
        return 0;
    }

    public void init() {
        a = "";
        listTimeTables = new ArrayList<>();
    }

}
