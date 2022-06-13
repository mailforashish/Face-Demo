package com.zeeplive.app.response.Walletfilter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletfilterResponce {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("result")
    @Expose
    private Result result;
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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
