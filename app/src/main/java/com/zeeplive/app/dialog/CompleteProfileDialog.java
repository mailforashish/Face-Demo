package com.zeeplive.app.dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.zeeplive.app.R;
import com.zeeplive.app.activity.MainActivity;
import com.zeeplive.app.activity.SocialLogin;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CompleteProfileDialog extends Dialog implements ApiResponseInterface, View.OnClickListener {
    MainActivity context;
    String name;
    String newGuestName;
    EditText tv_user_name;
    ImageView img_back_complete_profile, image_user;
    TextView tv_skip;
    Uri selectedImage;
    ApiManager apiManager;
    LinearLayout camera_Layout, gallery_layout;
    Dialog child;
    Button btn_complete;
    MultipartBody.Part picToUpload;

    public CompleteProfileDialog(@NonNull MainActivity context, String name) {
        super(context);
        this.context = context;
        this.name = name;
        setContentView(R.layout.complete_profile_dialog);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCanceledOnTouchOutside(false);
        //setCancelable(false);
        apiManager = new ApiManager(getContext(), this);
        show();
        init();
        child = new Dialog(context);
        context.registerReceiver(myImageReceiver, new IntentFilter("FBR-USER-IMAGE"));
    }


    public BroadcastReceiver myImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            selectedImage = Uri.parse(intent.getStringExtra("uri"));
            String fromCam = intent.getStringExtra("fromCam");
            Log.e("inBroad", intent.getStringExtra("uri"));
            if (selectedImage != null) {
                if (fromCam.equals("yes")) {
                    String pic = intent.getStringExtra("uri");
                    Bitmap photo = BitmapFactory.decodeFile(pic);
                    Log.e("inBroadfinalimage", pic);
                    ((CircleImageView) findViewById(R.id.image_user)).setImageBitmap(photo);
                } else {
                    ((CircleImageView) findViewById(R.id.image_user)).setImageURI(selectedImage);
                }

                try {
                    File file = null;
                    file = new Compressor(context).compressToFile(new File((String.valueOf(selectedImage))));
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    picToUpload = MultipartBody.Part.createFormData("profile_pic", file.getName(), requestBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }
    };

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        context.unregisterReceiver(myImageReceiver);
    }

    void init() {
        Log.e("parent_here", "complete profile dialog");
        try {
            tv_user_name = findViewById(R.id.et_user_name);
            tv_skip = findViewById(R.id.tv_skip);
            Log.e("gusetusername", name);
            if (name.equals("null")) {
                //  tv_skip.setVisibility(View.GONE);
            } else {
                tv_user_name.setText(name);
            }
            CircleImageView image_user = findViewById(R.id.image_user);
            btn_complete = findViewById(R.id.btn_complete);
            img_back_complete_profile = findViewById(R.id.img_back_complete_profile);
            newGuestName = tv_user_name.getText().toString();
            image_user.setOnClickListener(this);
            btn_complete.setOnClickListener(this);
            img_back_complete_profile.setOnClickListener(this);
            tv_skip.setOnClickListener(this);
        } catch (Exception e) {

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_user:
                showChildDialog();
                return;
            case R.id.btn_complete:
                newGuestName = tv_user_name.getText().toString();
                if (TextUtils.isEmpty(newGuestName)) {
                    Toast.makeText(context, "Enter name", Toast.LENGTH_SHORT).show();
                } else {
                    if (picToUpload != null) {
                        RequestBody conversationIdPic = RequestBody.create(MediaType.parse("text/plain"), newGuestName);
                        apiManager.upDateGuestProfile(conversationIdPic, picToUpload);
                    } else {
                        RequestBody conversationNameId = RequestBody.create(MediaType.parse("text/plain"), newGuestName);
                        apiManager.upDateGuestProfile(conversationNameId, null);
                    }
                    new SessionManager(context).saveGuestStatus(1);
                    //context.onStart();
                    dismiss();
                }
                return;
            case R.id.img_back_complete_profile:
            case R.id.tv_skip:
                /*  Intent intent = new Intent(context, SocialLogin.class);
                context.startActivity(intent);
                context.finish();*/
                new SessionManager(context).saveGuestStatus(1);
                dismiss();
                return;
        }
    }


    public void showChildDialog() {

        child = new Dialog(context);
        child.setContentView(R.layout.image_picker_layout);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        child.show();
        camera_Layout = child.findViewById(R.id.camera_Layout);
        gallery_layout = child.findViewById(R.id.gallery_layout);
        camera_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromCamera();
            }
        });
        gallery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromGallery();
            }
        });
    }

    public void getImageFromCamera() {
        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        ((MainActivity) context).startActivityForResult(takePicture, 0);
        child.dismiss();
    }

    public void getImageFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((MainActivity) context).startActivityForResult(pickPhoto, 1);
        child.dismiss();
    }


    @Override
    public void isError(String errorCode) {

    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.UPDATE_GUEST_PROFILE) {

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, SocialLogin.class);
        context.startActivity(intent);
        context.finish();

    }
}
