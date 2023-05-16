package com.example.bluetooth.bluetooth;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bluetooth.Const;
import com.example.bluetooth.DataRead_Write;
import com.example.bluetooth.timeTable.ListTimeTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class ServiceBluetooth extends Service {

    BluetoothDevice device;
    private BluetoothGattCharacteristic characteristicGatt;
    public BluetoothGatt bluetoothGatt;
    BluetoothGattCallback myGattCallback;

    String a;
    List<ListTimeTable> listTimeTables; //Список годувань

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiverServiceBluetooth receiver;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
        myGattCallback = new BluetoothGattCallback(this);

        // receiver = new MyBroadcastReceiverBluetooth();

        receiver = new BroadcastReceiverServiceBluetooth();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_DATA_READ);
        filter.addAction(Const.ACTION_PASSWORD_SEND);
        registerReceiver(receiver, filter);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            Log.d(Const.TAG, "ServiceBluetooth start command: " + command);

            switch (command) {
                case "Connect bluetooth":
                    device = intent.getParcelableExtra("bluetooth_device");
                    if (device != null) {
                        Log.d(Const.TAG, "device: " + device);
                        bluetoothGatt = device.connectGatt(this, true, myGattCallback);

                        Intent intent1 = new Intent("Start");
                        intent1.putExtra("device", "11111");
                        sendBroadcast(intent1);
                    }
                    break;
                case "Disconnect":
                    bluetoothGatt.disconnect();
                    Log.d(Const.TAG, "bluetoothGatt.disconnect");

                    break;
                case "Send data":
                    listTimeTables = DataRead_Write.dataReadListTimeTable(getApplicationContext());

                    String com = "D";
                    String dataSize = String.valueOf(listTimeTables.size());
                    if (listTimeTables.size() <= 9) {
                        dataSize = "00" + listTimeTables.size();
                    } else if (listTimeTables.size() <= 99) {
                        dataSize = "0" + listTimeTables.size();
                    }
                    List<String> textPackage = new ArrayList<>();
                    String end = "\n";

                    for (int i = 0; i < listTimeTables.size(); i++) {
                        String numberPackage = String.valueOf(i);
                        String sizePortion = listTimeTables.get(i).getSizePortion();
                        String repetitionPackage = "F";
                        if (Integer.parseInt(listTimeTables.get(i).getSizePortion()) <= 9) {
                            sizePortion = "00" + listTimeTables.get(i).getSizePortion();
                        } else if (Integer.parseInt(listTimeTables.get(i).getSizePortion()) <= 99) {
                            sizePortion = "0" + listTimeTables.get(i).getSizePortion();
                        }
                        if (i <= 9) {
                            numberPackage = "00" + i;
                        } else if (i <= 99) {
                            numberPackage = "0" + i;
                        }
                        if (listTimeTables.get(i).isRepetition()) {
                            repetitionPackage = "T";
                        }
                        String buff = com
                                + dataSize
                                + numberPackage
                                + getWeekDay(listTimeTables.get(i).getWeekDay())
                                + listTimeTables.get(i).getTime()
                                + sizePortion
                                + repetitionPackage
                                + end;
                        textPackage.add(buff);
                    }

//                    bluetoothGatt = device.connectGatt(this, true, myGattCallback);

                    for (String s : textPackage) {
//                        String s = "Пн, Вт" + "\n";
//                        Log.d(Const.TAG, "textPackage: " + s);
                        byte[] value = s.getBytes(StandardCharsets.UTF_8);
                        characteristicGatt = bluetoothGatt
                                .getService(UUID.fromString(Const.UUID_SERVICE))
                                .getCharacteristic(UUID.fromString(Const.YOUR_CHARACTERISTIC_UUID));

                        characteristicGatt.setValue(value);
                        bluetoothGatt.writeCharacteristic(characteristicGatt);

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case "Read data":
                    a += intent.getStringExtra("data");
                    String b = a;
                    Log.d(Const.TAG, "data Service: " + b);
                    break;
                case "Send password":
                    String password = intent.getStringExtra("password");

                    byte[] value = password.getBytes(StandardCharsets.UTF_8);
                    characteristicGatt = bluetoothGatt
                            .getService(UUID.fromString(Const.UUID_SERVICE))
                            .getCharacteristic(UUID.fromString(Const.YOUR_CHARACTERISTIC_UUID));
                    characteristicGatt.setValue(value);
                    bluetoothGatt.writeCharacteristic(characteristicGatt);
                    break;
            }
        }
        return START_NOT_STICKY;
    }


    public String getWeekDay(String weekDay) {

        switch (weekDay) {
            case "Пн":
                return "Mo";
            case "Вт":
                return "Tu";
            case "Ср":
                return "We";
            case "Чт":
                return "Th";
            case "Пт":
                return "Fr";
            case "Сб":
                return "Sa";
            case "Нд":
                return "Su";
        }
        return "";
    }

    public void init() {

        a = "";
        listTimeTables = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


}
