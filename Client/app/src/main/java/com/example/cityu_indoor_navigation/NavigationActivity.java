package com.example.cityu_indoor_navigation;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;


public class NavigationActivity extends AppCompatActivity {

    private SubsamplingScaleImageView mapImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_navigation);
        SubsamplingScaleImageView mapImageView;
        // Initialize SubsamplingScaleImageView
        mapImageView = findViewById(R.id.floorPlan);

        // Load image into SubsamplingScaleImageView
        mapImageView.setImage(ImageSource.resource(R.drawable.floor5));
    }
}

