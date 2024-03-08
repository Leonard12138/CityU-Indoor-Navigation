package com.example.cityu_indoor_navigation.datapick;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cityu_indoor_navigation.R;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class WifiDataPick extends AppCompatActivity implements View.OnClickListener,
        SensorEventListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private int scanCount;
    private boolean isUiThreadStop = false;
    private static final String TAG = "WifiDataPick";
    private float[] tempOri;

    private TextView wifiTextView;
    private EditText xEdit, yEdit, xInterval, yInterval;

    private WifiManager wifiManager;

    private SensorManager mSensorManager;   // Declare sensor management object

    private OkHttpClient myOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_data_pick);
        initView();

        requestLocationPermission();



        // Get the sensor management object
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Initialize OkHttpClient
        myOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)       // Set connection timeout
                .readTimeout(15, TimeUnit.SECONDS)         // Set read timeout
                .writeTimeout(15, TimeUnit.SECONDS)        // Set write timeout
                .build();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!Objects.requireNonNull(wifiManager).isWifiEnabled())
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isUiThreadStop) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wifiTextView.setText(obtainWifiInfo());
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Sleep error:" + e.getMessage());
                    }
                }
            }
        }).start();
    }

    /**
     * Initialize controls
     */
    private void initView() {
        xEdit = findViewById(R.id.x_position);
        yEdit = findViewById(R.id.y_position);
        xInterval = findViewById(R.id.x_interval);
        yInterval = findViewById(R.id.y_interval);
        wifiTextView = findViewById(R.id.wifi_information_exhibit);
        Button sendWifi = findViewById(R.id.wifi_send);    // Send button
        Button wifiBack = findViewById(R.id.wifi_back);    // Back button
        Button addX = findViewById(R.id.add_value_x);      // x step button
        Button addY = findViewById(R.id.add_value_y);      // y step button
        // Register events for buttons
        wifiBack.setOnClickListener(this);
        addX.setOnClickListener(this);
        addY.setOnClickListener(this);
        sendWifi.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.wifi_send) {
            if (TextUtils.isEmpty(xEdit.getText()) || TextUtils.isEmpty(yEdit.getText())) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(WifiDataPick.this);
                dialog.setTitle("System Prompt").setMessage("Both x and y coordinates must be entered!")
                        .setCancelable(false).setPositiveButton("OK", null).show();
            } else {
                sendWifiData();
            }
        } else if (id == R.id.add_value_x) {
            if (TextUtils.isEmpty(xInterval.getText())) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(WifiDataPick.this);
                dialog.setTitle("System Prompt").setMessage("x coordinate step value not entered!")
                        .setCancelable(false).setPositiveButton("OK", null).show();
            } else {
                float tempX = Float.valueOf(xEdit.getText().toString());
                float intervalX = Float.valueOf(xInterval.getText().toString());
                xEdit.setText(String.valueOf(tempX + intervalX));
            }
        } else if (id == R.id.add_value_y) {
            if (TextUtils.isEmpty(yInterval.getText())) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(WifiDataPick.this);
                dialog.setTitle("System Prompt").setMessage("y coordinate step value not entered!")
                        .setCancelable(false).setPositiveButton("OK", null).show();
            } else {
                double tempY = Double.valueOf(yEdit.getText().toString());
                double intervalY = Double.valueOf(yInterval.getText().toString());
                yEdit.setText(String.valueOf(tempY + intervalY));
            }
        } else if (id == R.id.wifi_back) {
            isUiThreadStop = true;
            finish();
        }
    }

    /**
     * Use a POST request to send Wifi data to the server
     */
    private void sendWifiData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wifiManager.startScan();
                    if (ActivityCompat.checkSelfPermission(WifiDataPick.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(WifiDataPick.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(WifiDataPick.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    List<ScanResult> wifiList = wifiManager.getScanResults();
                    boolean success = true; // Flag to track if any request fails
                    for (ScanResult scanResult : wifiList) {
                        String SSID = scanResult.SSID;
                        String BSSID = scanResult.BSSID;
                        int level = scanResult.level;

                        // Construct request body
                        RequestBody sendWifiDataRequestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("type", "wifi")
                                .addFormDataPart("x", xEdit.getText().toString())
                                .addFormDataPart("y", yEdit.getText().toString())
                                .addFormDataPart("ori", String.valueOf(tempOri[0]))
                                .addFormDataPart("ssid", SSID)
                                .addFormDataPart("bssid", BSSID)
                                .addFormDataPart("level", String.valueOf(level))
                                .build();

                        // Construct request
                        Request sendWifiDataRequest = new Request.Builder()
                                .url("http://192.168.0.104:8080/locateDataPick/uploadWifiData")//http://192.168.0.105:8080/locateDataPick/uploadWifiData
                                .url("http://localhost:8080/locateDataPick/uploadWifiData")
                                .post(sendWifiDataRequestBody)
                                .build();

                        // Execute the request and check response
                        try (okhttp3.Response response = myOkHttpClient.newCall(sendWifiDataRequest).execute()) {
                            if (!response.isSuccessful()) {
                                success = false; // Set the flag if any request fails
                                break; // Exit the loop if any request fails
                            }
                        }
                    }
                    // Show dialog based on success flag after all requests are processed
                    final String message = success ? "WifiData uploaded successfully" : "Error uploading WifiData";
                    final String title = success ? "Success" : "Error";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlertDialog(title, message);
                        }
                    });
                } catch (IOException error) {
                    error.printStackTrace();
                    Log.e(TAG, "Failed to send Wifi data: " + error.getMessage(), error);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlertDialog("Error", "Failed to send Wifi data: " + error.getMessage());
                        }
                    });
                }
            }
        }).start();
    }



    // Method to request location permission
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(WifiDataPick.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permissions.
            ActivityCompat.requestPermissions(WifiDataPick.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            Log.d(TAG, "Location permission denied");
        }
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted");
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            } else {
                new AlertDialog.Builder(WifiDataPick.this)
                        .setTitle("Location and Wi-Fi State Permissions Denied")
                        .setMessage("This app requires location permissions to function properly. Please grant the permissions.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "Positive button clicked. Opening location settings.");
                                // Dismiss the dialog (if needed)
                                dialogInterface.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
                Log.d(TAG, "Location permission denied");
            }
        }
    }



    /**
     * Get Wifi information
     *
     * @return Wifi information string
     */
    private String obtainWifiInfo() {
        wifiManager.startScan();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "Location or Wi-Fi permissions not granted"; // Placeholder value or handle differently
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && !isGpsEnabled()) {
            return "Please open GPS";
        }
        List<ScanResult> wifiList = wifiManager.getScanResults();
        scanCount++;
        StringBuilder wifiInformation = new StringBuilder(scanCount + "\nScan result:");

        for (ScanResult scanResult : wifiList)
            wifiInformation.append("\nWifi Network ID: ").append(scanResult.SSID).
                    append("\nMac Address: ").append(scanResult.BSSID).
                    append("\nWifi Signal Strength: ").append(scanResult.level).append("\n");
        //Log.d("WifiDataPick", "wifiManager.getScanResults() is empty?:" + wifiManager.getScanResults().isEmpty() + "\n" + "Obtained WiFi Info:\n" + wifiInformation.toString());
        return wifiInformation.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        switch (type) {
            case Sensor.TYPE_ORIENTATION:
                tempOri = event.values;
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {    // Overridden change

    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(WifiDataPick.this);
        dialog.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }
}
