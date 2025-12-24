package com.example.myapplication.services;



import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.permissions.PermissionManager;

public class RecordingServiceController {

    private final Context context;
    private final Button btnRecord;
    private final ImageView ledIndicator;

    public RecordingServiceController(Context context, Button btnRecord, ImageView ledIndicator) {
        this.context = context;
        this.btnRecord = btnRecord;
        this.ledIndicator = ledIndicator;
    }

    public void handleRecordClick(PermissionManager permissionManager) {
        if (isServiceRunning(ForegroundRecordService.class)) {
            stopRecordingService();
        } else {
            permissionManager.requestPermissions();
        }
    }

    public void toggleRecording() {
        if (!isServiceRunning(ForegroundRecordService.class)) {
            startRecordingService();
        } else {
            stopRecordingService();
        }
    }

    public void updateButtonStatus() {
        if (isServiceRunning(ForegroundRecordService.class)) {
            btnRecord.setText("Stop");
            ledIndicator.setImageResource(R.drawable.led_indicator_on);
            startGlow(); // start glowing led
        } else {
            btnRecord.setText("Start Record");
            ledIndicator.setImageResource(R.drawable.led_indicator_off);
            stopGlow(); // stop glowing led

        }
    }

    private void startRecordingService() {
        Intent svc = new Intent(context, ForegroundRecordService.class);
        ContextCompat.startForegroundService(context, svc);
        btnRecord.setText("Stop");
        ledIndicator.setImageResource(R.drawable.led_indicator_on);
        startGlow();

        Toast.makeText(context, "Recording service started", Toast.LENGTH_SHORT).show();
    }

    private void stopRecordingService() {
        Intent svc = new Intent(context, ForegroundRecordService.class);
        context.stopService(svc);
        btnRecord.setText("Start Record");
        ledIndicator.setImageResource(R.drawable.led_indicator_off);
        stopGlow();
        Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show();
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return false;

        // Use the 'serviceClass' parameter instead of hard-coding the name
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startGlow() {
        Animation glow = AnimationUtils.loadAnimation(context, R.anim.glow_animation);
        ledIndicator.startAnimation(glow);
    }

    private void stopGlow() {
        ledIndicator.clearAnimation();
    }
}
