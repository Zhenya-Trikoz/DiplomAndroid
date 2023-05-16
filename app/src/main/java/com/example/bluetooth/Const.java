package com.example.bluetooth;

public class Const {
    public static final String ACTION_DATA_READ = "ACTION_DATA_READ";
    public static final String ACTION_DATA_WRITE = "ACTION_DATA_WRITE";

    public static final String ACTION_CONNECT_DEVICE = "ACTION_CONNECT_DEVICE";
    public static final String ACTION_PASSWORD_SEND = "ACTION_PASSWORD_SEND";
    public static final String ACTION_ACCESS_PROVIDE = "ACTION_ACCESS_PROVIDE";
    public static final String ACTION_ACCESS_NOT_PROVIDE = "ACTION_ACCESS_NOT_PROVIDE";

    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static String TAG = "BluetoothGattTAG";
    public static String PREF = "my_pref";
    public static String PREF_LIST_TIME_TABLE = "PREF_LIST_TIME_TABLE";
    public static String PREF_LIST_DIAGRAM_TIME_TABLE = "PREF_LIST_DIAGRAM_TIME_TABLE";

    public static String COMMAND_SERVICE_CREATE_TIMETABLE_TODAY = "create timeTable today";
    public static String COMMAND_SERVICE_FINISH_TIMETABLE_TODAY = "finish timeTable today";
    public static String COMMAND_MIDNIGHT = "midnight";

    public static String UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String YOUR_CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG_UUID = "00002902-0000-1000-8000-00805f9b34fb";

}
