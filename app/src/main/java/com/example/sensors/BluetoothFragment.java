package com.example.sensors;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

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


    public void sendMessage(String message) {
        if (connectThread != null) {
            connectThread.sendMessage(message);
        }
    }

    private FusedLocationProviderClient fusedLocationProviderClient;

    private void getLocationAndSend(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, location -> {
            if(location != null) {
                connectThread.sendMessage(location.getLatitude() + " " + location.getLongitude());
            } else {
                Toast.makeText(requireContext(), "No GPS Location found", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private GyroscopeEventListener gyroEventListener;

    private void loadGyroscopeSensors() {
        SensorManager sensorManager;
        Sensor accelSensor;
        Sensor magneticFieldSensor;
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroEventListener = new GyroscopeEventListener();
        if (accelSensor != null) {
            sensorManager.registerListener(gyroEventListener, accelSensor, SensorManager.SENSOR_DELAY_NORMAL,
                    SensorManager.SENSOR_DELAY_UI);
        }

        if(magneticFieldSensor != null) {
            sensorManager.registerListener(gyroEventListener, magneticFieldSensor,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

    }

    private void getGyroscopeAndSend() {
        if(gyroEventListener != null) {
            double azimuth = gyroEventListener.getAzimuth();
            connectThread.sendMessage("Azimuth : " + azimuth);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadUIElements() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        Button startServerButton = requireView().findViewById(R.id.startServerButton);
        Button stopServerButton = requireView().findViewById(R.id.stopServerButton);
        Button sendMessageButton = requireView().findViewById(R.id.sendMessageButton);

        TextView statusTextView = requireView().findViewById(R.id.statusTextView);
        TextView receivedMsgTextView = requireView().findViewById(R.id.receivedMsgTextView);
        EditText sendMsgEditText = requireView().findViewById(R.id.messageToSendEditText);

        CheckBox sendLocationCheckBox = requireView().findViewById(R.id.sendLocationCheckBox);



        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == MessageConstants.MESSAGE_READ) {
                    byte[] bytes = (byte[]) msg.obj;

                    receivedMsgTextView.setText(new String(bytes));

                }

                if(msg.what == MessageConstants.MESSAGE_SUCCESSFULLY_CONNECTED) {
                    String deviceDetails = (String) msg.obj;
                    statusTextView.setText("Successfully connected to " + deviceDetails);
                }


            }
        };

        startServerButton.setOnClickListener(view -> {
            if(!BA.isEnabled()) {
                BA.enable();
            }

            if(BA.isEnabled()) {
                statusTextView.setText("Server started");
                ConnectThread connectThread = new ConnectThread(BA, handler);
                BluetoothFragment.this.connectThread = connectThread;
                connectThread.start();
            }
        });

        stopServerButton.setOnClickListener(view -> {
            BluetoothFragment.this.connectThread.cancel();
            BluetoothFragment.this.connectThread = null;
            statusTextView.setText("Disconnected");
        });

        sendMessageButton.setOnClickListener(view -> sendMessage(sendMsgEditText.getText().toString()));

        sendLocationCheckBox.setOnClickListener(new View.OnClickListener() {
            private Thread sendMessageThread;
            @Override
            public void onClick(View view) {
                if(sendLocationCheckBox.isChecked()) {
                    if(connectThread == null) {
                        Toast.makeText(requireContext(), "You are not connected to any device", Toast.LENGTH_SHORT).show();
                        sendLocationCheckBox.setChecked(false);
                    } else {
                        sendMessageThread = new Thread() {
                            @Override
                            public void run() {
                                while(true) {
                                    try {
                                        Thread.sleep(1000);
                                        getLocationAndSend(requireContext());
                                        getGyroscopeAndSend();
                                    } catch (InterruptedException e) {
                                        break;
                                    }
                                }
                            }


                        };

                        sendMessageThread.start();
                    }
                } else {
                    if(sendMessageThread != null) {
                        sendMessageThread.interrupt();
                        sendMessageThread = null;
                        Toast.makeText(requireContext(), "Message sending stopped", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUIElements();
        loadGyroscopeSensors();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireContext().unregisterReceiver(receiver);
    }
}