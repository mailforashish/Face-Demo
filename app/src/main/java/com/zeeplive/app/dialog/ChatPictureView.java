package com.zeeplive.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.transition.TransitionManager;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zeeplive.app.R;

public class ChatPictureView extends Dialog {

    Context context;
    String selectedPic;

    public ChatPictureView(Context context, String selectedPic) {
        super(context, android.R.style.Theme_Black);
        this.context = context;
        this.selectedPic = selectedPic;

        init();
    }

    void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adapter_viewpics2);

        ImageView imageView = findViewById(R.id.picture);

        if (!selectedPic.equals("")) {
            Glide.with(context)
                    .load(selectedPic).apply(new RequestOptions().placeholder(R.drawable.ic_photo_library_gray_100dp)
                    .error(R.drawable.ic_photo_library_gray_100dp)).into(imageView);
        }
        show();
    }
}