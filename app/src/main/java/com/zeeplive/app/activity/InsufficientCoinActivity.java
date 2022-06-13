package com.zeeplive.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zeeplive.app.R;
import com.zeeplive.app.adapter.CoinPlansAdapterMyRecharge;
import com.zeeplive.app.response.RechargePlanResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.RecyclerTouchListener;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class InsufficientCoinActivity extends AppCompatActivity implements ApiResponseInterface {
    ApiManager apiManager;
    RecyclerView plan_list;
    CoinPlansAdapterMyRecharge coinPlansAdapter;
    List<RechargePlanResponse.Data> list = new ArrayList<>();
    List<RechargePlanResponse.Data> list_Nri = new ArrayList<>();
    String upiId = "";
    int type, callRate;
    SessionManager sessionManager;
    ImageView insufficient_coin_back;
    TextView tv_refresh;

    int selecetdPlan = 0;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insufficient_coin);

        Intent mIntent = getIntent();
        type = mIntent.getIntExtra("type", 0);
        callRate = mIntent.getIntExtra("current_coin", 0);

        //for refresh activity on payment completed dialog..
       /* tv_refresh = findViewById(R.id.tv_refresh);
        tv_refresh.setText(mIntent.getExtras().getString("value"));*/

        init();
        userRechargeData();
    }


    void init() {
        try {
            TextView term_condition = findViewById(R.id.term_condition);
            insufficient_coin_back = findViewById(R.id.insufficient_coin_back);
            TextView tag_line = findViewById(R.id.tag_line);
            TextView coin_min = findViewById(R.id.coin_min);
            TextView my_coin = findViewById(R.id.tv_mycoin);
            insufficient_coin_back.setOnClickListener(view -> onBackPressed());
            coin_min.setText(callRate + "/min");
            my_coin.setText("" + callRate);


            if (type == 6) {
                coin_min.setVisibility(View.GONE);
                tag_line.setText("Purchase a plan to enable chat service");
                term_condition.setText("* Video plan will not be applicable on chat service");
            }

            plan_list = findViewById(R.id.plan_list);

            // plan_list.setLayoutManager(new LinearLayoutManager(InsufficientCoinActivity.this));
            gridLayoutManager = new GridLayoutManager(InsufficientCoinActivity.this, 2, LinearLayoutManager.HORIZONTAL, false);
            plan_list.setLayoutManager(gridLayoutManager);


            apiManager = new ApiManager(this, this);
            sessionManager = new SessionManager(this);
            Log.e("counteryInDailog", sessionManager.getUserLocation());
            if (sessionManager.getUserLocation().equals("India")) {
                //new code close api apiManager.getRechargeList(); 21/4/21
                coinPlansAdapter = new CoinPlansAdapterMyRecharge(InsufficientCoinActivity.this, list, "dialog");
                // apiManager.getRechargeList();
            } else {
                //    apiManager.getRechargeListStripe();
                coinPlansAdapter = new CoinPlansAdapterMyRecharge(InsufficientCoinActivity.this, list_Nri, "dialog");
            }
            plan_list.setAdapter(coinPlansAdapter);
            upiId = "BHARATPE.0100633819@indus";

            plan_list.addOnItemTouchListener(new RecyclerTouchListener(InsufficientCoinActivity.this, plan_list, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    if (!upiId.isEmpty()) {
                        if (sessionManager.getUserLocation().equals("India")) {

                            Intent intent = new Intent(InsufficientCoinActivity.this, SelectPaymentMethod.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("selected_plan", list.get(position));
                            intent.putExtras(bundle);
                            intent.putExtra("upi_id", upiId);
                            startActivity(intent);
                            overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                        } else {
                            Intent intent = new Intent(InsufficientCoinActivity.this, SelectPaymentMethod.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("selected_plan", list_Nri.get(position));
                            intent.putExtras(bundle);
                            intent.putExtra("upi_id", upiId);
                            startActivity(intent);
                            overridePendingTransition(R.anim.bottom_up, R.anim.nothing);

                        }

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
        Toast.makeText(InsufficientCoinActivity.this, errorCode, Toast.LENGTH_SHORT).show();
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
        RechargePlanResponse.Data list1 = new RechargePlanResponse.Data(44, 149, 1, 150, 1, 1, 7);
        list.add(list1);
        list1 = new RechargePlanResponse.Data(45, 399, 1, 440, 3, 2, 7);
        list.add(list1);
        list1 = new RechargePlanResponse.Data(46, 699, 1, 760, 5, 3, 7);
        list.add(list1);
        list1 = new RechargePlanResponse.Data(47, 999, 1, 1080, 7, 4, 7);
        list.add(list1);
        list1 = new RechargePlanResponse.Data(48, 2999, 1, 3120, 9, 7, 7);
        list.add(list1);
        list1 = new RechargePlanResponse.Data(49, 4999, 1, 5600, 15, 10, 7);
        list.add(list1);

        list1 = new RechargePlanResponse.Data(50, 3, 2, 160, 2, 1, 7);
        list_Nri.add(list1);
        list1 = new RechargePlanResponse.Data(51, 8, 2, 490, 7, 2, 7);
        list_Nri.add(list1);
        list1 = new RechargePlanResponse.Data(52, 17, 2, 1100, 17, 3, 7);
        list_Nri.add(list1);
        list1 = new RechargePlanResponse.Data(53, 53, 2, 3200, 20, 7, 7);
        list_Nri.add(list1);
        list1 = new RechargePlanResponse.Data(54, 90, 2, 5700, 30, 10, 7);
        list_Nri.add(list1);
        coinPlansAdapter.notifyDataSetChanged();
    }






  @Override
  protected void onResume() {
      super.onResume();
  }


}