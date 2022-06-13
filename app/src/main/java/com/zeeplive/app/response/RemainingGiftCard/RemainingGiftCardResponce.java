package com.zeeplive.app.response.RemainingGiftCard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RemainingGiftCardResponce {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("result")
    @Expose
    private List<RemainingGiftCardData> result = null;
    @SerializedName("total_gift")
    @Expose
    private Integer totalGift;
    @SerializedName("free_second")
    @Expose
    private String freeSecond;
    @SerializedName("error")
    @Expose
    private Object error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<RemainingGiftCardData> getResult() {
        return result;
    }

    public void setResult(List<RemainingGiftCardData> result) {
        this.result = result;
    }

    public Integer getTotalGift() {
        return totalGift;
    }

    public void setTotalGift(Integer totalGift) {
        this.totalGift = totalGift;
    }

    public String getFreeSecond() {
        return freeSecond;
    }

    public void setFreeSecond(String freeSecond) {
        this.freeSecond = freeSecond;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }


}
