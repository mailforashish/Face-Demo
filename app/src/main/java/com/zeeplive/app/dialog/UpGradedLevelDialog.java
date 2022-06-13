package com.zeeplive.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.zeeplive.app.R;
import com.zeeplive.app.activity.SelectPaymentMethod;
import com.zeeplive.app.databinding.UpgradedlevelDialogBinding;
import com.zeeplive.app.response.UpgradedLevel.UpgradedLevelResponce;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;

public class UpGradedLevelDialog extends Dialog implements ApiResponseInterface {
    UpgradedlevelDialogBinding binding;
    Context context;
    ApiManager apiManager;

    public UpGradedLevelDialog(Context context) {
        super(context);
        this.context = context;
        apiManager = new ApiManager(getContext(), this);
        init();
    }

    void init() {
        apiManager.UpgradeUserLevel();
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.upgradedlevel_dialog, null, false);
        setContentView(binding.getRoot());
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        show();

        binding.setClickListener(new UpGradedLevelDialog.EventHandler(getContext()));
    }

    @Override
    public void isError(String errorCode) {

    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }


    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.UPGRADED_LEVELS) {
            UpgradedLevelResponce rsp = (UpgradedLevelResponce) response;
            Log.e("levelData", rsp.getResult());
            //String level = getColoredSpanned(rsp.getResult(), "#ffdd24");
            String level = getColoredSpanned(rsp.getResult(), "#FF1493");
            String upgraded_to = getColoredSpanned("Upgraded to", "#a20be9");
            binding.tvUpgradeLevel.setText(Html.fromHtml(upgraded_to + " " + level));
        }
    }

    public class EventHandler {
        Context mContext;
        public EventHandler(Context mContext) {
            this.mContext = mContext;
        }
        public void sendOk() {
            dismiss();

        }
    }

}





