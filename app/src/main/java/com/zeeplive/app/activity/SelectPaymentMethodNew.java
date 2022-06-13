package com.zeeplive.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.paykun.sdk.PaykunApiCall;
import com.paykun.sdk.PaykunTransaction;
import com.paykun.sdk.PaymentMessage;
import com.paykun.sdk.PaymentMethods;
import com.paykun.sdk.PaymentTypes;
import com.paykun.sdk.SubPaymentTypes;
import com.paykun.sdk.Sub_Methods;
import com.paykun.sdk.helper.PaykunHelper;
import com.paykun.sdk.helper.PaykunResponseListener;
import com.paykun.sdk.logonsquare.Transaction;
import com.razorpay.PaymentResultListener;
import com.stripe.android.model.PaymentMethod;
import com.zeeplive.app.R;
import com.zeeplive.app.databinding.ActivitySelectPaymentMethodNewBinding;
import com.zeeplive.app.dialog.PaymentCompletedDialog;
import com.zeeplive.app.dialog.PaymentCompletedDialogNew;
import com.zeeplive.app.response.PaymentGatewayDetails.CashFree.CashFreePayment.CashFreePaymentRequest;
import com.zeeplive.app.response.RechargePlanResponse;
import com.zeeplive.app.response.WalletRechargeResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.util.HashMap;


public class SelectPaymentMethodNew extends AppCompatActivity implements PaykunResponseListener, ApiResponseInterface {
    ApiManager apiManager;
    SessionManager sessionManager;
    RechargePlanResponse.Data selectedPlan;
    ActivitySelectPaymentMethodNewBinding binding;
    private static final String TAG = "SelectPaymentMethodNew";
    int giftCard;
    String upiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent_new));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_payment_method_new);

        /*getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //getWindow().setStatusBarColor(getResources().getColor(R.color.transparent50));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_payment_method_new);
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);*/

        selectedPlan = (RechargePlanResponse.Data) getIntent().getSerializableExtra("selected_plan");
        //Log.e("selectedPlan", new Gson().toJson(selectedPlan));
        upiId = getIntent().getStringExtra("upi_id");
        giftCard = selectedPlan.getGift_card();
        Log.e("giftData", String.valueOf(giftCard));

        binding.coins.setText(String.valueOf(selectedPlan.getPoints()));
        binding.price.setText("₹ " + selectedPlan.getAmount());

        apiManager = new ApiManager(this, this);
        sessionManager = new SessionManager(this);

        binding.buttonPayNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPayment();
            }
        });
        backPage();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rlMain.setBackgroundResource(R.drawable.select_payment_method_new_bg);
            }
        }, 299);

    }

    // Initiate Transaction
    public void createPayment() {
        // private String merchantIdLive = "734111967908329"; // merchant id for live mode application id  = com.paykunsandbox.live
        //private String accessTokenLive = "D20996D758076CDFF678FF042873D082"; // access token for live mode application id  = com.paykunsandbox.live
        //private String merchantIdSandbox = "895775588965854"; // merchant id for sandbox mode application id = com.paykun.sandbox
        // private String accessTokenSandbox = "74B92BC0C039D9AD7D02FA4993253E8B"; // access token for sandbox application id = com.paykun.sandbox

        // Required data to be provided
        String merchantIdLive = "734111967908329";
        String accessTokenLive = "585426D66F9A3AD6D7F224E34E0C90F8";
        String productName = "Selected Plan";
        String orderId = String.valueOf(System.currentTimeMillis());
        String amount = String.valueOf(selectedPlan.getAmount());
        //String amount = "10";
        String customerName = new SessionManager(getApplicationContext()).getUserId();
        String customerPhone = "1090010000";
        //String customerEmail = "abc@g.com";
        String customerEmail = customerName + "@gmail.com";

        // You can customize which payment method should be provided
        // Here is the example for payment method customization, This is optional.

        // Create Map for payment methods
        HashMap<PaymentTypes, PaymentMethods> payment_methods = new HashMap<>();

        // Create payment method object to be added in payment method Map
        PaymentMethods paymentMethod = new PaymentMethods();
        paymentMethod.enable = true; // True if you want to enable this method or else false, default is true
        paymentMethod.priority = 1; // Set priority for payment method order at checkout page
        paymentMethod.set_as_prefered = true; // If you want this payment method to show in prefered payment method then set it to true

        // Add payment method into our Map
        payment_methods.put(PaymentTypes.UPI, paymentMethod);

        // Example for net banking
        paymentMethod = new PaymentMethods();
        paymentMethod.enable = true;
        paymentMethod.priority = 2;
        paymentMethod.set_as_prefered = false;
        paymentMethod.sub_methods.add(new Sub_Methods(SubPaymentTypes.KKBK, 1));
        paymentMethod.sub_methods.add(new Sub_Methods(SubPaymentTypes.HDFC, 2));
        paymentMethod.sub_methods.add(new Sub_Methods(SubPaymentTypes.ICIC, 3));
        paymentMethod.sub_methods.add(new Sub_Methods(SubPaymentTypes.SBIN, 4));
        paymentMethod.sub_methods.add(new Sub_Methods(SubPaymentTypes.UTIB, 5));
        paymentMethod.sub_methods.add(new Sub_Methods(SubPaymentTypes.ANDB, 6));
        paymentMethod.sub_methods.add(new Sub_Methods(SubPaymentTypes.UBIN, 7));
        paymentMethod.sub_methods.add(new Sub_Methods(SubPaymentTypes.BARB, 8));
        payment_methods.put(PaymentTypes.NB, paymentMethod);

        // Example for wallet
        paymentMethod = new PaymentMethods();
        paymentMethod.enable = true;
        paymentMethod.priority = 3;
        paymentMethod.set_as_prefered = false;
        payment_methods.put(PaymentTypes.WA, paymentMethod);

        // Example for card payment
        paymentMethod = new PaymentMethods();
        paymentMethod.enable = true;
        paymentMethod.priority = 4;
        paymentMethod.set_as_prefered = false;
        payment_methods.put(PaymentTypes.DCCC, paymentMethod);

        // Example for UPI Qr
        paymentMethod = new PaymentMethods();
        paymentMethod.enable = true;
        paymentMethod.priority = 3;
        paymentMethod.set_as_prefered = false;
        payment_methods.put(PaymentTypes.UPIQRCODE, paymentMethod);

        // Example for EMI
      /*paymentMethod = new PaymentMethods();
        paymentMethod.enable = true;
        paymentMethod.priority = 3;
        paymentMethod.set_as_prefered = false;
        payment_methods.put(PaymentTypes.EMI, paymentMethod);*/

        // Now, Create object for paykun transaction
        PaykunTransaction paykunTransaction = new PaykunTransaction(merchantIdLive, accessTokenLive, true);

        try {
            // Set all request data
            paykunTransaction.setCurrency("INR");
            paykunTransaction.setCustomer_name(customerName);
            paykunTransaction.setCustomer_email(customerEmail);
            paykunTransaction.setCustomer_phone(customerPhone);
            paykunTransaction.setProduct_name(productName);
            paykunTransaction.setOrder_no(orderId);
            paykunTransaction.setAmount(amount);
            paykunTransaction.setLive(true); // Currently only live transactions is supported so keep this as true

            // Optionally you can customize color and merchant logo for checkout page
            paykunTransaction.setTheme_color("#d81658");
            paykunTransaction.setTheme_logo("https://cdn.paykun.com/logo-color.svg");

            // Set the payment methods Map object that we have prepared above, this is optional
            paykunTransaction.setPayment_methods(payment_methods);

            new PaykunApiCall.Builder(SelectPaymentMethodNew.this).sendJsonObject(paykunTransaction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(Transaction paymentMessage) {
        Log.e("onPaymentSuccess", paymentMessage.toString());

        CashFreePaymentRequest cashFreePaymentRequest = new CashFreePaymentRequest(paymentMessage.getPaymentId(), String.valueOf(selectedPlan.getId()));
        apiManager.cashFreePayment(cashFreePaymentRequest);

        new PaymentCompletedDialogNew(this, paymentMessage.getPaymentId(), selectedPlan.getAmount(), giftCard);
        Toast.makeText(this, "Your Transaction is Success With Id " + paymentMessage.getPaymentId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(PaymentMessage paymentMessage) {
        if (paymentMessage.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_CANCELLED)) {
            Toast.makeText(this, "Your Transaction is cancelled", Toast.LENGTH_SHORT).show();
        } else if (paymentMessage.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_FAILED)) {
            Toast.makeText(this, "Your Transaction is failed", Toast.LENGTH_SHORT).show();
        } else if (paymentMessage.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_NETWORK_NOT_AVAILABLE)) {
            Toast.makeText(this, "Internet Issue", Toast.LENGTH_SHORT).show();
        } else if (paymentMessage.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_SERVER_ISSUE)) {
            Toast.makeText(this, "Server issue", Toast.LENGTH_SHORT).show();
        } else if (paymentMessage.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_ACCESS_TOKEN_MISSING)) {
            Toast.makeText(this, "Access Token missing", Toast.LENGTH_SHORT).show();
        } else if (paymentMessage.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_MERCHANT_ID_MISSING)) {
            Toast.makeText(this, "Merchant Id is missing", Toast.LENGTH_SHORT).show();
        } else if (paymentMessage.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_INVALID_REQUEST)) {
            Toast.makeText(this, "Invalid Request", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void isError(String errorCode) {

    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {

        if (ServiceCode == Constant.RECHARGE_WALLET) {
            WalletRechargeResponse rsp = (WalletRechargeResponse) response;
            Log.e("giftDataApiWall", String.valueOf(giftCard));
        }


    }

    private void backPage() {
        binding.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.rlMain.setBackgroundResource(0);
                        finish();
                    }
                }, 250);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        binding.rlMain.setBackgroundResource(0);
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
    }
}