package com.zeeplive.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.zeeplive.app.R;
import com.zeeplive.app.response.NewWalletResponce.WallateResponceFemale;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class FemaleSideDialogActivity extends AppCompatActivity implements ApiResponseInterface {
    Context context;
    String image;
    String name;
    String call_duration;
    String freeCalls;
    TextView tv_video_earning;
    TextView tv_gift_earning;
    String Video_earning;
    String gift_earning;
    ImageView img_back;
    TextView tv_free_call;
    ApiManager apiManager;
    private int durationNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_female_side_dialog);

        img_back = findViewById(R.id.img_back);
        tv_free_call = findViewById(R.id.tv_free_call);
        tv_video_earning = findViewById(R.id.tv_video_earning);
        tv_gift_earning = findViewById(R.id.tv_gift_earning);
        apiManager = new ApiManager(this, this);


        init();
    }

    void init() {
        apiManager.getWalletHistoryFemaleNew();
        Log.e("guest_here", "in guest dialog");
        try {
            Intent in = getIntent();
            Bundle b = in.getExtras();


            if (b != null) {
                image = b.getString("user_image");
                Log.e("user_image", image);
                name = b.getString("user_name");
                call_duration = b.getString("end_time");
                // freeCalls = b.getString("free_Gift");
                freeCalls = getIntent().getStringExtra("is_free_call");
                durationNew = getIntent().getIntExtra("Unqualifiedtime", 0);
                Log.e("callduration", "messageF1 "+ durationNew);
                Log.e("freeCall", "isFreeCallF1=" + freeCalls);

            }

            CircleImageView img_user = findViewById(R.id.img_user);
            TextView tv_guset_name = findViewById(R.id.tv_guset_name);
            TextView tv_call_duration = findViewById(R.id.tv_call_duration);
                /*if(null!=image)
                {
                    //imageview has image
                    Glide.with(getApplicationContext()).load(image).circleCrop().into(((CircleImageView) findViewById(R.id.img_user)));
                }else{
                    //imageview has no image
                    img_user.setImageResource(R.drawable.default_profile);
                }*/
            Glide.with(getApplicationContext()).load(image).circleCrop().into(((CircleImageView) findViewById(R.id.img_user)));
            tv_guset_name.setText(name);
            tv_call_duration.setText("Call Duration: " + String.valueOf(call_duration));

        } catch (Exception e) {
        }

        String freeGift = "true";
        String Unqualified = "false";

        //String time = call_duration; //mm:ss
        //Log.e("callduration in seconds", "message"+totalSeconds);
        Log.e("callduration", "messageF2  "+ durationNew);

        if (freeCalls.equals(freeGift)) {
            tv_free_call.setText("Free Gift Calls");
        } else if (freeCalls.equals(Unqualified) && durationNew <= 15000) {
            tv_free_call.setText("Unqualified Calls");
        } else {
            tv_free_call.setText("Earning Calls");
        }
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    @Override
    public void isError(String errorCode) {


    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.TRANSACTION_HISTORY_NEW) {
            WallateResponceFemale rsp = (WallateResponceFemale) response;
            tv_video_earning.setText(String.valueOf(rsp.getResult().getCurrentWeekReport().getTotalVideoCoins()));
            tv_gift_earning.setText(String.valueOf(rsp.getResult().getCurrentWeekReport().getTotalGifts()));

        }
    }
}