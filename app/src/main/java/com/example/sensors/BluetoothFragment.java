package com.example.sensors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BluetoothFragment extends Fragment {
    private BluetoothAdapter BA;
    private final Set<BluetoothDevice> nearbyDevices;
    private ConnectThread connectThread;

    ListView lv;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // MAC address
                nearbyDevices.add(device);
            }
        }
    };

    public BluetoothFragment() {
        // Required empty public constructor
        nearbyDevices = new HashSet<>();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BA = BluetoothAdapter.getDefaultAdapter();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        requireContext().registerReceiver(receiver, filter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
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
        int requestCode = 1;
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, requestCode);
        Toast.makeText(requireContext(), "Bluetooth device is now visible", Toast.LENGTH_SHORT).show();

    }

    private void listPairedDevices(){
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();

        ArrayList<String> list = new ArrayList<>();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter<String> adapter = new  ArrayAdapter<>(
                getContext(),android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void listNearbyDevices(){
        BA.startDiscovery();
        if(nearbyDevices.isEmpty()) {
            Toast.makeText(getContext(), "No Nearby Devices", Toast.LENGTH_SHORT).show();
            return;
        }
        List<BluetoothDevice> list = nearbyDevices.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Toast.makeText(getContext(), "Showing Nearby Devices", Toast.LENGTH_SHORT).show();

        final ArrayAdapter<BluetoothDevice> adapter = new BluetoothArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
    }

    public void sendMessage(String message) {
        if (connectThread != null) {
            connectThread.sendMessage(message);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadUIElements() {
        Button onBluetoothButton = requireView().findViewById(R.id.onBluetoothbutton);
        Button offBluetoothButton = requireView().findViewById(R.id.offBluetoothButton);
        Button setDiscoveryButton = requireView().findViewById(R.id.setDiscoverableButton);
        Button listPairedDevicesButton = requireView().findViewById(R.id.listPairedDevicesButton);
        Button listNearbyDevicesButton = requireView().findViewById(R.id.listNearbyDevicesButton);
        Button startServerButton = requireView().findViewById(R.id.startServerButton);
        Button sendMessageButton = requireView().findViewById(R.id.sendMessageButton);

        TextView receivedMsgTextView = requireView().findViewById(R.id.receivedMsgTextView);
        EditText sendMsgEditText = requireView().findViewById(R.id.messageToSendEditText);

        lv = requireView().findViewById(R.id.listView);

        /*
        lv.setOnItemClickListener((adapterView, view, position, l) -> {
            BluetoothDevice device = (BluetoothDevice) lv.getItemAtPosition(position);
            ConnectThread connectThread = new ConnectThread(BA, requireContext());
            connectThread.start();

        });*/

        onBluetoothButton.setOnClickListener(view -> turnBluetoothOn());

        offBluetoothButton.setOnClickListener(view -> turnBluetoothOff());

        setDiscoveryButton.setOnClickListener(view -> setBluetoothVisible());

        listPairedDevicesButton.setOnClickListener(view -> listPairedDevices());

        listNearbyDevicesButton.setOnClickListener(view -> listNearbyDevices());

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == MessageConstants.MESSAGE_READ) {
                    byte[] bytes = (byte[]) msg.obj;

                    receivedMsgTextView.setText(new String(bytes));

                }

            }
        };

        startServerButton.setOnClickListener(view -> {
            ConnectThread connectThread = new ConnectThread(BA, handler);
            BluetoothFragment.this.connectThread = connectThread;
            connectThread.start();
        });

        sendMessageButton.setOnClickListener(view -> sendMessage(sendMsgEditText.getText().toString()));


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUIElements();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireContext().unregisterReceiver(receiver);
    }
}