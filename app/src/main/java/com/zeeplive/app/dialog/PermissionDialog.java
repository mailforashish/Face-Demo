package com.zeeplive.app.dialog;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.zeeplive.app.R;
import com.zeeplive.app.activity.MainActivity;
import com.zeeplive.app.utils.SessionManager;

public class PermissionDialog extends Dialog {
    MainActivity context;
    public static final int RequestPermissionCode = 7;

    public PermissionDialog(MainActivity context) {
        super(context);
        this.context = context;
        init();
    }

    void init() {
        try {
            this.setContentView(R.layout.user_permission_dialog);
            this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //this.setCancelable(false);
            show();

            TextView btn_close = findViewById(R.id.btn_close);
         /*   TextView cemera = findViewById(R.id.cemera);
            TextView phone = findViewById(R.id.phone);
            TextView location = findViewById(R.id.location);
            TextView microphone = findViewById(R.id.microphone);
            TextView storage = findViewById(R.id.storage);*/
            Button btn_allow_all = findViewById(R.id.btn_allow_all);

            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SessionManager(context).setUserAskpermission();
                    dismiss();

                }
            });
            btn_allow_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Adding if condition inside button.
                    ActivityCompat.requestPermissions(
                            context,
                            new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION

                            },
                            RequestPermissionCode

                    );
                    new SessionManager(context).setUserAskpermission();
                    dismiss();
                }
            });


        } catch (Exception e) {
        }
    }



}