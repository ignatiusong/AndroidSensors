package com.example.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GyroscopeFragment extends Fragment {
    private TextView azimuthTextView;
    private TextView pitchTextView ;
    private TextView rollTextView;


    // Things to handle the rotation vector
    private SensorManager sensorManager;
    private Sensor accelSensor;
    private Sensor magneticFieldSensor;

    public GyroscopeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gyroscope, container, false);
    }

    private void loadUIElements() {
        // Get UI elements
        azimuthTextView = requireView().findViewById(R.id.azimuthTextView);
        pitchTextView = requireView().findViewById(R.id.pitchTextView);
        rollTextView = requireView().findViewById(R.id.rollTextView);

    }

    private void loadSensors() {
        // Load sensors
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    private void registerListeners() {
        SensorEventListener gyroEventListener = new GyroscopeEventListener(
                azimuthTextView, pitchTextView, rollTextView);
        if (accelSensor != null) {
            sensorManager.registerListener(gyroEventListener, accelSensor, SensorManager.SENSOR_DELAY_NORMAL,
                    SensorManager.SENSOR_DELAY_UI);
        }

        if(magneticFieldSensor != null) {
            sensorManager.registerListener(gyroEventListener, magneticFieldSensor,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUIElements();
        loadSensors();
        registerListeners();

    }
}