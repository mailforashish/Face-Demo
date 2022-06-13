package com.zeeplive.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.zeeplive.app.R;
import com.zeeplive.app.databinding.DialogChangePasswordBinding;
import com.zeeplive.app.response.ReportResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;

public class ChangePasswordDialog extends Dialog {

    DialogChangePasswordBinding binding;

    public ChangePasswordDialog(@NonNull Context context) {
        super(context);

        init();
    }

    void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_change_password, null, false);
        setContentView(binding.getRoot());
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        show();

        binding.setClickListener(new EventHandler(getContext()));
    }

    public class EventHandler implements ApiResponseInterface {
        Context mContext;

        public EventHandler(Context mContext) {
            this.mContext = mContext;
        }

        public void changePassword() {
            if (binding.newPassword.getText().toString().length() >= 6) {
                if (binding.newPassword.getText().toString().equalsIgnoreCase(binding.confirmNewPassword.getText().toString())) {
                    new ApiManager(getContext(), this).changePassword(binding.newPassword.getText().toString());

                } else {
                    Toast.makeText(getContext(), "Password must be same", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Password length must be 6 digit or more", Toast.LENGTH_LONG).show();
            }
        }

        @Override

        public void isError(String errorCode) {
            Toast.makeText(getContext(), errorCode, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void isSuccess(Object response, int ServiceCode) {
            if (ServiceCode == Constant.CHANGE_PASSWORD) {

                ReportResponse reportResponse = (ReportResponse) response;
                if (reportResponse.getResult() != null && reportResponse.getResult().length() > 0) {
                    dismiss();
                    Toast.makeText(mContext, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}