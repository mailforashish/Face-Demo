package com.zeeplive.app.activity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zeeplive.app.R;
import com.zeeplive.app.adapter.GiftAdapter;
import com.zeeplive.app.adapter.MessageAdapter;
import com.zeeplive.app.empadapter.MessageEmployeeAdapter;
import com.zeeplive.app.helper.ItemClickSupport;
import com.zeeplive.app.helper.NetworkCheck;
import com.zeeplive.app.helper.VisualizerView;
import com.zeeplive.app.response.Report.ReportData;
import com.zeeplive.app.response.Report.ResultReportIssue;
import com.zeeplive.app.response.gift.Gift;
import com.zeeplive.app.response.gift.ResultGift;
import com.zeeplive.app.response.message.Message;
import com.zeeplive.app.response.message.Message_;
import com.zeeplive.app.response.message.RequestAllMessages;
import com.zeeplive.app.response.message.RequestMessageRead;
import com.zeeplive.app.response.message.ResultMessage;
import com.zeeplive.app.response.message.ResultMessageRead;
import com.zeeplive.app.response.message.ResultSendMessage;
import com.zeeplive.app.retrofit.ApiClient;
import com.zeeplive.app.retrofit.ApiClientChat;
import com.zeeplive.app.retrofit.ApiInterface;
import com.zeeplive.app.socketmodel.SocketSendMessage;
import com.zeeplive.app.socketmodel.Socketuserid;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatEmployeeActivity extends AppCompatActivity {
    private String converID = "";
    private String proPic = "";
    private String reciverId = "";
    private String reciverName = "";
    private String reciverProfilePic = "";
    private MessageEmployeeAdapter messageAdapter;
    private ListView messagesView;
    private NetworkCheck networkCheck;
    private RecyclerView rv_gift;
    private GridLayoutManager gridLayoutManager;
    private GiftAdapter giftAdapter;
    private ArrayList<Gift> giftArrayList = new ArrayList<>();

    RelativeLayout rl_cancelAudio;
    ImageView img_audio;
    private static final String IMAGE_VIEW_TAG = "LAUNCHER LOGO";

    //audioRecord And Visual

    public static final String DIRECTORY_NAME_TEMP = "AudioTemp";
    public static final int REPEAT_INTERVAL = 40;

    VisualizerView visualizerView;

    private MediaRecorder recorder = null;

    File audioDirTemp;
    private boolean isRecording = false;


    private Handler handlerVisual; // Handler for updating the visualizer
    // private boolean recording; // are we curr
    private int inActivity = 0;
    private String userType = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_chatemployee);

        getIntentData();
        initControls();
        //  initSocket();
        loadLoader();
    }

    private void loadLoader() {
        Glide.with(getApplicationContext())
                .load(R.drawable.loader)
                .into((ImageView) findViewById(R.id.img_giftloader));
    }

    private void initSocket() {

        try {
            socket = IO.socket(ApiClient.SOCKET_URL);
            socket.connect();
            Socketuserid socketuserid = new Socketuserid(Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()));
            socket.emit("login", new Gson().toJson(socketuserid));
            //       Log.e("socketStatus: ", "Connected");
        } catch (URISyntaxException e) {
            e.printStackTrace();
     //       Log.e("connectError: ", e.getMessage());
        }

        SocketSendMessage socketSendMessage = new SocketSendMessage(reciverId,
                new SessionManager(getApplicationContext()).getUserId(),
                converID
        );

        socket.emit("message.read", new Gson().toJson(socketSendMessage));

        SocketSendMessage socketSendMessage1 = new SocketSendMessage(reciverId,
                new SessionManager(getApplicationContext()).getUserId()
        );
        socket.emit("friend.online.status", new Gson().toJson(socketSendMessage1));

        socket.on("message.read", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                     /*   Log.e("socketMessage:", "Message Read");
                        Log.e("socketRecData:", new Gson().toJson(args[0]));*/
                    }
                });
            }
        });

        socket.on("friend.online.status", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      /*  Log.e("socketOnline:", "Online Status");
                        Log.e("socketOnlineData:", new Gson().toJson(args[0]));*/
                    }
                });
            }
        });

        socket.on("friend.offline", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                  /*      Log.e("friend.offline:", "Online Status");
                        Log.e("friend.offlineData:", new Gson().toJson(args[0]));*/

                        JSONObject data = (JSONObject) args[0];
                        try {

                      //      Log.e("MessRecData:", data.getString("data"));
                            JSONObject data2 = data.getJSONObject("data");
                            String userID = data2.getString("user_id");
                            if (userID.equals(reciverId)) {

                                ((TextView) findViewById(R.id.tv_userstatus)).setText("offline");
                                ((TextView) findViewById(R.id.tv_userstatus)).setTextColor(getResources().getColor(R.color.colorRedoffline));
                                ((TextView) findViewById(R.id.tv_userstatus)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_offline, 0, 0, 0);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });


        socket.on("message.get", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (((TextView) findViewById(R.id.tv_nochatmsg)).getVisibility() == View.VISIBLE) {
                            ((TextView) findViewById(R.id.tv_nochatmsg)).setVisibility(View.GONE);
                        }

                        /*Log.e("socketMessage:", "Message Rec");
                        Log.e("socketRecData:", new Gson().toJson(args[0]));
*/
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

                            String audioDur = "";
                            try {
                                audioDur = data2.getString("audio_duration");
                            } catch (Exception e) {
                                audioDur = "N/A";
                            }
                            if (!body.equals("")) {
                                if (converID.equals(conversationId)) {
                                    if (inActivity == 0) {
                                        readMessage();

                                        final Message_ message;
                                        message = new Message_(id, body, mimeType, audioDur);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                messageAdapter.add(message);
                                                messagesView.setSelection(messagesView.getCount() - 1);
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (
                                JSONException e) {
                            e.printStackTrace();
  //                          Log.e("socketError:", e.getMessage());

                        }


                    }
                });
            }
        });

    }

    private void readMessage() {
        ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);
        RequestMessageRead requestMessageRead = new RequestMessageRead(Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()), converID);

        Call<ResultMessageRead> call = apiservice.readMessage(requestMessageRead);
        if (networkCheck.isNetworkAvailable(getApplicationContext())) {
            call.enqueue(new Callback<ResultMessageRead>() {
                @Override
                public void onResponse(Call<ResultMessageRead> call, Response<ResultMessageRead> response) {
    //                Log.e("onResponseReadMessage: ", new Gson().toJson(response.body()));
                }

                @Override
                public void onFailure(Call<ResultMessageRead> call, Throwable t) {
      //              Log.e("onErrorReadMessage: ", t.getMessage());

                }
            });
        }

    }

    final int radius = 50;
    final int margin = 10;

    private void getIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            converID = bundle.getString("converID");
            proPic = bundle.getString("ProPic");
            reciverId = bundle.getString("recID");
            reciverName = bundle.getString("recName");
            reciverProfilePic = bundle.getString("recProfilePic");

            try {
                userType = bundle.getString("isAdmin");
                if (userType.equals("0") && reciverName.equals("Zeeplive Official")) {
                    userType = "admin";
                    ((RelativeLayout) findViewById(R.id.rl_bottom)).setVisibility(View.GONE);
                    converID = "5b7aa00db07d21f0ce9f6523";
                } else {
                    userType = "0";
                }
            } catch (Exception e) {

            }

        /*    converID = bundle.getString("converID");
            proPic = "sdssd";
            reciverId = bundle.getString("receiver_id");
            reciverName = bundle.getString("receiver_name");
            reciverProfilePic = bundle.getString("receiver_image");*/

            ((TextView) findViewById(R.id.tv_username)).setText(reciverName);
            Picasso.get().load(proPic)
                    .placeholder(R.drawable.defchat)
                    .error(R.drawable.defchat)
                    .into(((ImageView) findViewById(R.id.img_profile)));

            getAllMessages();

        } else {
            Toast.makeText(this, "Something Went Wrong! Come Back Again", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Message> messageArrayList;
    private ArrayList<Message_> message_arrayList;

    private void getAllMessages() {
        messageArrayList = new ArrayList<Message>();
        message_arrayList = new ArrayList<Message_>();
        ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);
        RequestAllMessages requestAllMessages = new RequestAllMessages(Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()), converID, userType);
        Call<ResultMessage> call = apiservice.getAllMessages(requestAllMessages);

        call.enqueue(new Callback<ResultMessage>() {
            @Override
            public void onResponse(Call<ResultMessage> call, Response<ResultMessage> response) {
             //   Log.e("onResponseAllMessage: ", new Gson().toJson(response.body()));
                try {
                    messageArrayList.addAll(response.body().getData().getMessages());
                    sortArray(messageArrayList);
                    for (int i = 0; i < messageArrayList.size(); i++) {
                        //      Log.e("SortArrayData", String.valueOf(messageArrayList.get(i).getId()));
                        message_arrayList.addAll(messageArrayList.get(i).getMessages());
                    }
                    messageAdapter.notifyDataSetChanged();
                    messagesView.setSelection(messagesView.getCount() - 1);

                } catch (Exception e) {
                    ((TextView) findViewById(R.id.tv_nochatmsg)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tv_nochatmsg)).setText("No message's here.");
                }
            }

            @Override
            public void onFailure(Call<ResultMessage> call, Throwable t) {
             //   Log.e("onResponseAllMessage: ", t.getMessage());

            }
        });

    }

    private void sortArray(ArrayList<Message> arraylist) {
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //your own date format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); //your own date format
        Collections.sort(arraylist, new Comparator<Message>() {
            @Override
            public int compare(Message message, Message t1) {
                try {
                    return simpleDateFormat.parse(message.getId()).compareTo(simpleDateFormat.parse(t1.getId()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }

        });
    }

    private Socket socket;

    private void initControls() {
        networkCheck = new NetworkCheck();
        readMessage();
        Context context;
        context = this;
        messageAdapter = new MessageEmployeeAdapter(context, message_arrayList);
        messagesView = (ListView) findViewById(R.id.lv_allmessages);
        messagesView.setAdapter(messageAdapter);

        rv_gift = findViewById(R.id.rv_gift);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, LinearLayoutManager.HORIZONTAL, false);
        rv_gift.setLayoutManager(gridLayoutManager);
        giftAdapter = new GiftAdapter(giftArrayList, R.layout.rv_gift, getApplicationContext());
        rv_gift.setAdapter(giftAdapter);

        ((ImageView) findViewById(R.id.img_send)).setEnabled(false);
        ((ImageView) findViewById(R.id.img_send)).setImageDrawable(getResources().getDrawable(R.drawable.inactivedownloadarrow));

        ((RelativeLayout) findViewById(R.id.rl_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ChatEmployeeActivity.this);
                Intent broadcastIntent = new Intent("REFRESH_DATA");
                localBroadcastManager.sendBroadcast(broadcastIntent);
                finish();
            }
        });

        ((ImageView) findViewById(R.id.img_video)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                Call<ResultSendMessage> call = apiservice.callRequest("FSAfsafsdf",
                        converID, new SessionManager(getApplicationContext()).getUserId(),
                        new SessionManager(getApplicationContext()).getUserName(), "fasfsdfds", "1", reciverId, reciverName, reciverProfilePic,
                        "2", "callrequest", "1", "videocall");

                call.enqueue(new Callback<ResultSendMessage>() {
                    @Override
                    public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
               //         Log.e("onResponseCallReq: ", new Gson().toJson(response.body()));
                        final Message_ message = new Message_(new SessionManager(getApplicationContext()).getUserId(),
                                "callrequest", "videocall");

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
                 //       Log.e("onErrorCallReq: ", t.getMessage());

                    }
                });

            }
        });

        ((TextView) findViewById(R.id.tv_nochatmsg)).setTypeface(Typeface.createFromAsset(this.getAssets(),
                "fonts/Poppins-Regular_0.ttf"));
        ((TextView) findViewById(R.id.tv_audiomessage)).setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(),
                "fonts/POPPINS-SEMIBOLD_0.TTF"));

        ((EditText) findViewById(R.id.et_message)).setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(),
                "fonts/Poppins-Regular_0.ttf"));

        ((EditText) findViewById(R.id.et_message)).addTextChangedListener(new TextWatcher() {
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

        ((EditText) findViewById(R.id.et_message)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //do what you want on the press of 'done'
                ((ImageView) findViewById(R.id.img_send)).performClick();
                return true;
            }
        });

        ((ImageView) findViewById(R.id.img_send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        "fasfsdfds");
                RequestBody senderType = RequestBody.create(MediaType.parse("text/plain"),
                        "2");
                RequestBody receiverId = RequestBody.create(MediaType.parse("text/plain"),
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
                        conversationId, id, name_1, senderProfilePic, senderType, receiverId, receiverName, receiverImageUrl,
                        receiverType, body, isFriendAccept, mimeType);

                call.enqueue(new Callback<ResultSendMessage>() {
                    @Override
                    public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
                   //     Log.e("onResponseSendMessage: ", new Gson().toJson(response.body()));
                        try {
                            if (response.body().getData().get(0).getStatus().equals("sent")) {

                                SocketSendMessage socketSendMessage = new SocketSendMessage(
                                        new SessionManager(getApplicationContext()).getUserId(),
                                        reciverId, converID, new SessionManager(getApplicationContext()).getUserName(),
                                        ((EditText) findViewById(R.id.et_message)).getText().toString().trim(),
                                        new SessionManager(getApplicationContext()).getUserProfilepic(), "html/plain", "2"
                                );
                                socket.emit("message.send", new Gson().toJson(socketSendMessage));
                     //           Log.e("socketMessageSend:", new Gson().toJson(socketSendMessage));

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
                                if (((TextView) findViewById(R.id.tv_nochatmsg)).getVisibility() == View.VISIBLE) {
                                    ((TextView) findViewById(R.id.tv_nochatmsg)).setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResultSendMessage> call, Throwable t) {
                //        Log.e("onResponseSendMessagE: ", t.getMessage());
                        if (t instanceof SocketTimeoutException) {
                            // "Connection Timeout";
                            Log.e("Connection Timeout: ", "Connection Timeout");
                            //       ((ImageView) findViewById(R.id.img_send)).performClick();
                        } else if (t instanceof IOException) {
                            // "Timeout";
                            Log.e("Connection Timeout: ", "Timeout");

                        } else {
                            //Call was cancelled by user
                            if (call.isCanceled()) {
                                System.out.println("Call was cancelled forcefully");
                                Log.e("Connection Timeout: ", "Call was cancelled forcefully");

                            } else {
                                //Generic error handling
                                // System.out.println("Network Error :: " + error.getLocalizedMessage());
                                Log.e("Connection Timeout: ", "Network Error :: " + t.getLocalizedMessage());

                            }
                        }
                    }
                });

            }
        });

        initKeyBoardListener();

        messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (((RelativeLayout) findViewById(R.id.rl_bottom)).getVisibility() == View.VISIBLE) {
                    //    ((RelativeLayout) findViewById(R.id.rl_bottom)).setVisibility(View.GONE);
                    hideKeybaord(view);
                }
                if (((RelativeLayout) findViewById(R.id.rl_gift)).getVisibility() == View.VISIBLE) {
                    ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.GONE);
                    messagesView.setVisibility(View.VISIBLE);
                }
            }
        });



    /*    ((RelativeLayout) findViewById(R.id.rl_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                hideKeybaord(view);
                ((RelativeLayout) findViewById(R.id.rl_blockmenu)).setVisibility(View.VISIBLE);
                ((RelativeLayout) findViewById(R.id.rl_blockmenu)).setAnimation(slideUp);
            }
        });

        messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (((RelativeLayout) findViewById(R.id.rl_blockmenu)).getVisibility() == View.VISIBLE) {
                    ((TextView) findViewById(R.id.tv_cancel)).performClick();
                }
                if (((MaterialCardView) findViewById(R.id.mv_report)).getVisibility() == View.VISIBLE) {
                    ((Button) findViewById(R.id.btn_cancelreport)).performClick();
                }
            }
        });

        ((TextView) findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                ((RelativeLayout) findViewById(R.id.rl_blockmenu)).setVisibility(View.GONE);
                ((RelativeLayout) findViewById(R.id.rl_blockmenu)).setAnimation(slideDown);

            }
        });

        ((TextView) findViewById(R.id.tv_report)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((RelativeLayout) findViewById(R.id.rl_blockmenu)).setVisibility(View.GONE);

                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                ((MaterialCardView) findViewById(R.id.mv_report)).setVisibility(View.VISIBLE);
                ((MaterialCardView) findViewById(R.id.mv_report)).setAnimation(slideUp);
            }
        });

        ((TextView) findViewById(R.id.tv_block)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((RelativeLayout) findViewById(R.id.rl_blockmenu)).setVisibility(View.GONE);
                ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);
                RequestBlockUnclock requestBlockUnclock = new RequestBlockUnclock("block", converID,
                        String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUser().getId()), reciverId,
                        String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUser().getId()));

                Call<ResultBlockUnblock> call = apiservice.blockUnblockUser(SharedPrefManager.getInstance(getApplicationContext()).getUser().getAuthToken(),
                        "default11", requestBlockUnclock);
                if (networkCheck.isNetworkAvailable(getApplicationContext())) {
                    call.enqueue(new Callback<ResultBlockUnblock>() {
                        @Override
                        public void onResponse(Call<ResultBlockUnblock> call, Response<ResultBlockUnblock> response) {
                            Log.e("onBlock: ", new Gson().toJson(response.body()));
                            try {
                                if (response.body().getType().equals("error")) {
                                    Toast.makeText(ChatActivity.this,
                                            response.body().getErrors().get(0), Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(ChatActivity.this,
                                            response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
                                Toast.makeText(ChatActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<ResultBlockUnblock> call, Throwable t) {
                            Log.e("onFailure: ", t.getMessage());
                            Toast.makeText(ChatActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        ((Button) findViewById(R.id.btn_cancelreport)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                ((MaterialCardView) findViewById(R.id.mv_report)).setVisibility(View.GONE);
                ((MaterialCardView) findViewById(R.id.mv_report)).setAnimation(slideDown);
            }
        });

        ((Button) findViewById(R.id.btn_reportreport)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                ((MaterialCardView) findViewById(R.id.mv_report)).setVisibility(View.GONE);
                ((MaterialCardView) findViewById(R.id.mv_report)).setAnimation(slideDown);

                ApiInterface apiservice = ApiClient.getClient().create(ApiInterface.class);
                RequestReport requestReport = new RequestReport(issueId, Integer.parseInt(reciverId));

                Call<ResultSendFriendRequest> call = apiservice.reportUser(SharedPrefManager.getInstance(getApplicationContext()).getUser().getAuthToken(),
                        "default11", requestReport);

                if (networkCheck.isNetworkAvailable(getApplicationContext())) {
                    call.enqueue(new Callback<ResultSendFriendRequest>() {
                        @Override
                        public void onResponse(Call<ResultSendFriendRequest> call, Response<ResultSendFriendRequest> response) {
                            try {
                                Toast.makeText(ChatActivity.this,
                                        response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(ChatActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultSendFriendRequest> call, Throwable t) {
                            Toast.makeText(ChatActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        ((RadioButton) findViewById(R.id.rdo1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issueId = reportDataArrayList.get(0).getId();
            }
        });

        ((RadioButton) findViewById(R.id.rdo2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issueId = reportDataArrayList.get(1).getId();
            }
        });

        ((RadioButton) findViewById(R.id.rdo3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issueId = reportDataArrayList.get(2).getId();
            }
        });

        ((RadioButton) findViewById(R.id.rdo4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issueId = reportDataArrayList.get(3).getId();
            }
        });
*/
        //     loadReportIssueData();

        EmojiconEditText emojiconEditText = findViewById(R.id.et_message);
        ImageView img_smile = findViewById(R.id.img_smile);
        EmojIconActions emojIcon;
        View rootView = findViewById(R.id.root_view);

        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, img_smile);
        emojIcon.setIconsIds(R.drawable.ic_keyboard, R.drawable.smile);
        emojIcon.setUseSystemEmoji(true);

        img_smile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojIcon.ShowEmojIcon();
            }
        });


        ((ImageView) findViewById(R.id.img_gift)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeybaord(view);


                if (((RelativeLayout) findViewById(R.id.rl_gift)).getVisibility() == View.GONE) {
                    ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.VISIBLE);
                    ApiInterface apiservice = ApiClient.getClient().create(ApiInterface.class);
                    String authToken = Constant.BEARER + new SessionManager(getApplicationContext()).getUserToken();
                    Call<ResultGift> call = apiservice.getGift(authToken);
                    if (networkCheck.isNetworkAvailable(getApplicationContext())) {
                        call.enqueue(new Callback<ResultGift>() {
                            @Override
                            public void onResponse(Call<ResultGift> call, Response<ResultGift> response) {
               //                 Log.e("onGift: ", new Gson().toJson(response.body()));
                                if (response.body().isStatus()) {
                                    //                                   ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.VISIBLE);
                                    ((ImageView) findViewById(R.id.img_giftloader)).setVisibility(View.GONE);
                                    giftArrayList.clear();
                                    giftArrayList.addAll(response.body().getResult());
                                    giftAdapter.notifyDataSetChanged();

                                    if (((TextView) findViewById(R.id.tv_nochatmsg)).getVisibility() == View.VISIBLE) {
                                        ((TextView) findViewById(R.id.tv_nochatmsg)).setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultGift> call, Throwable t) {
                //                Log.e("onErrorGift: ", t.getMessage());

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
                ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                Call<ResultSendMessage> call = apiservice.callRequest("FSAfsafsdf",
                        converID, new SessionManager(getApplicationContext()).getUserId(),
                        new SessionManager(getApplicationContext()).getUserName(), "fasfsdfds", "2",
                        reciverId, reciverName, reciverProfilePic,
                        "1", "giftrequest", "1", "image/giftrequest");


                call.enqueue(new Callback<ResultSendMessage>() {
                    @Override
                    public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
                  //      Log.e("onResponseCallReq: ", new Gson().toJson(response.body()));
                        final Message_ message = new Message_(new SessionManager(getApplicationContext()).getUserId(),
                                "giftrequest", "image/giftrequest");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageAdapter.add(message);
                                messagesView.setSelection(messagesView.getCount() - 1);
                            }
                        });

                        if (((TextView) findViewById(R.id.tv_nochatmsg)).getVisibility() == View.VISIBLE) {
                            ((TextView) findViewById(R.id.tv_nochatmsg)).setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onFailure(Call<ResultSendMessage> call, Throwable t) {
                   //     Log.e("onErrorCallReq: ", t.getMessage());

                    }
                });
            }
        });

        img_audio = findViewById(R.id.img_audio);

        rl_cancelAudio = findViewById(R.id.rl_cancelAudio);
        img_audio.setTag("audio");


        img_audio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //    img_audio.setImageDrawable(getResources().getDrawable(R.drawable.gift));

                ((RelativeLayout) findViewById(R.id.rl_visualizer)).setVisibility(View.VISIBLE);

                ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data//data to be dragged
                        , shadowBuilder //drag shadow
                        , view//local data about the drag and drop operation
                        , 0//no needed flags
                );
                img_audio.setVisibility(View.GONE);
                rl_cancelAudio.setVisibility(View.VISIBLE);

                if (!isRecording) {
                    // isRecording = true;

                    recorder = new MediaRecorder();

                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(audioDirTemp + "/audio_file"
                            + ".mp3");

                    MediaRecorder.OnErrorListener errorListener = null;
                    recorder.setOnErrorListener(errorListener);
                    MediaRecorder.OnInfoListener infoListener = null;
                    recorder.setOnInfoListener(infoListener);

                    try {
                        recorder.prepare();
                        recorder.start();
                        isRecording = true; // we are currently recording
                    } catch (IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }
                    handlerVisual.post(updateVisualizer);
                    StartTime = SystemClock.uptimeMillis();
                    handlerTimer.postDelayed(runnableTimer, 0);
                    ((TextView) findViewById(R.id.tv_audiomessage)).setVisibility(View.VISIBLE);

                }

                return true;
            }
        });


        rl_cancelAudio.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                int action = dragEvent.getAction();
                // Handles each of the expected events
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Determines if this View can accept the dragged data
                        if (dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            // if you want to apply color when drag started to your view you can uncomment below lines
                            // to give any color tint to the View to indicate that it can accept
                            // data.

                            //  view.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);//set background color to your view

                            // Invalidate the view to force a redraw in the new tint
                            //  view.invalidate();

                            // returns true to indicate that the View can accept the dragged data.
                            return true;

                        }

                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        return false;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        // Applies a YELLOW or any color tint to the View, when the dragged view entered into drag acceptable view
                        // Return true; the return value is ignored.

//                view.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);

                        // Invalidate the view to force a redraw in the new tint
                        //              view.invalidate();
                        ((TextView) findViewById(R.id.tv_audiomessage)).setText("Let go to cancel");
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        // Ignore the event
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        // Re-sets the color tint to blue, if you had set the BLUE color or any color in ACTION_DRAG_STARTED. Returns true; the return value is ignored.

                        //  view.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

                        //If u had not provided any color in ACTION_DRAG_STARTED then clear color filter.
//                view.getBackground().clearColorFilter();
                        // Invalidate the view to force a redraw in the new tint
                        //              view.invalidate();
                        ((TextView) findViewById(R.id.tv_audiomessage)).setText("Swipe up to cancel, let go to send");

                        return true;
                    case DragEvent.ACTION_DROP:
                        // Gets the item containing the dragged data
                        ClipData.Item item = dragEvent.getClipData().getItemAt(0);

                        // Gets the text data from the item.
                        String dragData = item.getText().toString();

                        // Displays a message containing the dragged data.
                        //    Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_SHORT).show();

                        // Turns off any color tints
//                view.getBackground().clearColorFilter();

                        // Invalidates the view to force a redraw
                        //              view.invalidate();

                        //            View v = (View) event.getLocalState();
//                v.setVisibility(View.VISIBLE);//finally set Visibility to VISIBLE

                        // Returns true. DragEvent.getResult() will return true.
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        // Turns off any color tinting
//                view.getBackground().clearColorFilter();

                        // Invalidates the view to force a redraw
                        //              view.invalidate();

                        img_audio.setImageDrawable(getResources().getDrawable(R.drawable.audio));
                        rl_cancelAudio.setVisibility(View.GONE);
                        img_audio.setVisibility(View.VISIBLE);
                        ((RelativeLayout) findViewById(R.id.rl_visualizer)).setVisibility(View.GONE);
                        releaseRecorder();
                        if (handlerTimer != null) {
                            handlerTimer.removeCallbacks(runnableTimer);
                        }
                        ((TextView) findViewById(R.id.tv_audiomessage)).setVisibility(View.GONE);

                        // Does a getResult(), and displays what happened.
                        if (dragEvent.getResult()) {
                        } else {
                            sendAudio();
                        }

                        // returns true; the value is ignored.
                        return true;

                    // An unknown action type was received.
                    default:
                  //      Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                        break;
                }

                return false;
            }
        });

        visualizerView = (VisualizerView)

                findViewById(R.id.visualizer);


        audioDirTemp = new

                File(Environment.getExternalStorageDirectory(),

                DIRECTORY_NAME_TEMP);
        if (audioDirTemp.exists()) {
            deleteFilesInDir(audioDirTemp);
        } else {
            audioDirTemp.mkdirs();
        }

        // create the Handler for visualizer update
        handlerVisual = new

                Handler();

        handlerTimer = new

                Handler();


    }

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    int Hours, Seconds, Minutes, MilliSeconds;
    private String stringAudioDuration;

    Handler handlerTimer;

    public Runnable runnableTimer = new Runnable() {

        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Hours = Minutes / 60;
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);

            stringAudioDuration = String.format("%02d", Minutes) + ":"
                    + String.format("%02d", Seconds);

            handlerTimer.postDelayed(this, 0);
        }

    };


    private ArrayList<ReportData> reportDataArrayList = new ArrayList<>();
    private int issueId;

    /*private void loadReportIssueData() {
        ApiInterface apiservice = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultReportIssue> call = apiservice.getReportIssues(SharedPrefManager.getInstance(getApplicationContext()).getUser().getAuthToken(), "default11");

        if (networkCheck.isNetworkAvailable(getApplicationContext())) {
            call.enqueue(new Callback<ResultReportIssue>() {
                @Override
                public void onResponse(Call<ResultReportIssue> call, Response<ResultReportIssue> response) {
                    Log.e("onReportIss: ", new Gson().toJson(response.body()));
                    try {
                        reportDataArrayList.addAll(response.body().getData());
                        issueId = reportDataArrayList.get(0).getId();
                        ((RadioButton) findViewById(R.id.rdo1)).setText(reportDataArrayList.get(0).getIssue());
                        ((RadioButton) findViewById(R.id.rdo2)).setText(reportDataArrayList.get(1).getIssue());
                        ((RadioButton) findViewById(R.id.rdo3)).setText(reportDataArrayList.get(2).getIssue());
                        ((RadioButton) findViewById(R.id.rdo4)).setText(reportDataArrayList.get(3).getIssue());
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailure(Call<ResultReportIssue> call, Throwable t) {
                    Log.e("onReportIssError: ", t.getMessage());

                }
            });
        }
    }
*/

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
                   //     Log.e("Keyboard", "SHOW");
                        ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.GONE);
                        ((ImageView) findViewById(R.id.img_video)).setVisibility(View.GONE);
                        ((ImageView) findViewById(R.id.img_send)).setVisibility(View.VISIBLE);
                        messagesView.setSelection(messagesView.getCount() - 1);
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                  //      Log.e("Keyboard", "HIDE");
                        ((ImageView) findViewById(R.id.img_send)).setVisibility(View.GONE);
                        ((ImageView) findViewById(R.id.img_video)).setVisibility(View.VISIBLE);

                    }
                }
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight;
            }
        });
    }

    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        initSocket();

        SocketSendMessage socketSendMessage1 = new SocketSendMessage(reciverId,
                new SessionManager(getApplicationContext()).getUserId());
        socket.emit("friend.online.status", new Gson().toJson(socketSendMessage1));

        socket.on("friend.online", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    /*    Log.e("friend.online:", "Online Status");
                        Log.e("friend.online:", new Gson().toJson(args[0]));
                    */    JSONObject data = (JSONObject) args[0];
                        try {

                     //       Log.e("MessRecData:", data.getString("data"));
                            JSONObject data2 = data.getJSONObject("data");
                            String userID = data2.getString("user_id");
                            if (userID.equals(reciverId)) {

                                ((TextView) findViewById(R.id.tv_userstatus)).setText("online");
                                ((TextView) findViewById(R.id.tv_userstatus)).setTextColor(getResources().getColor(R.color.colorGreen));
                                ((TextView) findViewById(R.id.tv_userstatus)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_online, 0, 0, 0);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
        super.onResume();
    }


    private void releaseRecorder() {
        try {
            if (recorder != null) {
                isRecording = false; // stop recording
                handlerVisual.removeCallbacks(updateVisualizer);
                visualizerView.clear();
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteFilesInDir(File path) {

        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {

                if (files[i].isDirectory()) {

                } else {
                    files[i].delete();
                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inActivity = 2;
        // socket.off("disconnecting");
        releaseRecorder();
    }

    // updates the visualizer every 50 milliseconds
    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) // if we are already recording
            {
                // get the current amplitude
                int x = recorder.getMaxAmplitude();
                visualizerView.addAmplitude(x); // update the VisualizeView
                visualizerView.invalidate(); // refresh the VisualizerView

                // update in 40 milliseconds
                handlerVisual.postDelayed(this, REPEAT_INTERVAL);
            }
        }
    };

    private void sendAudio() {

        File audioPath = new File(audioDirTemp + "/audio_file" + ".mp3");

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
                "fasfsdfds");
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

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), audioPath);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("files", audioPath.getName(), requestFile);

        RequestBody isFriendAccept = RequestBody.create(MediaType.parse("text/plain"),
                "1");
        RequestBody audioDuration = RequestBody.create(MediaType.parse("text/plain"),
                stringAudioDuration);


        Call<ResultSendMessage> call = apiservice.sendMessageAudio(UserId,
                conversationId, id, name_1, senderProfilePic, senderType, receiverId, receiverName, receiverImageUrl,
                receiverType, body, isFriendAccept, audioDuration);


        if (networkCheck.isNetworkAvailable(getApplicationContext())) {
            call.enqueue(new Callback<ResultSendMessage>() {
                @Override
                public void onResponse(Call<ResultSendMessage> call, Response<ResultSendMessage> response) {
               //     Log.e("onSendAudio: ", new Gson().toJson(response.body()));

                    SocketSendMessage socketSendMessage = new SocketSendMessage(
                            new SessionManager(getApplicationContext()).getUserId(),
                            reciverId, converID, new SessionManager(getApplicationContext()).getUserName(),
                            response.body().getData().get(0).getBody(),
                            "asd", "audio/mp3", "1", stringAudioDuration
                    );
                    socket.emit("message.send", new Gson().toJson(socketSendMessage));
               //     Log.e("socketMessageSend:", new Gson().toJson(socketSendMessage));
                    final Message_ message = new Message_(new SessionManager(getApplicationContext()).getUserId(),
                            response.body().getData().get(0).getBody(), "audio/mp3", stringAudioDuration);
                    if (!message.getBody().equals("")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageAdapter.add(message);
                                messagesView.setSelection(messagesView.getCount() - 1);
                            }
                        });
                        if (((TextView) findViewById(R.id.tv_nochatmsg)).getVisibility() == View.VISIBLE) {
                            ((TextView) findViewById(R.id.tv_nochatmsg)).setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onFailure(Call<ResultSendMessage> call, Throwable t) {
               //     Log.e("onAudioError: ", t.getMessage());

                }
            });
        }
    }


    @Override
    public void onBackPressed() {

        if (((RelativeLayout) findViewById(R.id.rl_gift)).getVisibility() == View.VISIBLE) {
            ((RelativeLayout) findViewById(R.id.rl_gift)).setVisibility(View.GONE);
        } else {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ChatEmployeeActivity.this);
            Intent broadcastIntent = new Intent("REFRESH_DATA");
            localBroadcastManager.sendBroadcast(broadcastIntent);
            finish();
            super.onBackPressed();
        }
    }
}