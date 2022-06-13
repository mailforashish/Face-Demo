package com.zeeplive.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zeeplive.app.R;
import com.zeeplive.app.response.UserListResponse;

import java.util.List;

public class ProfilePicsAdapter extends RecyclerView.Adapter<ProfilePicsAdapter.ViewHolder> {
    Context context;
    List<UserListResponse.UserPics> imageList;

    public ProfilePicsAdapter(Context context, List<UserListResponse.UserPics> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (imageList.get(position).getImage_name().equals("add_pic")) {
            Glide.with(context).load(R.drawable.ic_add)
                    .fitCenter()
                    .into(holder.imageView);

        } else {
            if (!imageList.get(position).getImage_name().equals("")) {
                Glide.with(context)
                        .load(imageList.get(position).getImage_name())
                        .placeholder(R.drawable.default_profile)
                        .centerCrop()
                        .into(holder.imageView);
            }
        }
        /* .apply(RequestOptions.circleCropTransform())*/
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pic);

         /*   imageView.setOnClickListener(view -> {


            });*/
        }
    }
}