package com.example.myapplication;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myapplication.databinding.ActivityVideoListBinding;
import com.example.myapplication.models.VideoModel;
import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {
    private ActivityVideoListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvVideos.setLayoutManager(new LinearLayoutManager(this));
        loadVideos();
    }

    private void loadVideos() {
        List<VideoModel> videoList = new ArrayList<>();

        Uri collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE // Add this
        };

        // Filter to only show videos from your specific folder
        String selection = MediaStore.Video.Media.RELATIVE_PATH + " LIKE ?";
        String[] selectionArgs = new String[]{"%Movies/MyCameraApp%"};

        try (Cursor cursor = getContentResolver().query(collection, projection, selection, selectionArgs, MediaStore.Video.Media.DATE_ADDED + " DESC")) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE); // Add this

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    int duration = cursor.getInt(durationColumn);
                    long sizeInBytes = cursor.getLong(sizeColumn); // Get size

                    // Convert bytes to MB
                    String sizeFormatted = String.format("%.2f MB", sizeInBytes / (1024.0 * 1024.0));
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                    videoList.add(new VideoModel(contentUri, name, (duration / 1000) + " sec",sizeFormatted));
                }
            }
        }

        // Simple Adapter implementation
        VideoAdapter adapter = new VideoAdapter(videoList, uri -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/mp4");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });
        binding.rvVideos.setAdapter(adapter);
    }
}