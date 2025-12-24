package com.example.myapplication.power;


import android.content.Context;
import android.os.PowerManager;

public class WakeLockManager {

    private final Context context;
    private PowerManager.WakeLock wakeLock;

    public WakeLockManager(Context context) {
        this.context = context;
    }

    public void acquireWakeLock() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MakeVideoRecorder:RecordingWakeLock");
//            wakeLock.acquire(10 * 60 * 1000L); // 10 minutes
            // Remove the 10 * 60 * 1000L to keep it active until releaseWakeLock() is called
            wakeLock.acquire();
        }
    }

    public void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}
