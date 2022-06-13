package com.zeeplive.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeeplive.app.R;
import com.zeeplive.app.response.LoginResponse;
import com.zeeplive.app.response.SentOtpResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

public class VerifyOtp extends AppCompatActivity implements ApiResponseInterface {

    EditText otpEdittext;
    TextView resendOtp;
    String emailId;
    Button submit_btn;
    ApiManager apiManager;
    String purpose = "login in zeeplive";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_verify_otp);

        otpEdittext = findViewById(R.id.otp);
        submit_btn = findViewById(R.id.submit_btn);
        resendOtp = findViewById(R.id.resend_otp);

        emailId = getIntent().getStringExtra("email_id");
        apiManager = new ApiManager(this, this);
        apiManager.sendOtp(emailId, purpose);


        submit_btn.setOnClickListener(v -> {
            if (otpEdittext.getText().toString().length() == 6) {
                apiManager.verifyOtp(emailId, otpEdittext.getText().toString());

            } else {
                Toast.makeText(this, "Enter 6 Digit OTP", Toast.LENGTH_SHORT).show();
            }
        });

        resendOtp.setOnClickListener(v -> {
            apiManager.sendOtp(emailId, purpose);
        });
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(this, errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.SEND_OTP) {
            SentOtpResponse rsp = (SentOtpResponse) response;

            if (rsp.getResult() != null) {
                Toast.makeText(this, rsp.getResult(), Toast.LENGTH_LONG).show();
            }
        }
        if (ServiceCode == Constant.VERIFY_OTP) {
            LoginResponse rsp = (LoginResponse) response;
            if (rsp.getResult() != null) {
                new SessionManager(this).createLoginSession(rsp);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        }
    }
}