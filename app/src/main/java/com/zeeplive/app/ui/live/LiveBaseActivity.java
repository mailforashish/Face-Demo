package com.zeeplive.app.ui.live;

import android.os.Bundle;

import com.zeeplive.app.framework.PreprocessorFaceUnity;
import com.zeeplive.app.ui.BaseActivity;

import io.agora.capture.video.camera.CameraManager;
/**
 * Common capabilities of a live room. Such as, camera capture，
 * , agora rtc, messaging, permission check, communication with
 * the back-end server, and so on.
 */
public abstract class LiveBaseActivity extends BaseActivity {

    private CameraManager mCameraVideoManager;
    private PreprocessorFaceUnity mFUPreprocessor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keepScreenOn(getWindow());
        initCameraIfNeeded();
    }

    private void initCameraIfNeeded() {
        if (mCameraVideoManager == null) {
            mCameraVideoManager = cameraVideoManager();
        }

        if (mCameraVideoManager != null) {
            mCameraVideoManager.enablePreprocessor(config().isBeautyEnabled());
        }

        if (mFUPreprocessor == null &&
                mCameraVideoManager != null) {
            mFUPreprocessor = (PreprocessorFaceUnity)
                    mCameraVideoManager.getPreprocessor();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        initCameraIfNeeded();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    protected void startCameraCapture() {
        initCameraIfNeeded();
        if (mCameraVideoManager != null) {
            enablePreProcess(config().isBeautyEnabled());
            mCameraVideoManager.startCapture();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mCameraVideoManager.stopCapture();
    }

    protected void stopCameraCapture() {
        initCameraIfNeeded();
        if (mCameraVideoManager != null) {
            mCameraVideoManager.stopCapture();
        }
    }

    protected void enablePreProcess(boolean enabled) {
        initCameraIfNeeded();
        if (mCameraVideoManager != null) {
            mCameraVideoManager.enablePreprocessor(enabled);
        }
    }

    protected void setBlurValue(float blur) {
        initCameraIfNeeded();
        if (mFUPreprocessor != null) {
            mFUPreprocessor.setBlurValue(blur);
        }
    }

    protected void setWhitenValue(float whiten) {
        initCameraIfNeeded();
        if (mFUPreprocessor != null) {
            mFUPreprocessor.setWhitenValue(whiten);
        }
    }

    protected void setCheekValue(float cheek) {
        initCameraIfNeeded();
        if (mFUPreprocessor != null) {
            mFUPreprocessor.setCheekValue(cheek);
        }
    }

    protected void setEyeValue(float eye) {
        initCameraIfNeeded();
        if (mFUPreprocessor != null) {
            mFUPreprocessor.setEyeValue(eye);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void finish() {
        super.finish();
    }

}
