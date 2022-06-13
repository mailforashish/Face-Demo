package com.zeeplive.app.response.NewWalletResponce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletResponceFemaleData {
    @SerializedName("current_week-report")
    @Expose
    private CurrentWeekReport currentWeekReport;
    @SerializedName("last_week-report")
    @Expose
    private LastWeekReport lastWeekReport;

    public CurrentWeekReport getCurrentWeekReport() {
        return currentWeekReport;
    }

    public void setCurrentWeekReport(CurrentWeekReport currentWeekReport) {
        this.currentWeekReport = currentWeekReport;
    }

    public LastWeekReport getLastWeekReport() {
        return lastWeekReport;
    }

    public void setLastWeekReport(LastWeekReport lastWeekReport) {
        this.lastWeekReport = lastWeekReport;
    }
}
