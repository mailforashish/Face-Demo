package com.zeeplive.app.response.Friend;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Friend {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("friend_id")
    @Expose
    private int friendId;
    @SerializedName("conversation_id")
    @Expose
    private String conversationId;
    @SerializedName("request_date")
    @Expose
    private String requestDate;
    @SerializedName("is_approved")
    @Expose
    private int isApproved;
    @SerializedName("pay_user_id")
    @Expose
    private int payUserId;
    @SerializedName("is_paid")
    @Expose
    private int isPaid;
    @SerializedName("is_payment_required")
    @Expose
    private int isPaymentRequired;
    @SerializedName("payment_transaction_id")
    @Expose
    private int paymentTransactionId;
    @SerializedName("approved_date")
    @Expose
    private String approvedDate;
    @SerializedName("is_active")
    @Expose
    private int isActive;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("age")
    @Expose
    private int age;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("profile_photo")
    @Expose
    private String profilePhoto;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("coin_per_minute")
    @Expose
    private int coinPerMinute;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public int getPayUserId() {
        return payUserId;
    }

    public void setPayUserId(int payUserId) {
        this.payUserId = payUserId;
    }

    public int getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(int isPaid) {
        this.isPaid = isPaid;
    }

    public int getIsPaymentRequired() {
        return isPaymentRequired;
    }

    public void setIsPaymentRequired(int isPaymentRequired) {
        this.isPaymentRequired = isPaymentRequired;
    }

    public int getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(int paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getCoinPerMinute() {
        return coinPerMinute;
    }

    public void setCoinPerMinute(int coinPerMinute) {
        this.coinPerMinute = coinPerMinute;
    }
}
