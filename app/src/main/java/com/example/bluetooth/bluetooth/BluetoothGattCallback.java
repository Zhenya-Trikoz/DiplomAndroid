package com.example.bluetooth.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bluetooth.constFields.ConstFields;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BluetoothGattCallback extends android.bluetooth.BluetoothGattCallback {
    private final Context context;
    private BluetoothGattCharacteristic characteristicGatt;
    private Intent intent;

    public BluetoothGattCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            Log.d(ConstFields.TAG, "Connect Bluetooth");
            gatt.discoverServices();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            characteristicGatt = gatt.getService(UUID.fromString(ConstFields.UUID_SERVICE))
                    .getCharacteristic(UUID.fromString(ConstFields.CHARACTERISTIC_UUID));

            gatt.setCharacteristicNotification(characteristicGatt, true);
            gatt.readCharacteristic(characteristicGatt);

        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(ConstFields.TAG, "Data sent successfully");
        } else {
            Log.d(ConstFields.TAG, "Failed to send data");
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        String value = new String(data, StandardCharsets.UTF_8).replaceAll("[\\n\\r]", "");
        Log.d(ConstFields.TAG, "Bluetooth read data: " + value);

        if (value.equals("C")) {
            intent = new Intent(ConstFields.ACTION_CONNECT_DEVICE);
        } else if (value.startsWith("P")) {
            intent = new Intent(ConstFields.ACTION_ACCESS_PROVIDE);
        } else if (value.startsWith("NP")) {
            intent = new Intent(ConstFields.ACTION_ACCESS_NOT_PROVIDE);
        } else if (value.startsWith("C2")) {
            intent = new Intent(ConstFields.ACTION_MEASURE_WEIGHT_READ);
            String s = value.substring(2);
            Log.d(ConstFields.TAG, s);
            intent.putExtra(ConstFields.ACTION_MEASURE_WEIGHT_READ, value.substring(2));
        } else {
            intent = new Intent(ConstFields.ACTION_DATA_READ);
            intent.putExtra(ConstFields.EXTRA_DATA, value);
        }
        context.sendBroadcast(intent);

    }

}