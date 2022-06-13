package com.zeeplive.app.response.Walletfilter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilterwalletHistory {
    @SerializedName("duringtime")
    @Expose
    private String duringtime;
    @SerializedName("total_credited")
    @Expose
    private String totalCredited;
    @SerializedName("total_debit")
    @Expose
    private String totalDebit;
    @SerializedName("total_duration")
    @Expose
    private Float totalDuration;

    public String getDuringtime() {
        return duringtime;
    }

    public void setDuringtime(String duringtime) {
        this.duringtime = duringtime;
    }

    public String getTotalCredited() {
        return totalCredited;
    }

    public void setTotalCredited(String totalCredited) {
        this.totalCredited = totalCredited;
    }

    public String getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(String totalDebit) {
        this.totalDebit = totalDebit;
    }

    public Float getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Float totalDuration) {
        this.totalDuration = totalDuration;
    }
}
