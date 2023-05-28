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

import com.example.bluetooth.Const;
import com.example.bluetooth.DataRead_Write;
import com.example.bluetooth.timeTable.ListTimeTable;

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
                    }
                    break;
                case "Disconnect":
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
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
                    String end = "~";

                    for (int i = 0; i < listTimeTables.size(); i++) {
                        String numberPackage = String.valueOf(i);

                        String[] s = listTimeTables.get(i).getTime().split(":");
                        String time = "" + (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                        if (Integer.parseInt(time) <= 9) {
                            time = "000" + time;
                        } else if (Integer.parseInt(time) <= 99) {
                            time = "00" + time;
                        } else if (Integer.parseInt(time) <= 999) {
                            time = "0" + time;
                        }
                        String sizePortion = listTimeTables.get(i).getSizePortion();

                        String repetitionPackage = "0";
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
                            repetitionPackage = "1";
                        }
                        String buff = com
                                + dataSize
                                + numberPackage
                                + getWeekDay(listTimeTables.get(i).getWeekDay())
                                + time
                                + sizePortion
                                + repetitionPackage
                                + end;
                        Log.d(Const.TAG, "buff: " + buff);
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
                    String password = "P" + intent.getStringExtra("password") + "~";

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


    public int getWeekDay(String weekDay) {

        switch (weekDay) {
            case "Пн":
                return 1;
            case "Вт":
                return 2;
            case "Ср":
                return 3;
            case "Чт":
                return 4;
            case "Пт":
                return 5;
            case "Сб":
                return 6;
            case "Нд":
                return 7;
        }
        return 0;
    }

    public void init() {

        a = "";
        listTimeTables = new ArrayList<>();
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }

}
