package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);

        // Load existing setting
        String quality = prefs.getString("video_quality", "SD");
        if (quality.equals("FHD")) binding.rbFhd.setChecked(true);
        else if (quality.equals("HD")) binding.rbHd.setChecked(true);
        else binding.rbSd.setChecked(true);

        binding.btnSaveSettings.setOnClickListener(v -> {
            String selectedQuality = "SD";
            if (binding.rbFhd.isChecked()) selectedQuality = "FHD";
            else if (binding.rbHd.isChecked()) selectedQuality = "HD";

            prefs.edit().putString("video_quality", selectedQuality).apply();
            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}