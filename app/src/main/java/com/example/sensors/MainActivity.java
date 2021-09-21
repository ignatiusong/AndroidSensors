package com.example.sensors;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class    MainActivity extends AppCompatActivity {
    private TextView azimuthTextView;
    private TextView pitchTextView ;
    private TextView rollTextView;


    // Things to handle the rotation vector
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private Sensor accelSensor;
    private Sensor magneticFieldSensor;
    private final float[] mRotationMatrix = new float[16];


    // Get permissions to use location
    private String[] requiredPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION};//, Manifest.permission.ACCESS_COARSE_LOCATION};

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String[]> requestMultiplePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.containsValue(false)) {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                } else {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                }
            });


    private void checkMultiplePermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Have all permissions", Toast.LENGTH_SHORT).show();
            // You can use the API that requires the permission.
            //} else if (shouldShowRequestPermissionRationale(...)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            //showInContextUI(...);
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestMultiplePermissionLauncher.launch(requiredPermissions);
        }

    }

    private void loadUIElements() {
        // Get UI elements
        azimuthTextView = findViewById(R.id.azimuthTextView);
        pitchTextView = findViewById(R.id.pitchTextView);
        rollTextView = findViewById(R.id.rollTextView);

    }

    private void loadSensors() {
        // Load sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    private void registerListeners() {
        SensorEventListener gyroEventListener = new GyroscopeEventListener(sensorManager,
                azimuthTextView, pitchTextView, rollTextView);
        sensorManager.registerListener(gyroEventListener, accelSensor, 1000000);
        sensorManager.registerListener(gyroEventListener, magneticFieldSensor, 1000000);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadUIElements();
        loadSensors();
        registerListeners();

        // Check and request for permissions if necessary
        checkMultiplePermissions();

        if(savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("some_int" , 0);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.gpsFragment, new GpsFragment()).commit();
        }








    }


}