package com.example.bluetooth.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bluetooth.R;

import java.util.List;

@SuppressLint("MissingPermission")
public class AdapterListViewBluetooth extends ArrayAdapter<BluetoothDevice> {

    private List<BluetoothDevice> list;

    public AdapterListViewBluetooth(@NonNull Context context, int resource, List<BluetoothDevice> btList) {
        super(context, resource, btList);
        list = btList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_list_bluetooth_device, null, false);

            viewHolder.textNameDevice = convertView.findViewById(R.id.textTime);
            viewHolder.textMacAddress = convertView.findViewById(R.id.textSizePortion);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textNameDevice.setText(list.get(position).getName());
        viewHolder.textMacAddress.setText(list.get(position).getAddress());

        return convertView;
    }

    static class ViewHolder {
        TextView textNameDevice;
        TextView textMacAddress;
    }
}
