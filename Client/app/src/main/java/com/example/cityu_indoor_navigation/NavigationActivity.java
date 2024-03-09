package com.example.cityu_indoor_navigation;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.IOException;
import java.util.List;
import android.Manifest;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NavigationActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private SubsamplingScaleImageView mapImageView;

    private OkHttpClient myOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_navigation);
        SubsamplingScaleImageView mapImageView;
        // Initialize SubsamplingScaleImageView
        mapImageView = findViewById(R.id.floorPlan);

        // Load image into SubsamplingScaleImageView
        mapImageView.setImage(ImageSource.resource(R.drawable.floor5));
        sendWifiDataToServer();
    }

    private void sendWifiDataToServer() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request necessary permissions if not already granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return; // Exit the method as we can't proceed without permissions
        }

        List<ScanResult> scanResults = wifiManager.getScanResults(); // Get the latest WiFi scan results
        StringBuilder wifiDataBuilder = new StringBuilder();
        for (ScanResult result : scanResults) {
            // Format: BSSID,Level\n
            wifiDataBuilder.append(result.BSSID).append(",").append(result.level).append("\n");
        }

        // Now send this data to the server
        postWifiData(wifiDataBuilder.toString());
    }

    private void postWifiData(String wifiData) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("wifi_data", wifiData)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.0.104:8080/navigation/processWifiData") // //http://192.168.0.105:8080/navigation/processWifiData
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // Handle response failure
                    throw new IOException("Unexpected code " + response);
                }
                // Process the successful response (e.g., update UI or use the location data)
                final String responseData = response.body().string();
                Log.d(TAG, "Server Response: " + responseData);
                // Here, you might want to broadcast the received data or directly update the UI
            }
        });
    }



}

