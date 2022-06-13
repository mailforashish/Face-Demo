package com.zeeplive.app;

public class TestModel {
    String profile_id, reciverName, reciverProfilePic;
    int UID;

    public TestModel(String profile_id, String reciverName, String reciverProfilePic, int UID) {
        this.profile_id = profile_id;
        this.reciverName = reciverName;
        this.reciverProfilePic = reciverProfilePic;
        this.UID = UID;
    }


    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }


    public String getReciverName() {
        return reciverName;
    }

    public void setReciverName(String reciverName) {
        this.reciverName = reciverName;
    }

    public String getReciverProfilePic() {
        return reciverProfilePic;
    }

    public void setReciverProfilePic(String reciverProfilePic) {
        this.reciverProfilePic = reciverProfilePic;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }
}


