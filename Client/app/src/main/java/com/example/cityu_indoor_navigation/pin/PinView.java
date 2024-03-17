package com.example.cityu_indoor_navigation.pin;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import java.util.ArrayList;
import java.util.List;

import com.example.cityu_indoor_navigation.R;

public class PinView extends SubsamplingScaleImageView {
    private final Paint paint = new Paint();
    private PointF sPin; // Single pin for custom drawable
    private Bitmap pin;
    private List<PointF> pathPoints = new ArrayList<>(); // List of pins for the path

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    public void setPin(PointF sPin) {
        this.sPin = sPin;
        invalidate();
    }

    public void addPathPoint(PointF point) {
        this.pathPoints.add(point);
        invalidate();
    }

    public void clearPathPoints() {
        this.pathPoints.clear();
        invalidate();
    }

    private void initialise() {
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        pin = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isReady()) return;

        if (sPin != null && pin != null) {
            PointF vPin = sourceToViewCoord(sPin);
            // Calculate the desired size for the pin (e.g., 50x50 pixels)
            int pinWidth = 50;
            int pinHeight = 50;
            // Scale the pin Bitmap to the desired size
            Bitmap scaledPin = Bitmap.createScaledBitmap(pin, pinWidth, pinHeight, true);
            // Draw the scaled pin Bitmap onto the canvas
            canvas.drawBitmap(scaledPin, vPin.x - (pinWidth / 2), vPin.y - pinHeight, paint);
        }


        for (PointF point : pathPoints) {
            PointF vPoint = sourceToViewCoord(point);
            canvas.drawCircle(vPoint.x, vPoint.y, 10, paint);
        }
    }
}

