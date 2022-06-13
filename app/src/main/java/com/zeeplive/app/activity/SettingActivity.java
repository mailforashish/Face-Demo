package com.zeeplive.app.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.zeeplive.app.R;
import com.zeeplive.app.databinding.ActivitySettingBinding;
import com.zeeplive.app.dialog.AccountInfoDialog;
import com.zeeplive.app.dialog.ChangePasswordDialog;
import com.zeeplive.app.dialog.ComplaintDialog;
import com.zeeplive.app.response.ProfileDetailsResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.SessionManager;

public class SettingActivity extends AppCompatActivity implements ApiResponseInterface {
    ActivitySettingBinding binding;
    String username = "";
    String guestPassword;
    SessionManager sessionManager;
    ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        //setContentView(R.layout.activity_setting);
        binding.settingBack.setOnClickListener(view -> onBackPressed());
        sessionManager = new SessionManager(this);
        guestPassword = sessionManager.getGuestPassword();
        apiManager = new ApiManager(this, this);
        apiManager.getProfileDetails();

        binding.setClickListener(new EventHandler(this));

    }

    public class EventHandler {
        Context mContext;
        public EventHandler(Context mContext) {
            this.mContext = mContext;
        }

        public void complaint() {
            new ComplaintDialog(mContext);
        }

        public void viewTicket() {
            Intent my_wallet = new Intent(mContext, ViewTicketActivity.class);
            startActivity(my_wallet);
        }

        public void aboutUS() {
        }

        public void rateZeeplive() {
        }

        public void clear_cache() {
        }

        public void privacyPolicy() {
            Intent intent = new Intent(mContext, PrivacyPolicy.class);
            startActivity(intent);
        }

        public void changePassword() {
            new ChangePasswordDialog(mContext);
        }

        public void accountInfo() {
            new AccountInfoDialog(mContext, username, guestPassword);
        }

        public void logout() {
            logoutDialog();

        }




    }

    void logoutDialog() {
        Dialog dialog = new Dialog(SettingActivity.this);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        TextView closeDialog = dialog.findViewById(R.id.close_dialog);
        TextView logout = dialog.findViewById(R.id.logout);

        closeDialog.setOnClickListener(view -> dialog.dismiss());
        logout.setOnClickListener(view -> {
            dialog.dismiss();
            String cName = new SessionManager(SettingActivity.this).getUserLocation();
            String eMail = new SessionManager(SettingActivity.this).getUserEmail();
            String passWord = new SessionManager(SettingActivity.this).getUserPassword();
            new SessionManager(SettingActivity.this).logoutUser();
            apiManager.getUserLogout();
            new SessionManager(SettingActivity.this).setUserLocation(cName);
            new SessionManager(SettingActivity.this).setUserEmail(eMail);
            new SessionManager(SettingActivity.this).setUserPassword(passWord);
            new SessionManager(SettingActivity.this).setUserAskpermission();
            finishAffinity();
            //finish();
        });
    }


    @Override
    public void isError(String errorCode) {
        Toast.makeText(SettingActivity.this, errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        ProfileDetailsResponse rsp = (ProfileDetailsResponse) response;
        try {
            if (rsp.getSuccess().getLogin_type().equals("manualy") && new SessionManager(getApplicationContext()).getGender().equals("male")) {
                binding.changePassword.setVisibility(View.VISIBLE);
            }
            if (rsp.getSuccess().getUsername().startsWith("guest")) {
                binding.changePassword.setVisibility(View.GONE);
                binding.accountInfo.setVisibility(View.VISIBLE);
                username = rsp.getSuccess().getUsername();
                Log.e("useridseeting",username);
            }
        } catch (Exception e) {

        }
    }



}