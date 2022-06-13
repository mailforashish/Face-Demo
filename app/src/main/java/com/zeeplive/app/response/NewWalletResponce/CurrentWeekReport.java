package com.zeeplive.app.response.NewWalletResponce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentWeekReport {

    @SerializedName("total_gifts")
    @Expose
    private Integer totalGifts;
    @SerializedName("total_video_coins")
    @Expose
    private Integer totalVideoCoins;
    @SerializedName("total_audio_coins")
    @Expose
    private Integer totalAudioCoins;
    @SerializedName("total_coins")
    @Expose
    private Integer totalCoins;

    public Integer getTotalGifts() {
        return totalGifts;
    }

    public void setTotalGifts(Integer totalGifts) {
        this.totalGifts = totalGifts;
    }

    public Integer getTotalVideoCoins() {
        return totalVideoCoins;
    }

    public void setTotalVideoCoins(Integer totalVideoCoins) {
        this.totalVideoCoins = totalVideoCoins;
    }

    public Integer getTotalAudioCoins() {
        return totalAudioCoins;
    }

    public void setTotalAudioCoins(Integer totalAudioCoins) {
        this.totalAudioCoins = totalAudioCoins;
    }

    public Integer getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(Integer totalCoins) {
        this.totalCoins = totalCoins;
    }
}
