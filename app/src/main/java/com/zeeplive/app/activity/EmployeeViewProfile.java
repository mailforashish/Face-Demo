package com.zeeplive.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zeeplive.app.R;
import com.zeeplive.app.adapter.GiftCountDisplayAdapter;
import com.zeeplive.app.response.AddRemoveFavResponse;
import com.zeeplive.app.response.DisplayGiftCount.GiftCountResult;
import com.zeeplive.app.response.DisplayGiftCount.GiftDetails;
import com.zeeplive.app.response.DisplayGiftCount.Result;
import com.zeeplive.app.response.RecentRechargeResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class EmployeeViewProfile extends AppCompatActivity implements ApiResponseInterface {

    String prodileId, recProfileId;
    String name;
    String image;
    String isFav;
    int position;

    //giftcount
    RecyclerView rv_giftshow;
    GiftCountDisplayAdapter giftCountDisplayAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<GiftDetails> giftDetailsArrayList;
    ArrayList<Result> resultArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparentBlack));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_view_profile_employee);

        getDataFromRecent();
        initControls();
    }

    private void getDataFromRecent() {
        Intent in = getIntent();
        Bundle b = in.getExtras();

        if (b != null) {
            name = b.getString("recName");
            recProfileId = b.getString("recId");

            ((TextView) findViewById(R.id.user_name)).setText(name);
            prodileId = b.getString("recId");
            ((TextView) findViewById(R.id.user_id)).setText("ID :" + prodileId);

            // ((TextView) findViewById(R.id.call_rate_tv)).setText(b.getString("recPoints"));
            image = b.getString("recImage");

            Glide.with(getApplicationContext())
                    .load(b.getString("recImage"))
                    .centerCrop()
                    .error(R.drawable.maleplaceholder)
                    .into(((ImageView) findViewById(R.id.img_user)));

        }
    }

    private void initControls() {

        rv_giftshow = findViewById(R.id.rv_giftshow);
        giftDetailsArrayList = new ArrayList<>();
        resultArrayList = new ArrayList<>();
        giftCountDisplayAdapter = new GiftCountDisplayAdapter(getApplicationContext(), giftDetailsArrayList, resultArrayList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_giftshow.setLayoutManager(linearLayoutManager);
        rv_giftshow.setAdapter(giftCountDisplayAdapter);

        new ApiManager(getApplicationContext(), this).getGiftCountForHost(String.valueOf(prodileId));


        ((Button) findViewById(R.id.add_friend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ApiManager(getApplicationContext(), EmployeeViewProfile.this).doFavourite(Integer.parseInt(prodileId));
            }
        });

        ((Button) findViewById(R.id.chat_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                gotoChatConversation();

                gotoFirebaseChatConversation();

            }
        });
    }

    public void gotoFirebaseChatConversation() {
        Intent intent = new Intent(this, InboxDetails.class);
        intent.putExtra("receiver_id", recProfileId);
        intent.putExtra("receiver_name", name);


        if (image == null) {
            intent.putExtra("receiver_image", "empty");
        } else {
            intent.putExtra("receiver_image", image);
        }
        startActivity(intent);

        new SessionManager(this).isRecentChatListUpdateNeeded(true);
    }


   /* public void gotoChatConversation() {


        ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

        RequestChatRoom requestChatRoom = new RequestChatRoom("FSAfsafsdf", Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()),
                new SessionManager(getApplicationContext()).getUserName(), "ProfilePhoto",
                "1", Integer.parseInt(prodileId), name,
                image, "2",
                0, 25, 0, 20, "",
                "countrtStstic", String.valueOf(prodileId));
        Call<ResultChatRoom> chatRoomCall = apiservice.createChatRoom("application/json", requestChatRoom);
        //     Log.e("ConId", new Gson().toJson(requestChatRoom));
        chatRoomCall.enqueue(new Callback<ResultChatRoom>() {
            @Override
            public void onResponse(Call<ResultChatRoom> call, Response<ResultChatRoom> response) {

                try {
                    if (!response.body().getData().getId().equals("")) {
                        Intent intent;
                        //Intent intent = new Intent(ViewProfile.this, InboxDetails.class);
                        if (new SessionManager(EmployeeViewProfile.this).getGender().equals("female")) {
                            intent = new Intent(EmployeeViewProfile.this, ChatEmployeeActivity.class);
                            intent.putExtra("recID", String.valueOf(prodileId));
                            intent.putExtra("recName", name);
                            intent.putExtra("callrate", String.valueOf(25));
                            intent.putExtra("converID", response.body().getData().getId());
                            intent.putExtra("tokenUserId", String.valueOf(prodileId));

                            intent.putExtra("ProPic", image);
                            intent.putExtra("recProfilePic", image);
                        } else {
                            intent = new Intent(EmployeeViewProfile.this, ChatActivity.class);
                            intent.putExtra("receiver_id", String.valueOf(prodileId));
                            intent.putExtra("receiver_name", name);
                            intent.putExtra("callrate", String.valueOf(25));
                            intent.putExtra("converID", response.body().getData().getId());
                            intent.putExtra("tokenUserId", String.valueOf(prodileId));
                            intent.putExtra("receiver_image", image);
                        }

                        startActivity(intent);
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


    @Override
    public void isError(String errorCode) {

    }

    List<RecentRechargeResponse.Result> resultList;

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        try {

            if (ServiceCode == Constant.GET_GIFT_COUNT) {
                GiftCountResult rsp = (GiftCountResult) response;
                try {
                    resultArrayList.addAll(rsp.getResult());

                    ((TextView) findViewById(R.id.call_rate_tv)).setText(rsp.getPoints());
                    if (resultArrayList.size() == 0) {
                        ((TextView) findViewById(R.id.tv_gifrecmsg)).setVisibility(View.GONE);
                        rv_giftshow.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < rsp.getResult().size(); i++) {
                        giftDetailsArrayList.add(rsp.getResult().get(i).getGiftDetails());
                    }
                    //giftDetailsArrayList.add(rsp.getResult().get(0).getGiftDetails());
                    giftCountDisplayAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }

            if (ServiceCode == Constant.DO_FAVOURITE) {
                AddRemoveFavResponse rsp = (AddRemoveFavResponse) response;
                Toast.makeText(this,
                        rsp.getResult(), Toast.LENGTH_SHORT).show();

                Intent myIntent = new Intent("FBR");
                myIntent.putExtra("action", "reload");
                this.sendBroadcast(myIntent);
            }
        } catch (Exception e) {
        }
    }
}
