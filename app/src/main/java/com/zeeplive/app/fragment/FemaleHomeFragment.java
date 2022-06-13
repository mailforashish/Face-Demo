package com.zeeplive.app.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zeeplive.app.R;
import com.zeeplive.app.activity.MainActivity;
import com.zeeplive.app.databinding.FragmentFemaleHomeBinding;
import com.zeeplive.app.helper.CameraPreview;
import com.zeeplive.app.response.EndCallData.EndCallData;
import com.zeeplive.app.response.OnlineStatusResponse;
import com.zeeplive.app.response.ProfileDetailsResponse;
import com.zeeplive.app.response.language.LanguageResponce;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FemaleHomeFragment extends Fragment implements ApiResponseInterface {

    FragmentFemaleHomeBinding binding;
    public int onlineStatus;


    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;


    public FemaleHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_female_home, container, false);

        myContext = getContext();
        initialize();

        if (!recording) {
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                // release the old camera instance
                // switch camera, from the front and the back and vice versa

                releaseCamera();
                chooseCamera();
            } else {
                Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                toast.show();
            }
        }

        binding.statusSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

            if (((MainActivity) getActivity()).isBlockFunction() == 1) {
                binding.statusSwitch.setEnabled(false);
            } else {
                if (b) {
                    new ApiManager(getContext(), this).changeOnlineStatus(1);
                    // FirebaseDatabase.getInstance().goOnline();
                } else {
                    //  FirebaseDatabase.getInstance().goOffline();
                    new ApiManager(getContext(), this).changeOnlineStatus(0);
                }
            }
        });

//        new ApiManager(getContext(), this).changeOnlineStatus(0);

        return binding.getRoot();
    }

    private void checkLastCallData() {

        try {
            if (new SessionManager(getContext()).getUserGetendcalldata().equals("error")) {
                // Log.e("callEndArrayData", new Gson().toJson(new SessionManager(getContext()).getUserEndcalldata()));
                ArrayList<EndCallData> endCallData = new ArrayList<>();

                JSONArray arr = new JSONArray(new Gson().toJson(new SessionManager(getContext()).getUserEndcalldata()));
                //loop through each object
                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonProductObject = arr.getJSONObject(i);
                    String uniqueId = jsonProductObject.getString("unique_id");
                    String endTime = jsonProductObject.getString("end_time");
                    //Log.e("inErrorofEndcall", name);

                    EndCallData endCallData1 = new EndCallData(uniqueId, endTime);

                    endCallData.add(endCallData1);
                }

                //   Log.e("endCallArrayData", endCallData.size() + "");

                new ApiManager(getContext()).submitEndCallData(endCallData);

/*            endCallData.add(new SessionManager(getContext()).getUserEndcalldata());
            new ApiManager(getContext()).submitEndCallData(endCallData);*/
            }
        } catch (Exception e) {
            checkLastCallData();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.fastmodeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

            if (((MainActivity) getActivity()).isBlockFunction() == 1) {
                binding.fastmodeSwitch.setEnabled(false);
            } else {
                if (b) {
                    binding.profilePicBg.setVisibility(View.GONE);
                    binding.svCamera.setVisibility(View.VISIBLE);
                    if (recording) {
                        // stop recording and release camera
                        try {
                            recording = false;
                            mediaRecorder.stop(); // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                        } catch (Exception e) {
                        }
                        //   Toast.makeText(getActivity(), "Video captured!", Toast.LENGTH_LONG).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.profilePicBg.setVisibility(View.VISIBLE);
                                binding.svCamera.setVisibility(View.GONE);

                                try {
                                    recording = false;
                                    mediaRecorder.stop(); // stop the recording
                                    releaseMediaRecorder(); // release the MediaRecorder object
                                } catch (Exception e) {
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendVideo();
                                    }
                                }, 300);
                            }
                        }, 10000);

                    } else {
                        if (!prepareMediaRecorder()) {
                            //   Toast.makeText(getActivity(), "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                        }
                        // work on UiThread for better performance
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                // If there are stories, add them to the table

                                try {
                                    mediaRecorder.start();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.profilePicBg.setVisibility(View.VISIBLE);
                                            binding.svCamera.setVisibility(View.GONE);

                                            try {
                                                recording = false;
                                                mediaRecorder.stop(); // stop the recording
                                                releaseMediaRecorder(); // release the MediaRecorder object
                                            } catch (Exception e) {
                                            }
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    sendVideo();
                                                }
                                            }, 300);
                                        }
                                    }, 10000);
                                } catch (final Exception ex) {
                                    // Log.i("---","Exception in thread");
                                }
                            }
                        });
                        new SessionManager(getContext()).setFastModeState(1);
                        recording = true;
                    }

                } else {
                    new SessionManager(getContext()).setFastModeState(0);

                    try {
                        binding.profilePicBg.setVisibility(View.VISIBLE);
                        binding.svCamera.setVisibility(View.GONE);
                        recording = false;
                        mediaRecorder.stop(); // stop the recording
                        releaseMediaRecorder(); // release the MediaRecorder object

                    } catch (Exception e) {
                    }
                }
            }
        });

    }

    boolean recording = false;


    @Override
    public void onResume() {
        super.onResume();

        if (new SessionManager(getContext()).getFastModeState() == 1) {
            binding.fastmodeSwitch.setChecked(true);
            binding.profilePicBg.setVisibility(View.VISIBLE);
            binding.svCamera.setVisibility(View.GONE);
        }


        if (new SessionManager(getContext()).getOnlineFromBack() == 0) {
            binding.statusSwitch.setChecked(false);
        }

        new ApiManager(getContext(), this).getProfileDetails();

        new ApiManager(getContext(), this).getUserLanguage();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
        }

        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(getContext(), "No front facing camera found.", Toast.LENGTH_LONG).show();
            }
            try {
                mCamera = Camera.open(findFrontFacingCamera());
                mCamera.setDisplayOrientation(90);
                mPreview.refreshCamera(mCamera);
            } catch (Exception e) {
            }
        }

    }

    private void sendVideo() {
        String vdoPath = Environment.getExternalStorageDirectory() + "/com.zeeplive.app/myvideo.mp4";
        Uri vdoUri = Uri.parse(vdoPath);
        try {
            File vdo = new File(vdoPath);
            if (vdo.exists()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), vdo);
                MultipartBody.Part file = MultipartBody.Part.createFormData("profile_video", vdo.getName(), requestBody);

                new ApiManager(getContext(), this).sendVideo(file);
            }

            //   new ApiManager(getContext(), this).sendVideo(vdoToUpload);
        } catch (Exception e) {
            Log.e("errorVdoFRG", e.getMessage());
        }

    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(getContext(), errorCode, Toast.LENGTH_SHORT).show();
    }

    String userId = "", userName = "", userImg = "";


    @Override
    public void isSuccess(Object response, int ServiceCode) {
        try {

            if (ServiceCode == Constant.PROFILE_DETAILS) {
                ProfileDetailsResponse rsp = (ProfileDetailsResponse) response;

                //    Log.e("inProfile", "m here");

                if (rsp.getSuccess().getProfile_images() != null && rsp.getSuccess().getProfile_images().size() > 0) {
                    if (!rsp.getSuccess().getProfile_images().get(0).getImage_name().equals("")) {
                        Glide.with(getContext()).load(rsp.getSuccess().getProfile_images().get(0).getImage_name())
                                .placeholder(R.drawable.default_profile).into(binding.profilePicBg);
                    }
                }

                String img = "";
                if (rsp.getSuccess().getProfile_images() != null && rsp.getSuccess().getProfile_images().size() > 0) {
                    img = rsp.getSuccess().getProfile_images().get(0).getImage_name();
                }

                userId = String.valueOf(rsp.getSuccess().getProfile_id());
                userName = rsp.getSuccess().getName();
                userImg = img;
                new SessionManager(getContext()).setUserProfilepic(img);
                // markOnlineStatus(rsp.getSuccess().getIs_online());
              /*  Log.e("onlineStatus", new SessionManager(getContext()).getOnlineState() + "");
                if (new SessionManager(getContext()).getOnlineState() == 1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new SessionManager(getContext()).setOnlineState(0);
                            markOnlineStatus(1);
                        }
                    }, 2000);
                } else {
                    markOnlineStatus(rsp.getSuccess().getIs_online());
                }*/
                //  markOnlineStatus(rsp.getSuccess().getIs_online());

            }

            if (ServiceCode == Constant.MANAGE_ONLINE_STATUS) {
                //    Log.e("inMANAGE", "m here");
                OnlineStatusResponse reportResponse = (OnlineStatusResponse) response;
                if (reportResponse.getResult() != null) {
                    new SessionManager(getContext()).setOnlineFromBack(1);
                    //    markOnlineStatus(reportResponse.getResult().getIs_online());
                    //  Toast.makeText(getContext(), reportResponse.getResult().getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            if (ServiceCode == Constant.LAN_DATA) {
                //    Log.e("inLANDATA", "m here");
                LanguageResponce rsp = (LanguageResponce) response;

                if (!rsp.getSuccess()) {
                    //        Log.e("errCode", rsp.getError());
                    logoutDialog();
                }
            }
        } catch (Exception e) {
        }
    }

    void logoutDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_exit);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        TextView closeDialog = dialog.findViewById(R.id.close_dialog);
        TextView tv_msg = dialog.findViewById(R.id.tv_msg);
        TextView logout = dialog.findViewById(R.id.logout);

        tv_msg.setText("You have been logout. As your access token is expired.");

        closeDialog.setVisibility(View.GONE);
        closeDialog.setOnClickListener(view -> dialog.dismiss());

        logout.setText("OK");
        logout.setOnClickListener(view -> {
            dialog.dismiss();
            String eMail = new SessionManager(getContext()).getUserEmail();
            String passWord = new SessionManager(getContext()).getUserPassword();
            new SessionManager(getContext()).logoutUser();
            new ApiManager(getContext(), this).getUserLogout();
            new SessionManager(getContext()).setUserEmail(eMail);
            new SessionManager(getContext()).setUserPassword(passWord);
            getActivity().finish();
        });
    }

    public void markOnlineStatus(int is_online) {
        onlineStatus = is_online;

        if (is_online == 1) {
            binding.onlineStatus.setText("Online");
            binding.statusSwitch.setChecked(true);
            binding.statusDescription.setText("You are available for video calls now");
        } else {
            binding.onlineStatus.setText("Offline");
            binding.statusSwitch.setChecked(false);
            binding.statusDescription.setText("You are not available for video calls now");
        }
    }


    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }


    public void initialize() {
        cameraPreview = (LinearLayout) binding.svCamera;

        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

       /* capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

        switchCamera = (Button) findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);
   */
    }


    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                try {
                    mCamera = Camera.open(cameraId);
                    mCamera.setDisplayOrientation(90);
                    // mPicture = getPictureCallback();
                    mPreview.refreshCamera(mCamera);
                } catch (Exception e) {
                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        checkLastCallData();
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }


    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setOrientationHint(270);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/com.zeeplive.app/myvideo.mp4");
        mediaRecorder.setMaxDuration(100000); //set maximum duration 10 sec.
        mediaRecorder.setMaxFileSize(10000000); //set maximum file size 50M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}