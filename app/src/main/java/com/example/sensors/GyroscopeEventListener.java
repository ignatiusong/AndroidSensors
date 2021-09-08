package com.example.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class GyroscopeEventListener implements SensorEventListener {
    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    private TextView azimuthTextView;
    private TextView pitchTextView;
    private TextView rollTextView;


    public GyroscopeEventListener(SensorManager sensorManager, TextView azimuthTextView,
                                  TextView pitchTextView, TextView rollTextView) {
        this.sensorManager = sensorManager;
        this.azimuthTextView = azimuthTextView;
        this.pitchTextView = pitchTextView;
        this.rollTextView = rollTextView;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
            updateOrientationAngles();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
            updateOrientationAngles();
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "rotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        // "orientationAngles" now has up-to-date information.

        updateText();
    }

    private void updateText() {
        this.azimuthTextView.setText(orientationAngles[0] + "");
        this.pitchTextView.setText(orientationAngles[1] + "");
        this.rollTextView.setText(orientationAngles[2] + "");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
