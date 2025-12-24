package com.example.myapplication.camera;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FallbackStrategy;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CameraXManager {

    private static final String TAG = "CameraXManager";
    private final LifecycleService service;
    private VideoCapture<Recorder> videoCapture;
    private RecordingManager recordingManager;

    public CameraXManager(LifecycleService service) {
        this.service = service;
        this.recordingManager = new RecordingManager(service);
    }

    public void setupCameraX() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(service);

        cameraProviderFuture.addListener(() -> {
            try {
                // 1. Read the saved quality from SharedPreferences
                android.content.SharedPreferences prefs = service.
                        getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
                String savedQuality = prefs.getString("video_quality", "SD");

                Quality targetQuality;
                if (savedQuality.equals("FHD")) targetQuality = Quality.FHD;
                else if (savedQuality.equals("HD")) targetQuality = Quality.HD;
                else targetQuality = Quality.SD;

                // 1. Define the quality you want (FHD = 1080p, HD = 720p, SD = 480p)
                QualitySelector qualitySelector = QualitySelector.from(Quality.SD,
                        FallbackStrategy.higherQualityOrLowerThan(targetQuality));

                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Recorder recorder = new Recorder.Builder()
                        .setQualitySelector(qualitySelector)
                        .build();
                videoCapture = VideoCapture.withOutput(recorder);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(service, cameraSelector, videoCapture);

                Log.i(TAG, "VideoCapture bound to service lifecycle");
                recordingManager.startImmediateRecording(recorder);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Failed to get camera provider", e);
            }
        }, ContextCompat.getMainExecutor(service));
    }

    public void releaseCamera() {
        try {
            ProcessCameraProvider.getInstance(service).get().unbindAll();
            recordingManager.stopRecording();
        } catch (Exception ignored) {}
    }
}
