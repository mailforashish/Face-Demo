package com.zeeplive.app;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import com.zeeplive.app.framework.PreprocessorFaceUnity;
import com.zeeplive.app.utils.SessionManager;

import java.lang.ref.SoftReference;

import io.agora.capture.video.camera.Constant;


public class Config {
    public static class UserProfile {
        private String userId;
        private String userName;
        private String imageUrl;
        private String token;
        private long agoraUid;
        private SoftReference<Drawable> userIcon;

        public boolean isValid() {
            return userId != null;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName != null ? userName : userId;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }


        public long getAgoraUid() {
            return agoraUid;
        }

        public void setAgoraUid(long agoraUid) {
            this.agoraUid = agoraUid;
        }

        public Drawable getProfileIcon() {
            return userIcon == null ? null : userIcon.get();
        }

        public void setProfileIcon(Drawable userProfileDrawable) {
            this.userIcon = new SoftReference<>(userProfileDrawable);
        }
    }

    public static final int LIVE_TYPE_SINGLE_HOST = 1;

    private AppLifecycle mApplication;

    Config(AppLifecycle application) {
        mApplication = application;
        mUserProfile = new UserProfile();
        SharedPreferences sp = mApplication.preferences();

        mBeautyEnabled = sp.getBoolean(SessionManager.KEY_BEAUTY_ENABLED, true);

        mBlurValue = sp.getFloat(SessionManager.KEY_BLUR, PreprocessorFaceUnity.DEFAULT_BLUR_VALUE);
        mWhitenValue = sp.getFloat(SessionManager.KEY_WHITEN, PreprocessorFaceUnity.DEFAULT_WHITEN_VALUE);
        mCheekValue = sp.getFloat(SessionManager.KEY_CHEEK, PreprocessorFaceUnity.DEFAULT_CHEEK_VALUE);
        mEyeValue = sp.getFloat(SessionManager.KEY_EYE, PreprocessorFaceUnity.DEFAULT_EYE_VALUE);

        mResolutionIndex = sp.getInt(SessionManager.KEY_RESOLUTION, SessionManager.VIDEO_DEFAULT_RESOLUTION_INDEX);
        mFrameRateIndex = sp.getInt(SessionManager.KEY_FRAME_RATE, SessionManager.VIDEO_DEFAULT_FRAME_RATE_INDEX);
        mBitrate = sp.getInt(SessionManager.KEY_BITRATE, SessionManager.VIDEO_DEFAULT_BITRATE);
    }

    private UserProfile mUserProfile;
    // Camera capture configurations
    private int mCameraFacing = Constant.CAMERA_FACING_FRONT;

    // Beautification configs
    private boolean mBeautyEnabled;
    private float mBlurValue;
    private float mWhitenValue;
    private float mCheekValue;
    private float mEyeValue;

    // Video configs
    private int mResolutionIndex;
    private int mFrameRateIndex;
    private int mBitrate;

    public UserProfile getUserProfile() {
        return mUserProfile;
    }


    public boolean isBeautyEnabled() {
        return mBeautyEnabled;
    }

    public void setBeautyEnabled(boolean enabled) {
        mBeautyEnabled = enabled;
        mApplication.preferences().edit()
                .putBoolean(SessionManager.KEY_BEAUTY_ENABLED, enabled).apply();
    }


    public int getCameraFacing() {
        return mCameraFacing;
    }

    public void setCameraFacing(int facing) {
        this.mCameraFacing = facing;
    }

    public float blurValue() {
        return mBlurValue;
    }

    public void setBlurValue(float blur) {
        mBlurValue = blur;
        mApplication.preferences().edit()
                .putFloat(SessionManager.KEY_BLUR, blur).apply();
    }

    public float whitenValue() {
        return mWhitenValue;
    }

    public void setWhitenValue(float whiten) {
        mWhitenValue = whiten;
        mApplication.preferences().edit()
                .putFloat(SessionManager.KEY_WHITEN, whiten).apply();
    }

    public float cheekValue() {
        return mCheekValue;
    }

    public void setCheekValue(float cheek) {
        mCheekValue = cheek;
        mApplication.preferences().edit()
                .putFloat(SessionManager.KEY_CHEEK, cheek).apply();
    }

    public float eyeValue() {
        return mEyeValue;
    }

    public void setEyeValue(float eye) {
        mEyeValue = eye;
        mApplication.preferences().edit()
                .putFloat(SessionManager.KEY_EYE, eye).apply();
    }

    public int resolutionIndex() {
        return mResolutionIndex;
    }

    public void setResolutionIndex(int index) {
        mResolutionIndex = index;
    }

    public int frameRateIndex() {
        return mFrameRateIndex;
    }

    public void setFrameRateIndex(int index) {
        mFrameRateIndex = index;
    }

    public int videoBitrate() {
        return mBitrate;
    }

    public void setVideoBitrate(int bitrate) {
        mBitrate = bitrate;
    }


}
