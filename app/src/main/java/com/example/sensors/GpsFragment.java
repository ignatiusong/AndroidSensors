package com.example.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class GpsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // UI Elements
    private Button updateStartGPSbutton;
    private TextView startLatitudeTextView;
    private TextView startLongitudeTextView;

    private Button updateEndGPSbutton;
    private TextView endLatitudeTextView;
    private TextView endLongitudeTextView;

    private Button distanceButton;
    private TextView distanceTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gps, container, false);
    }


    private void loadUIElements() {
        // Get UI elements
        updateStartGPSbutton = getView().findViewById(R.id.getStartGPSbutton);
        startLatitudeTextView = getView().findViewById(R.id.startLatitudeTextView);
        startLongitudeTextView = getView().findViewById(R.id.startLongitudeTextView);

        updateEndGPSbutton = getView().findViewById(R.id.getEndGPSbutton);
        endLatitudeTextView = getView().findViewById(R.id.endLatitudeTextView);
        endLongitudeTextView = getView().findViewById(R.id.endLongitudeTextView);

        distanceButton = getView().findViewById(R.id.distanceButton);
        distanceTextView = getView().findViewById(R.id.distanceTextView);


    }



    // Things to actually get location
    private FusedLocationProviderClient fusedLocationProviderClient;

    private void getLocation(TextView latitude, TextView longitude, Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
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


        fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                latitude.setText("" + location.getLatitude());
                longitude.setText("" + location.getLongitude());

            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUIElements();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        updateStartGPSbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation(startLatitudeTextView, startLongitudeTextView, getContext());
            }
        });

        updateEndGPSbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation(endLatitudeTextView, endLongitudeTextView, getContext());
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

}