package com.example.myapplication;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.permissions.PermissionManager;
import com.example.myapplication.services.RecordingServiceController;
import com.example.myapplication.utils.OverlayPermissionHelper;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Button btnRecord;
    private PermissionManager permissionManager;
    private RecordingServiceController serviceController;
    private OverlayPermissionHelper overlayHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Start Camera Button Logic
        // Initialize helpers
        serviceController = new RecordingServiceController(this, binding.idBtnRecord,binding.ledStatusIndicator);
        overlayHelper = new OverlayPermissionHelper(this);
        permissionManager = new PermissionManager(this, serviceController);


        binding.idBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Starting Camera...", Toast.LENGTH_SHORT).show();
                // TODO: Add your logic to start the BackgroundRecordService here
                // First check for Overlay (Floating window) permission
                if (!overlayHelper.hasOverlayPermission()) {
                    overlayHelper.showOverlayDialog();
                    return;
                }

                // Call the new process that handles Battery and Recording
                startRecordingProcess();
            }
        });

        // 2. Settings Button Logic
        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Opening Settings...", Toast.LENGTH_SHORT).show();
                // TODO: Add your logic to open a Settings Activity here
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // 3. Records View Button Logic
        binding.btnRecordsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoListActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * New method to handle the sequence of checks before recording
     */
    private void startRecordingProcess(){
        // 1. Check standard permissions (Camera, Audio)
        if (permissionManager.arePermissionsGranted()) {

            // 2. Ask to ignore battery optimization (Ensures long background recording)
            // Note: Ensure you added the method to PermissionManager as discussed previously
            permissionManager.requestIgnoreBatteryOptimizations();

            // 3. Start/Stop the service via the controller
            serviceController.handleRecordClick(permissionManager);
        } else {
            // Request Camera/Audio permissions if not granted
            permissionManager.requestPermissions();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        serviceController.updateButtonStatus();
//        updateLedUI(); // Add this line to sync the LED when app opens (shifted to service controller)
        updateSystemInfo();
    }

    private void updateSystemInfo() {
        // 1. Get RAM Info
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        double availableRam = mi.availMem / (1024.0 * 1024.0 * 1024.0); // GB
        double totalRam = mi.totalMem / (1024.0 * 1024.0 * 1024.0); // GB
        binding.txtRamInfo.setText(String.format("RAM: %.1f/%.1f GB", availableRam, totalRam));

        // 2. Get Storage Info
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalBlocks = stat.getBlockCountLong();

        double availableStorage = (availableBlocks * blockSize) / (1024.0 * 1024.0 * 1024.0);
        double totalStorage = (totalBlocks * blockSize) / (1024.0 * 1024.0 * 1024.0);
        binding.txtStorageInfo.setText(String.format("Disk: %.1f/%.1f GB", availableStorage, totalStorage));
    }

}