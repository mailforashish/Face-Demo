package com.zeeplive.app.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zeeplive.app.R;
import com.zeeplive.app.activity.PrivacyPolicy;
import com.zeeplive.app.activity.SettingActivity;
import com.zeeplive.app.activity.ViewTicketActivity;
import com.zeeplive.app.databinding.FragmentSettingBinding;
import com.zeeplive.app.dialog.AccountInfoDialog;
import com.zeeplive.app.dialog.ChangePasswordDialog;
import com.zeeplive.app.dialog.ComplaintDialog;
import com.zeeplive.app.response.ProfileDetailsResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.SessionManager;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SettingFragment extends Fragment implements ApiResponseInterface {

    FragmentSettingBinding binding;
    String username = "";
    String guestPassword;
    SessionManager sessionManager;
    ApiManager apiManager;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       // binding.settingBack.setOnClickListener(view -> onBackPressed());
        sessionManager = new SessionManager(getContext());
        guestPassword = sessionManager.getGuestPassword();
        apiManager = new ApiManager(getContext(), this);
        apiManager.getProfileDetails();

        binding.setClickListener(new EventHandler(getContext()));

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

        public void settingBack(){
            FragmentManager fm = getFragmentManager();
            fm.popBackStackImmediate();

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
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_exit);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        TextView closeDialog = dialog.findViewById(R.id.close_dialog);
        TextView logout = dialog.findViewById(R.id.logout);

        closeDialog.setOnClickListener(view -> dialog.dismiss());
        logout.setOnClickListener(view -> {
            //dialog.dismiss();
            String cName = new SessionManager(getContext()).getUserLocation();
            String eMail = new SessionManager(getContext()).getUserEmail();
            String passWord = new SessionManager(getContext()).getUserPassword();
            new SessionManager(getContext()).logoutUser();
            apiManager.getUserLogout();
            new SessionManager(getContext()).setUserLocation(cName);
            new SessionManager(getContext()).setUserEmail(eMail);
            new SessionManager(getContext()).setUserPassword(passWord);
            new SessionManager(getContext()).setUserAskpermission();
            dialog.dismiss();
        });
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(getContext(), errorCode, Toast.LENGTH_SHORT).show();
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