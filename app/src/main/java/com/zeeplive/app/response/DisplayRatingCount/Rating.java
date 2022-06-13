package com.zeeplive.app.response.DisplayRatingCount;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rating {

    @SerializedName("count_tag")
    @Expose
    private Integer countTag;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("tag")
    @Expose
    private String tag;

    public Integer getCountTag() {
        return countTag;
    }

    public void setCountTag(Integer countTag) {
        this.countTag = countTag;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}