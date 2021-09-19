package com.example.sensors;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
    // UI Elements
    private Button updateStartGPSbutton;
    private TextView startLatitudeTextView;
    private TextView startLongitudeTextView;

    private Button updateEndGPSbutton;
    private TextView endLatitudeTextView;
    private TextView endLongitudeTextView;

    private Button distanceButton;
    private TextView distanceTextView;

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

    // Things to actually get location
    private FusedLocationProviderClient fusedLocationProviderClient;

    private void getLocation(TextView latitude, TextView longitude) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }


        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Toast.makeText(MainActivity.this, "Successfully gotten location",
                        Toast.LENGTH_SHORT).show();
                latitude.setText("" + location.getLatitude());
                longitude.setText("" + location.getLongitude());

            }
        });

    }

    private void loadUIElements() {
        // Get UI elements
        updateStartGPSbutton = findViewById(R.id.getStartGPSbutton);
        startLatitudeTextView = findViewById(R.id.startLatitudeTextView);
        startLongitudeTextView = findViewById(R.id.startLongitudeTextView);

        updateEndGPSbutton = findViewById(R.id.getEndGPSbutton);
        endLatitudeTextView = findViewById(R.id.endLatitudeTextView);
        endLongitudeTextView = findViewById(R.id.endLongitudeTextView);

        distanceButton = findViewById(R.id.distanceButton);
        distanceTextView = findViewById(R.id.distanceTextView);

        azimuthTextView = findViewById(R.id.azimuthTextView);
        pitchTextView = findViewById(R.id.pitchTextView);
        rollTextView = findViewById(R.id.rollTextView);

    }

    private void loadSensors() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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

        updateStartGPSbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation(startLatitudeTextView, startLongitudeTextView);
            }
        });

        updateEndGPSbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation(endLatitudeTextView, endLongitudeTextView);
            }
        });

        distanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calculate haversine distance between two points
                double startLatitude = Math.toRadians(
                        Double.parseDouble(startLatitudeTextView.getText().toString()));
                double startLongitude = Math.toRadians(
                        Double.parseDouble(startLongitudeTextView.getText().toString()));
                double endLatitude = Math.toRadians(
                        Double.parseDouble(endLatitudeTextView.getText().toString()));
                double endLongitude = Math.toRadians(
                        Double.parseDouble(endLongitudeTextView.getText().toString()));

                double radius = 6371;

                double first_part = Math.pow(Math.sin((endLatitude - startLatitude)/2), 2);
                double second_part = Math.cos(startLatitude) * Math.cos(endLatitude) *
                        Math.pow(Math.sin((endLongitude - startLongitude) /2), 2);
                double combined = Math.asin(Math.sqrt(first_part + second_part));

                double distance = 2 * radius * combined;

                distanceTextView.setText(distance + "");



            }
        });

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








    }


}