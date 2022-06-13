package com.zeeplive.app.response.Stripe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestModel {
    @SerializedName("amount")
    @Expose
    private String amount;

    public RequestModel(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
