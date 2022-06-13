package com.zeeplive.app;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.zeeplive.app.activity.MainActivity;
import com.zeeplive.app.activity.SocialLogin;
import com.zeeplive.app.brokenview.BrokenCallback;
import com.zeeplive.app.brokenview.BrokenTouchListener;
import com.zeeplive.app.brokenview.BrokenViews;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.zeeplive.app.activity.SocialLogin.currentVersion;


public class GetAppVersion extends AsyncTask<Void, String, String> {
    private Activity ctx;
    Dialog dialog;

    public GetAppVersion(Activity ctx) {
        this.ctx = ctx;
        dialog = new Dialog(ctx);
    }

    @Override
    protected String doInBackground(Void... voids) {
        String newVersion = null;
        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + ctx.getPackageName() + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            if (document != null) {
                Elements element = document.getElementsContainingOwnText("Current Version");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newVersion = sibElemet.text();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newVersion;

    }


    @Override
    protected void onPostExecute(String onlineVersion) {
        super.onPostExecute(onlineVersion);
        if (onlineVersion != null && !onlineVersion.isEmpty()) {

            Log.e("version", "appVersion" + onlineVersion);
            if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                UpdateMeeDialog updateMeeDialog = new UpdateMeeDialog();
               // updateMeeDialog.showDialogAddRoute(ctx);
                Log.d("update", "Current version " + currentVersion + " playstore version " + onlineVersion);
            }

        }

        // Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);

    }


    public class UpdateMeeDialog extends BrokenCallback {

        private Button buttonUpdate;
        ConstraintLayout parentLayout;
        CircleImageView onCancel;

        private BrokenViews mBrokenView;
        private BrokenTouchListener colorfulListener;
        private BrokenTouchListener whiteListener;
        private Paint whitePaint;

        public void showDialogAddRoute(Activity context) {
            dialog.setContentView(R.layout.dialog_update);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationWindMill;

            mBrokenView = BrokenViews.add2Window(context);
            whitePaint = new Paint();
            whitePaint.setColor(0xffffffff);
            colorfulListener = new BrokenTouchListener.Builder(mBrokenView).build();
            whiteListener = new BrokenTouchListener.Builder(mBrokenView).setPaint(whitePaint).build();

            buttonUpdate = dialog.findViewById(R.id.buttonUpdate);
            onCancel = dialog.findViewById(R.id.img_Cancel);
            parentLayout = dialog.findViewById(R.id.parentLayout);
            dialog.show();
            final BrokenCallback callback = new UpdateMeeDialog();
            mBrokenView.setCallback(true ? callback : null);

            parentLayout.setOnTouchListener(colorfulListener);
            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    colorfulListener = new BrokenTouchListener.Builder(mBrokenView).setEnableArea(buttonUpdate).build();
                }
            });

            onCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }

        @Override
        public void onStart(View v) {

        }

        @Override
        public void onCancel(View v) {

        }

        @Override
        public void onRestart(View v) {

        }

        @Override
        public void onFalling(View v) {

        }

        @Override
        public void onFallingEnd(View v) {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.zeeplive.app")));
            dialog.dismiss();
        }

        @Override
        public void onCancelEnd(View v) {

        }


    }


}