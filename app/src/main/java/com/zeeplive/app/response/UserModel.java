package com.zeeplive.app.response;

public class UserModel {

    String userName, totalFlash;
    int user_pic_url;

    public UserModel(String userName, String totalFlash, int user_pic_url) {
        this.userName = userName;
        this.totalFlash = totalFlash;
        this.user_pic_url = user_pic_url;
    }

    public String getUserName() {
        return userName;
    }

    public String getTotalFlash() {
        return totalFlash;
    }

    public int getUser_pic_url() {
        return user_pic_url;
    }
}
