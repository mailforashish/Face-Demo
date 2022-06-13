package com.zeeplive.app.response.Walletfilter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletfilterRequestLastWeek {
    @SerializedName("last_week")
    @Expose
    private String byWeek;

    public String getByWeek() {
        return byWeek;
    }

    public void setByWeek(String byWeek) {
        this.byWeek = byWeek;
    }
}
