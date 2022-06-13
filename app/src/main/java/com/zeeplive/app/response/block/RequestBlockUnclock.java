package com.zeeplive.app.response.block;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestBlockUnclock {
    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("conversationId")
    @Expose
    private String conversationId;

    @SerializedName("_id")
    @Expose
    private int id;

    @SerializedName("block_id")
    @Expose
    private String blockid;

    @SerializedName("blocker_user_id")
    @Expose
    private String blockeruserid;

    public RequestBlockUnclock(String action, String conversationId, int id, String blockid, String blockeruserid) {
        this.action = action;
        this.conversationId = conversationId;
        this.id = id;
        this.blockid = blockid;
        this.blockeruserid = blockeruserid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBlockid() {
        return blockid;
    }

    public void setBlockid(String blockid) {
        this.blockid = blockid;
    }

    public String getBlockeruserid() {
        return blockeruserid;
    }

    public void setBlockeruserid(String blockeruserid) {
        this.blockeruserid = blockeruserid;
    }
}
