package com.zeeplive.app.dialog;


import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.tabs.TabLayout;
import com.zeeplive.app.R;
import com.zeeplive.app.activity.InboxDetails;
import com.zeeplive.app.adapter.GiftAdapterNew;
import com.zeeplive.app.adapter.GiftMenuPagerAdapter;
import com.zeeplive.app.fragment.GiftFragment;
import com.zeeplive.app.response.WalletBalResponse;
import com.zeeplive.app.response.gift.Gift;
import com.zeeplive.app.response.gift.ResultGift;
import com.zeeplive.app.response.gift.SendGiftRequest;
import com.zeeplive.app.response.gift.SendGiftResult;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.util.List;

import static com.appsflyer.AFDeepLinkManager.instance;
import static com.facebook.FacebookSdk.getApplicationContext;

public class GiftDailogNew extends DialogFragment implements ApiResponseInterface, View.OnClickListener {
    private View view;
    private TabLayout tabLayoutGift;
    private GiftMenuPagerAdapter giftMenuPagerAdapter;
    InboxDetails context;
    public static String btValue;
    String receiverUserId;
    LottieAnimationView car_anim;

    private TextView tv_1, tv_11, tv_33, tv_77, tv_send, availableCoins;
    private RelativeLayout purchase_coins;
    private ApiManager apiManager;
    List<Gift> giftList;
    int currentCoin = 0;
    public int position;
    MediaPlayer mp;

    public GiftDailogNew(InboxDetails context, String receiverUserId) {
        this.context = context;
        this.receiverUserId = receiverUserId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gift_dialog_new, container);
        getDialog().setCancelable(true);

        apiManager = new ApiManager(getContext(), this);
        //mp = MediaPlayer.create(this,R.raw.car);
        mp = MediaPlayer.create(context, R.raw.car);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        if (!new SessionManager(getContext()).getGender().equals("female")) {
            apiManager.getWalletAmount2();
        }
        /*if(getActivity() instanceof InboxDetails) {
            receiverUserId = ((InboxDetails) getActivity()).receiverUserId;
        }*/
        apiManager.getGiftList();

        tabLayoutGift = view.findViewById(R.id.tabLayoutGift);
        ViewPager tabViewpagerGift = view.findViewById(R.id.tabViewpagerGift);
        giftMenuPagerAdapter = new GiftMenuPagerAdapter(getChildFragmentManager(), context);
        tabViewpagerGift.setAdapter(giftMenuPagerAdapter);
        tabLayoutGift.setupWithViewPager(tabViewpagerGift);
        for (int i = 0; i < tabLayoutGift.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayoutGift.getTabAt(i);
            tab.setCustomView(giftMenuPagerAdapter.getTabView(i));

        }
        giftMenuPagerAdapter.setOnSelectView(tabLayoutGift, 0);
        purchase_coins = view.findViewById(R.id.purchase_coins);
        availableCoins = view.findViewById(R.id.availableCoins);
        car_anim = view.findViewById(R.id.car_anim);
        car_anim.setVisibility(View.GONE);
        tv_1 = view.findViewById(R.id.tv_1);
        tv_11 = view.findViewById(R.id.tv_11);
        tv_33 = view.findViewById(R.id.tv_33);
        tv_77 = view.findViewById(R.id.tv_77);
        tv_send = view.findViewById(R.id.tv_send);

        tv_1.setOnClickListener(this);
        tv_11.setOnClickListener(this);
        tv_33.setOnClickListener(this);
        tv_77.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        purchase_coins.setOnClickListener(this);

        car_anim.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e("Animation:", "start");
                Animation in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_enter);
                car_anim.startAnimation(in);
                car_anim.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e("Animation:", "end");
                Animation out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_leave);
                car_anim.startAnimation(out);
                car_anim.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e("Animation:", "cancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e("Animation:", "repeat");
            }
        });

        tv_11.setBackgroundColor(getResources().getColor(R.color.blueNew));
        tv_1.setBackgroundColor(getResources().getColor(R.color.transparent));
        btValue = tv_11.getText().toString();
        initListners();
    }


    protected void initListners() {
        tabLayoutGift.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                giftMenuPagerAdapter.setOnSelectView(tabLayoutGift, position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                giftMenuPagerAdapter.setUnSelectView(tabLayoutGift, position);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    public void isError(String errorCode) {
        Toast.makeText(getContext(), errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.WALLET_AMOUNT2) {
            WalletBalResponse rsp = (WalletBalResponse) response;
            currentCoin = rsp.getResult().getTotal_point();
            availableCoins.setText(String.valueOf(rsp.getResult().getTotal_point()));

            Log.e("cuurentCoinA", String.valueOf(currentCoin));
            Log.e("receiveridA", receiverUserId);
        }
        if (ServiceCode == Constant.SEND_GIFT) {
            SendGiftResult giftResponse = (SendGiftResult) response;
        }

        if (ServiceCode == Constant.SEND_GIFT) {
            SendGiftResult rsp = (SendGiftResult) response;
            currentCoin = rsp.getResult();
        }

        if (ServiceCode == Constant.GET_GIFT_LIST) {
            ResultGift giftResponse = (ResultGift) response;

            if (giftResponse.getResult() != null) {
                giftList = giftResponse.getResult();
                Log.e("giftListHereA", String.valueOf(giftList.size()));
                giftList.addAll(giftResponse.getResult());
            } else {
                Toast.makeText(getContext(), "No Gift available", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onClick(View view) {
        Log.e("reach", "working adapter:==" + getId());
        switch (view.getId()) {

            case R.id.tv_1:
                tv_1.setBackgroundResource(R.drawable.gift_1_bg);
                tv_11.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_33.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_77.setBackgroundColor(getResources().getColor(R.color.transparent));
                btValue = tv_1.getText().toString();
                notifyDataChanged();
                break;
            case R.id.tv_11:
                tv_11.setBackgroundColor(getResources().getColor(R.color.blueNew));
                tv_1.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_33.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_77.setBackgroundColor(getResources().getColor(R.color.transparent));
                btValue = tv_11.getText().toString();
                notifyDataChanged();
                break;
            case R.id.tv_33:
                tv_33.setBackgroundColor(getResources().getColor(R.color.blueNew));
                tv_1.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_11.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_77.setBackgroundColor(getResources().getColor(R.color.transparent));
                btValue = tv_33.getText().toString();
                notifyDataChanged();
                break;
            case R.id.tv_77:
                tv_77.setBackgroundColor(getResources().getColor(R.color.blueNew));
                tv_1.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_11.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_33.setBackgroundColor(getResources().getColor(R.color.transparent));
                btValue = tv_77.getText().toString();
                notifyDataChanged();
                break;
            case R.id.tv_send:
                tv_send.setBackgroundResource(R.drawable.gift_send_button);
                giftRecyclerviewClickListenerGiftPager(GiftFragment.positions);
                car_anim.setVisibility(View.VISIBLE);
                car_anim.playAnimation();
                mp.start();
                break;
            case R.id.purchase_coins:
                new InsufficientCoins(getActivity(), 2, Integer.parseInt(availableCoins.getText().toString()));
                break;
            default:
                // code block
                break;


        }

    }

    public void notifyDataChanged() {
        Log.e("reach", "working adapter:==");
        //GiftFragment.giftAdapter.notifyDataSetChanged();
        //call from here
        Intent myIntent = new Intent("FBR-USER-INPUT");
        myIntent.putExtra("value", btValue);
        getContext().sendBroadcast(myIntent);


    }

    private void giftRecyclerviewClickListenerGiftPager(int position) {
        Log.e("posInGPf", String.valueOf(position));
        // If Gift item clicked by female user gift request will be send
        if (new SessionManager(getContext()).getGender().equals("female")) {
            Toast.makeText(getContext(), "You can send gift request only on during call.", Toast.LENGTH_SHORT).show();
        } else {
            // for testing purpose use here to get data right..
            int gift = giftList.get(position).getAmount() * Integer.parseInt(btValue);
            Log.e("totalAmount", String.valueOf(gift));

            // If Gift item clicked by male user gift item will be send
            if (currentCoin > giftList.get(position).getAmount()) {
                Log.e("cuurentCoin", String.valueOf(currentCoin));
                Log.e("receiverid", receiverUserId);

                apiManager.sendUserGift(new SendGiftRequest(Integer.parseInt(receiverUserId), "",
                        giftList.get(position).getId(), giftList.get(position).getAmount(), String.valueOf(System.currentTimeMillis()),
                        String.valueOf(System.currentTimeMillis())));

                ((InboxDetails) getActivity()).sendMessage("gift", String.valueOf(giftList.get(position).getId()), String.valueOf(giftList.get(position).getAmount()));
            } else {
                Toast.makeText(getContext(), "Out of coin", Toast.LENGTH_SHORT).show();
            }
        }
    }


}