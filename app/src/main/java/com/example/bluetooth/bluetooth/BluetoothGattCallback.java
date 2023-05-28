package com.example.bluetooth.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bluetooth.Const;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BluetoothGattCallback extends android.bluetooth.BluetoothGattCallback {
    private Context context;
    private BluetoothGattCharacteristic characteristicGatt;
    Intent intent;

    public BluetoothGattCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            Log.d(Const.TAG, "Connect Bluetooth");
            gatt.discoverServices();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            characteristicGatt = gatt.getService(UUID.fromString(Const.UUID_SERVICE))
                    .getCharacteristic(UUID.fromString(Const.YOUR_CHARACTERISTIC_UUID));

            gatt.setCharacteristicNotification(characteristicGatt, true);
            gatt.readCharacteristic(characteristicGatt);

        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
//            Log.d(Const.TAG, "Data sent successfully");
        } else {
            Log.d(Const.TAG, "Failed to send data");
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        String value = new String(data, StandardCharsets.UTF_8);
        Log.d(Const.TAG, "BluetoothGatt data: " + value);

        if (value.equals("C")) {
            intent = new Intent(Const.ACTION_CONNECT_DEVICE);
        } else if (value.equals("P")) {
            intent = new Intent(Const.ACTION_ACCESS_PROVIDE);
        } else if (value.equals("NP")) {
            intent = new Intent(Const.ACTION_ACCESS_NOT_PROVIDE);
        } else {
            intent = new Intent(Const.ACTION_DATA_READ);
            intent.putExtra(Const.EXTRA_DATA, value);
        }
        context.sendBroadcast(intent);

    }

}