package com.example.cityu_indoor_navigation.pin;

import android.graphics.PointF;

public class Pin {
    public PointF location; // Location of the pin on the image
    public String label; // Optional: a label for the pin

    public Pin(PointF location, String label) {
        this.location = location;
        this.label = label;
    }
}

