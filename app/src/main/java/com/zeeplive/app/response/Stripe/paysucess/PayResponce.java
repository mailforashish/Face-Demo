package com.zeeplive.app.response.Stripe.paysucess;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayResponce {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("error")
    @Expose
    private Object error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
