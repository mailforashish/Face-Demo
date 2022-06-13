package com.zeeplive.app.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zeeplive.app.R;
import com.zeeplive.app.adapter.TransactionAdapter;
import com.zeeplive.app.databinding.ActivityIncomeReportBinding;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.zeeplive.app.R;
import com.zeeplive.app.adapter.TransactionAdapter;
import com.zeeplive.app.databinding.ActivityIncomeReportBinding;
import com.zeeplive.app.helper.EndlessRecyclerOnScrollListener;
import com.zeeplive.app.response.NewWallet.WalletHistoryData;
import com.zeeplive.app.response.NewWallet.WalletResponce;
import com.zeeplive.app.response.NewWalletResponce.WallateResponceFemale;
import com.zeeplive.app.response.NewWalletResponce.WalletResponceFemaleData;
import com.zeeplive.app.response.WalletBalResponse;
import com.zeeplive.app.response.WalletRechargeResponse;
import com.zeeplive.app.response.Walletfilter.WalletfilterResponce;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;

import java.util.List;
import java.util.TreeMap;

public class IncomeReport extends AppCompatActivity implements ApiResponseInterface {

    TransactionAdapter adapter;
    ActivityIncomeReportBinding binding;
    ApiManager apiManager;
    int currentBalance = 0;
    int thresholdLimit = 1000;
    private String currBalance = "";
    private int page = 1;
    private int TOTAL_PAGES;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = true;
    private boolean isLastPage = false;
    TreeMap<String, List<WalletHistoryData>> walletHistory = new TreeMap<>();


    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_income_report);


        binding.heading.setText("Income Report");
        linearLayoutManager = new LinearLayoutManager(this);
        binding.transactionList.setLayoutManager(linearLayoutManager);
        binding.setClickListener(new EventHandler(this));

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).getVisibility() == View.VISIBLE) {
                    Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    ((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).setVisibility(View.GONE);
                    ((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).setAnimation(slideUp);

                    binding.availableCoins.setText(currBalance);
                } else {
                    Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

                    ((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).setVisibility(View.VISIBLE);
                    ((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).setAnimation(slideUp);
                }
            }
        });

        binding.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).getVisibility() == View.VISIBLE) {
                    Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    ((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).setVisibility(View.GONE);
                    ((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).setAnimation(slideUp);

                    binding.availableCoins.setText(currBalance);
                }
            }
        });


        binding.imgCloseMAIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).getVisibility() == View.VISIBLE) {
                    Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    ((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).setVisibility(View.GONE);
                    ((RelativeLayout) findViewById(R.id.rl_accountinfoMAIN)).setAnimation(slideUp);

                    binding.availableCoins.setText(currBalance);
                }
            }
        });


        binding.btnThisweek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiManager.getWalletHistroyFilter("this_week");

            }
        });

        binding.btnLastweek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiManager.getWalletHistroyFilter("last_week");

            }
        });

        apiManager = new ApiManager(this, this);
        apiManager.getWalletAmount();
        // apiManager.getTransactionHistoryFemale(page);
        //new api for WalletHistoryFemaleNew 15/4/21
        apiManager.getWalletHistoryFemaleNew();

        binding.tvPre.setClickable(false);
        binding.tvNxt.setClickable(false);

        binding.tvNxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = page + 1;
                apiManager.getTransactionHistoryFemale(page);
            }
        });

        binding.tvPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = page - 1;
                apiManager.getTransactionHistoryFemale(page);
            }
        });

      /*  binding.transactionList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (isLoading && page < TOTAL_PAGES) {
                    if ((visibleItemCount + lastVisibleItemPosition) >= totalItemCount
                            && lastVisibleItemPosition >= 0
                            && totalItemCount > 10) {
                        page = page + 1;
                        //      isLoading = false;
                        Log.e("pPrint", "" + page);
                        //  Toast.makeText(getContext(), page+"", Toast.LENGTH_SHORT).show();

                    } *//*else {
                        Toast.makeText(getContext(), "EOF", Toast.LENGTH_SHORT).show();
                    }*//*
                }
            }
        });
*/


    }

    public class EventHandler {
        Context mContext;

        public EventHandler(Context mContext) {
            this.mContext = mContext;
        }

        public void closeActivity() {
            finish();
        }

        public void redeemCoins() {
            if (currentBalance >= thresholdLimit) {
                apiManager.redeemCoins(String.valueOf(currentBalance));
            } else {
                Toast.makeText(mContext, "Current coins must be 1000 to redeem", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(this, errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.WALLET_AMOUNT) {
            WalletBalResponse rsp = (WalletBalResponse) response;

            currentBalance = rsp.getResult().getRedemablePoints();
            setProgressBar(currentBalance);


        } else if (ServiceCode == Constant.TRANSACTION_HISTORY) {
            WalletResponce rsp = (WalletResponce) response;


            walletHistory = rsp.getResult().getWalletHistory();
            TOTAL_PAGES = ((WalletResponce) response).getResult().getLastPage();


            adapter = new TransactionAdapter(this, walletHistory.descendingMap());
            binding.transactionList.setAdapter(adapter);

            currBalance = ((WalletResponce) response).getResult().getWalletBalance().getTotalPoint().toString();
            binding.availableCoins.setText(currBalance);
            loading = true;

            if (page == 1) {
                binding.tvPre.setClickable(false);
                binding.tvNxt.setClickable(true);
            } else if (page == TOTAL_PAGES) {
                binding.tvPre.setClickable(true);
                binding.tvNxt.setClickable(false);
            } else {
                binding.tvPre.setClickable(true);
                binding.tvNxt.setClickable(true);
            }

        } else if (ServiceCode == Constant.REDEEM_EARNING) {
            WalletRechargeResponse rsp = (WalletRechargeResponse) response;
            Toast.makeText(this, "Redeem request sent successfully", Toast.LENGTH_LONG).show();
            apiManager.getWalletAmount();
        } else if (ServiceCode == Constant.FILTER_DATA) {
            WalletfilterResponce rsp = (WalletfilterResponce) response;
            binding.availableCoins.setText(rsp.getResult().getWalletHistory().get(0).getTotalCredited());
            //new  code for incoming report femaleHistoryWallet new 14/4/21
        } else if (ServiceCode == Constant.TRANSACTION_HISTORY_NEW) {
            WallateResponceFemale rsp = (WallateResponceFemale) response;
            binding.tvInputVideoCoinNaturalWeek.setText(String.valueOf(rsp.getResult().getCurrentWeekReport().getTotalVideoCoins()));
            binding.tvInputAudioCoinNaturalWeek.setText(String.valueOf(rsp.getResult().getCurrentWeekReport().getTotalAudioCoins()));
            binding.tvInputGiftCoinNaturalWeek.setText(String.valueOf(rsp.getResult().getCurrentWeekReport().getTotalGifts()));
            binding.tvInputTotalCoinNaturalWeek.setText(String.valueOf(rsp.getResult().getCurrentWeekReport().getTotalCoins()));

            binding.tvInputVideoCoinLastWeek.setText(String.valueOf(rsp.getResult().getLastWeekReport().getTotalVideoCoins()));
            binding.tvInputAudioCoinLastWeek.setText(String.valueOf(rsp.getResult().getLastWeekReport().getTotalAudioCoins()));
            binding.tvInputGiftCoinLastWeek.setText(String.valueOf(rsp.getResult().getLastWeekReport().getTotalGifts()));
            binding.tvInputTotalCoinLastWeek.setText(String.valueOf(rsp.getResult().getLastWeekReport().getTotalCoins()));

        }


    }

    // Set data
    private void setProgressBar(int currentBalance) {
        binding.availableCoins.setText(String.valueOf(currentBalance));
        binding.currentBal.setText(currentBalance + "/" + thresholdLimit);
        int percentage = (int) ((currentBalance / 100.0f) * 10);
        binding.progressPercent.setText(percentage + " %");
        binding.thresholdProgressbar.setProgress(currentBalance);

        if (currentBalance >= thresholdLimit) {
            binding.redeemCoins.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        } else {
            //  binding.redeemCoins.setEnabled(false);
            binding.redeemCoins.setBackgroundColor(getResources().getColor(R.color.grey500));
        }
    }
}
