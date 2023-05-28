package com.example.bluetooth.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetooth.Const;
import com.example.bluetooth.timeTable.TimeTable;
import com.example.bluetooth.R;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

@SuppressLint("MissingPermission")
public class Bluetooth extends AppCompatActivity {
    private TextView textViewStatusBt;
    private ListView listDeviceBt;

    public String[] ANDROID_12_BLUETOOTH_PERMISSIONS;
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private BluetoothAdapter mBTAdapter;
    private BtAdapter btAdapter;

    private List<BluetoothDevice> bluetoothDeviceList;

    int numberAttempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ACTION_CONNECT_DEVICE);
        intentFilter.addAction(Const.ACTION_ACCESS_PROVIDE);
        intentFilter.addAction(Const.ACTION_ACCESS_NOT_PROVIDE);
        registerReceiver(broadcastReceiverMainActivity, intentFilter);

        //Bluetooth ON
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            discoveryDevice();
        }

        listDeviceBt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startServiceBluetooth("Connect bluetooth", bluetoothDeviceList.get(position));
            }
        });

    }

    public void startServiceBluetooth(String command, BluetoothDevice device) {
        Intent intent = new Intent(this, ServiceBluetooth.class);
        intent.putExtra("command", command);
        intent.putExtra("bluetooth_device", device);

        startService(intent);
    }

    public void disconnect() {
        Intent intent = new Intent(this, ServiceBluetooth.class);
        intent.putExtra("command", "Disconnect");

        startService(intent);
    }

    public void passwordConfirmed() {
        Log.d(Const.TAG, "Password access");
        Intent intent = new Intent(this, TimeTable.class);
        startActivity(intent);
    }

    public void enterPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Bluetooth.this);
        builder.setTitle("Пароль");

        View view = LayoutInflater.from(Bluetooth.this).inflate(R.layout.layout_dialog_password, null);
        builder.setView(view);

        EditText editText = view.findViewById(R.id.editTextPassword);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = editText.getText().toString();

                if (password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Невірно введені дані, спробуйте знову", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Const.ACTION_PASSWORD_SEND);
                    intent.putExtra("password", password);
                    sendBroadcast(intent);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(blReceiver);
        unregisterReceiver(broadcastReceiverMainActivity);
    }

    private void discoveryDevice() {
        if (mBTAdapter.isEnabled()) {

            IntentFilter bluetoothFilter = new IntentFilter();
            bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
            bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(blReceiver, bluetoothFilter);
        }
        mBTAdapter.startDiscovery();

    }

    private final BroadcastReceiver broadcastReceiverMainActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_CONNECT_DEVICE)) {
                enterPassword();
            } else if (action.equals(Const.ACTION_ACCESS_PROVIDE)) {
                passwordConfirmed();
            } else if (action.equals(Const.ACTION_ACCESS_NOT_PROVIDE)) {
                numberAttempt++;
                if (numberAttempt == 3) {
                    Toast.makeText(getApplicationContext(), "Три стпроби було використано!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Виконайте підключення знову.", Toast.LENGTH_SHORT).show();
                    numberAttempt = 0;
                    disconnect();
                } else {
                    Toast.makeText(getApplicationContext(), "Пароль не правильний!\nСпробуйте знову.", Toast.LENGTH_SHORT).show();
                    enterPassword();
                }
            }
        }
    };
    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDeviceList.add(device);
                btAdapter.notifyDataSetChanged();
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Discovery finished", Toast.LENGTH_SHORT).show();
                textViewStatusBt.setText("Discovery finish");
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                textViewStatusBt.setText("Discovery start");
            }
        }
    };

    public void init() {

        listDeviceBt = findViewById(R.id.listDeviceBt);
        textViewStatusBt = findViewById(R.id.textViewStatusBt);
        numberAttempt = 0;
        //Init bluetooth
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        ANDROID_12_BLUETOOTH_PERMISSIONS = new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        // создаем адаптер
        bluetoothDeviceList = new ArrayList<>();
        btAdapter = new BtAdapter(this, R.layout.element_list_bluetooth_device, bluetoothDeviceList);
        listDeviceBt.setAdapter(btAdapter);

        configPermission();

    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enable", Toast.LENGTH_SHORT).show();
                discoveryDevice();
            } else {
                Toast.makeText(this, "Bluetooth disable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void configPermission() {
        if (!EasyPermissions.hasPermissions(this, ANDROID_12_BLUETOOTH_PERMISSIONS)) {
            EasyPermissions.requestPermissions(this, "please give me bluetooth permissions", 3, ANDROID_12_BLUETOOTH_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}