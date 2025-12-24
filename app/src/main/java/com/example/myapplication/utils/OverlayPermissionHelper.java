package com.example.myapplication.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public class OverlayPermissionHelper {

    private final Context context;

    public OverlayPermissionHelper(Context context) {
        this.context = context;
    }

    public boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(context);
    }

    public void showOverlayDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Draw over other apps")
                .setMessage("This app needs permission to show recording controls over other apps.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
