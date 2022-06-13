package com.zeeplive.app.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zeeplive.app.R;
import com.zeeplive.app.response.DisplayGiftCount.GiftCountResult;
import com.zeeplive.app.response.DisplayGiftCount.GiftDetails;
import com.zeeplive.app.response.DisplayGiftCount.Result;
import com.zeeplive.app.response.OnCamResponse;

import java.util.List;

public class GiftCountDisplayAdapter extends RecyclerView.Adapter<GiftCountDisplayAdapter.ViewHolder> {

    Context context;
    List<GiftDetails> list;
    List<Result> resultsArrayList;

    public GiftCountDisplayAdapter(Context context, List<GiftDetails> list, List<Result> resultsArrayList) {
        this.context = context;
        this.list = list;
        this.resultsArrayList = resultsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.giftcountdisplay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            Glide.with(context)
                    .load(list.get(position).getImage())
                    .into(holder.imageView);

            holder.tv_giftcount.setText("x " + resultsArrayList.get(position).getTotal());
        } catch (Exception e) {
        }
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
        TextView tv_giftcount;


        public ViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.img_giftshow);
            tv_giftcount = view.findViewById(R.id.tv_giftcount);
        }


    }
}