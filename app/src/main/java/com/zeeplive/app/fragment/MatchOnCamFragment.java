package com.zeeplive.app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.transition.Fade;
import androidx.transition.Transition;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieDrawable;
import com.google.gson.Gson;
import com.zeeplive.app.R;
import com.zeeplive.app.activity.MatchVideoChatActivity;
import com.zeeplive.app.activity.VideoChatActivity;
import com.zeeplive.app.databinding.FragmentMatchOnCamBinding;
import com.zeeplive.app.dialog.InsufficientCoins;
import com.zeeplive.app.dialog.MatchDialog;
import com.zeeplive.app.helper.NetworkCheck;
import com.zeeplive.app.response.Call.ResultCall;
import com.zeeplive.app.response.ChatRoom.RequestChatRoom;
import com.zeeplive.app.response.ChatRoom.ResultChatRoom;
import com.zeeplive.app.response.RemainingGiftCard.RemainingGiftCardResponce;
import com.zeeplive.app.response.UserListResponse;
import com.zeeplive.app.response.WalletBalResponse;
import com.zeeplive.app.retrofit.ApiClientChat;
import com.zeeplive.app.retrofit.ApiInterface;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;


public class MatchOnCamFragment extends Fragment implements ApiResponseInterface {
    FragmentMatchOnCamBinding binding;
    ApiManager apiManager;
    int currentPage;
    boolean isHeartSplashing;
    Transition transition;
    int current_users;
    List<UserListResponse.Data> list;
    List<UserListResponse.Data> matchList;
    int currentPages = 1;
    int walletBalance;
    private String convId = "";
    int userId;
    int callRate = 25;
    private NetworkCheck networkCheck;
    private boolean success;
    private int remGiftCard = 0;
    private String freeSeconds;
    private String callType = "";
    int onlineStatusRandom;
    int busyStatusRandom;
    CountDownTimer countDownTimer;
    private String value;



    public MatchOnCamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_match_on_cam, container, false);
        isHeartSplashing = false;
        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //creating transition
        transition = new Fade();
        transition.setDuration(300);
        networkCheck = new NetworkCheck();
        apiManager = new ApiManager(getContext(), this);

        binding.oncamInstructionsMatch.pumpingHeartNew.playAnimation();
        binding.oncamInstructionsMatch.circularWavesNew.startRippleAnimation();
        binding.oncamInstructionsMatch.pumpingHeartNew.setRepeatCount(LottieDrawable.INFINITE);

        binding.oncamInstructionsMatch.btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatchDialog matchDialog = new MatchDialog(getContext());
                //new MatchDialog(getContext());
                matchDialog.setDialogResult(new MatchDialog.OnMyDialogResult() {
                    public void finish(String result) {
                        // now you can use the 'result' on your activity
                        Log.e("MatchHostList", "search9 " + result);
                    }
                });
            }
        });

        // create instance of Random class
        Random rand = new Random();
        //generate a random integer from 0 to 899, then add 100
        int current_users = rand.nextInt(900) + 2000;
        FetchGirlsData();
        showUsers(current_users);


        //MatchDialog dialog = new MatchDialog(getActivity(),value);
        //initialization stuff, blah blah


    }

    private void FetchGirlsData() {
        apiManager.matchHostList();
    }


    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.GET_REMAINING_GIFT_CARD) {
            RemainingGiftCardResponce rsp = (RemainingGiftCardResponce) response;
            try {
                try {
                    success = rsp.getSuccess();
                    remGiftCard = rsp.getTotalGift();
                    freeSeconds = rsp.getFreeSecond();
                    if (remGiftCard > 0) {
                       // Log.e("MatchHostList", "search1 " + "hii");
                        apiManager.searchUser(String.valueOf(list.get(0).getProfile_id()), "1");
                        return;
                    }
                } catch (Exception e) {
                }
                if (new SessionManager(getApplicationContext()).getUserWallet() >= callRate) {
                    //Log.e("MatchHostList", "search2 " + "hii");
                    apiManager.searchUser(String.valueOf(matchList.get(0).getProfile_id()), "1");
                } else {
                    new InsufficientCoins(getActivity(), 2, callRate);
                }
            } catch (Exception e) {
                //Log.e("MatchHostList", "search3 " + "hii");
                apiManager.searchUser(String.valueOf(list.get(0).getProfile_id()), "1");
            }

        } else if (ServiceCode == Constant.WALLET_AMOUNT) {
            WalletBalResponse rsp = (WalletBalResponse) response;
            // if wallet balance greater than call rate , Generate token to make a call
            if (rsp.getResult().getTotal_point() >= callRate) {
                walletBalance = rsp.getResult().getTotal_point();
                if (networkCheck.isNetworkAvailable(getApplicationContext())) {
                    ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                apiManager.showDialog();
                            } catch (Exception e) {
                            }
                        }
                    }, 100);
                    RequestChatRoom requestChatRoom = new RequestChatRoom("FSAfsafsdf",
                            Integer.parseInt(new SessionManager(getApplicationContext()).getUserId()),
                            new SessionManager(getApplicationContext()).getUserName(),
                            "ProfilePhoto", "1", matchList.get(0).getProfile_id(),
                            matchList.get(0).getName(), matchList.get(0).getProfile_images().get(0).getImage_name(),
                            "2", 0, callRate, 0, 20, "",
                            "countrtStstic", String.valueOf(userId));

                    Call<ResultChatRoom> chatRoomCall = apiservice.createChatRoom("application/json", requestChatRoom);

                    chatRoomCall.enqueue(new Callback<ResultChatRoom>() {
                        @Override
                        public void onResponse(Call<ResultChatRoom> call, Response<ResultChatRoom> response) {
                            //Log.e("onResponseRoom: ", new Gson().toJson(response.body()));
                            //Log.e("MatchHostList", "search3 " + new Gson().toJson(response.body()));

                            try {
                                apiManager.closeDialog();
                            } catch (Exception e) {
                            }
                            try {
                                if (!response.body().getData().getId().equals("")) {
                                    convId = response.body().getData().getId();
                                    apiManager.generateAgoraToken(userId, String.valueOf(System.currentTimeMillis()), convId);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultChatRoom> call, Throwable t) {
                            // Log.e("onResponseChatRoom: ", t.getMessage());
                            try {
                                apiManager.generateAgoraToken(userId, String.valueOf(System.currentTimeMillis()), convId);
                                apiManager.closeDialog();
                            } catch (Exception e) {
                            }
                        }
                    });
                }

            } else {
                // Open Insufficient Coin popup
                new InsufficientCoins(getActivity(), 2, callRate);
            }
        }

        if (ServiceCode == Constant.MATCH_HOST_LIST) {
            UserListResponse rsp = (UserListResponse) response;
           // list = rsp.getResult().getData();
            //Log.e("MatchHostList", "ListSizeFinal1 " + list.size());

            binding.oncamInstructionsMatch.startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        visibleView();
                        new SessionManager(getActivity()).saveCallStatus(0);
                        Log.e("MatchHostCallStatus", "Call Status1 " + new SessionManager(getContext()).getCallStatus());
                    } catch (Exception e) {
                    }
                }

            });
        }
        if (ServiceCode == Constant.NEW_GENERATE_AGORA_TOKEN) {
            ResultCall rsp = (ResultCall) response;
            Log.e("MatchOnCamFraJson", new Gson().toJson(rsp));
            //Log.e("newWalletValue", rsp.getResult().getPoints().getTotalPoint() + "");
            walletBalance = rsp.getResult().getPoints().getTotalPoint();
            //int talkTime = walletBalance / userData.get(0).getCallRate() * 1000 * 60;
            int talkTime = walletBalance * 1000;
            Log.e("MatchOnCamFra", "remT1 " + String.valueOf(talkTime));
            Log.e("MatchOnCamFra", "remT2 " + String.valueOf(callRate));

            int canCallTill = talkTime - 2000;
            Log.e("MatchOnCamFra", "remT3 " + String.valueOf(canCallTill));
            Log.e("MatchOnCamFrawalBalIfA0", String.valueOf(walletBalance));

            //int talkTime2 = userData.getCall_rate() * 1000 * 60;
            // Minus 2 sec to prevent balance goes into minus
            Intent intent = new Intent(getActivity(), MatchVideoChatActivity.class);
            intent.putExtra("TOKEN", rsp.getResult().getData().getToken());
            intent.putExtra("ID", String.valueOf(matchList.get(0).getProfile_id()));
            intent.putExtra("UID", String.valueOf(userId));
            intent.putExtra("CALL_RATE", String.valueOf(callRate));
            intent.putExtra("UNIQUE_ID", rsp.getResult().getData().getUniqueId());

            if (remGiftCard > 0 && walletBalance <= 20) {
                int newFreeSec = Integer.parseInt(freeSeconds) * 1000;
                intent.putExtra("AUTO_END_TIME", newFreeSec);
                intent.putExtra("is_free_call", "true");

                Log.e("MatchOnCamFra", "remC1 " + String.valueOf(remGiftCard));
                Log.e("MatchOnCamFra", "remW2 " + String.valueOf(walletBalance));

            } else {
                //intent.putExtra("AUTO_END_TIME", canCallTill);
                intent.putExtra("AUTO_END_TIME", canCallTill);
                intent.putExtra("is_free_call", "false");

                Log.e("MatchOnCamFra", "remC3 " + String.valueOf(remGiftCard));
                Log.e("MatchOnCamFra", "remW4 " + String.valueOf(walletBalance));

            }
            intent.putExtra("receiver_name", matchList.get(0).getName());
            intent.putExtra("converID", convId);

            if (matchList.get(0).getProfile_images() == null || matchList.get(0).getProfile_images().size() == 0) {
                intent.putExtra("receiver_image", "empty");
            } else {
                intent.putExtra("receiver_image", matchList.get(0).getProfile_images().get(0).getImage_name());
            }
            binding.oncamInstructionsMatch.tvTimer.setText("15");
            startActivity(intent);

        }
        if (ServiceCode == Constant.SEARCH_USER) {
            UserListResponse rsp = (UserListResponse) response;

            if (rsp != null) {
                try {
                    /*int onlineStatus = rsp.getResult().getData().get(0).getIs_online();
                    int busyStatus = rsp.getResult().getData().get(0).getIs_busy();*/
                    int onlineStatus = onlineStatusRandom;
                    int busyStatus = busyStatusRandom;

                    if (onlineStatus == 1 && busyStatus == 0) {
                        //Check wallet balance before going to make a video call
                        walletBalance = new SessionManager(getContext()).getUserWallet();
                        if (callType.equals("video")) {
                            if (remGiftCard > 0 && walletBalance <= 20) {

                                Log.e("MatchOnCamFra", "remC5 " + String.valueOf(remGiftCard));
                                Log.e("MatchOnCamFra", "remW6 " + String.valueOf(walletBalance));

                                apiManager.generateCallRequest(userId, String.valueOf(System.currentTimeMillis()), convId, callRate,
                                        Boolean.parseBoolean("true"), String.valueOf(remGiftCard));
                            } else if (remGiftCard == 0 && walletBalance >= 20) {

                                Log.e("MatchOnCamFra", "remC7 " + String.valueOf(remGiftCard));
                                Log.e("MatchOnCamFra", "remW8 " + String.valueOf(walletBalance));
                                apiManager.generateCallRequest(userId, String.valueOf(System.currentTimeMillis()), convId, callRate,
                                        Boolean.parseBoolean("false"), String.valueOf(remGiftCard));

                            } else if (remGiftCard > 0 && walletBalance >= 20) {

                                Log.e("MatchOnCamFra", "remC9 " + String.valueOf(remGiftCard));
                                Log.e("MatchOnCamFra", "remW10 " + String.valueOf(walletBalance));
                                apiManager.generateCallRequest(userId, String.valueOf(System.currentTimeMillis()), convId, callRate,
                                        Boolean.parseBoolean("false"), String.valueOf(remGiftCard));

                            } else {
                                new InsufficientCoins(getActivity(), 2, callRate);

                            }

                        }
                    } else if (onlineStatus == 1) {
                        Toast.makeText(getContext(), matchList.get(0).getName() + " is Busy", Toast.LENGTH_SHORT).show();

                    } else if (onlineStatus == 0) {
                        Toast.makeText(getContext(), matchList.get(0).getName() + " is Offline", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "User is Offline!", Toast.LENGTH_SHORT).show();
                    new SessionManager(getApplicationContext()).setOnlineState(0);
                    //finish();
                }
            }
        }


    }

    @Override
    public void isError(String errorCode) {

    }

    private void visibleView() {
        binding.oncamInstructionsMatch.rlMatch.setVisibility(View.VISIBLE);
        binding.oncamInstructionsMatch.rlMatchClose.setVisibility(View.VISIBLE);
        binding.oncamInstructionsMatch.tvTimer.setVisibility(View.VISIBLE);
        binding.oncamInstructionsMatch.tvShowUser.setVisibility(View.INVISIBLE);
        binding.oncamInstructionsMatch.tvGirls.setVisibility(View.GONE);
        binding.oncamInstructionsMatch.startBtn.setVisibility(View.GONE);

        countDownTimer =  new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                binding.oncamInstructionsMatch.tvTimer.setText("" + millisUntilFinished / 1000);
               // binding.oncamInstructionsMatch.tvTimer.setVisibility(View.GONE);
            }

            public void onFinish() {
                //Log.e("MatchHostList", "ListSizeOnclick2 " + list.size());
                matchList = MatchOnCamFragment.this.getRandomElement(list);
                userId = matchList.get(0).getId();
                onlineStatusRandom = matchList.get(0).getIs_online();
                busyStatusRandom = matchList.get(0).getIs_busy();
               // Log.e("MatchHostList", "Name " + matchList.get(0).getName());
               // Log.e("MatchHostList", "ProfileId " + matchList.get(0).getProfile_id());
                //Log.e("MatchHostList", "USERID " + userId);
               // Log.e("MatchHostList", "onlineStatusRandom " + onlineStatusRandom);
               // Log.e("MatchHostList", "busyStatusRandom " + busyStatusRandom);
                if (list.size() <= 1) {
                   // Log.e("MatchHostList", "ListSizeApi " + list.size());
                    apiManager.matchHostList();
                } else if (onlineStatusRandom == 1 && busyStatusRandom == 0) {
                    callType = "video";
                    apiManager.getRemainingGiftCardFunction();
                }
            }
        }.start();

        binding.oncamInstructionsMatch.rlMatchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SessionManager(getActivity()).saveCallStatus((1));
                countDownTimer.cancel();
                binding.oncamInstructionsMatch.rlMatch.setVisibility(View.GONE);
                binding.oncamInstructionsMatch.rlMatchClose.setVisibility(View.GONE);
                binding.oncamInstructionsMatch.tvTimer.setVisibility(View.GONE);
                binding.oncamInstructionsMatch.tvShowUser.setVisibility(View.VISIBLE);
                binding.oncamInstructionsMatch.tvGirls.setVisibility(View.VISIBLE);
                binding.oncamInstructionsMatch.startBtn.setVisibility(View.VISIBLE);

                Random rand = new Random();
                //generate a random integer from 0 to 899, then add 100
                int current_users = rand.nextInt(900) + 2000;
                showUsers(current_users);
            }
        });



    }

    private void showUsers(int current_users) {
        Thread thread = new Thread() {
            public void run() {
                for (int i = 0; i <= current_users; i++) {
                    try {
                        final int as = i;
                        if (getActivity() == null)
                            return;
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                //counter.setText("" + a);
                               // Log.e("Match", "Value_of_i = " + as);
                                binding.oncamInstructionsMatch.tvShowUser.setText("" + as);
                            }
                        });
                        //Log.e("Match","Value_of_i = "+i);
                        sleep(01);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();

    }


    // Function select an element base on index and return
    // an element
    public List<UserListResponse.Data> getRandomElement(List<UserListResponse.Data> list) {
        Random rand = new Random();

        List<UserListResponse.Data> newList = new ArrayList<>();
        int randomIndex = rand.nextInt(list.size());
        newList.add(list.get(randomIndex));
        list.remove(randomIndex);
       // Log.e("MatchHostList", "ListIndex " + randomIndex);
        return newList;

    }

    @Override
    public void onResume() {
        super.onResume();

        binding.oncamInstructionsMatch.rlMatch.setVisibility(View.GONE);
        binding.oncamInstructionsMatch.rlMatchClose.setVisibility(View.GONE);
        binding.oncamInstructionsMatch.tvTimer.setVisibility(View.GONE);
        binding.oncamInstructionsMatch.tvShowUser.setVisibility(View.VISIBLE);
        binding.oncamInstructionsMatch.tvGirls.setVisibility(View.VISIBLE);
        binding.oncamInstructionsMatch.startBtn.setVisibility(View.VISIBLE);

        Log.e("MatchHostCallStatus", "Call Status2 " + new SessionManager(getContext()).getCallStatus());
        if(new SessionManager(getContext()).getCallStatus() != 1) {
             //makeAgainCall();
        } else {
            Log.e("MatchHostCallStatus", "Call Status3 " + new SessionManager(getContext()).getCallStatus());
            Log.e("MatchHostList", "Not Again call " + "bye bye");
        }


    }


   /* public void makeAgainCall() {
        try {
            Log.e("MatchHostList", "AgainListSizeOnclick2 " + list.size());
            matchList = MatchOnCamFragment.this.getRandomElement(list);
            userId = matchList.get(0).getId();
            onlineStatusRandom = matchList.get(0).getIs_online();
            busyStatusRandom = matchList.get(0).getIs_busy();
            Log.e("MatchHostList", "AgainName " + matchList.get(0).getName());
            Log.e("MatchHostList", "AgainProfileId " + matchList.get(0).getProfile_id());
            Log.e("MatchHostList", "AgainUSERID " + userId);
            Log.e("MatchHostList", "AgainonlineStatusRandom " + onlineStatusRandom);
            Log.e("MatchHostList", "AgainbusyStatusRandom " + busyStatusRandom);
            if (list.size() <= 1) {
                Log.e("MatchHostList", "AgainListSizeApi " + list.size());
                apiManager.matchHostList();
            } else if (onlineStatusRandom == 1 && busyStatusRandom == 0) {
                callType = "video";
                apiManager.getRemainingGiftCardFunction();
            }

        } catch (Exception e) {
        }
    }*/


    /*public interface InterfaceCallAgain {
        void makeAgainCall();
    }*/

}

