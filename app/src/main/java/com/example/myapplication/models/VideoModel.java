package com.example.myapplication.models;

import android.net.Uri;

public class VideoModel {
    private final Uri uri;
    private final String name;
    private final String duration;
    private final String size; // Added size

    public VideoModel(Uri uri, String name, String duration, String size) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
    }

    public Uri getUri() { return uri; }
    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getSize() { return size; } // Added getter
}