package com.zeeplive.app.response.Stripe.key;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KeyResponce {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private StripeKeyData data;
    @SerializedName("error")
    @Expose
    private Object error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public StripeKeyData getData() {
        return data;
    }

    public void setData(StripeKeyData data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

}
