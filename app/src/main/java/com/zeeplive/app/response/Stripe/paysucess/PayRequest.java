package com.zeeplive.app.response.Stripe.paysucess;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayRequest {
    @SerializedName("plan_id")
    @Expose
    private String planId;
    @SerializedName("order_id")
    @Expose
    private String orderId;

    public PayRequest(String planId, String orderId) {
        this.planId = planId;
        this.orderId = orderId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
