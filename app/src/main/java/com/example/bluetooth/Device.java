package com.example.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class Device extends AppCompatActivity {

    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic characteristicGatt;
    BluetoothGattDescriptor descriptor;
    private String mReceivedData = ""; // Створення порожнього рядка для збереження отриманих даних


    TextView name, logger;
    String TAG = "BluetoothGattTAG";
    Button start;

    String UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    String YOUR_CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    String CLIENT_CHARACTERISTIC_CONFIG_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    /////
    Button button2, button3;
    Button buttonSend;
    ListView listView;
    ArrayAdapter<String> adapter;
    BluetoothGatt gatt1;
    /////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        Bundle arguments = getIntent().getExtras();
        device = (BluetoothDevice) arguments.get("device");

        init();
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
        list1();

                //Відправка даних на блютуз
//                for (int i = 0; i < 10; i++) {
//                    String s = "HELLO WORLD" + i + "\n";
//                    byte[] value = s.getBytes(StandardCharsets.UTF_8);
//
//                    characteristicGatt.setValue(value);
//                    gatt1.writeCharacteristic(characteristicGatt);
//
//                    try {
//                        Thread.sleep(5);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }



//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                start(device);
//            }
//        });
    }

    public void list1() {
        listView = findViewById(R.id.List1);
        List<String> list = new ArrayList<>();
        // создаем адаптер
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        // устанавливаем для списка адаптер
        listView.setAdapter(adapter);

        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");
        list.add("1111");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void start(BluetoothDevice device) {
        name.setText(device.getName());

    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.d(TAG, "Connect Bluetooth");
                gatt1 = gatt;
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //Знаходимо характеристику яку будем використовувати для передачі даних
                characteristicGatt = gatt.getService(UUID.fromString(UUID_SERVICE)).
                        getCharacteristic(UUID.fromString(YOUR_CHARACTERISTIC_UUID));
//                        Log.d(TAG, String.valueOf(UUID.fromString(service.getUuid().toString())));
                // Set notification for the characteristic

                byte[] value = "HELLO WORLD".getBytes(StandardCharsets.UTF_8);

                characteristicGatt.setValue(value);
                gatt.writeCharacteristic(characteristicGatt);

                gatt.setCharacteristicNotification(characteristicGatt, true);
                gatt.readCharacteristic(characteristicGatt);


//                descriptor = characteristicGatt.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG_UUID));
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);
//                    gatt.setCharacteristicNotification(mCharacteristic, true); // Встановлення повідомлень про зміну характеристики
                //}
            }
        }

//        @Override
//        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicRead(gatt, characteristic, status);
////                Log.d("OnCharacteristicRead");
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                byte[] characteristicValue = characteristic.getValue();
//                String value = new String(characteristicValue, StandardCharsets.UTF_8);
//                Log.d(TAG, "onCharacteristicRead Received data: " + value);
//
//            } else if (status == BluetoothGatt.GATT_READ_NOT_PERMITTED) {
//                Log.d(TAG, "No permitted to read a characteristic");
//            } else if (status == BluetoothGatt.GATT_FAILURE) {
//                Log.d(TAG, "failed to read a characteristic");
//            }
//        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //if (characteristic.getUuid().equals(YOUR_CHARACTERISTIC_UUID)) {
            byte[] data = characteristic.getValue();
            String value = new String(data, StandardCharsets.UTF_8);
            Log.d(TAG, "Received data: " + value);
            // }
        }

    };

    public void sendData() {


    }

    public void init() {

//        name = findViewById(R.id.textViewNameDevice);
//        logger = findViewById(R.id.textViewLog);
//        start = findViewById(R.id.buttonStartConnect);

    }
}