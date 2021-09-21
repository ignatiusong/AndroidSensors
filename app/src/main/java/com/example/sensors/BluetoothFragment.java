package com.example.sensors;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class BluetoothFragment extends Fragment {
    private final int REQUEST_ENABLE_BT = 2020;
    private BluetoothAdapter BA;

    public BluetoothFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BA = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void turnBluetoothOn() {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    private void turnBluetoothOff() {
        BA.disable();
        Toast.makeText(getContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }

    private void setBluetoothVisible() {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    private void loadUIElements() {
        Button onBluetoothButton = getView().findViewById(R.id.onBluetoothbutton);
        Button offBluetoothButton = getView().findViewById(R.id.offBluetoothButton);
        Button setDiscoveryButton = getView().findViewById(R.id.setDiscoverableButton);

        onBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnBluetoothOn();
            }
        });

        offBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnBluetoothOff();
            }
        });

        setDiscoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBluetoothVisible();
            }
        });
    }
}