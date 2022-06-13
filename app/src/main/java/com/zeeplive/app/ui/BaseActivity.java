package com.zeeplive.app.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zeeplive.app.AppLifecycle;
import com.zeeplive.app.R;
import com.zeeplive.app.Config;

import com.zeeplive.app.ui.actionsheets.AbstractActionSheet;
import com.zeeplive.app.ui.actionsheets.BeautySettingActionSheet;


import java.util.Stack;

import io.agora.capture.video.camera.CameraManager;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.RtmClient;

/**
 * Capabilities that are shared by all activity, such as
 * messaging, action sheets, dialogs, server requests and so on.
 */
public abstract class BaseActivity extends AppCompatActivity  {
    private static final String TAG = BaseActivity.class.getSimpleName();

    protected static final int ACTION_SHEET_VIDEO = 0;
    protected static final int ACTION_SHEET_BEAUTY = 1;
    protected static final int ACTION_SHEET_BG_MUSIC = 2;
    protected static final int ACTION_SHEET_VOICE = 3;
    protected static final int ACTION_SHEET_ROOM_USER = 4;
    protected static final int ACTION_SHEET_PK_ROOM_LIST = 5;
    protected static final int ACTION_SHEET_PRODUCT_LIST = 6;


    private static final int TOAST_SHORT_INTERVAL = 2000;
    private static final int ACTION_SHEET_DIALOG_STYLE_RES = R.style.live_room_dialog;

    protected int systemBarHeight;
    protected int displayHeight;
    protected int displayWidth;

    private Stack<AbstractActionSheet> mActionSheetStack = new Stack<>();
    private BottomSheetDialog mSheetDialog;
    private long mLastToastTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setGlobalLayoutListener();
        systemBarHeight = getStatusBarHeight();
        getDisplaySize();
    }

    private void setGlobalLayoutListener() {
        final View layout = findViewById(Window.ID_ANDROID_CONTENT);
        ViewTreeObserver observer = layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onGlobalLayoutCompleted();
            }
        });
    }

    /**
     * Give a chance to obtain view layout attributes when the
     * content view layout process is completed.
     * Some layout attributes will be available here but not
     * in onCreate(), like measured width/height.
     * This callback will be called ONLY ONCE before the whole
     * window content is ready to be displayed for first time.
     */
    protected void onGlobalLayoutCompleted() {

    }

    protected void hideStatusBar(Window window, boolean darkText) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && darkText) {
            flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        window.getDecorView().setSystemUiVisibility(flag |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    protected void hideStatusBar(boolean darkText) {
        hideStatusBar(getWindow(), darkText);
    }

    private int getStatusBarHeight() {
        int id = getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        return id > 0 ? getResources().getDimensionPixelSize(id) : id;
    }

    protected void keepScreenOn(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void getDisplaySize() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        displayWidth = metric.widthPixels;
        displayHeight = metric.heightPixels;
    }

    protected void showActionSheetDialog(final AbstractActionSheet sheet) {
        dismissActionSheetDialog();
        mSheetDialog = new BottomSheetDialog(this, ACTION_SHEET_DIALOG_STYLE_RES);
        mSheetDialog.setCanceledOnTouchOutside(true);
        mSheetDialog.setContentView(sheet);
        hideStatusBar(mSheetDialog.getWindow(), false);

        mSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (mActionSheetStack.isEmpty()) {
                    // Happens only in case of errors.
                    return;
                }

                if (sheet != mActionSheetStack.peek()) {
                    // When this action sheet is not at the top of
                    // stack, it means that a new action sheet
                    // is about to be shown and it needs a fallback
                    // history, and this sheet needs to be retained.
                    return;
                }

                // At this moment, we want to fallback to
                // the previous action sheet if exists.
                mActionSheetStack.pop();
                if (!mActionSheetStack.isEmpty()) {
                    AbstractActionSheet sheet = mActionSheetStack.peek();
                    ((ViewGroup) sheet.getParent()).removeAllViews();
                    showActionSheetDialog(mActionSheetStack.peek());
                }
            }
        });

        mSheetDialog.show();
    }

    protected AbstractActionSheet showActionSheetDialog(int sheetType, boolean newStack, AbstractActionSheet.AbsActionSheetListener listener) {
        AbstractActionSheet actionSheet;
        switch (sheetType) {
            case ACTION_SHEET_BEAUTY:
                actionSheet = new BeautySettingActionSheet(this);
                break;
            default:
                actionSheet = new BeautySettingActionSheet(this);

        }

        actionSheet.setActionSheetListener(listener);
        if (newStack) mActionSheetStack.clear();
        mActionSheetStack.push(actionSheet);
        showActionSheetDialog(actionSheet);
        return actionSheet;
    }

    protected void showCustomActionSheetDialog(boolean newStack, AbstractActionSheet sheet) {
        if (newStack) mActionSheetStack.clear();
        mActionSheetStack.push(sheet);
        showActionSheetDialog(sheet);
    }

    protected void dismissActionSheetDialog() {
        if (mSheetDialog != null && mSheetDialog.isShowing()) {
            mSheetDialog.dismiss();
        }
    }

    protected boolean actionSheetShowing() {
        return mSheetDialog != null && mSheetDialog.isShowing();
    }


    public AppLifecycle application() {
        return (AppLifecycle)  getApplication();
    }

    public Config config() {
        return application().config();
    }

    public SharedPreferences preferences() {
        return application().preferences();
    }


    protected void showShortToast(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    protected void showLongToast(String message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    protected CameraManager cameraVideoManager() {
        return application().cameraVideoManager();
    }

    protected void showToast(String message, int length) {
        long current = System.currentTimeMillis();
        if (current - mLastToastTime > TOAST_SHORT_INTERVAL) {
            // avoid showing the toast too frequently
            Toast.makeText(this, message, length).show();
            mLastToastTime = current;
        }
    }




}
