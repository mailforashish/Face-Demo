package com.zeeplive.app.ui.live;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.elvishew.xlog.XLog;

import com.zeeplive.app.R;
import com.zeeplive.app.ui.actionsheets.BeautySettingActionSheet;

public abstract class LiveRoomActivity extends LiveBaseActivity implements
        BeautySettingActionSheet.BeautyActionSheetListener,
        TextView.OnEditorActionListener{

    private static final String TAG = LiveRoomActivity.class.getSimpleName();
    private static final int IDEAL_MIN_KEYBOARD_HEIGHT = 200;

    private Rect mDecorViewRect;
    private int mInputMethodHeight;

    // UI components of a live room
    protected AppCompatEditText messageEditText;
    protected Dialog curDialog;
    protected InputMethodManager inputMethodManager;
    // Rtc Engine requires that the calls of startAudioMixing
    // should not be too frequent if online musics are played.
    // The interval is better not to be fewer than 100 ms.
    private volatile long mLastMusicPlayedTimeStamp;

    private boolean mActivityFinished;
    protected boolean inEarMonitorEnabled;
    private boolean mHeadsetWithMicrophonePlugged;

    private BroadcastReceiver mHeadPhoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_HEADSET_PLUG.equals(action)) {
                boolean plugged = intent.getIntExtra("state", -1) == 1;
                boolean hasMic = intent.getIntExtra("microphone", -1) == 1;
                mHeadsetWithMicrophonePlugged = plugged && hasMic;
                XLog.d("Wired headset is plugged：" + mHeadsetWithMicrophonePlugged);
            }
        }
    };

   // private NetworkReceiver mNetworkReceiver = new NetworkReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().getViewTreeObserver()
                .addOnGlobalLayoutListener(this::detectKeyboardLayout);

        inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        IntentFilter headPhoneFilter = new IntentFilter();
        headPhoneFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(mHeadPhoneReceiver, headPhoneFilter);
    }


    private void detectKeyboardLayout() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        if (mDecorViewRect == null) {
            mDecorViewRect = rect;
        }

        int diff = mDecorViewRect.height() - rect.height();

        // The global layout listener may be invoked several
        // times when the activity is launched, we need to care
        // about the value of detected input method height to
        // filter out the cases that are not desirable.
        if (diff == mInputMethodHeight) {
            // The input method is still shown
            return;
        }

        if (diff > IDEAL_MIN_KEYBOARD_HEIGHT && mInputMethodHeight == 0) {
            mInputMethodHeight = diff;
        } else if (mInputMethodHeight > 0) {
            mInputMethodHeight = 0;
        }
    }

    protected String virtualImageIdToName(int id) {
        switch (id) {
            case 0: return "dog";
            case 1: return "girl";
            default: return null;
        }
    }


    @Override
    public void onActionSheetBeautyEnabled(boolean enabled) {
        enablePreProcess(enabled);
    }

    @Override
    public void onActionSheetBlurSelected(float blur) {
        setBlurValue(blur);
    }

    @Override
    public void onActionSheetWhitenSelected(float whiten) {
        setWhitenValue(whiten);
    }

    @Override
    public void onActionSheetCheekSelected(float cheek) {
        setCheekValue(cheek);
    }

    @Override
    public void onActionSheetEyeEnlargeSelected(float eye) {
        setEyeValue(eye);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Editable editable = messageEditText.getText();
            if (TextUtils.isEmpty(editable)) {
                //showShortToast(getResources().getString(R.string.live_send_empty_message));
            } else {
                //sendChatMessage(editable.toString());
                messageEditText.setText("");
            }

            inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
            return true;
        }
        return false;
    }



    protected boolean isCurDialogShowing() {
        return curDialog != null && curDialog.isShowing();
    }

    protected void closeDialog() {
        if (isCurDialogShowing()) {
            curDialog.dismiss();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        startCameraCapture();
    }


    @Override
    public void onStop() {
        super.onStop();
        stopCameraCapture();
    }

    @Override
    public void finish() {
        super.finish();
        mActivityFinished = true;
        stopCameraCapture();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHeadPhoneReceiver);
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
       // registerReceiver(mNetworkReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
       // unregisterReceiver(mNetworkReceiver);
    }

   /* protected static class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return;

            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null || !info.isAvailable() || !info.isConnected()) {
                Toast.makeText(context, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
            } else {
                int type = info.getType();
                if (ConnectivityManager.TYPE_WIFI == type) {
                    Toast.makeText(context, R.string.network_switch_to_wifi, Toast.LENGTH_SHORT).show();
                } else if (ConnectivityManager.TYPE_MOBILE == type) {
                    Toast.makeText(context, R.string.network_switch_to_mobile , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/
}
