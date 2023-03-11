package com.example.bluetooth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

@SuppressLint("MissingPermission")
public class MainActivity extends AppCompatActivity {

    private Button buttonEnableBt, buttonSearchBt, buttonDisableBt;
    private TextView textViewStatusBt;
    private ListView listDeviceBt;
    //
    public String[] ANDROID_12_BLUETOOTH_PERMISSIONS;
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION = 3;
    private BluetoothAdapter mBTAdapter;
    private ArrayAdapter<String> mBTArrayAdapter;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();


        buttonEnableBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothOn();
            }
        });

        buttonDisableBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothOff();
            }
        });

        buttonSearchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoveryDevice();
            }
        });

        listDeviceBt.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //textViewStatusBt.setText(mBTArrayAdapter.get(position));
            }
        });

    }

    private void discoveryDevice() {
        // создаем адаптер
        mBTArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        listDeviceBt.setAdapter(mBTArrayAdapter);

        if (mBTAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION);

            }

            IntentFilter bluetoothFilter = new IntentFilter();
            bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
            bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(blReceiver, bluetoothFilter);
        }
        boolean ableToStartDiscovery = mBTAdapter.startDiscovery();
        if (ableToStartDiscovery) {
            Toast.makeText(getApplicationContext(), "Start discovery", Toast.LENGTH_SHORT).show();
        }
    }

    BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Discovery finished", Toast.LENGTH_SHORT).show();
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void init() {
        //init button
        buttonEnableBt = findViewById(R.id.buttonEnableBt);
        buttonDisableBt = findViewById(R.id.buttonDisableBt);
        buttonSearchBt = findViewById(R.id.buttonSearchBt);

        listDeviceBt = findViewById(R.id.listDeviceBt);
        textViewStatusBt = findViewById(R.id.textViewStatusBt);
        //Init bluetooth
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        ANDROID_12_BLUETOOTH_PERMISSIONS = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
        };
        configPermission();
        //

        //


    }

    private void bluetoothOn() {

        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            textViewStatusBt.setText("Bluetooth enabled");
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    private void bluetoothOff() {
        mBTAdapter.disable(); // turn off
        textViewStatusBt.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(), "Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }


    public void configPermission() {
        if (!EasyPermissions.hasPermissions(this, ANDROID_12_BLUETOOTH_PERMISSIONS)) {
            EasyPermissions.requestPermissions(this, "please give me bluetooth permissions", 3, ANDROID_12_BLUETOOTH_PERMISSIONS);
        }
    }


    // Enter here after user selects "yes" or "no" to enabling radio
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enable", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth disable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}