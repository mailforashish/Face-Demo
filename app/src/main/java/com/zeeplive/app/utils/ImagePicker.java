package com.zeeplive.app.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;

import com.zeeplive.app.R;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;

public class ImagePicker {

    Activity activity;
    String userChoosenTask;

    public void selectImage(final Activity activity, final int reqCode) {

        this.activity = activity;

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle("Select Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = GetRuntimePermission.checkPermissions(activity);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    if (result)
                        galleryIntent(reqCode);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
       /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, 1);*/

        FishBun.with(activity)
                .setImageAdapter(new GlideAdapter())

                .setCamera(true)

                .startAlbum();
    }

    private void galleryIntent(int reqCode) {
      /*  Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(i, 2);*/

        //  FishBun.with(activity).setImageAdapter(new GlideAdapter()).startAlbum();
        FishBun.with(activity)
                .setImageAdapter(new GlideAdapter())
                .setRequestCode(reqCode)
                .setMaxCount(3)
                //  .setMinCount(3)
                .setPickerSpanCount(3)
                .setActionBarColor(activity.getResources().getColor(R.color.colorPrimaryDark), Color.parseColor("#5D4037"), false)
                .setActionBarTitleColor(Color.parseColor("#ffffff"))
                //  .setArrayPaths(path)
                .setAlbumSpanCount(2, 3)
                .setButtonInAlbumActivity(false)
                .setCamera(true)
                .exceptGif(true)
                .setReachLimitAutomaticClose(true)
                // .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(activity, R.drawable.back_arrow))
                //   .setAllViewTitle("All")
                //  .setActionBarTitle("FishBun Dark")
                //  .textOnNothingSelected("Please select three or more!")
                .startAlbum();

    }
}
