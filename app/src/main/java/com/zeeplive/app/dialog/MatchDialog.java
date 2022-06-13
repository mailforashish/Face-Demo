package com.zeeplive.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.zeeplive.app.R;
import com.zeeplive.app.databinding.MatchDialogBinding;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;


public class MatchDialog extends Dialog implements ApiResponseInterface {
    MatchDialogBinding binding;
    Context context;
    ApiManager apiManager;
   // public static String finalValue ;
    private String value = "female";
    OnMyDialogResult mDialogResult; // the callback

    public MatchDialog(Context context) {
        super(context);
        this.context = context;
        apiManager = new ApiManager(getContext(), this);
        init();
    }

    void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.match_dialog, null, false);
        setContentView(binding.getRoot());
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
        show();

        binding.setClickListener(new EventHandler(getContext()));
    }

    @Override
    public void isError(String errorCode) {

    }


    @Override
    public void isSuccess(Object response, int ServiceCode) {

    }

    public class EventHandler {
        Context mContext;

        public EventHandler(Context mContext) {
            this.mContext = mContext;
        }
        public void female() {
            if (binding.femaleCheckBox.isChecked()) {
                binding.maleCheckBox.setChecked(false);
                binding.bothCheckBox.setChecked(false);
                value = "female";
            }
        }

        public void male() {
            if (binding.maleCheckBox.isChecked()) {
                binding.femaleCheckBox.setChecked(false);
                binding.bothCheckBox.setChecked(false);
                value = "male";
            }
        }


        public void both() {
            if (binding.bothCheckBox.isChecked()) {
                binding.femaleCheckBox.setChecked(false);
                binding.maleCheckBox.setChecked(false);
                value = "both";
            }
        }

        public void continueButton() {

            if( mDialogResult != null ){
                mDialogResult.finish(String.valueOf(value));
            }
            dismiss();

        }

    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
    }


}


