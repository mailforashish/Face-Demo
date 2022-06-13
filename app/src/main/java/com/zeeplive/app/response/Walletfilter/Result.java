package com.zeeplive.app.response.Walletfilter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zeeplive.app.response.WalletHistoryResponse;

import java.util.List;

public class Result {



    @SerializedName("walletHistory")
    @Expose
    private List<FilterwalletHistory> walletHistory = null;

    public List<FilterwalletHistory> getWalletHistory() {
        return walletHistory;
    }

    public void setWalletHistory(List<FilterwalletHistory> walletHistory) {
        this.walletHistory = walletHistory;
    }

}
