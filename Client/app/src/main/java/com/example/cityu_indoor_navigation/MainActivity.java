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

        // Dynamically request permissions
        //getPermissions();
        // Initialize buttons and text boxes
        // Connect, disconnect, magnetic field collection, acceleration data collection button variables
        Button buttonWifi = findViewById(R.id.wifi_data_pick);

        buttonWifi.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // Button function to be added
        // WiFi signal collection
        if (v.getId() == R.id.wifi_data_pick) {
            Intent intentWifi = new Intent(MainActivity.this, WifiDataPick.class);
            startActivity(intentWifi);
        }
    }

    public void startDataCollection(View view) {
        // Method implementation to be added
    }

//    private void getPermissions() {
//        List<String> permissionList = new ArrayList<>();
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.READ_PHONE_STATE);
//        }
//        // Request all unrequested permissions at once
//        if (!permissionList.isEmpty()) {
//            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
//            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0) {
//                    for (int result : grantResults) {
//                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(this, "You must grant all permissions to use this app",
//                                    Toast.LENGTH_SHORT).show();
//                            finish();
//                            return;
//                        }
//                    }
//                } else {
//                    Toast.makeText(this, "An unknown error occurred", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                break;
//        }
//    }
}
