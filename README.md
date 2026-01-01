# My Background Video Recorder

A robust Android application designed to record video in the background using CameraX, ensuring continuous recording even when the screen is off or the app is minimized.

## 🚀 Key Features
- **Background Recording**: Uses a Foreground Service to maintain recording stability.
- **CameraX Integration**: High-level API for camera control with support for both front and back cameras.
- **Quality Selection**: Allows users to choose between SD, HD, and FHD resolutions.
- **System Monitoring**: Displays real-time RAM and Storage availability.
- **Overlay Support**: Requests system alert window permissions to ensure UI persistence if needed.
- **Battery Optimization**: Prompts users to ignore battery optimizations for uninterrupted long-term recording.
- **Video Management**: Built-in list view to play and manage recorded videos.

## 🛠 Project Structure & Documentation

### 1. Camera Management (`com.example.myapplication.camera`)
- **`CameraXManager`**: Handles the initialization of CameraX, binding use cases (VideoCapture) to the lifecycle of the foreground service, and managing camera switching.
- **`RecordingManager`**: Manages the `PendingRecording` and `Recording` objects. It uses `MediaStore` to save videos directly to the public 'Movies' directory.

### 2. Foreground Service (`com.example.myapplication.services`)
- **`ForegroundRecordService`**: A `LifecycleService` that keeps the recording process alive. It displays a persistent notification and manages the `WakeLock`.
- **`RecordingServiceController`**: A helper class that provides a simplified interface for the UI to start/stop the service and update button states/LED indicators.

### 3. Permissions (`com.example.myapplication.permissions`)
- **`PermissionManager`**: Centralizes logic for requesting Camera, Audio, and Notification permissions. Handles Android 13+ (API 33) and Android 14+ (API 34) specific requirements.
- **`OverlayPermissionHelper`**: Specifically handles the `SYSTEM_ALERT_WINDOW` permission for drawing over other apps.

### 4. Power Management (`com.example.myapplication.power`)
- **`WakeLockManager`**: Manages CPU `WakeLock` to prevent the device from entering deep sleep during recording, which would otherwise stop the process.

### 5. UI Components
- **`MainActivity`**: The primary dashboard showing system stats and record controls.
- **`VideoListActivity` & `VideoAdapter`**: Interface for browsing and viewing saved recordings.
- **`SettingsActivity`**: Allows configuration of camera facing and video quality.

### 6. Notifications (`com.example.myapplication.notification`)
- **`NotificationHelper`**: Manages the creation of the mandatory Foreground Service Notification Channel and the notification itself.

## 📋 Requirements
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14.0)
- **Permissions Required**:
  - `CAMERA`
  - `RECORD_AUDIO`
  - `FOREGROUND_SERVICE_CAMERA` & `FOREGROUND_SERVICE_MICROPHONE` (Android 14+)
  - `POST_NOTIFICATIONS` (Android 13+)
  - `SYSTEM_ALERT_WINDOW`
  - `WAKE_LOCK`

## 📦 How to Build
1. Clone the repository.
2. Open in Android Studio (Hedgehog or newer recommended).
3. Sync Project with Gradle Files.
4. Run on a physical device (Camera features may not fully work on emulators).

---
*Developed as a high-performance background utility.*
