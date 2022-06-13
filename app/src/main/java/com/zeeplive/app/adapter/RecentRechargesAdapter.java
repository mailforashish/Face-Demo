package com.zeeplive.app.adapter;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zeeplive.app.R;
import com.zeeplive.app.fragment.EmployeeViewProfileFragment;
import com.zeeplive.app.helper.NetworkCheck;
import com.zeeplive.app.response.RecentRechargeResponse;

import java.util.List;

import com.zeeplive.app.activity.ChatActivity;
import com.zeeplive.app.activity.ChatEmployeeActivity;
import com.zeeplive.app.activity.InboxDetails;
import com.zeeplive.app.activity.ViewProfile;
import com.zeeplive.app.helper.NetworkCheck;
import com.zeeplive.app.response.ChatRoom.RequestChatRoom;
import com.zeeplive.app.response.ChatRoom.ResultChatRoom;
import com.zeeplive.app.response.RecentRechargeResponse;
import com.zeeplive.app.retrofit.ApiClientChat;
import com.zeeplive.app.retrofit.ApiInterface;
import com.zeeplive.app.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentRechargesAdapter extends RecyclerView.Adapter<RecentRechargesAdapter.myViewHolder> {

    Context context;
    List<RecentRechargeResponse.Result> list;
    private NetworkCheck networkCheck;

    public RecentRechargesAdapter(Context context, List<RecentRechargeResponse.Result> list) {

        this.context = context;
        this.list = list;
        networkCheck = new NetworkCheck();
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recent_recharges, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {

        try {
            if (list.get(position).getUser_id().getProfile_images().size() > 0) {
                Glide.with(context).load(list.get(position).getUser_id().getProfile_images().get(0).getImage_name())
                        .apply(new RequestOptions().placeholder(R.drawable.default_profile).error
                                (R.drawable.default_profile).circleCrop()).into(holder.user_image);
            } else {
                Glide.with(context).load(R.drawable.default_profile).apply(new RequestOptions()).into(holder.user_image);
            }

            holder.available_coins.setText(String.valueOf(list.get(position).getPoints()));
            holder.user_name.setText(list.get(position).getUser_id().getName());
            //  holder.description.setText(list.get(position).getTransaction_des());
            holder.is_online.setText("ID : " + list.get(position).getUser_id().getProfile_id());

        } catch (Exception e) {
        }
      /*  if (list.get(position).getStatus() == 1) {
            holder.is_online.setText("Online");
            holder.is_online.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_green, 0, 0, 0);
        } else if (list.get(position).getStatus() == 0) {
            holder.is_online.setText("Offline");
            holder.is_online.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_grey, 0, 0, 0);
        } else if (list.get(position).getStatus() == 7) {
            holder.is_online.setText("Busy");
            holder.is_online.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_orange, 0, 0, 0);
        }*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        ImageView user_image;
        TextView available_coins, user_name, user_age, is_online;

        public myViewHolder(View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            available_coins = itemView.findViewById(R.id.available_coins);
            user_age = itemView.findViewById(R.id.user_age);
            user_image = itemView.findViewById(R.id.user_image);
            user_name = itemView.findViewById(R.id.user_name);
            // description = itemView.findViewById(R.id.description);
            is_online = itemView.findViewById(R.id.is_online);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String imgUrl = "";
                    if (list.get(getAdapterPosition()).getUser_id().getProfile_images() == null || list.get(getAdapterPosition()).getUser_id().getProfile_images().size() == 0) {
                        imgUrl = "N/A";
                    } else {
                        imgUrl = list.get(getAdapterPosition()).getUser_id().getProfile_images().get(0).getImage_name();
                    }

                    AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
                    Fragment fr = new EmployeeViewProfileFragment();
                    FragmentTransaction  ft = activity.getSupportFragmentManager().beginTransaction().addToBackStack(null);
                    Bundle args = new Bundle();
                    args.putString("recName", list.get(getAdapterPosition()).getUser_id().getName());
                    args.putString("recId", list.get(getAdapterPosition()).getUser_id().getId() + "");
                    args.putString("recProfileId", list.get(getAdapterPosition()).getUser_id().getProfile_id() + "");
                    args.putString("recPoints", list.get(getAdapterPosition()).getPoints() + "");
                    args.putString("recImage", list.get(getAdapterPosition()).getUser_id().getProfile_images().get(0).getImage_name());
                    fr.setArguments(args);
                    ft.add(R.id.fragment_view, fr);
                    ft.commit();


                    /*Intent intent = new Intent(context, EmployeeViewProfile.class);
                    intent.putExtra("recName", list.get(getAdapterPosition()).getUser_id().getName());
                    intent.putExtra("recId", list.get(getAdapterPosition()).getUser_id().getId() + "");
                    intent.putExtra("recProfileId", list.get(getAdapterPosition()).getUser_id().getProfile_id() + "");
                    intent.putExtra("recPoints", list.get(getAdapterPosition()).getPoints() + "");
                    intent.putExtra("recImage", list.get(getAdapterPosition()).getUser_id().getProfile_images().get(0).getImage_name());
                    context.startActivity(intent);*/

                  /*  if (networkCheck.isNetworkAvailable(context)) {
                        ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                        RequestChatRoom requestChatRoom = new RequestChatRoom("FSAfsafsdf", Integer.parseInt(new SessionManager(context).getUserId()),
                                new SessionManager(context).getUserName(), "ProfilePhoto",
                                "1", list.get(getAdapterPosition()).getUser_id().getProfile_id(), list.get(getAdapterPosition()).getUser_id().getName()
                                , imgUrl
                                , "2", 0, 0, 0, 20, "", "countrtStstic", String.valueOf(list.get(getAdapterPosition()).getUser_id().getId()));
                        Call<ResultChatRoom> chatRoomCall = apiservice.createChatRoom("application/json", requestChatRoom);

                        chatRoomCall.enqueue(new Callback<ResultChatRoom>() {
                            @Override
                            public void onResponse(Call<ResultChatRoom> call, Response<ResultChatRoom> response) {

                                try {
                                    if (!response.body().getData().getId().equals("")) {
                                     *//*   Intent intent = new Intent(context, ChatEmployeeActivity.class);

                                        intent.putExtra("recID", String.valueOf(list.get(getAdapterPosition()).getUser_id().getProfile_id()));
                                        intent.putExtra("tokenUserId", String.valueOf(list.get(getAdapterPosition()).getUser_id().getId()));
                                        intent.putExtra("recName", list.get(getAdapterPosition()).getUser_id().getName());
                                        intent.putExtra("callrate", String.valueOf(0));
                                        intent.putExtra("converID", response.body().getData().getId());
                                        if (list.get(getAdapterPosition()).getUser_id().getProfile_images() == null || list.get(getAdapterPosition()).getUser_id().getProfile_images().size() == 0) {
                                            intent.putExtra("recProfilePic", "empty");
                                        } else {
                                            intent.putExtra("recProfilePic", list.get(getAdapterPosition()).getUser_id().getProfile_images().get(0).getImage_name());
                                            intent.putExtra("ProPic", list.get(getAdapterPosition()).getUser_id().getProfile_images().get(0).getImage_name());
                                        }*//*

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultChatRoom> call, Throwable t) {
                                //         Log.e("onResponseChatRoom: ", t.getMessage());
                            }
                        });
                    }*/

                    /*
                    Intent intent = new Intent(context, InboxDetails.class);
                    intent.putExtra("receiver_id", String.valueOf(list.get(getAdapterPosition()).getUser_id().getProfile_id()));
                    intent.putExtra("tokenUserId", String.valueOf(list.get(getAdapterPosition()).getUser_id().getId()));
                    intent.putExtra("receiver_name", list.get(getAdapterPosition()).getUser_id().getName());

                    if (list.get(getAdapterPosition()).getUser_id().getProfile_images() == null || list.get(getAdapterPosition()).getUser_id().getProfile_images().size() == 0) {
                        intent.putExtra("receiver_image", "empty");
                    } else {
                        intent.putExtra("receiver_image", list.get(getAdapterPosition()).getUser_id().getProfile_images().get(0).getImage_name());
                    }
                    context.startActivity(intent);*/
                }
            });
        }
    }
}