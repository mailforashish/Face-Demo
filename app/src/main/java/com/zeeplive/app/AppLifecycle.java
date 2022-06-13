package com.zeeplive.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.faceunity.FURenderer;

import com.zeeplive.app.framework.PreprocessorFaceUnity;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.utils.SessionManager;

import io.agora.capture.video.camera.CameraManager;

public class AppLifecycle extends Application implements LifecycleObserver {
    private static Context appContext;
    public static boolean wasInBackground;
    private Config mConfig;
    private SharedPreferences mPref;
    private CameraManager mCameraVideoManager;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        mPref = getSharedPreferences(SessionManager.PREF_NAME, Context.MODE_PRIVATE);
        mConfig = new Config(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public Config config() {
        return mConfig;
    }

    public static Context getAppContext() {
        return appContext;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        // app moved to foreground
        wasInBackground = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onMoveToBackground() {
        // app moved to background
        wasInBackground = false;
        new ApiManager(appContext).changeOnlineStatusBack(0);
        new SessionManager(appContext).setUserLoaddata();
        initVideoGlobally();
    }


    public SharedPreferences preferences() {
        return mPref;
    }

    public CameraManager cameraVideoManager() {
        return mCameraVideoManager;
    }

    private void initVideoGlobally() {
        new Thread(() -> {
            FURenderer.initFURenderer(getApplicationContext());
            PreprocessorFaceUnity preprocessor =
                    new PreprocessorFaceUnity(this);
            mCameraVideoManager = new CameraManager(
                    this, preprocessor);
            mCameraVideoManager.setCameraStateListener(preprocessor);
        }).start();
    }
}