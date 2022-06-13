package com.zeeplive.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;
import com.zeeplive.app.R;
import com.zeeplive.app.adapter.HomeUserAdapter;
import com.zeeplive.app.adapter.LanguageAdapter;
import com.zeeplive.app.adapter.OfferImageAdapter;
import com.zeeplive.app.adapter.ProfilePagerAdapter;
import com.zeeplive.app.databinding.ActivityCardBinding;
import com.zeeplive.app.dialog.InsufficientCoins;
import com.zeeplive.app.fragment.HomeFragment;
import com.zeeplive.app.helper.NetworkCheck;
import com.zeeplive.app.response.AgoraTokenResponse;
import com.zeeplive.app.response.BannerResponse;
import com.zeeplive.app.response.Call.ResultCall;
import com.zeeplive.app.response.ChatRoom.RequestChatRoom;
import com.zeeplive.app.response.ChatRoom.ResultChatRoom;
import com.zeeplive.app.response.DisplayGiftCount.GiftCountResult;
import com.zeeplive.app.response.PaymentGatewayDetails.PaymentGatewayResponce;
import com.zeeplive.app.response.ProfileDetailsResponse;
import com.zeeplive.app.response.RemainingGiftCard.RemainingGiftCardResponce;
import com.zeeplive.app.response.UserListResponse;
import com.zeeplive.app.response.UserListResponseNew.ResultDataNewProfile;
import com.zeeplive.app.response.UserListResponseNew.UserListResponseNewData;
import com.zeeplive.app.response.VoiceCall.VoiceCallResponce;
import com.zeeplive.app.response.WalletBalResponse;
import com.zeeplive.app.response.language.LanguageData;
import com.zeeplive.app.response.language.LanguageResponce;
import com.zeeplive.app.response.videoplay.VideoPlayResponce;
import com.zeeplive.app.retrofit.ApiClientChat;
import com.zeeplive.app.retrofit.ApiInterface;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardActivity extends AppCompatActivity implements ApiResponseInterface {
    ActivityCardBinding binding;
    ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card);

        binding.settingBack.setOnClickListener(view -> onBackPressed());
        binding.setClickListener(new EventHandler(this));

        apiManager = new ApiManager(this, this);
        checkFreeGift();

    }

    public class EventHandler {
        Context mContext;

        public EventHandler(Context mContext) {
            this.mContext = mContext;
        }
    }

    private void checkFreeGift() {
        if (new SessionManager(getApplicationContext()).getGender().equals("male")) {
            Log.e("I am here GiftCard", "step1");
            apiManager.getRemainingGiftCardDisplayFunction();
        }
    }

    @Override
    public void isError(String errorCode) {
        if (errorCode.equals("227")) {
            Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.GET_REMAINING_GIFT_CARD_DISPLAY) {
            RemainingGiftCardResponce rsp = (RemainingGiftCardResponce) response;
            try {
                int remGiftCard = rsp.getTotalGift();
                Log.e("RemainingCardDis", String.valueOf(remGiftCard));
                if (remGiftCard > 0) {
                    binding.tvCardCount.setText("x " + String.valueOf(remGiftCard));
                }
            } catch (Exception e) {
                Log.e("freeGiftCardErrorDis", e.getMessage());

            }
        }


    }


}