package com.zeeplive.app.response.Video;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoResponce {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("video")
    @Expose
    private String video;
    @SerializedName("error")
    @Expose
    private Object error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

}
