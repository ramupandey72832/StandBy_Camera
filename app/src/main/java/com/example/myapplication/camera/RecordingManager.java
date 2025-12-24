package com.example.myapplication.camera;

import android.content.ContentValues;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.video.FileOutputOptions;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoRecordEvent;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import java.io.File;

public class RecordingManager {

    private Recording currentRecording;
    private final LifecycleService service;

    public RecordingManager(LifecycleService service) {
        this.service = service;
    }

    public void startImmediateRecording(@NonNull Recorder recorder) {
        try {
//            File file = new File(service.getExternalFilesDir(null),
//                    "record_" + System.currentTimeMillis() + ".mp4");
//
//            FileOutputOptions outputOptions = new FileOutputOptions.Builder(file).build();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Recording_" + System.currentTimeMillis());
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/MyCameraApp");

            MediaStoreOutputOptions outputOptions = new MediaStoreOutputOptions
                    .Builder(service.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    .setContentValues(contentValues)
                    .build();
            PendingRecording pending = recorder.prepareRecording(service, outputOptions);

            if (service.checkSelfPermission(android.Manifest.permission.RECORD_AUDIO)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                pending = pending.withAudioEnabled();
            }

            currentRecording = pending.start(ContextCompat.getMainExecutor(service), event -> {
                if (event instanceof VideoRecordEvent.Start) {
                    Log.i("RecordingManager", "Recording started");
                } else if (event instanceof VideoRecordEvent.Finalize) {
                    Log.i("RecordingManager", "Recording finalized");
                }
            });

        } catch (Exception e) {
            Log.e("RecordingManager", "Failed to start recording", e);
        }
    }

    public void stopRecording() {
        if (currentRecording != null) {
            currentRecording.stop();
            currentRecording = null;
        }
    }
}
