package com.zeeplive.app.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zeeplive.app.Config;
import com.zeeplive.app.R;
import com.zeeplive.app.adapter.GiftAdapter;
import com.zeeplive.app.adapter.MessageAdaptervdo;
import com.zeeplive.app.body.CallRecordBody;
import com.zeeplive.app.dialog.WaitingForConnect;
import com.zeeplive.app.empadapter.MessageAdaptervdoEmployee;
import com.zeeplive.app.framework.PreprocessorFaceUnity;
import com.zeeplive.app.helper.ItemClickSupport;
import com.zeeplive.app.helper.NetworkCheck;
import com.zeeplive.app.response.EndCallData.EndCallData;
import com.zeeplive.app.response.Misscall.RequestMissCall;
import com.zeeplive.app.response.Misscall.ResultMissCall;
import com.zeeplive.app.response.WalletBalResponse;
import com.zeeplive.app.response.gift.Gift;
import com.zeeplive.app.response.gift.ResultGift;
import com.zeeplive.app.response.gift.SendGiftRequest;
import com.zeeplive.app.response.gift.SendGiftResult;
import com.zeeplive.app.response.message.Message_;
import com.zeeplive.app.response.message.ResultSendMessage;
import com.zeeplive.app.retrofit.ApiClient;
import com.zeeplive.app.retrofit.ApiClientChat;
import com.zeeplive.app.retrofit.ApiInterface;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.socketmodel.SocketCallOperation;
import com.zeeplive.app.socketmodel.SocketSendMessage;
import com.zeeplive.app.socketmodel.Socketuserid;
import com.zeeplive.app.ui.CameraTextureView;
import com.zeeplive.app.ui.actionsheets.BeautySettingActionSheet;
import com.zeeplive.app.ui.actionsheets.LiveRoomSettingActionSheet;
import com.zeeplive.app.ui.live.LiveBaseActivity;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import io.agora.capture.video.camera.CameraVideoChannel;
import io.agora.capture.video.camera.VideoModule;
import io.agora.framework.modules.channels.ChannelManager;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zeeplive.app.utils.SessionManager.GENDER;
import static java.util.prefs.Preferences.MAX_NAME_LENGTH;

public class VideoChatActivity extends LiveBaseActivity implements View.OnClickListener, TextWatcher,
        BeautySettingActionSheet.BeautyActionSheetListener,
        LiveRoomSettingActionSheet.LiveRoomSettingActionSheetListener, ApiResponseInterface {

    Chronometer chronometer;
    Date callStartTime;
    String gender;

    ////////////////////////////
    private static final String TAG = VideoChatActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;

    // Permission WRITE_EXTERNAL_STORAGE is not mandatory
    // for Agora RTC SDK, just in case if you wanna save
    // logs to external sdcard.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;

    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;

    WaitingForConnect waitingForConnect;
    private static String token, call_rate, reciverId, unique_id, UID;
    ApiManager apiManager;
    int AUTO_END_TIME;
    Handler handler, talkTimeHandler;

    private CameraVideoChannel mCameraChannel;
    private PreprocessorFaceUnity mPreprocessor;
    private int mVideoInitCount;


    // private MediaPlayer mp;

    // Customized logger view
    //  private LoggerRecyclerView mLogView;


    /*   * Event handler registered into RTC engine for RTC callbacks.
     * Note that UI operations needs to be in UI thread because RTC
     * engine deals with the events in a separate thread.*/


    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onJoinChannelSuccess", "Join channel success, uid: " + (uid & 0xFFFFFFFFL));

                }
            });
        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // Cancel handler if receiver picked the call in 25 sec
                    handler.removeCallbacksAndMessages(null);
                    waitingForConnect.dismiss();
                    // mp.stop();
                    //  mLogView.logI("First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    setupRemoteVideo(uid);

                    callStartTime = Calendar.getInstance().getTime();
                    // start a chronometer
                    chronometer.start();

                    if (gender.equals("male")) {
                        apiManager.sendCallRecord(new CallRecordBody(UID, unique_id,
                                new CallRecordBody.Duration(String.valueOf(System.currentTimeMillis()), "")));

                      /*  Log.e("startCallReq", new Gson().toJson(new CallRecordBody(UID, unique_id,
                                new CallRecordBody.Duration(String.valueOf(System.currentTimeMillis()), ""))));*/

                        // Disconnect call when balance ends
                        talkTimeHandler.postDelayed(() -> {
                                    endCall();
                                    Toast.makeText(VideoChatActivity.this, "Out of Balance", Toast.LENGTH_LONG).show();
                                }
                                , AUTO_END_TIME);
                    }
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //   mLogView.logI("User offline, uid: " + (uid & 0xFFFFFFFFL));
                    onRemoteUserLeft();

                    endCall();
                    Toast.makeText(VideoChatActivity.this, "Call Disconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private void setupRemoteVideo(int uid) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        int count = mRemoteContainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = mRemoteContainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }

        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);

        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteView.setTag(uid);
    }

    private void onRemoteUserLeft() {
        removeRemoteVideo();
    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer.removeView(mRemoteView);
        }
        mRemoteView = null;
    }

    private MessageAdaptervdo messageAdapter;
    private MessageAdaptervdoEmployee messageAdaptervdoEmployee;
    private ListView messagesView;
    private Socket socket;

    private RecyclerView rv_gift;
    private GridLayoutManager gridLayoutManager;
    private GiftAdapter giftAdapter;
    private ArrayList<Gift> giftArrayList = new ArrayList<>();
    private ArrayList<Message_> message_arrayList = new ArrayList<>();
    private NetworkCheck networkCheck;
    private int fPosition;
    private String reciverName = "";
    private String reciverProfilePic = "";
    private String converID = "";

    // for gift deduction
    private String startLong = "", giftLong = "";
    private AppCompatImageView mBeautyBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.Black));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        networkCheck = new NetworkCheck();
        setContentView(R.layout.activity_video_chat);

     /*   mp = MediaPlayer.create(this, R.raw.let_me_love_you);
        mp.start();*/

        HashMap<String, String> user = new SessionManager(this).getUserDetails();
        gender = user.get(GENDER);


        Long tsLong = System.currentTimeMillis() / 1000;
        startLong = tsLong.toString();

        initUI();

        try {
            apiManager = new ApiManager(this, this);
            token = getIntent().getStringExtra("TOKEN");
            reciverId = getIntent().getStringExtra("ID");
            call_rate = getIntent().getStringExtra("CALL_RATE");
            unique_id = getIntent().getStringExtra("UNIQUE_ID");
            AUTO_END_TIME = getIntent().getIntExtra("AUTO_END_TIME", 2000);

            reciverName = getIntent().getStringExtra("receiver_name");
            reciverProfilePic = getIntent().getStringExtra("receiver_image");
            converID = getIntent().getStringExtra("converID");
            try {
                UID = getIntent().getStringExtra("UID");
           /* Log.e("UID", UID);
            Log.e("call_rate", call_rate);*/
            } catch (Exception e) {
            }
           /* Log.e("TOKEN", token);
            Log.e("reciverId", reciverId);
            Log.e("unique_id", unique_id);
            Log.e("AUTO_END", AUTO_END_TIME + "");
            Log.e("reciverName", reciverName);
            Log.e("reciverProfilePic", reciverProfilePic);
            Log.e("converID", converID);*/


            if (converID.equals("") || converID.equals("null")) {
                ((RelativeLayout) findViewById(R.id.rl_chat)).setVisibility(View.GONE);
                ((RelativeLayout) findViewById(R.id.rl_giftin)).setVisibility(View.GONE);
            }


            StringBuilder sb = new StringBuilder(reciverName);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));

            ((TextView) findViewById(R.id.tv_username)).setText(sb.toString());

            ((TextView) findViewById(R.id.tv_username)).setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(),
                    "fonts/Poppins-Regular_0.ttf"));


            //if (gender.equals("male")) {}else {
            Picasso.get()
                    .load(reciverProfilePic)
                    .into(((ImageView) findViewById(R.id.img_profilepic)));
        } catch (Exception e) {

        }


        //  Log.d("TokenAg : ", token);

        waitingForConnect = new WaitingForConnect(this, reciverProfilePic, reciverName);
        handler = new Handler();
        talkTimeHandler = new Handler();


        // End call if receiver not taking the call in 25 seconds
        handler.postDelayed(() -> {
            waitingForConnect.dismiss();

            SocketCallOperation socketCallOperation = new SocketCallOperation(new SessionManager(getApplicationContext()).getUserId(), reciverId);
            socket.emit("misscall", new Gson().toJson(socketCallOperation));
            try {
                ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);
                RequestMissCall requestMissCall = new RequestMissCall(Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()),
                        Integer.parseInt(reciverId), converID, "missedvideocall");
                Call<ResultMissCall> call = apiservice.sendMissCallData(requestMissCall);
                if (networkCheck.isNetworkAvailable(getApplicationContext())) {
                    call.enqueue(new Callback<ResultMissCall>() {
                        @Override
                        public void onResponse(Call<ResultMissCall> call, Response<ResultMissCall> response) {
                            //   Log.e("onResponceMissApi", new Gson().toJson(response.body()));
                        }

                        @Override
                        public void onFailure(Call<ResultMissCall> call, Throwable t) {
                            //  Log.e("onErrorMissApi", t.getMessage());
                        }
                    });
                }

                endCall();
                Toast.makeText(this, "Not answering the call", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }

        }, 20000);

        // Ask for permissions at runtime.
        // This is just an example set of permissions. Other permissions
        // may be needed, and please refer to our online documents.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }
        new ApiManager(this, this).getWalletAmount2();
        loadLoader();


    }

    private void loadLoader() {
        Glide.with(getApplicationContext())
                .load(R.drawable.loader)
                .into((ImageView) findViewById(R.id.img_giftloader));
    }

    private void initUI() {
        chronometer = findViewById(R.id.chronometer);
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);
        mBeautyBtn = findViewById(R.id.live_prepare_beauty_btn);
        mBeautyBtn.setOnClickListener(this);

        if (gender.equals("male")) {
            mSwitchCameraBtn.setVisibility(View.GONE);
        } else {
            mSwitchCameraBtn.setVisibility(View.GONE);
        }
        //  mLogView = findViewById(R.id.log_recycler_view);

        // Sample logs are optional.
        showSampleLogs();

        ((ImageView) findViewById(R.id.img_send)).setEnabled(false);
        ((ImageView) findViewById(R.id.img_send)).setImageDrawable(getResources().getDrawable(R.drawable.inactivedownloadarrow));

        ((RelativeLayout) findViewById(R.id.rl_chat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RelativeLayout) findViewById(R.id.rl_bottom)).setVisibility(View.VISIBLE);
                ((RelativeLayout) findViewById(R.id.rl_msgsend)).setVisibility(View.VISIBLE);
                ((RelativeLayout) findViewById(R.id.rl_end)).setVisibility(View.VISIBLE);

/*
                if (gender.equals("male")) {

                } else {
                    ((RelativeLayout) findViewById(R.id.rl_bottom)).setVisibility(View.VISIBLE);
                }
*/

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(((EditText) findViewById(R.id.et_message)).getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                ((EditText) findViewById(R.id.et_message)).requestFocus();
            }
        });

        mRemoteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((RelativeLayout) findViewById(R.id.rl_bottom)).getVisibility() == View.VISIBLE) {
                    ((RelativeLayout) findViewById(R.id.rl_bottom)).setVisibility(View.GONE);
                    hideKeybaord(view);
                }
                if (((RelativeLayout) findViewById(R.id.rl_gift)).getVisibility() == View.VISIBLE) {
                    ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.GONE);
                    messagesView.setVisibility(View.VISIBLE);
                }
            }
        });


        initKeyBoardListener();
        Context context;
        context = this;
        messageAdapter = new MessageAdaptervdo(context, message_arrayList);
        messageAdaptervdoEmployee = new MessageAdaptervdoEmployee(context, message_arrayList);
        messagesView = (ListView) findViewById(R.id.lv_allmessages);

        messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mRemoteContainer.performClick();

            }
        });

        if (gender.equals("male")) {
            messagesView.setAdapter(messageAdapter);
        } else {
            messagesView.setAdapter(messageAdaptervdoEmployee);
        }

        rv_gift = findViewById(R.id.rv_gift);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, LinearLayoutManager.HORIZONTAL, false);
        rv_gift.setLayoutManager(gridLayoutManager);
        giftAdapter = new GiftAdapter(giftArrayList, R.layout.rv_gift, getApplicationContext());
        rv_gift.setAdapter(giftAdapter);


        ((RelativeLayout) findViewById(R.id.rl_giftin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //     if (gender.equals("male")) {
                messagesView.setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.img_gift)).performClick();
             /*   } else {
                    ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                    Call<ResultSendMessage> call = apiservice.callRequest("FSAfsafsdf",
                            converID, new SessionManager(getApplicationContext()).getUserId(),
                            "user name", "fasfsdfds", "1", reciverId, reciverName, new SessionManager(getApplicationContext()).getUserProfilepic(),
                            "2", "giftrequest", "1", "image/giftrequest");

                    call.enqueue(new Callback<ResultSendMessage>() {
                        @Override
                        public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
                            Log.e("onResponseCallReq: ", new Gson().toJson(response.body()));
                            final Message_ message = new Message_(new SessionManager(getApplicationContext()).getUserId(),
                                    "giftrequest", "image/giftrequest");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageAdapter.add(message);
                                    messagesView.setSelection(messagesView.getCount() - 1);
                                }
                            });

                        }

                        @Override
                        public void onFailure(Call<ResultSendMessage> call, Throwable t) {
                            Log.e("onErrorCallReq: ", t.getMessage());

                        }
                    });
                }*/
            }
        });

        ((ImageView) findViewById(R.id.img_gift)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeybaord(view);

                if (((RelativeLayout) findViewById(R.id.rl_gift)).getVisibility() == View.GONE) {
                    ((RelativeLayout) findViewById(R.id.rl_bottom)).setVisibility(View.VISIBLE);
                    ((RelativeLayout) findViewById(R.id.rl_msgsend)).setVisibility(View.GONE);
                    ((RelativeLayout) findViewById(R.id.rl_end)).setVisibility(View.GONE);
                    ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.VISIBLE);
                    ApiInterface apiservice = ApiClient.getClient().create(ApiInterface.class);
                    String authToken = Constant.BEARER + new SessionManager(getApplicationContext()).getUserToken();
                    Call<ResultGift> call = apiservice.getGift(authToken);
                    if (networkCheck.isNetworkAvailable(getApplicationContext())) {
                        call.enqueue(new Callback<ResultGift>() {
                            @Override
                            public void onResponse(Call<ResultGift> call, Response<ResultGift> response) {
                                //        Log.e("onGift: ", new Gson().toJson(response.body()));

                                if (response.body().isStatus()) {
                                   /* ((RelativeLayout) findViewById(R.id.rl_bottom)).setVisibility(View.VISIBLE);
                                    ((RelativeLayout) findViewById(R.id.rl_msgsend)).setVisibility(View.GONE);
                                    ((RelativeLayout) findViewById(R.id.rl_end)).setVisibility(View.GONE);
                                   */
                                    ((ImageView) findViewById(R.id.img_giftloader)).setVisibility(View.GONE);

//                                    ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.VISIBLE);
                                    giftArrayList.clear();
                                    giftArrayList.addAll(response.body().getResult());
                                    giftAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultGift> call, Throwable t) {
                                //         Log.e("onErrorGift: ", t.getMessage());

                            }
                        });
                    }

                } else {
                    ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.GONE);
                }
            }
        });

        ItemClickSupport.addTo(rv_gift).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                if (gender.equals("male")) {
                    int currentCoin = Integer.parseInt(((TextView) findViewById(R.id.tv_coinchat)).getText().toString());
                    // currentCoin = currentCoin - giftArrayList.get(position).getAmount();
                    if (currentCoin > giftArrayList.get(position).getAmount()) {
                        fPosition = position;
                        new ApiManager(getApplicationContext(), VideoChatActivity.this).sendUserGift(new SendGiftRequest(Integer.parseInt(reciverId),
                                giftArrayList.get(position).getId(), giftArrayList.get(position).getAmount()));



                 /*   ResponceDataguestLogin responceDataguestLogin = new ResponceDataguestLogin(String.valueOf(currentCoin));
                    SharedPrefManager.getInstance(getApplicationContext()).updatePurchasedMinutes(responceDataguestLogin);*/

                        ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                        RequestBody UserId = RequestBody.create(MediaType.parse("text/plain"),
                                "FSAfsafsdf");
                        RequestBody conversationId = RequestBody.create(MediaType.parse("text/plain"),
                                converID);
                        RequestBody id = RequestBody.create(MediaType.parse("text/plain"),
                                new SessionManager(getApplicationContext()).getUserId());
                        RequestBody name_1 = RequestBody.create(MediaType.parse("text/plain"),
                                new SessionManager(getApplicationContext()).getUserName());
                        RequestBody senderProfilePic = RequestBody.create(MediaType.parse("text/plain"),
                                new SessionManager(getApplicationContext()).getUserProfilepic());
                        RequestBody senderType = RequestBody.create(MediaType.parse("text/plain"),
                                "1");
                        RequestBody receiverId = RequestBody.create(MediaType.parse("text/plain"),
                                reciverId);
                        RequestBody receiverName = RequestBody.create(MediaType.parse("text/plain"),
                                reciverName);
                        RequestBody receiverImageUrl = RequestBody.create(MediaType.parse("text/plain"),
                                reciverProfilePic);
                        RequestBody receiverType = RequestBody.create(MediaType.parse("text/plain"),
                                "2");
                        RequestBody body = RequestBody.create(MediaType.parse("text/plain"),
                                giftArrayList.get(position).getGiftPhoto());
                        RequestBody isFriendAccept = RequestBody.create(MediaType.parse("text/plain"),
                                "1");
                        RequestBody mimeType = RequestBody.create(MediaType.parse("text/plain"),
                                "image/gift");
                        RequestBody giftCoins = RequestBody.create(MediaType.parse("text/plain"),
                                String.valueOf(giftArrayList.get(position).getAmount()));

                        Call<ResultSendMessage> call = apiservice.sendMessageGift(UserId,
                                conversationId, id, name_1, senderProfilePic, senderType, receiverId, receiverName, receiverImageUrl,
                                receiverType, body, isFriendAccept, giftCoins, mimeType);

                        call.enqueue(new Callback<ResultSendMessage>() {
                            @Override
                            public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
                                //  Log.e("onResponseSendMessage: ", new Gson().toJson(response.body()));
                                if (((RelativeLayout) findViewById(R.id.rl_gift)).getVisibility() == View.VISIBLE) {
                                    ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.GONE);
                                    messagesView.setVisibility(View.VISIBLE);

                              /*      int talkTime = giftArrayList.get(position).getAmount() * 1000 * 60;
                                    // Minus 2 sec to prevent balance goes into minus
                                    int canCallTill = AUTO_END_TIME - talkTime;

                                    Long tsLong = System.currentTimeMillis() / 1000;
                                    giftLong = tsLong.toString();


                                    Log.e("talkTimeVideo", talkTime + "");
                                    Log.e("canCallTillVideo", canCallTill + "");
                                    Log.e("startTimestamp", startLong);
                                    Log.e("giftTimestamp", giftLong);
                                    Log.e("AUTO_END_TIME", AUTO_END_TIME+"");

                                    long first, second;
                                    first = Long.parseLong(startLong);
                                    second = Long.parseLong(giftLong);

                                    Log.e("TimestampDiff", String.valueOf((second - first) * 1000));

                                    startLong = giftLong;
                                    canCallTill = canCallTill - ((Integer.parseInt(giftLong) - Integer.parseInt(startLong)) * 1000);

                                    AUTO_END_TIME = canCallTill;
                                    Log.e("AUTO_END_TIME2", AUTO_END_TIME+"");

                                    if (talkTimeHandler != null) {
                                        talkTimeHandler.removeCallbacksAndMessages(null);
                                    }

                                    talkTimeHandler.postDelayed(() -> {
                                                Toast.makeText(VideoChatActivity.this, "Out of Balance", Toast.LENGTH_LONG).show();
                                            }
                                            , AUTO_END_TIME);

*/
                                }
                            }


                            @Override
                            public void onFailure(Call<ResultSendMessage> call, Throwable t) {
                                //  Log.e("onResponseSendMessagE: ", t.getMessage());
                            }
                        });
                    }
                } else {
                    ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                    Call<ResultSendMessage> call = apiservice.callRequest("FSAfsafsdf",
                            converID, new SessionManager(getApplicationContext()).getUserId(),
                            new SessionManager(getApplicationContext()).getUserName(), "fasfsdfds", "2",
                            reciverId, reciverName, reciverProfilePic,
                            "1", "giftrequest", "1", "image/giftrequest");


                    call.enqueue(new Callback<ResultSendMessage>() {
                        @Override
                        public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
                            //        Log.e("onResponseCallReq: ", new Gson().toJson(response.body()));
                            final Message_ message = new Message_(new SessionManager(getApplicationContext()).getUserId(),
                                    "giftrequest", "image/giftrequest");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageAdapter.add(message);
                                    messagesView.setSelection(messagesView.getCount() - 1);
                                }
                            });

                            if (((RelativeLayout) findViewById(R.id.rl_gift)).getVisibility() == View.VISIBLE) {
                                ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.GONE);
                                messagesView.setVisibility(View.VISIBLE);
                            }
                        /*    if (((TextView) findViewById(R.id.tv_nochatmsg)).getVisibility() == View.VISIBLE) {
                                ((TextView) findViewById(R.id.tv_nochatmsg)).setVisibility(View.GONE);
                            }
*/
                        }

                        @Override
                        public void onFailure(Call<ResultSendMessage> call, Throwable t) {
                            //      Log.e("onErrorCallReq: ", t.getMessage());

                        }
                    });

                }


            }
        });

        ((EditText) findViewById(R.id.et_message)).

                addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (i2 > 0) {
                            ((ImageView) findViewById(R.id.img_send)).setEnabled(true);
                            ((ImageView) findViewById(R.id.img_send)).setImageDrawable(getResources().getDrawable(R.drawable.activedownloadarrow));
                        } else {
                            ((ImageView) findViewById(R.id.img_send)).setEnabled(false);
                            ((ImageView) findViewById(R.id.img_send)).setImageDrawable(getResources().getDrawable(R.drawable.inactivedownloadarrow));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

        ((ImageView) findViewById(R.id.img_send)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (gender.equals("male")) {
                            if (((EditText) findViewById(R.id.et_message)).toString().trim().length() > 0) {
                                ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                                RequestBody UserId = RequestBody.create(MediaType.parse("text/plain"),
                                        "FSAfsafsdf");
                                RequestBody conversationId = RequestBody.create(MediaType.parse("text/plain"),
                                        converID);
                                RequestBody id = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserId());
                                RequestBody name_1 = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserName());
                                RequestBody senderProfilePic = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserProfilepic());
                                RequestBody senderType = RequestBody.create(MediaType.parse("text/plain"),
                                        "1");
                                RequestBody receiverid = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverId);
                                RequestBody receiverName = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverName);
                                RequestBody receiverImageUrl = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverProfilePic);
                                RequestBody receiverType = RequestBody.create(MediaType.parse("text/plain"),
                                        "2");
                                RequestBody body = RequestBody.create(MediaType.parse("text/plain"),
                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim());
                                RequestBody isFriendAccept = RequestBody.create(MediaType.parse("text/plain"),
                                        "1");
                                RequestBody mimeType = RequestBody.create(MediaType.parse("text/plain"),
                                        "html/plain");

                                Call<ResultSendMessage> call = apiservice.sendMessage(UserId,
                                        conversationId, id, name_1, senderProfilePic, senderType, receiverid, receiverName, receiverImageUrl,
                                        receiverType, body, isFriendAccept, mimeType);

                                call.enqueue(new Callback<ResultSendMessage>() {
                                    @Override
                                    public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
                                        //  Log.e("onResponseSendMessage: ", new Gson().toJson(response.body()));
                                        try {
                                            if (response.body().getData().get(0).getStatus().equals("sent")) {

                                                SocketSendMessage socketSendMessage = new SocketSendMessage(
                                                        new SessionManager(getApplicationContext()).getUserId(),
                                                        reciverId, converID, new SessionManager(getApplicationContext()).getUserName(),
                                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim(),
                                                        new SessionManager(getApplicationContext()).getUserProfilepic(), "html/plain"
                                                );
                                                socket.emit("message.send", new Gson().toJson(socketSendMessage));
                            /*        Log.e("socketMessage:", new Gson().toJson(socketSendMessage));
                                    Log.e("socketMessage:", new Gson().toJson(socketSendMessage));
*/
                                                final Message_ message = new Message_(new SessionManager(getApplicationContext()).getUserId(),
                                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim(), "html/plain");
                                                if (!message.getBody().equals("")) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            messageAdapter.add(message);
                                                            messagesView.setSelection(messagesView.getCount() - 1);
                                                        }
                                                    });
                                                }
                                                ((EditText) findViewById(R.id.et_message)).setText("");
                                            }/* else {
                                Log.e("onResponseSubErr: ", response.body().getErrors().get(0));
                                Toast.makeText(ChatActivity.this,
                                        response.body().getErrors().get(0), Toast.LENGTH_SHORT).show();
                            }*/
                                        } catch (Exception e) {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResultSendMessage> call, Throwable t) {
                                        //         Log.e("onResponseSendMessagE: ", t.getMessage());
                                    }
                                });

                            }
                        } else {
                            if (((EditText) findViewById(R.id.et_message)).toString().trim().length() > 0) {

                                ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                                RequestBody UserId = RequestBody.create(MediaType.parse("text/plain"),
                                        "FSAfsafsdf");
                                RequestBody conversationId = RequestBody.create(MediaType.parse("text/plain"),
                                        converID);
                                RequestBody id = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserId());
                                RequestBody name_1 = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserName());
                                RequestBody senderProfilePic = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserProfilepic());
                                RequestBody senderType = RequestBody.create(MediaType.parse("text/plain"),
                                        "2");
                                RequestBody receiverid = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverId);
                                RequestBody receiverName = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverName);
                                RequestBody receiverImageUrl = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverProfilePic);
                                RequestBody receiverType = RequestBody.create(MediaType.parse("text/plain"),
                                        "1");
                                RequestBody body = RequestBody.create(MediaType.parse("text/plain"),
                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim());
                                RequestBody isFriendAccept = RequestBody.create(MediaType.parse("text/plain"),
                                        "1");
                                RequestBody mimeType = RequestBody.create(MediaType.parse("text/plain"),
                                        "html/plain");

                                Call<ResultSendMessage> call = apiservice.sendMessage(UserId,
                                        conversationId, id, name_1, senderProfilePic, senderType, receiverid, receiverName, receiverImageUrl,
                                        receiverType, body, isFriendAccept, mimeType);

                                call.enqueue(new Callback<ResultSendMessage>() {
                                    @Override
                                    public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
                                        //         Log.e("onResponseSendMessage: ", new Gson().toJson(response.body()));
                                        try {
                                            if (response.body().getData().get(0).getStatus().equals("sent")) {

                                                SocketSendMessage socketSendMessage = new SocketSendMessage(
                                                        new SessionManager(getApplicationContext()).getUserId(),
                                                        reciverId, converID, new SessionManager(getApplicationContext()).getUserName(),
                                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim(),
                                                        new SessionManager(getApplicationContext()).getUserProfilepic(), "html/plain"
                                                );
                                                socket.emit("message.send", new Gson().toJson(socketSendMessage));
                         /*               Log.e("socketMessage:", new Gson().toJson(socketSendMessage));
                                        Log.e("socketMessage:", "Message Sent");
*/
                                                final Message_ message = new Message_(new SessionManager(getApplicationContext()).getUserId(),
                                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim(), "html/plain");
                                                if (!message.getBody().equals("")) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            messageAdapter.add(message);
                                                            messagesView.setSelection(messagesView.getCount() - 1);
                                                        }
                                                    });
                                                }
                                                ((EditText) findViewById(R.id.et_message)).setText("");
                                            }
                                        } catch (Exception e) {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResultSendMessage> call, Throwable t) {
                                        //               Log.e("onResponseSendMessagE: ", t.getMessage());

                                    }
                                });
                            }
                        }
                    }
                });

        ((EditText) findViewById(R.id.et_message)).
                setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        //do what you want on the press of 'done'
                        if (gender.equals("male")) {
                            if (((EditText) findViewById(R.id.et_message)).toString().trim().length() > 0) {
                                ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                                RequestBody UserId = RequestBody.create(MediaType.parse("text/plain"),
                                        "FSAfsafsdf");
                                RequestBody conversationId = RequestBody.create(MediaType.parse("text/plain"),
                                        converID);
                                RequestBody id = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserId());
                                RequestBody name_1 = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserName());
                                RequestBody senderProfilePic = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserProfilepic());
                                RequestBody senderType = RequestBody.create(MediaType.parse("text/plain"),
                                        "1");
                                RequestBody receiverid = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverId);
                                RequestBody receiverName = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverName);
                                RequestBody receiverImageUrl = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverProfilePic);
                                RequestBody receiverType = RequestBody.create(MediaType.parse("text/plain"),
                                        "2");
                                RequestBody body = RequestBody.create(MediaType.parse("text/plain"),
                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim());
                                RequestBody isFriendAccept = RequestBody.create(MediaType.parse("text/plain"),
                                        "1");
                                RequestBody mimeType = RequestBody.create(MediaType.parse("text/plain"),
                                        "html/plain");

                                Call<ResultSendMessage> call = apiservice.sendMessage(UserId,
                                        conversationId, id, name_1, senderProfilePic, senderType, receiverid, receiverName, receiverImageUrl,
                                        receiverType, body, isFriendAccept, mimeType);

                                call.enqueue(new Callback<ResultSendMessage>() {
                                    @Override
                                    public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
                                        //  Log.e("onResponseSendMessage: ", new Gson().toJson(response.body()));
                                        try {
                                            if (response.body().getData().get(0).getStatus().equals("sent")) {

                                                SocketSendMessage socketSendMessage = new SocketSendMessage(
                                                        new SessionManager(getApplicationContext()).getUserId(),
                                                        reciverId, converID, new SessionManager(getApplicationContext()).getUserName(),
                                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim(),
                                                        new SessionManager(getApplicationContext()).getUserProfilepic(), "html/plain"
                                                );
                                                socket.emit("message.send", new Gson().toJson(socketSendMessage));
                            /*        Log.e("socketMessage:", new Gson().toJson(socketSendMessage));
                                    Log.e("socketMessage:", new Gson().toJson(socketSendMessage));
*/
                                                final Message_ message = new Message_(new SessionManager(getApplicationContext()).getUserId(),
                                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim(), "html/plain");
                                                if (!message.getBody().equals("")) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            messageAdapter.add(message);
                                                            messagesView.setSelection(messagesView.getCount() - 1);
                                                        }
                                                    });
                                                }
                                                ((EditText) findViewById(R.id.et_message)).setText("");
                                            }/* else {
                                Log.e("onResponseSubErr: ", response.body().getErrors().get(0));
                                Toast.makeText(ChatActivity.this,
                                        response.body().getErrors().get(0), Toast.LENGTH_SHORT).show();
                            }*/
                                        } catch (Exception e) {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResultSendMessage> call, Throwable t) {
                                        //             Log.e("onResponseSendMessagE: ", t.getMessage());
                                    }
                                });

                            }
                        } else {
                            if (((EditText) findViewById(R.id.et_message)).toString().trim().length() > 0) {

                                ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                                RequestBody UserId = RequestBody.create(MediaType.parse("text/plain"),
                                        "FSAfsafsdf");
                                RequestBody conversationId = RequestBody.create(MediaType.parse("text/plain"),
                                        converID);
                                RequestBody id = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserId());
                                RequestBody name_1 = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserName());
                                RequestBody senderProfilePic = RequestBody.create(MediaType.parse("text/plain"),
                                        new SessionManager(getApplicationContext()).getUserProfilepic());
                                RequestBody senderType = RequestBody.create(MediaType.parse("text/plain"),
                                        "2");
                                RequestBody receiverid = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverId);
                                RequestBody receiverName = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverName);
                                RequestBody receiverImageUrl = RequestBody.create(MediaType.parse("text/plain"),
                                        reciverProfilePic);
                                RequestBody receiverType = RequestBody.create(MediaType.parse("text/plain"),
                                        "1");
                                RequestBody body = RequestBody.create(MediaType.parse("text/plain"),
                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim());
                                RequestBody isFriendAccept = RequestBody.create(MediaType.parse("text/plain"),
                                        "1");
                                RequestBody mimeType = RequestBody.create(MediaType.parse("text/plain"),
                                        "html/plain");

                                Call<ResultSendMessage> call = apiservice.sendMessage(UserId,
                                        conversationId, id, name_1, senderProfilePic, senderType, receiverid, receiverName, receiverImageUrl,
                                        receiverType, body, isFriendAccept, mimeType);

                                call.enqueue(new Callback<ResultSendMessage>() {
                                    @Override
                                    public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
                                        //           Log.e("onResponseSendMessage: ", new Gson().toJson(response.body()));
                                        try {
                                            if (response.body().getData().get(0).getStatus().equals("sent")) {

                                                SocketSendMessage socketSendMessage = new SocketSendMessage(
                                                        new SessionManager(getApplicationContext()).getUserId(),
                                                        reciverId, converID, new SessionManager(getApplicationContext()).getUserName(),
                                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim(),
                                                        new SessionManager(getApplicationContext()).getUserProfilepic(), "html/plain"
                                                );
                                                socket.emit("message.send", new Gson().toJson(socketSendMessage));
                       /*                 Log.e("socketMessage:", new Gson().toJson(socketSendMessage));
                                        Log.e("socketMessage:", "Message Sent");
*/
                                                final Message_ message = new Message_(new SessionManager(getApplicationContext()).getUserId(),
                                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim(), "html/plain");
                                                if (!message.getBody().equals("")) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            messageAdapter.add(message);
                                                            messagesView.setSelection(messagesView.getCount() - 1);
                                                        }
                                                    });
                                                }
                                                ((EditText) findViewById(R.id.et_message)).setText("");
                                            }
                                        } catch (Exception e) {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResultSendMessage> call, Throwable t) {
                                        //                              Log.e("onResponseSendMessagE: ", t.getMessage());

                                    }
                                });
                            }
                        }
                        return true;
                    }
                });


        mCameraChannel = (CameraVideoChannel) VideoModule.instance().
                getVideoChannel(ChannelManager.ChannelID.CAMERA);
        mPreprocessor = (PreprocessorFaceUnity) VideoModule.instance().
                getPreprocessor(ChannelManager.ChannelID.CAMERA);

        mPreprocessor.setOnBundleLoadedListener(this::tryInitializeVideo);
        mPreprocessor.setOnFirstFrameListener(this::tryInitializeVideo);
        config().setBeautyEnabled(true);
        startCameraCapture();
        mPreprocessor.onAnimojiSelected(-1);
        mLocalContainer.addView(new CameraTextureView(this));
        checkFUAuth();
    }

    private void checkFUAuth() {
        if (mPreprocessor != null &&
                !mPreprocessor.FUAuthenticated()) {
            showLongToast(getString(R.string.no_fu_auth_notice));
            //if (tabId == Config.LIVE_TYPE_SINGLE_HOST) {
            // mStartBroadBtn.setEnabled(false);
            // }
        }
    }

    private void tryInitializeVideo() {
        if (mVideoInitCount >= 2) {
            return;
        }

        mVideoInitCount++;
        if (mVideoInitCount == 2) {
            runOnUiThread(() -> mLocalContainer.
                    addView(new CameraTextureView(this)));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(@NotNull Editable editable) {
        if (editable.length() > MAX_NAME_LENGTH) {
            //showShortToast(mNameTooLongToastMsg);
            // mEditText.setText(editable.subSequence(0, MAX_NAME_LENGTH));
            // mEditText.setSelection(MAX_NAME_LENGTH);
        }
    }


    private void showSampleLogs() {
//        mLogView.logI("Welcome to Agora 1v1 video call");
//        mLogView.logW("You will see custom logs here");
//        mLogView.logE("You can also use this to show errors");
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }
    }

    public void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();
        https:
//console.agora.io/invite?sign=YXBwSWQlM0QyYWY5M2VlODZlYWU0OGQxOTA4NGNjODhlOWUzYzkyNCUyNm5hbWUlM0RsaXZlJTI2dGltZXN0YW1wJTNEMTU5MTEwNTU5MCUyNmNoYW5uZWwlM0RkZW1vJTI2dG9rZW4lM0Q%3D

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid asactivity_video_call ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        /*String token = getString(R.string.agora_access_token);*/
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        //   mRtcEngine.joinChannel(token, "zeeplive", "Extra Optional Data", 0);
        mRtcEngine.joinChannel(token, "zeeplive" + unique_id, "Extra Optional Data", 0);
        //  Toast.makeText(this, "zeeplive" + unique_id, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //socket.off("message.get");
        if (!mCallEnd) {
            leaveChannel();
        }
        //mp.stop();
        endCall();
        RtcEngine.destroy();


    }

    @Override
    protected void onPause() {
        super.onPause();
        EndCallData endCallData = new EndCallData(unique_id, String.valueOf(System.currentTimeMillis()));
        list.add(endCallData);
        new SessionManager(getApplicationContext()).setUserEndcalldata(list);
        new SessionManager(getApplicationContext()).setUserGetendcalldata("error");

    }

    private void leaveChannel() {
        try {
            mRtcEngine.leaveChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    public void onCallClicked(View view) {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.btn_endcall);
        } else {

            endCall();

            mCallEnd = true;
            mCallBtn.setImageResource(R.drawable.btn_startcall);
            mCallBtn.setVisibility(View.GONE);
        }

        showButtons(!mCallEnd);

    }

    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }

    private void endCall() {
        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();

        // Calculate call charges accordingly
        getCallDuration(Calendar.getInstance().getTime());
    }

    private void removeLocalVideo() {
        if (mLocalView != null) {
            mLocalContainer.removeView(mLocalView);
        }
        mLocalView = null;
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        // mMuteBtn.setVisibility(visibility);
        //   mSwitchCameraBtn.setVisibility(visibility);
    }


    @Override
    public void onBackPressed() {
        if (((RelativeLayout) findViewById(R.id.rl_bottom)).getVisibility() == View.VISIBLE) {
            ((RelativeLayout) findViewById(R.id.rl_bottom)).setVisibility(View.GONE);
            //hideKeybaord(view);
        }
        if (((RelativeLayout) findViewById(R.id.rl_gift)).getVisibility() == View.VISIBLE) {
            ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.GONE);
            messagesView.setVisibility(View.VISIBLE);
        } else {
            endCall();
        }
    }

    ArrayList<EndCallData> list = new ArrayList<>();

    void getCallDuration(Date endTime) {

        if (callStartTime != null) {
            long mills = endTime.getTime() - callStartTime.getTime();

            int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) (mills / (1000 * 60)) % 60;
            int sec = (int) (mills - hours * 3600000 - mins * 60000) / 1000;


            if (mins < 1 && sec > 1 && sec < 60) {

                apiManager.endCall(new CallRecordBody("", unique_id,
                        new CallRecordBody.Duration("", String.valueOf(System.currentTimeMillis()))));


                if (gender.equals("male")) {
                    endAllApi();
                }
            } else {
                int roundOf = 1;
                int totalMins;

                if (sec > 6) {
                    totalMins = mins + roundOf;
                } else {
                    totalMins = mins;
                }

                apiManager.endCall(new CallRecordBody("", unique_id,
                        new CallRecordBody.Duration("", String.valueOf(System.currentTimeMillis()))));
                if (gender.equals("male")) {
                    endAllApi();
                }
            }
        } else {
            if (talkTimeHandler != null) {
                talkTimeHandler.removeCallbacksAndMessages(null);
            }

            finish();
        }
    }

    private void endAllApi() {
        String duration = chronometer.getText().toString();

        ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

        RequestMissCall requestMissCall = new RequestMissCall(Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()),
                Integer.parseInt(reciverId), converID, "videocall",
                duration, 0, "00000000000");

        //    Log.e("reqEndTime", new Gson().toJson(requestMissCall));

        SocketCallOperation socketCallOperation = new SocketCallOperation(new SessionManager(getApplicationContext()).getUserId(), reciverId);
        socket.emit("callDone", new Gson().toJson(socketCallOperation));


        Call<ResultMissCall> call = apiservice.sendMissCallData(requestMissCall);
        if (networkCheck.isNetworkAvailable(getApplicationContext())) {
            call.enqueue(new Callback<ResultMissCall>() {
                @Override
                public void onResponse(Call<ResultMissCall> call, Response<ResultMissCall> response) {
                    //             Log.e("onResponceCallEnd", new Gson().toJson(response.body()));
                }

                @Override
                public void onFailure(Call<ResultMissCall> call, Throwable t) {
                    //            Log.e("onErrorCallEnd", t.getMessage());
                }
            });
        }
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(this, errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
      /*  if (ServiceCode == Constant.DEDUCT_CALL_CHARGE) {
            WalletRechargeResponse rsp = (WalletRechargeResponse) response;

            finish();
        }*/
        if (ServiceCode == Constant.SEND_CALL_RECORD) {
            Object rsp = response;
        }
        if (ServiceCode == Constant.END_CALL) {
            Object rsp = response;

            if (talkTimeHandler != null) {
                talkTimeHandler.removeCallbacksAndMessages(null);
            }

            chronometer.stop();

            finish();
        }

        if (ServiceCode == Constant.WALLET_AMOUNT2) {
            WalletBalResponse rsp = (WalletBalResponse) response;
            ((TextView) findViewById(R.id.tv_coinchat)).setText(String.valueOf(rsp.getResult().getTotal_point()));
        }

        if (ServiceCode == Constant.SEND_GIFT) {
            SendGiftResult rsp = (SendGiftResult) response;
            ((TextView) findViewById(R.id.tv_coinchat)).setText(String.valueOf(rsp.getResult()));


            try {

                SocketSendMessage socketSendMessage = new SocketSendMessage(
                        new SessionManager(getApplicationContext()).getUserId(),
                        reciverId, converID, new SessionManager(getApplicationContext()).getUserName(),
                        giftArrayList.get(fPosition).getGiftPhoto(),
                        new SessionManager(getApplicationContext()).getUserProfilepic(), "image/gift"
                );
                socket.emit("message.send", new Gson().toJson(socketSendMessage));
                //  Log.e("socketMessage:", new Gson().toJson(socketSendMessage));

                final Message_ message = new Message_(new SessionManager(getApplicationContext()).getUserId()
                        , giftArrayList.get(fPosition).getGiftPhoto(), "image/jpeg");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.add(message);
                        messagesView.setSelection(messagesView.getCount() - 1);
                    }
                });

                long tsLong = System.currentTimeMillis() / 1000;
                giftLong = Long.toString(tsLong);

                int didu = ((Integer.parseInt(giftLong) - Integer.parseInt(startLong)) / 60);
                didu = didu + 1;
                didu = didu * Integer.parseInt(call_rate);
                didu = rsp.getResult() - didu;
                //didu = didu - 2000;
                int talkTime = didu / Integer.parseInt(call_rate) * 1000 * 60;

                Log.e("AUTO_END_TIME2", AUTO_END_TIME + "");

                AUTO_END_TIME = talkTime;
                //  startLong = giftLong;

                Log.e("TimestampDiff", didu + "");
                Log.e("walletBalance", rsp.getResult() + "");
                Log.e("AUTO_END_TIME", AUTO_END_TIME + "");

                if (talkTimeHandler != null) {
                    talkTimeHandler.removeCallbacksAndMessages(null);
                }

                talkTimeHandler.postDelayed(() -> {
                            Toast.makeText(VideoChatActivity.this, "Out of Balance", Toast.LENGTH_LONG).show();
                            endCall();
                        }
                        , AUTO_END_TIME);


            } catch (Exception e) {

            }

        }
    }


    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    private void initKeyBoardListener() {
        // Threshold for minimal keyboard height.
        final int MIN_KEYBOARD_HEIGHT_PX = 150;
        // Top-level window decor view.
        final View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            // Retrieve visible rectangle inside window.
            private final Rect windowVisibleDisplayFrame = new Rect();
            private int lastVisibleDecorViewHeight;

            @Override
            public void onGlobalLayout() {
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        //             Log.e("Keyboard", "SHOW");
                        messagesView.setSelection(messagesView.getCount() - 1);
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        //           Log.e("Keyboard", "HIDE");
                        ((RelativeLayout) findViewById(R.id.rl_bottom)).setVisibility(View.GONE);
                    }
                }
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight;
            }
        });


    }

    @Override
    protected void onResume() {
        initSocket();
        super.onResume();
    }

    public void initSocket() {
        try {
            socket = IO.socket(ApiClient.SOCKET_URL);
            socket.connect();
            Socketuserid socketuserid = new Socketuserid(Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()));
            socket.emit("login", new Gson().toJson(socketuserid));
            //        Log.e("SocVDOcallStatus: ", new SessionManager(getApplicationContext()).getUserId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //  Log.e("connectError: ", e.getMessage());
        }

        socket.on("message.get", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*       Log.e("socketMessage:", "Message Rec");

                         */
                        //        Log.e("socketRecData:", new Gson().toJson(args[0]));
                        JSONObject data = (JSONObject) args[0];
                        try {

                            //      Log.e("socketData:", data.getString("data"));
                            JSONObject data2 = data.getJSONObject("data");
                            //      Log.e("from id:", data2.getString("from"));
                            //    Log.e("from message:", data2.getString("message"));
                            String body = data2.getString("message");
                            String id = data2.getString("from");
                            String mimeType = data2.getString("mimeType");
                            String conversationId = data2.getString("conversationId");

                            if (!body.equals("")) {
                                if (converID.equals(conversationId)) {
                                    final Message_ message = new Message_(id, body, mimeType);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (gender.equals("male")) {
                                                messageAdapter.add(message, reciverProfilePic);
                                            } else {
                                                messageAdaptervdoEmployee.add(message, reciverProfilePic);
                                            }
                                            messagesView.setSelection(messagesView.getCount() - 1);
                                        }
                                    });

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //   Log.e("socketError:", e.getMessage());
                        }


                    }
                });
            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.live_prepare_close:
                onBackPressed();
                break;
            case R.id.live_prepare_switch_camera:
                break;
            case R.id.live_prepare_go_live_btn:
                break;
            case R.id.live_prepare_beauty_btn:
                showActionSheetDialog(ACTION_SHEET_BEAUTY, true, this);
                break;
            case R.id.live_prepare_setting_btn:
                showActionSheetDialog(ACTION_SHEET_VIDEO, true, this);
                break;
        }
    }


    @Override
    public void onActionSheetBeautyEnabled(boolean enabled) {
        findViewById(R.id.live_prepare_beauty_btn).setActivated(enabled);
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
    public void onActionSheetResolutionSelected(int index) {
        config().setResolutionIndex(index);
    }

    @Override
    public void onActionSheetFrameRateSelected(int index) {
        config().setFrameRateIndex(index);
    }

    @Override
    public void onActionSheetBitrateSelected(int bitrate) {
        config().setVideoBitrate(bitrate);
    }

    @Override
    public void onActionSheetSettingBackPressed() {

    }

}