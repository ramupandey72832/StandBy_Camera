package com.example.myapplication.services;



import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.lifecycle.LifecycleService;

import com.example.myapplication.notification.NotificationHelper;
import com.example.myapplication.power.WakeLockManager;
import com.example.myapplication.camera.CameraXManager;

public class ForegroundRecordService extends LifecycleService {

    public static boolean isRunning = false;

    private NotificationHelper notificationHelper;
    private WakeLockManager wakeLockManager;
    private CameraXManager cameraXManager;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        notificationHelper = new NotificationHelper(this);
        wakeLockManager = new WakeLockManager(this);
        cameraXManager = new CameraXManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        notificationHelper.createNotificationChannel();
        startForeground(1,
                notificationHelper.createNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1,
                    notificationHelper.createNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA);
        } else {
            startForeground(1, notificationHelper.createNotification());
        }

        wakeLockManager.acquireWakeLock();
        cameraXManager.setupCameraX();

        //START_STICKY. This tells Android:
        // "If you absolutely must kill this service due to low memory,
        // please recreate it as soon as memory is available again."
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        cameraXManager.releaseCamera();
        wakeLockManager.releaseWakeLock();
        isRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
