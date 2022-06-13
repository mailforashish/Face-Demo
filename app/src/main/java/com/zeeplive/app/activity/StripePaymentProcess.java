package com.zeeplive.app.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent;
import com.stripe.android.view.CardInputWidget;
import com.zeeplive.app.R;
import com.zeeplive.app.response.Stripe.RequestModel;
import com.zeeplive.app.response.Stripe.ServerResponce;
import com.zeeplive.app.response.Stripe.key.KeyResponce;
import com.zeeplive.app.response.Stripe.paysucess.PayRequest;
import com.zeeplive.app.response.WalletRechargeResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class StripePaymentProcess extends AppCompatActivity implements ApiResponseInterface {

    public String planId, planAmount;

    private String paymentIntentClientSecret;
    private Stripe stripe;
    private TextView mAmount;
    Button payButton;
    CardInputWidget cardInputWidget;
    ApiManager apiManager;
    ImageView img_sloading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_stripe_payment_process);

        payButton = findViewById(R.id.purchase);
        cardInputWidget = findViewById(R.id.cardInputWidget);

        img_sloading = findViewById(R.id.img_sloading);
        apiManager = new ApiManager(this, this);
        getInten();


        startSloading();
    }

    private void startSloading() {
        Glide.with(getApplicationContext())
                .load(R.drawable.sloading)
                .into(img_sloading);
    }

    public void getInten() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            planId = bundle.getString("planid");
            planAmount = bundle.getString("planamount");

            /*Log.e("planId", planId);
            Log.e("planAmount", planAmount);*/

            //     apiManager.getStripeKey();
            stripe = new Stripe(
                    getApplicationContext(),
                    Objects.requireNonNull(new SessionManager(getApplicationContext()).getUserStriepK())
            );

            startCheckout();

        }
    }

    private void startCheckout() {
        int amount = Integer.parseInt(planAmount) * 100;

      /*  RequestModel requestModel = new RequestModel(String.valueOf(amount));
        apiManager.getStripePaymentInit(requestModel);*/

        paymentIntentClientSecret = new SessionManager(getApplicationContext()).getUserStriepS();

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                if (params != null) {
                    ((RelativeLayout) findViewById(R.id.rl_main)).setVisibility(View.GONE);
                    ((RelativeLayout) findViewById(R.id.rl_paymenyloader)).setVisibility(View.VISIBLE);

                    try {
                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                        stripe.confirmPayment(StripePaymentProcess.this, confirmParams);
                    } catch (Exception e) {
                        Toast.makeText(StripePaymentProcess.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult>, ApiResponseInterface {
        private final WeakReference<StripePaymentProcess> activityRef;

        PaymentResultCallback(@NotNull StripePaymentProcess activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onError(@NotNull Exception e) {
            final StripePaymentProcess activity = activityRef.get();
            Log.e("onError", e.getMessage());

            ((RelativeLayout) findViewById(R.id.rl_main)).setVisibility(View.VISIBLE);
            ((RelativeLayout) findViewById(R.id.rl_paymenyloader)).setVisibility(View.GONE);

            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            if (activity == null) {
                return;
            }

        }

        @Override
        public void onSuccess(@NotNull PaymentIntentResult paymentIntentResult) {
            final StripePaymentProcess activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = paymentIntentResult.getIntent();
            StripeIntent.Status status = paymentIntent.getStatus();
            if (status == StripeIntent.Status.Succeeded) {

               /* Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Log.e("TAG", "onSuccess:Payment " + gson.toJson(paymentIntent));

                activity.displayAlert("Payment Completed",
                        gson.toJson(paymentIntent));*/


                PayRequest payRequest = new PayRequest(planId, paymentIntent.getId());
                //     Log.e("stripeData", new Gson().toJson(payRequest));
                new ApiManager(getApplicationContext(), this).sendDataFromStripe(payRequest);
//                stripePaymentProcess.apiManager.sendDataFromStripe(payRequest);
            } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                activity.displayAlert("Payment Failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage());
            }

        }

        @Override
        public void isError(String errorCode) {

        }

        @Override
        public void isSuccess(Object response, int ServiceCode) {
            if (ServiceCode == Constant.STRIPE_SUCESS) {
                finish();
            }
        }
    }

    private void displayAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("ok", null);
        builder.create().show();

    }


    @Override
    public void isError(String errorCode) {

    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.STRIPE_KEY) {
            KeyResponce rsp = (KeyResponce) response;

            stripe = new Stripe(
                    getApplicationContext(),
                    Objects.requireNonNull(rsp.getData().getPublishableKey())
            );

        }

        if (ServiceCode == Constant.STRIPE_INIT) {
            ServerResponce rsp = (ServerResponce) response;
            paymentIntentClientSecret = rsp.getId();
        }


    }
}