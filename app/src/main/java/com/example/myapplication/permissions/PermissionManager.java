package com.example.myapplication.permissions;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.services.RecordingServiceController;

import java.util.Map;

public class PermissionManager {

    private final AppCompatActivity activity;
    private final RecordingServiceController serviceController;
    private final ActivityResultLauncher<String[]> launcher;

    public PermissionManager(AppCompatActivity activity, RecordingServiceController controller) {
        this.activity = activity;
        this.serviceController = controller;

        launcher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                this::onPermissionsResult
        );
    }

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
            });
        } else {
            launcher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            });
        }
    }

    /**
     * Checks if the app is on the battery optimization whitelist.
     * If not, it opens the system dialog to ask the user to disable optimization.
     */
    public void requestIgnoreBatteryOptimizations() {
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(activity.getPackageName())) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
        }
    }

    private void onPermissionsResult(Map<String, Boolean> result) {
        boolean camera = Boolean.TRUE.equals(result.get(Manifest.permission.CAMERA));
        boolean audio = Boolean.TRUE.equals(result.get(Manifest.permission.RECORD_AUDIO));
        boolean notif = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notif = Boolean.TRUE.equals(result.get(Manifest.permission.POST_NOTIFICATIONS));
        }

        if (camera && audio && notif) {
            serviceController.toggleRecording();
        } else {
            Toast.makeText(activity, "Permissions required", Toast.LENGTH_SHORT).show();
        }
    }

    // This is the method MainActivity is looking for
    public boolean arePermissionsGranted() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ needs specific permissions
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
