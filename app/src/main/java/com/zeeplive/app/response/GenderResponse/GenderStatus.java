package com.zeeplive.app.response.GenderResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenderStatus {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("already_registered")
    @Expose
    private Integer alreadyRegistered;
    @SerializedName("error")
    @Expose
    private String error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getAlreadyRegistered() {
        return alreadyRegistered;
    }

    public void setAlreadyRegistered(Integer alreadyRegistered) {
        this.alreadyRegistered = alreadyRegistered;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}