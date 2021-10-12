package com.example.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class GyroscopeEventListener implements SensorEventListener {
    public final float[] accelerometerReading = new float[3];
    public final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    public final float[] orientationAngles = new float[3];

    private TextView azimuthTextView = null;
    private TextView pitchTextView = null;
    private TextView rollTextView = null;

    private boolean hasText = false;

    public GyroscopeEventListener() {

    }

    public GyroscopeEventListener(TextView azimuthTextView,
                                  TextView pitchTextView, TextView rollTextView) {
        this.azimuthTextView = azimuthTextView;
        this.pitchTextView = pitchTextView;
        this.rollTextView = rollTextView;
        this.hasText = true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);

            updateOrientationAngles();
            updateText();
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
            updateOrientationAngles();
            updateText();
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

    }

    private void updateText() {
        if(hasText) {
            this.azimuthTextView.setText(Math.toDegrees(orientationAngles[0]) + "");
            this.pitchTextView.setText(Math.toDegrees(orientationAngles[1]) + "");
            this.rollTextView.setText(Math.toDegrees(orientationAngles[2]) + "");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public double getAzimuth() {
        return orientationAngles[0];
    }
}
