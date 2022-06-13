package com.zeeplive.app.response.Stripe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerResponce {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("error")
    @Expose
    private Object error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

}
