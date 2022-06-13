package com.zeeplive.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zeeplive.app.R;
import com.zeeplive.app.activity.ChatActivity;
import com.zeeplive.app.activity.ChatEmployeeActivity;
import com.zeeplive.app.activity.MainActivity;
import com.zeeplive.app.adapter.ChatlistAdapter;
import com.zeeplive.app.helper.ItemClickSupport;
import com.zeeplive.app.helper.NetworkCheck;
import com.zeeplive.app.response.Chat.ChatList;
import com.zeeplive.app.response.Chat.RequestChatList;
import com.zeeplive.app.response.Chat.ResultChatList;
import com.zeeplive.app.response.ChatRoom.RequestChatRoom;
import com.zeeplive.app.response.ChatRoom.ResultChatRoom;
import com.zeeplive.app.response.Friend.Friend;
import com.zeeplive.app.retrofit.ApiClient;
import com.zeeplive.app.retrofit.ApiClientChat;
import com.zeeplive.app.retrofit.ApiInterface;
import com.zeeplive.app.socketmodel.Socketuserid;
import com.zeeplive.app.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageEmployeeFragment extends Fragment {
    private View globelView;
    MainActivity navigationActivity;
    private NetworkCheck networkCheck;
    private BroadcastReceiver mBroadcastReceiver;
    private Context mContext;
    int scount = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {

                String action = intent.getAction();
                if ("REFRESH_DATA".equals(action)) {
                    page = "1";
                    getChatList1();
                }
            }
        };

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("REFRESH_DATA");
        localBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);

    }

    @Override
    public void onDetach() {
        super.onDetach();

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.unregisterReceiver(mBroadcastReceiver);

        mContext = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        globelView = inflater.inflate(R.layout.fragment_messageemployee, container, false);
        navigationActivity = (MainActivity) getActivity();


        setTextfontsFunction();
        networkCheck = new NetworkCheck();
        initControls();
        getChatList();
        loadLoader();

        //initSocket();
        return globelView;
    }

    private void loadLoader() {
        Glide.with(getContext())
                .load(R.drawable.loader)
                .into((ImageView) globelView.findViewById(R.id.img_loader));
    }

    private RecyclerView rv_chatlist;
    private ChatlistAdapter chatlistAdapter;
    //private FriendlistAdapter friendlistAdapter;
    private LinearLayoutManager linearLayoutManager;

    private ArrayList<ChatList> chatListArrayList;
    private ArrayList<Friend> friendArrayList = new ArrayList<>();
    private String page = "2";
    private boolean isLoading = true, nxtPageMsg = false;

    private void getChatList() {
        chatListArrayList = new ArrayList<>();
        try {
            chatListArrayList = navigationActivity.chatListArrayList;
            nxtPageMsg = navigationActivity.nxtPageMsg;

            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_chatlist.setLayoutManager(linearLayoutManager);
           /* chatlistAdapter = new ChatlistAdapter(chatListArrayList, R.layout.rv_messagelist, getContext());
            rv_chatlist.setAdapter(chatlistAdapter);
            getChatListagain();
*/
            if (chatListArrayList.size() > 0) {
                chatlistAdapter = new ChatlistAdapter(chatListArrayList, R.layout.rv_messagelist, getContext());
                rv_chatlist.setAdapter(chatlistAdapter);
                getChatListagain();
            }
        } catch (Exception e) {

        }
    }

    private void getChatList1() {
        if (networkCheck.isNetworkAvailable(getContext())) {
            ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

            //RequestChatList requestChatList = new RequestChatList(Integer.parseInt(new SessionManager(getContext()).getUserId()));
            RequestChatList requestChatList = new RequestChatList(Long.parseLong(new SessionManager(getContext()).getUserId()));

            Map<String, String> data = new HashMap<>();
            data.put("page", page);
            //    Log.e("pageNo: ", page);

            Call<ResultChatList> call = apiservice.getChatList(data, requestChatList);
            call.enqueue(new Callback<ResultChatList>() {
                @Override
                public void onResponse(Call<ResultChatList> call, Response<ResultChatList> response) {
                    //     Log.e("onChatListPage: ", new Gson().toJson(response.body()));

                    try {
                        nxtPageMsg = response.body().getPaging().isNextPage();
                        //     Log.e("nxtPageMsg: ", String.valueOf(nxtPageMsg));

                        isLoading = true;

                        chatListArrayList.clear();
                        chatListArrayList.addAll(response.body().getData());
                        int totalunread = 0;
                        for (int i = 0; i < chatListArrayList.size(); i++) {
                            //   Log.e("TAGmsgCount", totalunread + "onResponse: ");
                            //   Log.e("unread", String.valueOf(chatListArrayList.get(i).getUnread()));
                            totalunread = totalunread + chatListArrayList.get(i).getUnread();
                        }
                        navigationActivity.updateMessageCount(totalunread);

                        chatlistAdapter.notifyDataSetChanged();

                        if (chatListArrayList.size() == 0) {
                            ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setVisibility(View.VISIBLE);
                            ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setText("No chat list here.");
                        }
                    } catch (Exception e) {
                        ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setVisibility(View.VISIBLE);
                        ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setText("No chat list here.");

                    }
                    ((ImageView) globelView.findViewById(R.id.img_loader)).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResultChatList> call, Throwable t) {
                    //      Log.e("onChatListError: ", t.getMessage());
                    ((ImageView) globelView.findViewById(R.id.img_loader)).setVisibility(View.GONE);

                }
            });
        }

    }


    public void getChatListagain() {
        if (networkCheck.isNetworkAvailable(getContext())) {
            ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

            //RequestChatList requestChatList = new RequestChatList(Integer.parseInt(new SessionManager(getContext()).getUserId()));
            RequestChatList requestChatList = new RequestChatList(Long.parseLong(new SessionManager(getContext()).getUserId()));

            Map<String, String> data = new HashMap<>();
            data.put("page", page);

            Call<ResultChatList> call = apiservice.getChatList(data, requestChatList);
            //Log.e("user_id: ", String.valueOf(SharedPrefManager.getInstance(getContext()).getUser().getId()));
            call.enqueue(new Callback<ResultChatList>() {
                @Override
                public void onResponse(Call<ResultChatList> call, Response<ResultChatList> response) {
                    //  Log.e("onChatList: ", new Gson().toJson(response.body()));
                    try {
                        nxtPageMsg = response.body().getPaging().isNextPage();
                        isLoading = true;


                        if (page.equals("1")) {
                            chatListArrayList.clear();
                        }
                        //    chatListArrayList.addAll(response.body().getData());
                        int totalunread = 0;
                        for (int i = 0; i < chatListArrayList.size(); i++) {
                            //      Log.e("TAGmsgCount", totalunread + "onResponse: ");
                            //   Log.e("unread", String.valueOf(chatListArrayList.get(i).getUnread()));
                            totalunread = totalunread + chatListArrayList.get(i).getUnread();
                        }
                        navigationActivity.updateMessageCount(totalunread);

//                        chatlistAdapter.notifyDataSetChanged();
                        chatlistAdapter.addAll(response.body().getData());
                        if (chatListArrayList.size() == 0) {
                            ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setVisibility(View.VISIBLE);
                            ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setText("No chat list here.");
                        }
                    } catch (Exception e) {
                        ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setVisibility(View.VISIBLE);
                        ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setText("No chat list here.");

                    }
                    ((ImageView) globelView.findViewById(R.id.img_loader)).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResultChatList> call, Throwable t) {
                    //    Log.e("onChatListError: ", t.getMessage());
                    ((ImageView) globelView.findViewById(R.id.img_loader)).setVisibility(View.GONE);
                }
            });
        }
    }

    public int friendLastpage = 0;

  /*  private void getFrienfList() {
        friendArrayList = new ArrayList<>();
        try {
            friendArrayList = navigationActivity.friendArrayList;
            friendLastpage = navigationActivity.friendLastpage;
            friendlistAdapter = new FriendlistAdapter(friendArrayList, R.layout.rv_friendlist, getContext());
            rv_chatlist.setAdapter(friendlistAdapter);
        } catch (Exception e) {
        }
        getFriendListagain();
    }*/

    private int friendPage = 1;

   /* public void getFriendListagain() {
        if (networkCheck.isNetworkAvailable(getContext())) {
            ApiInterface apiservice = ApiClient.getClient().create(ApiInterface.class);

            RequestFriendList requestFriendList = new RequestFriendList(2, friendPage);
            Log.e("requestFriendList: ", new Gson().toJson(requestFriendList));
            Call<ResultFriendList> call = apiservice.getFriendList(SharedPrefManager.getInstance(getContext()).getUser().getAuthToken(),
                    "default11", requestFriendList);
            mSwipeRefreshLayout.setRefreshing(true);

            call.enqueue(new Callback<ResultFriendList>() {
                @Override
                public void onResponse(Call<ResultFriendList> call, Response<ResultFriendList> response) {
                    Log.e("onFriendList: ", new Gson().toJson(response.body()));
                    if (friendPage == 1) {
                        friendArrayList = new ArrayList<>();
                    }
                    try {
                        isLoading = false;
                        friendArrayList.addAll(response.body().getData().getFriends());
                        friendlistAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setVisibility(View.VISIBLE);
                        ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setText("No chat list here.");

                    }
                    mSwipeRefreshLayout.setRefreshing(false);

                }

                @Override
                public void onFailure(Call<ResultFriendList> call, Throwable t) {
                    Log.e("onFirentListError: ", t.getMessage());
                    mSwipeRefreshLayout.setRefreshing(false);

                }
            });

        }
    }*/


    private void initControls() {

        rv_chatlist = globelView.findViewById(R.id.rv_chatlist);
        mSwipeRefreshLayout = (SwipeRefreshLayout) globelView.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.TRANSPARENT);

        try {
            Field f = mSwipeRefreshLayout.getClass().getDeclaredField("mCircleView");
            f.setAccessible(true);
            ImageView img = (ImageView) f.get(mSwipeRefreshLayout);

            Glide.with(this)
                    .load(R.drawable.loader)
                    .into(img);
            //    img.setImageResource(R.drawable.loader);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        mSwipeRefreshLayout.setEnabled(false);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                friendPage = 1;
                //          getFriendListagain();
                //mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        rv_chatlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (isLoading) {
                    if ((visibleItemCount + lastVisibleItemPosition) >= totalItemCount
                            && lastVisibleItemPosition >= 0) {

                        if (nxtPageMsg && scount == 0) {
                            int numPage = Integer.parseInt(page);
                            numPage = numPage + 1;
                            page = String.valueOf(numPage);
                            isLoading = false;
                            ((ImageView) globelView.findViewById(R.id.img_loader)).setVisibility(View.VISIBLE);
                            getChatListagain();
                            //      Toast.makeText(getContext(), page + "", Toast.LENGTH_SHORT).show();
                        } else {
                            if (scount == 1 && friendPage < friendLastpage) {
                                isLoading = false;
                                friendPage = friendPage + 1;
                                mSwipeRefreshLayout.setRefreshing(true);
                                //                getFriendListagain();
                            }
//friend list code
                        }
                    } else {
                        //              Toast.makeText(getContext(), "EOF", Toast.LENGTH_SHORT).show();
                    }
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });


        ((TextView) globelView.findViewById(R.id.tv_chat)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/POPPINS-SEMIBOLD_0.TTF"));

      /*  ((RelativeLayout) globelView.findViewById(R.id.rr_messages)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((TextView) globelView.findViewById(R.id.tv_nochatmsg)).getVisibility() == View.VISIBLE) {
                    ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setVisibility(View.GONE);
                }
                ((TextView) globelView.findViewById(R.id.tv_messages)).setTextColor(Color.parseColor("#ff3587"));
                ((TextView) globelView.findViewById(R.id.tv_friends)).setTextColor(Color.parseColor("#ffffff"));
                ((View) globelView.findViewById(R.id.v_messages)).setVisibility(View.VISIBLE);
                ((View) globelView.findViewById(R.id.v_friends)).setVisibility(View.GONE);
                scount = 0;
                mSwipeRefreshLayout.setEnabled(false);
                getChatList();
            }
        });

        ((RelativeLayout) globelView.findViewById(R.id.rr_friends)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((TextView) globelView.findViewById(R.id.tv_nochatmsg)).getVisibility() == View.VISIBLE) {
                    ((TextView) globelView.findViewById(R.id.tv_nochatmsg)).setVisibility(View.GONE);
                }
                ((TextView) globelView.findViewById(R.id.tv_messages)).setTextColor(Color.parseColor("#ffffff"));
                ((TextView) globelView.findViewById(R.id.tv_friends)).setTextColor(Color.parseColor("#ff3587"));
                ((View) globelView.findViewById(R.id.v_messages)).setVisibility(View.GONE);
                ((View) globelView.findViewById(R.id.v_friends)).setVisibility(View.VISIBLE);
                scount = 1;
                mSwipeRefreshLayout.setEnabled(true);
                //      getFrienfList();
            }
        });*/

        // //((RecyclerView) globelView.findViewById(R.id.rv_chatlist))
        ItemClickSupport.addTo(rv_chatlist).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
                //startActivity(new Intent(getActivity(), ChatActivity.class));
                if (networkCheck.isNetworkAvailable(getContext())) {
                    ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

                    try {
                        RequestChatRoom requestChatRoom = new RequestChatRoom("FSAfsafsdf", Integer.parseInt(new SessionManager(getContext()).getUserId()),
                                new SessionManager(getContext()).getUserName(), "fasfsdfds",
                                "1", chatListArrayList.get(position).getChattingWith().getId(),
                                chatListArrayList.get(position).getChattingWith().getName(),
                                chatListArrayList.get(position).getChattingWith().getImage(),
                                chatListArrayList.get(position).getChattingWith().getUserType());

                        Call<ResultChatRoom> chatRoomCall = apiservice.createChatRoom("application/json", requestChatRoom);
               /* Log.e("id ", "FSAfsafsdf");
                Log.e("userid ", String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUser().getId()));
                Log.e("username ", SharedPrefManager.getInstance(getApplicationContext()).getUser().getName());
                Log.e("userImage ", "fasfsdfds");
                Log.e("inType ", "1");
                Log.e("EmployeeId ", employeeId);
                Log.e("EmployeeName ", employeeName);
                Log.e("EmployeeImage ", employeeProfileImage);
                Log.e("inType ", "2");*/

                        chatRoomCall.enqueue(new Callback<ResultChatRoom>() {
                            @Override
                            public void onResponse(Call<ResultChatRoom> call, Response<ResultChatRoom> response) {
                                //       Log.e("onResponseChatRoom: ", new Gson().toJson(response.body()));
                                try {
                                    if (!response.body().getData().getId().equals("")) {
                                        startActivity(new Intent(getActivity(), ChatEmployeeActivity.class)
                                                .putExtra("converID", response.body().getData().getId())
                                                .putExtra("ProPic", chatListArrayList.get(position).getChattingWith().getImage())
                                                .putExtra("recName", chatListArrayList.get(position).getChattingWith().getName())
                                                .putExtra("isAdmin", String.valueOf(position))
                                                .putExtra("recProfilePic", chatListArrayList.get(position).getChattingWith().getImage())
                                                .putExtra("recID", String.valueOf(chatListArrayList.get(position).getChattingWith().getId())));

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultChatRoom> call, Throwable t) {
                                //         Log.e("onResponseChatRoom: ", t.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        RequestChatRoom requestChatRoom = new RequestChatRoom("FSAfsafsdf", Integer.parseInt(new SessionManager(getContext()).getUserId()),
                                new SessionManager(getContext()).getUserName(), "fasfsdfds",
                                "1", friendArrayList.get(position).getId(),
                                friendArrayList.get(position).getUserName(),
                                friendArrayList.get(position).getProfilePhoto(),
                                friendArrayList.get(position).getUserType());

                        Call<ResultChatRoom> chatRoomCall = apiservice.createChatRoom("application/json", requestChatRoom);

                        chatRoomCall.enqueue(new Callback<ResultChatRoom>() {
                            @Override
                            public void onResponse(Call<ResultChatRoom> call, Response<ResultChatRoom> response) {
                                //         Log.e("onResponseChatRoom: ", new Gson().toJson(response.body()));
                                try {
                                    if (!response.body().getData().getId().equals("")) {
                                        startActivity(new Intent(getActivity(), ChatEmployeeActivity.class)
                                                .putExtra("converID", response.body().getData().getId())
                                                .putExtra("ProPic", friendArrayList.get(position).getProfilePhoto())
                                                .putExtra("recName", friendArrayList.get(position).getUserName())
                                                .putExtra("recProfilePic", friendArrayList.get(position).getProfilePhoto())
                                                .putExtra("recID", String.valueOf(friendArrayList.get(position).getId())));

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultChatRoom> call, Throwable t) {
                                //         Log.e("onResponseChatRoom: ", t.getMessage());
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setTextfontsFunction() {
        ((TextView) globelView.findViewById(R.id.tv_messages)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/POPPINS-BOLD_0.TTF"));
        ((TextView) globelView.findViewById(R.id.tv_friends)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/POPPINS-BOLD_0.TTF"));
    }

    private Socket socket;

    private void initSocket() {

        try {
            socket = IO.socket(ApiClient.SOCKET_URL);
            socket.connect();
            Socketuserid socketuserid = new Socketuserid(Integer.parseInt(new SessionManager(getContext()).getUserId()));
            socket.emit("login", new Gson().toJson(socketuserid));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //    Log.e("connectError: ", e.getMessage());
        }


        socket.on("message.get", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                     /*   Log.e("socketMessage:", "Message Rec");
                        Log.e("socketRecData:", new Gson().toJson(args[0]));
*/
                        JSONObject data = (JSONObject) args[0];
                        try {

                            //    Log.e("MessRecData:", data.getString("data"));
                            JSONObject data2 = data.getJSONObject("data");
                            //      Log.e("from id:", data2.getString("from"));
                            //    Log.e("from message:", data2.getString("message"));
                            String body = data2.getString("message");
                            if (!body.equals("")) {
                                page = "1";
                                getChatList1();
                                //    getChatListagain();
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
    public void onPause() {
        socket.off("message.get");
        super.onPause();
    }

    @Override
    public void onResume() {
        initSocket();
        super.onResume();
    }
}