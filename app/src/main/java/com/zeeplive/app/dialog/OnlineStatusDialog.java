package com.zeeplive.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.zeeplive.app.R;
import com.zeeplive.app.databinding.DialogChangeOnlineStatusBinding;
import com.zeeplive.app.fragment.MyAccountFragment;
import com.zeeplive.app.response.OnlineStatusResponse;
import com.zeeplive.app.response.ReportResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;

public class OnlineStatusDialog extends Dialog implements ApiResponseInterface {

    DialogChangeOnlineStatusBinding binding;
    int status;
    MyAccountFragment context;

    public OnlineStatusDialog(@NonNull MyAccountFragment context, int onlineStatus) {
        super(context.getContext());
        this.status = onlineStatus;
        this.context = context;

        init();
    }

    void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_change_online_status, null, false);
        setContentView(binding.getRoot());
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.setClickListener(new EventHandler(getContext()));
        show();

        if (status == 1) {
            binding.statusSwitch.setChecked(true);
        } else {
            binding.statusSwitch.setChecked(false);
        }

        binding.statusSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                new ApiManager(getContext(), OnlineStatusDialog.this).changeOnlineStatus(1);

            } else {
                new ApiManager(getContext(), OnlineStatusDialog.this).changeOnlineStatus(0);
            }
        });
    }

    public class EventHandler {
        Context mContext;

        public EventHandler(Context mContext) {
            this.mContext = mContext;
        }

        public void closeDialog() {
            dismiss();
        }
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(getContext(), errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.MANAGE_ONLINE_STATUS) {

            OnlineStatusResponse reportResponse = (OnlineStatusResponse) response;
            if (reportResponse.getResult() != null) {

                if (reportResponse.getResult().getIs_online() == 1) {
                    status = 1;
                    context.markOnlineStatus(status);
                } else {
                    status = 0;
                    context.markOnlineStatus(status);
                }
                Toast.makeText(getContext(), reportResponse.getResult().getMsg(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}