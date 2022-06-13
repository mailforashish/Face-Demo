package com.zeeplive.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.google.gson.Gson;
import com.zeeplive.app.R;
import com.zeeplive.app.databinding.ActivitySelectPaymentMethodUpiBinding;
import com.zeeplive.app.dialog.PaymentCompletedDialogNew;
import com.zeeplive.app.helper.NetworkCheck;
import com.zeeplive.app.response.PaymentGatewayDetails.CashFree.CashFreePayment.CashFreePaymentRequest;
import com.zeeplive.app.response.RechargePlanResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.SessionManager;

import java.time.Month;
import java.util.ArrayList;

public class SelectPaymentMethodUpi extends AppCompatActivity implements ApiResponseInterface {

    ApiManager apiManager;
    SessionManager sessionManager;
    RechargePlanResponse.Data selectedPlan;
    ActivitySelectPaymentMethodUpiBinding binding;
    int giftCard;
    final int UPI_PAYMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent_new));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_payment_method_upi);

        selectedPlan = (RechargePlanResponse.Data) getIntent().getSerializableExtra("selected_plan");
        Log.e("selectedPlan", new Gson().toJson(selectedPlan));
        ///upiId = getIntent().getStringExtra("upi_id");
        giftCard = selectedPlan.getGift_card();
        Log.e("giftData", String.valueOf(giftCard));

        binding.coins.setText(String.valueOf(selectedPlan.getPoints()));
        binding.price.setText("₹ " + selectedPlan.getAmount());

        apiManager = new ApiManager(this, this);
        sessionManager = new SessionManager(this);

        binding.buttonPayUpi.setOnClickListener(v -> {
            try {
                //String amount = String.valueOf(selectedPlan.getAmount());
                String amount = "1";
                String upiId = "yap107295@equitas";
                String name = new SessionManager(getApplicationContext()).getUserId();
                String note = "ZeepLive !";
                String transactionId = "TID" + System.currentTimeMillis();
                String transactionRefId = "TID" + System.currentTimeMillis();
                payUsingUpi(amount, upiId, name, note, transactionRefId, transactionId);
            } catch (Exception e) {
            }
        });

    }

    void payUsingUpi(String amount, String upiId, String name, String note, String transactionRefId, String transactionId) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("tr", transactionRefId)
                .appendQueryParameter("ti", transactionId)
                .appendQueryParameter("cu", "INR")
                .build();

        Log.e("UPI", "UPI_URI " + uri);

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        Log.e("UPI", "UPI_CHOOSER " + chooser);
        Log.e("UPI", "upiPayIntent " + upiPayIntent);
        // check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(SelectPaymentMethodUpi.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("UPI", "UPI_RESULT_REQUEST_CODE " + requestCode);
        Log.e("UPI", "UPI_RESULT_RESULT_CODE " + resultCode);
        Log.e("UPI", "UPI_RESULT_DATA " + data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.e("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(SelectPaymentMethodUpi.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                CashFreePaymentRequest cashFreePaymentRequest = new CashFreePaymentRequest(approvalRefNo, String.valueOf(selectedPlan.getId()));
                apiManager.cashFreePayment(cashFreePaymentRequest);
                //new PaymentCompletedDialogNew(this, approvalRefNo, selectedPlan.getAmount(), giftCard);
                Toast.makeText(SelectPaymentMethodUpi.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "responseStr: " + approvalRefNo);

            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(SelectPaymentMethodUpi.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SelectPaymentMethodUpi.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SelectPaymentMethodUpi.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void isError(String errorCode) {

    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {

    }
}