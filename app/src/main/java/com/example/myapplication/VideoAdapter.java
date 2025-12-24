package com.example.myapplication;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.models.VideoModel;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final List<VideoModel> videoList;
    private final OnVideoClickListener listener;

    // Interface to handle clicks from the Activity
    public interface OnVideoClickListener {
        void onVideoClick(Uri uri);
    }

    public VideoAdapter(List<VideoModel> videoList, OnVideoClickListener listener) {
        this.videoList = videoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtDetails,txtSize;
        android.widget.ImageView imgThumbnail;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_video_name);
            txtDetails = itemView.findViewById(R.id.txt_video_details);
            txtSize = itemView.findViewById(R.id.txt_video_size); // Link it
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail); // Matches your XML ID

        }
    }

    // Inside VideoAdapter.java

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel video = videoList.get(position);
        holder.txtName.setText(video.getName());
        holder.txtDetails.setText(video.getDuration());
        holder.txtSize.setText(video.getSize()); // Set the size text

        // Improved onBindViewHolder with basic threading
        // Use ContentResolver to load the thumbnail
        new Thread(() -> {
            try {
                // Define the size of the thumbnail you want (matching your ImageView 80dp x 60dp)
                android.util.Size size = new android.util.Size(200, 150);

                // Load the thumbnail from the URI
                android.graphics.Bitmap bitmap = holder.itemView.getContext()
                        .getContentResolver().loadThumbnail(video.getUri(), size, null);

                // Switch back to UI thread to set the image
                holder.itemView.post(() -> holder.imgThumbnail.setImageBitmap(bitmap));
            } catch (Exception e) {
                // Fallback if thumbnail fails to load
                holder.itemView.post(() ->
                        holder.imgThumbnail.
                                setImageResource(android.R.drawable.presence_video_online));
            }
        }).start();

        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video.getUri()));
    }

    // Ensure your ViewHolder has the imgThumbnail reference

}