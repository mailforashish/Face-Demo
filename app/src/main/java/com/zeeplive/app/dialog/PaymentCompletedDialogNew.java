package com.zeeplive.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zeeplive.app.R;
import com.zeeplive.app.activity.InsufficientCoinActivity;
import com.zeeplive.app.activity.SelectPaymentMethod;
import com.zeeplive.app.activity.SelectPaymentMethodNew;
import com.zeeplive.app.utils.SessionManager;

public class PaymentCompletedDialogNew extends Dialog {
    int amount;
    String transaction_id;
    SelectPaymentMethodNew context;
    private Dialog cardDialog;
    private int getGiftCard;

    public PaymentCompletedDialogNew(SelectPaymentMethodNew context, String transactionId, int amount, int getGiftCard) {
        super(context);

        this.context = context;
        this.transaction_id = transactionId;
        this.amount = amount;
        this.getGiftCard = getGiftCard;
        init();
    }

    void init() {
        this.setContentView(R.layout.dialog_payment_successfull);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(false);


        TextView paidAmount = findViewById(R.id.payment_amount);
        TextView transactionId = findViewById(R.id.transaction_id);
        if (new SessionManager(getContext()).getUserLocation().equals("India")) {
            paidAmount.setText("Your payment of ₹" + amount + " was successfully completed");
        } else {
            paidAmount.setText("Your payment of $" + amount + " was successfully completed");
        }
        transactionId.setText(transaction_id);


        Button btn_done = findViewById(R.id.done_btn);
        btn_done.setOnClickListener(view -> {
            cardDialog = new Dialog(getContext());
            cardDialog.setContentView(R.layout.freecard_layout_new);
            cardDialog.setCancelable(false);
            cardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            cardDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationWindMill;
            TextView tv_freecardcount = cardDialog.findViewById(R.id.tv_freecardcount);
            Button btn_gotit = cardDialog.findViewById(R.id.btn_gotit);
            tv_freecardcount.setText(getGiftCard + " gift cards received. Enjoy your free chance of video call.");
            btn_gotit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardDialog.dismiss();
                   /* Intent intent = new Intent(getContext(),InsufficientCoinActivity.class);
                    String value = "refresh";
                    intent.putExtra("value", value);
                    context.startActivity(intent);*/
                   // ((InsufficientCoinActivity) getContext()).recreate();
                   /* if ((getContext() instanceof InsufficientCoinActivity)){
                        ((InsufficientCoinActivity) getContext()).recreate();
                    }*/


                    context.finish();


                }
            });
            cardDialog.show();

            //new UpGradedLevelDialog(context);
            dismiss();
            //context.finish();

        });

        show();
    }
}