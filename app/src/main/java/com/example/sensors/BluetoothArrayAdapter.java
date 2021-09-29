package com.example.sensors;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BluetoothArrayAdapter extends ArrayAdapter<BluetoothDevice> {
    private final Context mContext;
    private final List<BluetoothDevice> bluetoothDeviceList;
    private final int layoutID;

    public BluetoothArrayAdapter(@NonNull Context context, int resource, @NonNull List<BluetoothDevice> objects) {
        super(context, resource , objects);
        mContext = context;
        bluetoothDeviceList = objects;
        layoutID = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(layoutID,
                    parent,false);

        BluetoothDevice currentDevice = bluetoothDeviceList.get(position);

        TextView textView = listItem.findViewById(android.R.id.text1);
        if (currentDevice.getName() != null) {
            textView.setText(currentDevice.getName());
        } else {
            textView.setText(currentDevice.toString());
        }

        return listItem;

    }
}
