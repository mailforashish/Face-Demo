package com.zeeplive.app.adapter;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeplive.app.R;
import com.zeeplive.app.fragment.OnCamFragment;
import com.zeeplive.app.response.OnCamResponse;

import java.util.List;

public class OnCamAdapter extends RecyclerView.Adapter<OnCamAdapter.ViewHolder> {

    static OnCamFragment context;
    List<OnCamResponse.Result> list;

    public OnCamAdapter(OnCamFragment context, List<OnCamResponse.Result> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_oncam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setVideoViewData(list.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return list.size();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        VideoView videoView;
        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.picture);
            videoView = view.findViewById(R.id.video_view);
            progressBar = view.findViewById(R.id.progress_bar);
        }

        void setVideoViewData(OnCamResponse.Result result) {

            if (result.getVideo_name() != null && result.getVideo_name().length() > 1) {
                videoView.setVideoPath(result.getVideo_name());
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        progressBar.setVisibility(View.GONE);
                        mp.start();

                        float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                        float screenRatio = videoView.getWidth() / (float) videoView.getHeight();
                        float scale = videoRatio / screenRatio;
                        if (scale >= 1f) {
                            videoView.setScaleX(scale);
                        } else {
                            videoView.setScaleY(1f / scale);
                        }
                    }
                });

                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
//                        mp.start();
//                        context.autoSwipeViewpager(getAdapterPosition());
                    }
                });

               /* Glide.with(context)
                        .load(result.getVideo_thumbnail())
                        .apply(new RequestOptions().placeholder(R.drawable.ic_photo_library_gray_100dp).error(R.drawable.ic_photo_library_gray_100dp))
                        .into(imageView);*/
            }
        }
    }
}