package com.example.cityu_indoor_navigation;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_navigation);

        // Initialize Google API client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Initialize the map
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Customize map settings or add markers as needed

        //get latlong for corners for specified place
        LatLng one = new LatLng(22.335856294617436, 114.17413321365214);
        LatLng two = new LatLng(22.336369195365805, 114.17216012421308);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //add them to builder
        builder.include(one);
        builder.include(two);

        LatLngBounds bounds = builder.build();

        //get width and height to current display screen
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        // 20% padding
        int padding = (int) (width * 0.20);

        //set latlong bounds
        googleMap.setLatLngBoundsForCameraTarget(bounds);

        //move camera to fill the bound to screen
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

        //set zoom to level to current so that you won't be able to zoom out viz. move outside bounds
        googleMap.setMinZoomPreference(googleMap.getCameraPosition().zoom);

        // Restrict the visible region to City University of Hong Kong 22.336022214198483, 114.17323290451168
//        LatLng cityUniversityLocation = new LatLng(22.336022214198483, 114.17323290451168);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityUniversityLocation, 30));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Handle connected callback, if needed
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // Handle connection suspended callback, if needed
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Handle connection failed callback, if needed
    }
}
