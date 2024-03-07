package com.example.cityu_indoor_navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cityu_indoor_navigation.datapick.WifiDataPick;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons and text boxes
        Button buttonWifi = findViewById(R.id.wifi_data_pick);
        Button buttonNav = findViewById(R.id.navigationButton);

        buttonWifi.setOnClickListener(this);
        buttonNav.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Button function to be added
        // WiFi signal collection
        if (v.getId() == R.id.wifi_data_pick) {
            Intent intentWifi = new Intent(MainActivity.this, WifiDataPick.class);
            startActivity(intentWifi);
        }else if (v.getId() == R.id.navigationButton) {
            Intent intentNav = new Intent(MainActivity.this, NavigationActivity.class);
            startActivity(intentNav);
        }
    }



}
