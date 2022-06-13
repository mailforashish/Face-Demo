package com.zeeplive.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.zeeplive.app.R;
import com.zeeplive.app.utils.SessionManager;

public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1500;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        //  Log.e("countryCodeValue", countryCodeValue);
        try {
            //Log.e("mHere", "Nor P");
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String countryCodeValue = tm.getNetworkCountryIso();
            if (countryCodeValue.equals("in")) {
                sessionManager.setUserLocation("India");
            }
        } catch (Exception e) {
        }
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //    Log.e("mHere", "P");
        } else {
            try {
                //Log.e("mHere", "Nor P");
                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                String countryCodeValue = tm.getNetworkCountryIso();
                if (countryCodeValue.equals("in")) {
                    sessionManager.setUserLocation("India");
                }
            } catch (Exception e) {
            }

        }*/
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                new SessionManager(getApplicationContext()).setUserLoaddata();
                //   sessionManager.checkLogin();


                String c_name = new SessionManager(getApplicationContext()).getUserLocation();
                /*if (c_name.equals("null")) {
                    Intent i = new Intent(Splash.this, SocialLogin.class);
                    startActivity(i);
                } else {
                */
                sessionManager.checkLogin();
                // }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
