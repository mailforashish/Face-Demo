package com.zeeplive.app.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zeeplive.app.R;

import com.zeeplive.app.adapter.GiftCountDisplayAdapter;
import com.zeeplive.app.adapter.ProfilePagerAdapter;
import com.zeeplive.app.adapter.RateCountDisplayAdapter;
import com.zeeplive.app.databinding.ActivityViewProfileBinding;
import com.zeeplive.app.dialog.InsufficientCoins;
import com.zeeplive.app.dialog.ReportDialog;
import com.zeeplive.app.fragment.HomeFragment;
import com.zeeplive.app.helper.NetworkCheck;
import com.zeeplive.app.response.AgoraTokenResponse;
import com.zeeplive.app.response.Call.ResultCall;
import com.zeeplive.app.response.ChatRoom.RequestChatRoom;
import com.zeeplive.app.response.ChatRoom.ResultChatRoom;
import com.zeeplive.app.response.DisplayRatingCount.Rating;
import com.zeeplive.app.response.DisplayRatingCount.RatingDataResponce;
import com.zeeplive.app.response.RemainingGiftCard.RemainingGiftCardResponce;
import com.zeeplive.app.response.UserListResponse;
import com.zeeplive.app.response.DisplayGiftCount.GiftCountResult;
import com.zeeplive.app.response.DisplayGiftCount.GiftDetails;
import com.zeeplive.app.response.DisplayGiftCount.Result;
import com.zeeplive.app.response.UserListResponseNew.GetRatingTag;
import com.zeeplive.app.response.UserListResponseNew.ResultDataNewProfile;
import com.zeeplive.app.response.UserListResponseNew.UserListResponseNewData;
import com.zeeplive.app.response.VoiceCall.VoiceCallResponce;
import com.zeeplive.app.response.WalletBalResponse;
import com.zeeplive.app.response.videoplay.VideoPlayResponce;
import com.zeeplive.app.retrofit.ApiClientChat;
import com.zeeplive.app.retrofit.ApiInterface;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfile extends AppCompatActivity implements ApiResponseInterface {

    int isFavourite = 0;
    ApiManager apiManager;
    int userId, callRate;
    int hostId;
    ActivityViewProfileBinding binding;
    ArrayList<ResultDataNewProfile> userData = new ArrayList<>();

    int walletBalance;
    private NetworkCheck networkCheck;
    private String convId = "";
    //giftcount
    RecyclerView rv_giftshow;
    GiftCountDisplayAdapter giftCountDisplayAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<GiftDetails> giftDetailsArrayList;
    ArrayList<Result> resultArrayList;
    //show rating and tag count 6/5/21
    RecyclerView rv_tagshow;
    RateCountDisplayAdapter rateCountDisplayAdapter;
    LinearLayoutManager linearLayoutManagerRating;
    GridLayoutManager gridLayoutManager;

    ArrayList<GetRatingTag> ratingArrayList;

    private boolean success;
    private int remGiftCard = 0;
    private String freeSeconds;
    // int unique_id ;
    String hostIdFemale, hostProfileID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().setStatusBarColor(getResources().getColor(R.color.transparentBlack));
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_profile);
        binding.setClickListener(new EventHandler(this));
        networkCheck = new NetworkCheck();

        init();

        // Getting all permissions before going to make a call
        getPermission();
        // createChatRoom();

    }

    private void createChatRoom() {
    /* if (networkCheck.isNetworkAvailable(getApplicationContext())) {
            ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);
            RequestChatRoom requestChatRoom = new RequestChatRoom("FSAfsafsdf",
                    Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()),
                    new SessionManager(getApplicationContext()).getUserName(),
                    "ProfilePhoto", "1", userData.getProfile_id(),
                    userData.getName(), userData.getProfile_images().get(0).getImage_name(),
                    "2", 0, callRate, 0, 20, "",
                    "countrtStstic", String.valueOf(userId));

            Call<ResultChatRoom> chatRoomCall = apiservice.createChatRoom("application/json", requestChatRoom);

            chatRoomCall.enqueue(new Callback<ResultChatRoom>() {
                @Override
                public void onResponse(Call<ResultChatRoom> call, Response<ResultChatRoom> response) {
                    // Log.e("onResponseRoom: ", new Gson().toJson(response.body()));
                    try {
                        if (!response.body().getData().getId().equals("")) {
                            convId = response.body().getData().getId();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResultChatRoom> call, Throwable t) {
                    // Log.e("onResponseChatRoom: ", t.getMessage());
                }
            });

        }*/


    }


    void init() {
        apiManager = new ApiManager(this, this);

        // collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        binding.collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.transparentBlack));
        binding.collapsingToolbar.setStatusBarScrimColor(getResources().getColor(R.color.colorPrimary));

        Animation zoom = AnimationUtils.loadAnimation(this, R.anim.continuous_zoom_out_zoom_in);
        Animation roted = AnimationUtils.loadAnimation(this, R.anim.callnew);
        binding.imgVideo.startAnimation(roted);


        //userData = (UserListResponse.Data) getIntent().getSerializableExtra("user_data");
        hostIdFemale = String.valueOf(getIntent().getSerializableExtra("id"));
        hostProfileID = String.valueOf(getIntent().getSerializableExtra("profileId"));
        Log.e("AddonView", hostIdFemale);
        Log.e("AddonView", hostProfileID);

        rv_giftshow = findViewById(R.id.rv_giftshow);
        //rating recyclerview for show rating
        rv_tagshow = findViewById(R.id.rv_rateShow);

        giftDetailsArrayList = new ArrayList<>();
        resultArrayList = new ArrayList<>();
        //array list inisilise object for
        ratingArrayList = new ArrayList<>();

        giftCountDisplayAdapter = new GiftCountDisplayAdapter(getApplicationContext(), giftDetailsArrayList, resultArrayList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_giftshow.setLayoutManager(linearLayoutManager);
        rv_giftshow.setAdapter(giftCountDisplayAdapter);

        //rate value set adapter here 6/5/21
        /*int numberOfColumns = 3;
        rv_tagshow.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        rateCountDisplayAdapter = new RateCountDisplayAdapter(this, ratingArrayList);
        rv_tagshow.setAdapter(rateCountDisplayAdapter);*/


        /*StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
        rv_tagshow.setLayoutManager(staggeredGridLayoutManager);
        rateCountDisplayAdapter = new RateCountDisplayAdapter(this,ratingArrayList);
        rv_tagshow.setAdapter(rateCountDisplayAdapter);*/

        FlexboxLayoutManager flexLayout = new FlexboxLayoutManager(ViewProfile.this);
        rv_tagshow.setLayoutManager(flexLayout);
        rateCountDisplayAdapter = new RateCountDisplayAdapter(ViewProfile.this, ratingArrayList);
        rv_tagshow.setAdapter(rateCountDisplayAdapter);

        apiManager.getProfileData(String.valueOf(hostIdFemale), "");

        int updatewallet = new SessionManager(this).getUserWallet();
        if (HomeFragment.updatecoins > 0 && updatewallet < 25) {
            binding.tvGiftOffer.setVisibility(View.VISIBLE);
            binding.tvGiftOffer.startAnimation(zoom);
        } else {
            binding.tvGiftOffer.setVisibility(View.GONE);
        }
        //hide code here 11/5/21
        //  binding.setResponse(userData);
        //  isFavourite = userData.getFavorite_by_you_count();

        /*String[] dob = userData.getDob().split("-");
        int date = Integer.parseInt(dob[0]);
        int month = Integer.parseInt(dob[1]);
        int year = Integer.parseInt(dob[2]);
        binding.tvAge.setText("Age: " + getAge(year, month, date));*/
        //hide code here 19/5/21

        // setOnlineStatus();

       /* userId = userData.getId();
        hostId = userData.getProfile_id();

        apiManager.getVideoForProfile(String.valueOf(userId));

        apiManager.getGiftCountForHost(String.valueOf(userId));
        //call api getRateCountForHost 6/5/21 send host profile_id here
        apiManager.getRateCountForHost(String.valueOf(hostId));*/

        //    Log.e("id",userId+"");

        //  Log.e("userIDEmp", userId + "");
       /* callRate = userData.getCall_rate();
        binding.userName.setText(userData.getName());
        binding.userId.setText("ID : " + userData.getProfile_id());
        binding.callRateTv.setText(callRate + "/min");

        binding.audioCallRateTv.setText(userData.getAudio_call_rate() + "/min");
        binding.cityName.setText(userData.getCity());
        binding.aboutUser.setText(userData.getAbout_user());

        ProfilePagerAdapter adapter = new ProfilePagerAdapter(this, userData.getProfile_images(), true);
        binding.viewpager.setAdapter(adapter);

        new TabLayoutMediator(binding.indicatorDot, binding.viewpager,
                (tab, position) -> {
                    // tab.setText(" " + (position + 1));
                }
        ).attach();


        // Hide video call feature for female user
        if (new SessionManager(this).getGender().equals("female")) {
            binding.videoChat.setVisibility(View.GONE);
        } else {
        }*/

        //addRemoveFav();

    }


    void setOnlineStatus() {
        if (userData.get(0).getIsBusy() == 0) {
            if (userData.get(0).getIsOnline() == 1) {
                binding.isOnline.setText("Online");
                binding.isOnline.setBackgroundResource(R.drawable.viewprofile_online_background);
                // binding.isOnline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_green, 0, 0, 0);
            } else {
                binding.isOnline.setText("Offline");
                binding.isOnline.setBackgroundResource(R.drawable.viewprofile_offline_background);
                // binding.isOnline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_grey, 0, 0, 0);
            }
        } else {
            binding.isOnline.setText("Busy");
            //  binding.isOnline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_orange, 0, 0, 0);
            binding.isOnline.setBackgroundResource(R.drawable.viewprofile_busybackground);
        }
    }

    public void openVideoChat(View view) {
        // Check user is online before make a call
        callType = "video";
        apiManager.getRemainingGiftCardFunction();


      /*  try {
            try {
                if (remGiftCard > 0) {
                    apiManager.searchUser(String.valueOf(userData.getProfile_id()), "1");
                    return;
                }
            } catch (Exception e) {
            }

            if (new SessionManager(getApplicationContext()).getUserWallet() > callRate) {
                apiManager.searchUser(String.valueOf(userData.getProfile_id()), "1");
            } else {
                new InsufficientCoins(ViewProfile.this, 2, callRate);
            }
        } catch (Exception e) {
            apiManager.searchUser(String.valueOf(userData.getProfile_id()), "1");
        }*/
    }

    private String callType = "";

    public void openVoiceChat(View view) {
        // Check user is online before make a call
        callType = "audio";
        try {
            if (new SessionManager(getApplicationContext()).getUserWallet() > callRate) {
                apiManager.searchUser(String.valueOf(userData.get(0).getProfileId()), "1");
            } else {
                new InsufficientCoins(ViewProfile.this, 2, callRate);
            }
        } catch (Exception e) {
            apiManager.searchUser(String.valueOf(userData.get(0).getProfileId()), "1");
        }
    }


    public class EventHandler {
        Context mContext;

        public EventHandler(Context mContext) {
            this.mContext = mContext;
        }

        public void addToFav() {
            addRemoveFav();
            apiManager.doFavourite(userId);
            Log.e("newUserId", userId + "");
        }

        public void onBack() {
            onBackPressed();
        }

        public void gotoChatConversation() {

            //Here pass userId and callRate send data on InboxDetail activity. by Kalpesh Sir..
            Intent intent = new Intent(ViewProfile.this, InboxDetails.class);
            intent.putExtra("receiver_id", String.valueOf(userData.get(0).getProfileId()));
            intent.putExtra("newParem", String.valueOf(userData.get(0).getId()));
            intent.putExtra("receiver_name", userData.get(0).getName());
            intent.putExtra("user_id", String.valueOf(userId));
            //pass  Unique_id for gift Sends 4/5/21
            //intent.putExtra("Unique_id", String.valueOf(unique_id));
            intent.putExtra("call_rate", String.valueOf(callRate));

            /*Intent intent = new Intent(ViewProfile.this, InboxDetails.class);
            intent.putExtra("receiver_id", String.valueOf(userData.getProfile_id()));
            intent.putExtra("receiver_name", userData.getName());*/

            if (userData.get(0).getFemaleImages() == null || userData.get(0).getFemaleImages().size() == 0) {
                intent.putExtra("receiver_image", "empty");
            } else {
                intent.putExtra("receiver_image", userData.get(0).getFemaleImages().get(0).getImageName());
            }
            startActivity(intent);

            new SessionManager(ViewProfile.this).isRecentChatListUpdateNeeded(true);
        }

       /* public void gotoChatConversation() {


            if (networkCheck.isNetworkAvailable(getApplicationContext())) {

                ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                RequestChatRoom requestChatRoom = new RequestChatRoom("FSAfsafsdf", Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()),
                        new SessionManager(getApplicationContext()).getUserName(), "ProfilePhoto",
                        "1", userData.getProfile_id(), userData.getName(),
                        userData.getProfile_images().get(0).getImage_name(), "2",
                        0, callRate, 0, 20, "",
                        "countrtStstic", String.valueOf(userId));
                Call<ResultChatRoom> chatRoomCall = apiservice.createChatRoom("application/json", requestChatRoom);
                //     Log.e("ConId", new Gson().toJson(requestChatRoom));
                chatRoomCall.enqueue(new Callback<ResultChatRoom>() {
                    @Override
                    public void onResponse(Call<ResultChatRoom> call, Response<ResultChatRoom> response) {

                        try {
                            if (!response.body().getData().getId().equals("")) {
                                Intent intent;
                                //Intent intent = new Intent(ViewProfile.this, InboxDetails.class);
                                if (new SessionManager(ViewProfile.this).getGender().equals("female")) {
                                    intent = new Intent(ViewProfile.this, ChatEmployeeActivity.class);
                                    intent.putExtra("recID", String.valueOf(userData.getProfile_id()));
                                    intent.putExtra("recName", userData.getName());
                                    intent.putExtra("callrate", String.valueOf(userData.getCall_rate()));
                                    intent.putExtra("converID", response.body().getData().getId());
                                    intent.putExtra("tokenUserId", String.valueOf(userId));
                                    if (userData.getProfile_images() == null || userData.getProfile_images().size() == 0) {
                                        intent.putExtra("ProPic", "empty");
                                        intent.putExtra("recProfilePic", "empty");
                                    } else {
                                        intent.putExtra("ProPic", userData.getProfile_images().get(0).getImage_name());
                                        intent.putExtra("recProfilePic", userData.getProfile_images().get(0).getImage_name());
                                    }
                                } else {
                                    intent = new Intent(ViewProfile.this, ChatActivity.class);
                                    intent.putExtra("receiver_id", String.valueOf(userData.getProfile_id()));
                                    intent.putExtra("receiver_name", userData.getName());
                                    intent.putExtra("callrate", String.valueOf(userData.getCall_rate()));
                                    intent.putExtra("converID", response.body().getData().getId());
                                    intent.putExtra("tokenUserId", String.valueOf(userId));
                                    if (userData.getProfile_images() == null || userData.getProfile_images().size() == 0) {
                                        intent.putExtra("receiver_image", "empty");
                                    } else {
                                        intent.putExtra("receiver_image", userData.getProfile_images().get(0).getImage_name());
                                    }
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
            }


        }*/

        public void reportUser() {
            new ReportDialog(ViewProfile.this, String.valueOf(userId));
        }
    }

    public void addRemoveFav() {
        Intent myIntent = new Intent("FBR");
        myIntent.putExtra("action", "reload");
        this.sendBroadcast(myIntent);

        if (isFavourite == 0) {
            binding.nonFavourite.setText("Follow");
            binding.nonFavourite.setBackgroundResource(R.drawable.viewprofile_fallow_background);
            isFavourite = 1;
        } else {
            binding.nonFavourite.setText("UnFollow");
            binding.nonFavourite.setBackgroundResource(R.drawable.viewprofile_offline_background);
            isFavourite = 0;
        }
    }

    private void getPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }



    @Override
    public void isError(String errorCode) {
        if (errorCode.equals("227")) {
            new InsufficientCoins(ViewProfile.this, 2, callRate);
        } else {
            Toast.makeText(this, errorCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {

        if (ServiceCode == Constant.GET_REMAINING_GIFT_CARD) {
            RemainingGiftCardResponce rsp = (RemainingGiftCardResponce) response;
            try {
                try {
                    success = rsp.getSuccess();
                    remGiftCard = rsp.getTotalGift();
                    freeSeconds = rsp.getFreeSecond();
                    if (remGiftCard > 0) {
                        apiManager.searchUser(String.valueOf(userData.get(0).getProfileId()), "1");
                        return;
                    }
                } catch (Exception e) {
                }
                if (new SessionManager(getApplicationContext()).getUserWallet() >= callRate) {
                    Log.e("MatchHostList", "search1 " + "hii");
                    apiManager.searchUser(String.valueOf(userData.get(0).getProfileId()), "1");
                } else {
                    Log.e("MatchHostList", "search2 " + "hii");
                    new InsufficientCoins(ViewProfile.this, 2, callRate);
                }
            } catch (Exception e) {
                Log.e("MatchHostList", "search3 " + "hii");
                apiManager.searchUser(String.valueOf(userData.get(0).getProfileId()), "1");
            }

        }
        if (ServiceCode == Constant.GET_GIFT_COUNT) {
            GiftCountResult rsp = (GiftCountResult) response;

            try {
                resultArrayList.addAll(rsp.getResult());
                if (resultArrayList.size() == 0) {
                    binding.tvGifrecmsg.setVisibility(View.GONE);
                    rv_giftshow.setVisibility(View.GONE);
                } else {
                    for (int i = 0; i < rsp.getResult().size(); i++) {
                        giftDetailsArrayList.add(rsp.getResult().get(i).getGiftDetails());
                        //Log.e("receviedGiftfemale", new Gson().toJson(giftDetailsArrayList));
                    }
                    //giftDetailsArrayList.add(rsp.getResult().get(0).getGiftDetails());
                    giftCountDisplayAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
            }
        }


        if (ServiceCode == Constant.GENERATE_AGORA_TOKEN) {
            AgoraTokenResponse rsp = (AgoraTokenResponse) response;

            if (rsp.getResult().getNotification() != null && rsp.getResult().getNotification().getSuccess() == 1) {


                int talkTime = walletBalance / userData.get(0).getCallRate() * 1000 * 60;

                //  int talkTime2 = userData.getCall_rate() * 1000 * 60;
                // Minus 2 sec to prevent balance goes into minus
                int canCallTill = talkTime - 2000;

                Log.e("walletBalanceO", walletBalance + "");
                Log.e("callRatepO)", userData.get(0).getCallRate() + "");
                Log.e("talkTime0", talkTime + "");
                // Log.e("talkTime2", talkTime + "");
                Log.e("canCallTill0", canCallTill + "");

                Log.e("Videochat", "token0 " +  rsp.getResult().getToken());


                Intent intent = new Intent(ViewProfile.this, VideoChatActivity.class);
                intent.putExtra("TOKEN", rsp.getResult().getToken());
                intent.putExtra("ID", String.valueOf(userData.get(0).getProfileId()));
                intent.putExtra("UID", String.valueOf(userId));
                intent.putExtra("CALL_RATE", String.valueOf(userData.get(0).getCallRate()));
                intent.putExtra("UNIQUE_ID", rsp.getResult().getUnique_id());
                intent.putExtra("AUTO_END_TIME", canCallTill);
                intent.putExtra("receiver_name", userData.get(0).getName());
                intent.putExtra("converID", convId);

                if (userData.get(0).getFemaleImages() == null || userData.get(0).getFemaleImages().size() == 0) {
                    intent.putExtra("receiver_image", "empty");
                } else {
                    intent.putExtra("receiver_image", userData.get(0).getFemaleImages().get(0).getImageName());
                }
                startActivity(intent);

            } else {
                Toast.makeText(this, "Server is busy, Please try again", Toast.LENGTH_SHORT).show();
            }


        } else if (ServiceCode == Constant.WALLET_AMOUNT) {
            WalletBalResponse rsp = (WalletBalResponse) response;

            // if wallet balance greater than call rate , Generate token to make a call
            if (rsp.getResult().getTotal_point() >= callRate) {
                walletBalance = rsp.getResult().getTotal_point();
                if (networkCheck.isNetworkAvailable(getApplicationContext())) {
                    ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                apiManager.showDialog();
                            } catch (Exception e) {
                            }
                        }
                    }, 100);
                    RequestChatRoom requestChatRoom = new RequestChatRoom("FSAfsafsdf",
                            Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()),
                            new SessionManager(getApplicationContext()).getUserName(),
                            "ProfilePhoto", "1", userData.get(0).getProfileId(),
                            userData.get(0).getName(), userData.get(0).getFemaleImages().get(0).getImageName(),
                            "2", 0, callRate, 0, 20, "",
                            "countrtStstic", String.valueOf(userId));

                    Call<ResultChatRoom> chatRoomCall = apiservice.createChatRoom("application/json", requestChatRoom);

                    chatRoomCall.enqueue(new Callback<ResultChatRoom>() {
                        @Override
                        public void onResponse(Call<ResultChatRoom> call, Response<ResultChatRoom> response) {
                            // Log.e("onResponseRoom: ", new Gson().toJson(response.body()));
                            try {
                                apiManager.closeDialog();
                            } catch (Exception e) {
                            }
                            try {
                                if (!response.body().getData().getId().equals("")) {
                                    convId = response.body().getData().getId();

                                    apiManager.generateAgoraToken(userId, String.valueOf(System.currentTimeMillis()), convId);


                                   /* Log.e("userid", userId + "");
                                    Log.e("outgoingTime", String.valueOf(System.currentTimeMillis()));
                                    Log.e("convId", convId);*/

                                   /* Intent intent = new Intent(ViewProfile.this, ChatActivity.class);
                                    intent.putExtra("receiver_id", String.valueOf(userData.getProfile_id()));
                                    intent.putExtra("receiver_name", userData.getName());
                                    intent.putExtra("converID", response.body().getData().getId());
                                    if (userData.getProfile_images() == null || userData.getProfile_images().size() == 0) {
                                        intent.putExtra("receiver_image", "empty");
                                    } else {
                                        intent.putExtra("receiver_image", userData.getProfile_images().get(0).getImage_name());
                                    }
                                    startActivity(intent);*/
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultChatRoom> call, Throwable t) {
                            // Log.e("onResponseChatRoom: ", t.getMessage());
                            try {
                                apiManager.generateAgoraToken(userId, String.valueOf(System.currentTimeMillis()), convId);
                                apiManager.closeDialog();
                            } catch (Exception e) {
                            }
                        }
                    });
                }

            } else {
                // Open Insufficient Coin popup
                new InsufficientCoins(ViewProfile.this, 2, callRate);
            }
        }
        if (ServiceCode == Constant.NEW_GENERATE_AGORA_TOKEN) {
            ResultCall rsp = (ResultCall) response;
            Log.e("Datareq", new Gson().toJson(rsp));
            //Log.e("newWalletValue", rsp.getResult().getPoints().getTotalPoint() + "");
            walletBalance = rsp.getResult().getPoints().getTotalPoint();
            //int talkTime = walletBalance / userData.get(0).getCallRate() * 1000 * 60;
            int talkTime = walletBalance * 1000;
            Log.e("viewprofile", "remT1 " + String.valueOf(talkTime));
            Log.e("viewprofile", "remT2 " + String.valueOf(userData.get(0).getCallRate()));

            int canCallTill = talkTime - 2000;
            Log.e("viewprofile", "remT3 " + String.valueOf(canCallTill));
            Log.e("walBalIfA0", String.valueOf(walletBalance));

            //int talkTime2 = userData.getCall_rate() * 1000 * 60;
            // Minus 2 sec to prevent balance goes into minus
            Intent intent = new Intent(ViewProfile.this, VideoChatActivity.class);
            intent.putExtra("TOKEN", rsp.getResult().getData().getToken());
            intent.putExtra("ID", String.valueOf(userData.get(0).getProfileId()));
            intent.putExtra("UID", String.valueOf(userId));
            intent.putExtra("CALL_RATE", String.valueOf(userData.get(0).getCallRate()));
            intent.putExtra("UNIQUE_ID", rsp.getResult().getData().getUniqueId());

            if (remGiftCard > 0 && walletBalance <= 20) {
                int newFreeSec = Integer.parseInt(freeSeconds) * 1000;
                intent.putExtra("AUTO_END_TIME", newFreeSec);
                intent.putExtra("is_free_call", "true");

                Log.e("viewprofile", "remC1 " + String.valueOf(remGiftCard));
                Log.e("viewprofile", "remW2 " + String.valueOf(walletBalance));

            } else {
                //intent.putExtra("AUTO_END_TIME", canCallTill);
                intent.putExtra("AUTO_END_TIME", canCallTill);
                intent.putExtra("is_free_call", "false");

                Log.e("viewprofile", "remC3 " + String.valueOf(remGiftCard));
                Log.e("viewprofile", "remW4 " + String.valueOf(walletBalance));

            }
            intent.putExtra("receiver_name", userData.get(0).getName());
            intent.putExtra("converID", convId);

            if (userData.get(0).getFemaleImages() == null || userData.get(0).getFemaleImages().size() == 0) {
                intent.putExtra("receiver_image", "empty");
            } else {
                intent.putExtra("receiver_image", userData.get(0).getFemaleImages().get(0).getImageName());
            }
            startActivity(intent);

        }

        if (ServiceCode == Constant.SEARCH_USER) {
            UserListResponse rsp = (UserListResponse) response;

            if (rsp != null) {
                try {
                    int onlineStatus = rsp.getResult().getData().get(0).getIs_online();
                    int busyStatus = rsp.getResult().getData().get(0).getIs_busy();

                    if (onlineStatus == 1 && busyStatus == 0) {
                        //Check wallet balance before going to make a video call
                        //apiManager.getWalletAmount();
                        walletBalance = new SessionManager(this).getUserWallet();
                        if (callType.equals("video")) {
                            if (remGiftCard > 0 && walletBalance <= 20) {

                                Log.e("viewprofile", "remC5 " + String.valueOf(remGiftCard));
                                Log.e("viewprofile", "remW6 " + String.valueOf(walletBalance));

                                apiManager.generateCallRequest(userId, String.valueOf(System.currentTimeMillis()), convId, callRate,
                                        Boolean.parseBoolean("true"), String.valueOf(remGiftCard));
                            } else if (remGiftCard == 0 && walletBalance >= 20) {

                                Log.e("viewprofile", "remC7 " + String.valueOf(remGiftCard));
                                Log.e("viewprofile", "remW8 " + String.valueOf(walletBalance));
                                apiManager.generateCallRequest(userId, String.valueOf(System.currentTimeMillis()), convId, callRate,
                                        Boolean.parseBoolean("false"), String.valueOf(remGiftCard));

                            } else if (remGiftCard > 0 && walletBalance >= 20) {

                                Log.e("viewprofile", "remC9 " + String.valueOf(remGiftCard));
                                Log.e("viewprofile", "remW10 " + String.valueOf(walletBalance));
                                apiManager.generateCallRequest(userId, String.valueOf(System.currentTimeMillis()), convId, callRate,
                                        Boolean.parseBoolean("false"), String.valueOf(remGiftCard));

                            } else {
                                new InsufficientCoins(ViewProfile.this, 2, callRate);

                            }


                        } else if (callType.equals("audio")) {
                            apiManager.dailVoiceCallUser(String.valueOf(userData.get(0).getAudioCallRate()), String.valueOf(userId),
                                    String.valueOf(System.currentTimeMillis()));
                        }
                    } else if (onlineStatus == 1) {
                        Toast.makeText(this, userData.get(0).getName() + " is Busy", Toast.LENGTH_SHORT).show();

                    } else if (onlineStatus == 0) {
                        Toast.makeText(this, userData.get(0).getName() + " is Offline", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "User is Offline!", Toast.LENGTH_SHORT).show();
                    new SessionManager(getApplicationContext()).setOnlineState(0);
                    finish();
                }
            }
        }

        if (ServiceCode == Constant.PLAY_VIDEO) {
            try {
                VideoPlayResponce rsp = (VideoPlayResponce) response;
                if (rsp != null) {
                    //    Log.e("getVideoForProfile", new Gson().toJson(response));
                    binding.cvVideo.setVisibility(View.VISIBLE);
                    String videourl = ((VideoPlayResponce) response).getResult().get(0).getVideoName();
                    //Log.e("vvURL", videourl);
                    Uri uri = Uri.parse(videourl);
                    binding.vvVideo.setVideoURI(uri);

                    binding.vvVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {

                            binding.searchLoader.setVisibility(View.GONE);

                            mediaPlayer.setVolume(0f, 0f);
                            binding.vvVideo.start();

                        }
                    });

                    binding.vvVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.stop();
                            binding.cvVideo.setVisibility(View.GONE);
                            binding.vvVideo.setVisibility(View.GONE);
                        }
                    });
                }
            } catch (Exception e) {

            }

        }

        if (ServiceCode == Constant.GENERATE_VOICE_CALL_TOKEN) {
            VoiceCallResponce rsp = (VoiceCallResponce) response;
            walletBalance = rsp.getResult().getPoints().getTotalPoint();
            int talkTime = walletBalance / userData.get(0).getCallRate() * 1000 * 60;

            //  int talkTime2 = userData.getCall_rate() * 1000 * 60;
            // Minus 2 sec to prevent balance goes into minus
            int canCallTill = talkTime - 2000;

            Intent intent = new Intent(ViewProfile.this, VoiceChatViewActivity.class);
            intent.putExtra("TOKEN", rsp.getResult().getData().getToken());
            intent.putExtra("UID", String.valueOf(userId));
            //   intent.putExtra("CALL_RATE", String.valueOf(userData.getCall_rate()));
            intent.putExtra("UNIQUE_ID", rsp.getResult().getData().getUniqueId());
            intent.putExtra("AUTO_END_TIME", canCallTill);
            intent.putExtra("receiver_name", userData.get(0).getName());
            //   intent.putExtra("converID", convId);

            if (userData.get(0).getFemaleImages() == null || userData.get(0).getFemaleImages().size() == 0) {
                intent.putExtra("receiver_image", "empty");
            } else {
                intent.putExtra("receiver_image", userData.get(0).getFemaleImages().get(0).getImageName());
            }
            startActivity(intent);
        }


        //Show female profile data for male 10/5/21
        if (ServiceCode == Constant.GET_PROFILE_DATA) {
            // UserListResponse.Data userData;
            UserListResponseNewData rsp = (UserListResponseNewData) response;
            //userData = (ResultDataNewProfile) rsp.getResult();

            userData.addAll(rsp.getResult());
            Log.e("newdatalist", String.valueOf(userData.size()));
            createChatRoom();
            // binding.setResponse(userData);

            isFavourite = userData.get(0).getFavoriteByYouCount();
            if (isFavourite == 0) {
                binding.nonFavourite.setText("Follow");
                binding.nonFavourite.setBackgroundResource(R.drawable.viewprofile_fallow_background);
                isFavourite = 1;
            } else {
                binding.nonFavourite.setText("UnFollow");
                binding.nonFavourite.setBackgroundResource(R.drawable.viewprofile_offline_background);
                isFavourite = 0;
            }

            String[] dob = userData.get(0).getDob().split("-");
            int date = Integer.parseInt(dob[0]);
            int month = Integer.parseInt(dob[1]);
            int year = Integer.parseInt(dob[2]);
            binding.tvAge.setText("Age: " + getAge(year, month, date));

            userId = userData.get(0).getId();
            hostId = userData.get(0).getProfileId();


            apiManager.getVideoForProfile(String.valueOf(userId));

            apiManager.getGiftCountForHost(String.valueOf(userId));
            //call api getRateCountForHost 6/5/21 send host profile_id here
            apiManager.getRateCountForHost(String.valueOf(userId));

            callRate = userData.get(0).getCallRate();
            binding.userName.setText(userData.get(0).getName());
            binding.userId.setText("ID : " + userData.get(0).getProfileId());
            binding.callRateTv.setText(callRate + "/min");
            // binding.aboutUser.setText(userData.getAbout_user());

            binding.audioCallRateTv.setText(userData.get(0).getAudioCallRate() + "/min");
            binding.cityName.setText(userData.get(0).getCity());
//
            ProfilePagerAdapter adapter = new ProfilePagerAdapter(this, userData.get(0).getFemaleImages(), true);
            binding.viewpager.setAdapter(adapter);

            new TabLayoutMediator(binding.indicatorDot, binding.viewpager,
                    (tab, position) -> {
                        // tab.setText(" " + (position + 1));
                    }
            ).attach();

            // Hide video call feature for female user
            if (new SessionManager(this).getGender().equals("female")) {
                binding.videoChat.setVisibility(View.GONE);
            } else {
            }
            setOnlineStatus();
        }

        //Show Rating for female 6/5/21
        if (ServiceCode == Constant.GET_RATING_COUNT) {
            UserListResponseNewData rsp = (UserListResponseNewData) response;
            // Log.e("inViewPeofileFR", new Gson().toJson(rsp.getResult()));
            ratingArrayList.addAll(rsp.getResult().get(0).getGetRatingTag());
            // Log.e("inViewPeofile", new Gson().toJson(ratingArrayList));
            //String val = String.format("%.0f", 2.11);
            String score = rsp.getResult().get(0).getRatingsAverage();
            if (TextUtils.isEmpty(score)) {
                //  binding.tvRate.setText("Score 0");
                binding.tvRate.setVisibility(View.GONE);
            } else {
                binding.tvRate.setText("Score " + score);
            }
            // binding.tvRate.setText("Score " + rsp.getResult().get(0).getRatingsAverage());
            rateCountDisplayAdapter.notifyDataSetChanged();
        }

    }

    public String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month - 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        int ageInt = age;
        return Integer.toString(ageInt);
    }
}