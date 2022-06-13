package com.zeeplive.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeplive.app.R;
import com.zeeplive.app.activity.SelectPaymentMethod;
import com.zeeplive.app.adapter.CoinPlansAdapterMyaccount;
import com.zeeplive.app.response.RechargePlanResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.RecyclerTouchListener;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class InsufficientCoinsMyaccount extends Dialog implements ApiResponseInterface {

    ApiManager apiManager;
    RecyclerView plan_list;
    CoinPlansAdapterMyaccount coinPlansAdapter;
    List<RechargePlanResponse.Data> list = new ArrayList<>();
    List<RechargePlanResponse.Data> list_Nri = new ArrayList<>();
    String upiId = "";
    int type, callRate;
    SessionManager sessionManager;
    private GridLayoutManager gridLayoutManager;
    public InsufficientCoinsMyaccount(@NonNull Context context, int type, int callRate) {
        super(context);

        this.type = type;
        this.callRate = callRate;
        init();
        userRechargeData();
    }

    void init() {
        try {
            this.setContentView(R.layout.dialog_insufficient_coins_my_account);
            this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            this.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            //   this.setCancelable(false);


            TextView term_condition = findViewById(R.id.term_condition);
            TextView tag_line = findViewById(R.id.tag_line);
            TextView coin_min = findViewById(R.id.coin_min);
            TextView my_coin = findViewById(R.id.tv_mycoin);
            coin_min.setText(callRate + "/min");
            my_coin.setText("My Coin: " + callRate);

            if (type == 6) {
                coin_min.setVisibility(View.GONE);
                tag_line.setText("Purchase a plan to enable chat service");
                term_condition.setText("* Video plan will not be applicable on chat service");
            }

            plan_list = findViewById(R.id.plan_list);
            //plan_list.setLayoutManager(new LinearLayoutManager(getContext()));
            gridLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false);
            plan_list.setLayoutManager(gridLayoutManager);

            apiManager = new ApiManager(getContext(), this);
            sessionManager = new SessionManager(getContext());
            Log.e("counteryInDailog", sessionManager.getUserLocation());
            if (sessionManager.getUserLocation().equals("India")) {
                //new code close api apiManager.getRechargeList(); 21/4/21
                coinPlansAdapter = new CoinPlansAdapterMyaccount(getContext(), list, "dialog");
                // apiManager.getRechargeList();
            } else {
                //    apiManager.getRechargeListStripe();
                coinPlansAdapter = new CoinPlansAdapterMyaccount(getContext(), list_Nri, "dialog");
            }
            plan_list.setAdapter(coinPlansAdapter);
            upiId = "BHARATPE.0100633819@indus";
            show();

            plan_list.addOnItemTouchListener(new RecyclerTouchListener(getContext(), plan_list, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    if (!upiId.isEmpty()) {
                        if (sessionManager.getUserLocation().equals("India")) {
                            Intent intent = new Intent(getContext(), SelectPaymentMethod.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("selected_plan", list.get(position));
                            intent.putExtras(bundle);
                            intent.putExtra("upi_id", upiId);
                            getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(getContext(), SelectPaymentMethod.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("selected_plan", list_Nri.get(position));
                            intent.putExtras(bundle);
                            intent.putExtra("upi_id", upiId);
                            getContext().startActivity(intent);
                        }
                        dismiss();
                    }
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } catch (Exception e) {
        }
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(getContext(), errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.RECHARGE_LIST) {
            try {
                RechargePlanResponse rsp = (RechargePlanResponse) response;

                upiId = rsp.getResult().getUpi_id();
                List<RechargePlanResponse.Data> planList = rsp.getResult().getData();

                if (planList.size() > 0) {
                    for (int i = 0; i < planList.size(); i++) {
                        if (planList.get(i).getStatus() == 1) {
                            if (planList.get(i).getType() == type || planList.get(i).getType() == 7) {
                                list.add(planList.get(i));
                            }
                        }

                    }
                }


                //hide code here for set recharge value static21/4/21
               /* coinPlansAdapter = new CoinPlansAdapterMyaccount(getContext(), list, "dialog");
                plan_list.setAdapter(coinPlansAdapter);*/
            } catch (Exception e) {
            }

        }
    }

    private void userRechargeData() {
        RechargePlanResponse.Data list1 = new RechargePlanResponse.Data(44, 149, 1, 150, 1,1, 7);
        list.add(list1);
        list1 = new RechargePlanResponse.Data(45, 399, 1, 440, 3,2, 7);
        list.add(list1);
        list1 = new RechargePlanResponse.Data(46, 699, 1, 760, 5,3, 7);
        list.add(list1);
        list1 = new RechargePlanResponse.Data(47, 999, 1, 1080, 7, 4,7);
        list.add(list1);
        list1 = new RechargePlanResponse.Data(48, 2999, 1, 3120, 9,7, 7);
        list.add(list1);
        list1 = new RechargePlanResponse.Data(49, 4999, 1, 5600, 15, 10,7);
        list.add(list1);

        list1 = new RechargePlanResponse.Data(50, 3, 2, 160, 2,1, 7);
        list_Nri.add(list1);
        list1 = new RechargePlanResponse.Data(51, 8, 2, 490, 7,2, 7);
        list_Nri.add(list1);
        list1 = new RechargePlanResponse.Data(52, 17, 2, 1100, 17,3, 7);
        list_Nri.add(list1);
        list1 = new RechargePlanResponse.Data(53, 53, 2, 3200, 20, 7,7);
        list_Nri.add(list1);
        list1 = new RechargePlanResponse.Data(54, 90, 2, 5700, 30, 10,7);
        list_Nri.add(list1);
        coinPlansAdapter.notifyDataSetChanged();
    }


}