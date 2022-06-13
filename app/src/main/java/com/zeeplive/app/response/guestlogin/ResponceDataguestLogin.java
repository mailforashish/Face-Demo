package com.zeeplive.app.response.guestlogin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponceDataguestLogin {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("auth_token")
    @Expose
    private String auth_token;

    @SerializedName("device_type")
    @Expose
    private String device_type;

    @SerializedName("device_id")
    @Expose
    private String device_id;

    @SerializedName("unique_user_id")
    @Expose
    private String unique_user_id;

    @SerializedName("unique_key")
    @Expose
    private String unique_key;

    @SerializedName("user_name")
    @Expose
    private String user_name;

    @SerializedName("verification_code")
    @Expose
    private String verification_code;

    @SerializedName("reset_key")
    @Expose
    private String reset_key;

    @SerializedName("login_by")
    @Expose
    private String login_by;

    @SerializedName("first_name")
    @Expose
    private String first_name;

    @SerializedName("last_name")
    @Expose
    private String last_name;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("country_id")
    @Expose
    private String country_id;

    @SerializedName("profile_photo")
    @Expose
    private String profile_photo;

    @SerializedName("login_count")
    @Expose
    private String login_count;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("verified")
    @Expose
    private String verified;

    @SerializedName("created")
    @Expose
    private String created;

    @SerializedName("modified")
    @Expose
    private String modified;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("purchased_minutes")
    @Expose
    private String purchased_minutes;
    @SerializedName("login_with")
    @Expose
    private String loginWith;

    public ResponceDataguestLogin(Integer id, String user_name, String auth_token, String unique_user_id, String profile_photo, String password, String purchased_minutes) {
        this.id = id;
        this.user_name = user_name;
        this.auth_token = auth_token;
        this.unique_user_id = unique_user_id;
        this.profile_photo = profile_photo;
        this.password = password;
        this.purchased_minutes = purchased_minutes;
    }

    public ResponceDataguestLogin(Integer id, String user_name, String auth_token, String unique_user_id, String profile_photo, String password, String purchased_minutes,
                                  String loginWith) {
        this.id = id;
        this.user_name = user_name;
        this.auth_token = auth_token;
        this.unique_user_id = unique_user_id;
        this.profile_photo = profile_photo;
        this.password = password;
        this.purchased_minutes = purchased_minutes;
        this.loginWith = loginWith;
    }

    public ResponceDataguestLogin(Integer id, String auth_token, String device_type, String device_id, String unique_user_id, String unique_key, String user_name, String verification_code, String reset_key, String login_by, String first_name, String last_name, String gender, String phone, String email, String country_id, String profile_photo, String login_count, String status, String verified, String created, String modified, String name) {
        this.id = id;
        this.auth_token = auth_token;
        this.device_type = device_type;
        this.device_id = device_id;
        this.unique_user_id = unique_user_id;
        this.unique_key = unique_key;
        this.user_name = user_name;
        this.verification_code = verification_code;
        this.reset_key = reset_key;
        this.login_by = login_by;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.country_id = country_id;
        this.profile_photo = profile_photo;
        this.login_count = login_count;
        this.status = status;
        this.verified = verified;
        this.created = created;
        this.modified = modified;
        this.name = name;
    }

    public ResponceDataguestLogin(String purchased_minutes) {
        this.purchased_minutes = purchased_minutes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUnique_user_id() {
        return unique_user_id;
    }

    public void setUnique_user_id(String unique_user_id) {
        this.unique_user_id = unique_user_id;
    }

    public String getUnique_key() {
        return unique_key;
    }

    public void setUnique_key(String unique_key) {
        this.unique_key = unique_key;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(String verification_code) {
        this.verification_code = verification_code;
    }

    public String getReset_key() {
        return reset_key;
    }

    public void setReset_key(String reset_key) {
        this.reset_key = reset_key;
    }

    public String getLogin_by() {
        return login_by;
    }

    public void setLogin_by(String login_by) {
        this.login_by = login_by;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getLogin_count() {
        return login_count;
    }

    public void setLogin_count(String login_count) {
        this.login_count = login_count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPurchased_minutes() {
        return purchased_minutes;
    }

    public void setPurchased_minutes(String purchased_minutes) {
        this.purchased_minutes = purchased_minutes;
    }

    public String getLoginWith() {
        return loginWith;
    }

    public void setLoginWith(String loginWith) {
        this.loginWith = loginWith;
    }
}
