package com.example.cityu_indoor_navigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.graphics.PointF;


public class NavigationActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private SubsamplingScaleImageView mapImageView;
    private OkHttpClient myOkHttpClient;

    private static final int SCAN_INTERVAL = 3000; // Scan interval in milliseconds (e.g., 3 seconds)
    private Handler scanHandler;
    private final Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            wifiManager.startScan();
            scanHandler.postDelayed(this, SCAN_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_navigation);
        mapImageView = findViewById(R.id.floorPlan);
        mapImageView.setImage(ImageSource.resource(R.drawable.floor5));

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager != null && !wifiManager.isWifiEnabled())
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                wifiManager.setWifiEnabled(true);

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // Initialize the scan handler and start the initial scan
        scanHandler = new Handler();
        scanHandler.post(scanRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks
        scanHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(wifiReceiver);
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResults = wifiManager.getScanResults();
            sendWifiDataToServer(scanResults);
        }
    };

    private void sendWifiDataToServer(List<ScanResult> scanResults) {
        if (scanResults != null && !scanResults.isEmpty()) {
            myOkHttpClient = new OkHttpClient();

            // Convert WiFi scan results to JSON
            JSONArray jsonArray = new JSONArray();
            for (ScanResult result : scanResults) {
                JSONObject wifiObject = new JSONObject();
                try {
                    wifiObject.put("bssid", result.BSSID);
                    wifiObject.put("ssid", result.SSID);
                    wifiObject.put("level", result.level);
                    jsonArray.put(wifiObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Build the request body
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json"), // Use application/json content type
                    jsonArray.toString()
            );

            // Build the request
            Request request = new Request.Builder()
                    .url("http://192.168.0.105:8080/indoorLocate/processWifiData")
                    .post(requestBody)
                    .build();

            // Execute the request
            myOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("HTTP Request", "Failed", e);
                }

                // Inside onResponse method
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Handle successful response
                        String locationInfo = response.body().string();
                        Log.d("HTTP Response", locationInfo);

                        // Extract information from the returned string
                        String[] parts = locationInfo.split(", ");
                        String nodeId = parts[0].substring(parts[0].indexOf(": ") + 2);
                        float xCoordinate = Float.parseFloat(parts[1].substring(parts[1].indexOf(": ") + 2));
                        float yCoordinate = Float.parseFloat(parts[2].substring(parts[2].indexOf(": ") + 2));

                        // Use the extracted values as needed
                        Log.d("Location Info", "Node ID: " + nodeId + ", X: " + xCoordinate + ", Y: " + yCoordinate);

                        // Update the position of the map pin marker
                        updateMapPinPosition(xCoordinate, yCoordinate);

                        // Now you can use nodeId, xCoordinate, and yCoordinate as needed in your app
                    } else {
                        // Handle unsuccessful response
                        Log.e("HTTP Response", "Unsuccessful: " + response.message());
                    }
                }

            });
        }
    }

    private void updateMapPinPosition(float xCoordinate, float yCoordinate) {
        // Get the ImageView for the map pin
        ImageView mapPin = findViewById(R.id.mapPin);

        // Convert coordinates to pixels on the image
        PointF imageCoords = mapImageView.viewToSourceCoord(xCoordinate, yCoordinate);

        // Set the position of the map pin
        mapPin.setX(imageCoords.x - mapPin.getWidth() / 2);
        mapPin.setY(imageCoords.y - mapPin.getHeight());

        // Show the map pin
        mapPin.setVisibility(View.VISIBLE);
    }


}
