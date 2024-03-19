package com.example.cityu_indoor_navigation;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


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
import android.widget.LinearLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.cityu_indoor_navigation.pin.PinView;

public class NavigationActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    // Replace ImageView with SubsamplingScaleImageView
    private PinView mapImageView;
    private OkHttpClient myOkHttpClient;
    private static final int SCAN_INTERVAL = 3000; // Scan interval in milliseconds (e.g., 3 seconds)

    private int scrollX = 0; // Variable to store scroll position
    private Handler scanHandler;

    // Room search related UI components
    private EditText searchEditText;
    private Button searchButton;

    private String currentPositionNodeId;
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

//        mapPinImageView = findViewById(R.id.mapPin);
//        mapPinImageView.setImage(ImageSource.resource(R.drawable.map_pin));

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager != null && !wifiManager.isWifiEnabled())
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                wifiManager.setWifiEnabled(true);

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        // Initialize the scan handler and start the initial scan
        scanHandler = new Handler();
        scanHandler.post(scanRunnable);

        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);

        // Set click listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered room name
                String roomName = searchEditText.getText().toString().trim();
                // Get the current position node ID from your logic (Assuming it's stored in a variable named currentPositionNodeId)

                // Post the current position node ID and room name to the server
                postCurrentPositionAndRoomName(roomName);
            }
        });
    }

    private void postCurrentPositionAndRoomName(String roomName) {
        OkHttpClient client = new OkHttpClient();

        // Create JSON object with the required parameters
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("startNodeId", currentPositionNodeId);//!!!!!!!!!!!!!!!!!!!!!change to be currentPositionNodeId later
            requestBody.put("destinationRoomName", roomName);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Build the request body
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());

        // Build the request
        Request request = new Request.Builder()
                .url("http://172.28.231.215:8080/navigation/navigate")
                .post(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        // Directly parse the response body as a JSONArray
                        JSONArray pathArray = new JSONArray(responseBody);
                        Log.d("TAG", pathArray.toString());

                        runOnUiThread(() -> {
                            mapImageView.clearPathPoints(); // Clear existing path points
                            for (int i = 0; i < pathArray.length(); i++) {
                                try {
                                    JSONObject node = pathArray.getJSONObject(i);
                                    int xCoordinate = node.getInt("xCoordinate");
                                    int yCoordinate = node.getInt("yCoordinate");

                                    // Add each node's point to the path on the map
                                    mapImageView.addPathPoint(new PointF(xCoordinate, yCoordinate));
                                } catch (JSONException e) {
                                    Log.e("JSON Parsing", "Error in parsing path array", e);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("JSON Parsing", "Error parsing JSON response", e);
                    }
                } else {
                    Log.e("HTTP Response", "Unsuccessful: " + response.message());
                }
            }





        });
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
            if (ActivityCompat.checkSelfPermission(NavigationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            List<ScanResult> scanResults = wifiManager.getScanResults();
            sendWifiDataToServer(scanResults);
        }
    };

    // Method to update the map pin position
    private void updateMapPinPosition(int xCoordinate, int yCoordinate) {
        // Original image dimensions
        int originalWidth = 9006;
        int originalHeight = 5976;

        // SubsamplingScaleImageView dimensions
        int imageViewWidth = mapImageView.getSWidth();
        int imageViewHeight = mapImageView.getSHeight();

        // Calculate scaling factors
        float scalingFactorWidth = (float) imageViewWidth / originalWidth;
        float scalingFactorHeight = (float) imageViewHeight / originalHeight;

        // Translate coordinates to SubsamplingScaleImageView
        float pinLocationX = xCoordinate * scalingFactorWidth;
        float pinLocationY = yCoordinate * scalingFactorHeight;

        Log.d("X", "X"+pinLocationX);
        Log.d("Y", "Y"+pinLocationY);

        // Set the pin location
        PointF pinLocation = new PointF(pinLocationX, pinLocationY);
        mapImageView.setPin(pinLocation);

        float targetScale = 2.0f; // Example target scale

    }

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
                    .url("http://172.28.231.215:8080/indoorLocate/processWifiData")//172.28.178.14
                    .post(requestBody)
                    .build();

            // Execute the request
            myOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();

                    // Display an alert dialog to notify the user about the failed connection
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                            builder.setTitle("Connection Error")
                                    .setMessage("Failed to connect to the server. Please check your internet connection and try again.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // You can perform any additional actions here if needed
                                            dialog.dismiss(); // Dismiss the dialog
                                        }
                                    })
                                    .show();
                        }
                    });
                }


                                                        // Inside onResponse method
// Inside onResponse method
                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            if (response.isSuccessful()) {
                                                                String responseBody = response.body().string();
                                                                if (responseBody != null && !responseBody.isEmpty()) {
                                                                    try {
                                                                        JSONObject jsonResponse = new JSONObject(responseBody);
                                                                        Log.d("TAG", jsonResponse.toString());  // Debug message

                                                                        // Extract information from the JSON object
                                                                        String nodeId = jsonResponse.getString("nodeId");
                                                                        int xCoordinate = jsonResponse.getInt("xcoordinate");
                                                                        int yCoordinate = jsonResponse.getInt("ycoordinate");

                                                                        // Update currentPositionNodeId with the received nodeId
                                                                        currentPositionNodeId = nodeId;

                                                                        // Use the extracted values as needed
                                                                        Log.d("Location Info", "Node ID: " + nodeId + ", X: " + xCoordinate + ", Y: " + yCoordinate);

                                                                        // Update the position of the map pin marker on the main (UI) thread
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                updateMapPinPosition(xCoordinate, yCoordinate);
                                                                            }
                                                                        });

                                                                        // Now you can use nodeId, xCoordinate, and yCoordinate as needed in your app
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                        // Handle JSON parsing error
                                                                        Log.e("JSON Parsing Error", "Error parsing JSON response", e);
                                                                    }
                                                                } else {
                                                                    Log.e("Response Error", "Empty response body");
                                                                }
                                                            } else {
                                                                // Handle unsuccessful response
                                                                Log.e("HTTP Response", "Unsuccessful: " + response.message());
                                                            }
                                                        }


                                                    }
            );
        }
    }



}
