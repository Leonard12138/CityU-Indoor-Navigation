package com.example.cityu_indoor_navigation.datapick;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

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

    private BroadcastReceiver wifiScanReceiver;

    private EditText nodeIdInput;

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

        // Initialize BroadcastReceiver
        initWifiScanReceiver();

        // Register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);
    }

    private void initWifiScanReceiver() {
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    Log.d(TAG, "WiFi Scan Completed Successfully.");
                    scanSuccess();
                } else {
                    Log.d(TAG, "WiFi Scan Failed.");
                }
            }
        };
    }

    private void scanSuccess() {
        // You can process the scan results here
//        showAlertDialog("Scan Complete", "WiFi scan has completed successfully.");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister BroadcastReceiver to avoid memory leak
        unregisterReceiver(wifiScanReceiver);
    }

    /**
     * Initialize controls
     */
    private void initView() {
        xEdit = findViewById(R.id.x_position);
        yEdit = findViewById(R.id.y_position);
        xInterval = findViewById(R.id.x_interval);
        yInterval = findViewById(R.id.y_interval);
        nodeIdInput = findViewById(R.id.node_id);
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

        Button startNewScan = findViewById(R.id.start_new_scan);
        startNewScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWifiScan();
            }
        });
    }

    private void startWifiScan() {
        // Check for permissions before starting a scan
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider requesting the permission if not granted.
            return;
        }

        // Start the scan
        boolean scanStarted = wifiManager.startScan();
        if(scanStarted) {
            // Optionally, inform the user that a new scan has been started
            Toast.makeText(this, "New scan started", Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case where the scan did not start
            Toast.makeText(this, "Failed to start new scan", Toast.LENGTH_SHORT).show();
        }
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
                int tempX = Integer.valueOf(xEdit.getText().toString());
                int intervalX = Integer.valueOf(xInterval.getText().toString());
                xEdit.setText(String.valueOf(tempX + intervalX));
            }
        } else if (id == R.id.add_value_y) {
            if (TextUtils.isEmpty(yInterval.getText())) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(WifiDataPick.this);
                dialog.setTitle("System Prompt").setMessage("y coordinate step value not entered!")
                        .setCancelable(false).setPositiveButton("OK", null).show();
            } else {
                int tempY = Integer.valueOf(yEdit.getText().toString());
                int intervalY = Integer.valueOf(yInterval.getText().toString());
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
                    StringBuilder filteredWifiData = new StringBuilder(); // To store filtered Wi-Fi data for display
                    for (ScanResult scanResult : wifiList) {
                        String SSID = scanResult.SSID;
                        String BSSID = scanResult.BSSID;
                        int level = scanResult.level;

                        // Check if the signal strength is between -30 dBm and -90 dBm
                        if (level >= -90 && level <= -30) {
                            // Construct request body
                            RequestBody sendWifiDataRequestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("type", "wifi")
                                    .addFormDataPart("x", xEdit.getText().toString())
                                    .addFormDataPart("y", yEdit.getText().toString())
                                    .addFormDataPart("nodeId", nodeIdInput.getText().toString())
                                    .addFormDataPart("ori", String.valueOf(tempOri[0]))
                                    .addFormDataPart("ssid", SSID)
                                    .addFormDataPart("bssid", BSSID)
                                    .addFormDataPart("level", String.valueOf(level))
                                    .build();

                            // Construct request
                            Request sendWifiDataRequest = new Request.Builder()
                                    .url("http://172.28.178.14:8080/locateDataPick/uploadWifiData")//104desktop, 105laptop, 172.28.178.14 CITYU
                                    .post(sendWifiDataRequestBody)
                                    .build();

                            // Execute the request and check response
                            try (okhttp3.Response response = myOkHttpClient.newCall(sendWifiDataRequest).execute()) {
                                if (!response.isSuccessful()) {
                                    success = false; // Set the flag if any request fails
                                    break; // Exit the loop if any request fails
                                }
                            }

                            // Append the filtered Wi-Fi data to the StringBuilder for display
                            filteredWifiData.append("\nWifi Network ID: ").append(SSID)
                                    .append("\nMac Address: ").append(BSSID)
                                    .append("\nWifi Signal Strength: ").append(level).append("\n");
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

        for (ScanResult scanResult : wifiList) {
            int signalStrength = scanResult.level;
            if (signalStrength >= -90 && signalStrength <= -30) {
                wifiInformation.append("\nWifi Network ID: ").append(scanResult.SSID)
                        .append("\nMac Address: ").append(scanResult.BSSID)
                        .append("\nWifi Signal Strength: ").append(signalStrength).append("\n");
            }
        }

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
