package com.zeeplive.app.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.zeeplive.app.R;
import com.zeeplive.app.Swipe.OnSwipeTouchListener;
import com.zeeplive.app.Swipe.TouchListener;
import com.zeeplive.app.activity.MainActivity;
import com.zeeplive.app.activity.VideoChatActivity;
import com.zeeplive.app.activity.ViewProfile;
import com.zeeplive.app.adapter.HomeUserAdapter;
import com.zeeplive.app.adapter.LanguageAdapter;
import com.zeeplive.app.adapter.OfferImageAdapter;
import com.zeeplive.app.dialog.InsufficientCoins;
import com.zeeplive.app.response.BannerResponse;
import com.zeeplive.app.response.Call.ResultCall;
import com.zeeplive.app.response.PaymentGatewayDetails.PaymentGatewayResponce;
import com.zeeplive.app.response.ProfileDetailsResponse;
import com.zeeplive.app.response.RemainingGiftCard.RemainingGiftCardResponce;
import com.zeeplive.app.response.UserListResponse;
import com.zeeplive.app.response.language.LanguageData;
import com.zeeplive.app.response.language.LanguageResponce;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.PaginationAdapterCallback;
import com.zeeplive.app.utils.PaginationScrollListener;
import com.zeeplive.app.utils.SessionManager;


import java.util.ArrayList;
import java.util.List;

import pl.pzienowicz.autoscrollviewpager.AutoScrollViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ApiResponseInterface, PaginationAdapterCallback {

    private AutoScrollViewPager offerBanner;
    OfferImageAdapter offerImageAdapter;

    RecyclerView userList;
    HomeUserAdapter homeUserAdapter;
    List<UserListResponse.Data> list;
    ApiManager apiManager;
    GridLayoutManager gridLayoutManager;

    private static final int PAGE_START = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;

    private ArrayList<LanguageData> languageResponceArrayList;
    private TextView tv_all;
    private TextView tv_lan1;
    private TextView tv_lan2;
    private TextView tv_lan3;
    private TextView tv_lan4;
    private TextView tv_lan5;
    private TextView tv_lan6;
    private TextView tv_more;

    private TextView tv_popular, tv_newone;

    private LanguageAdapter languageAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ViewGroup viewGroup;
    public static int updatecoins;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewGroup = view.findViewById(android.R.id.content);
        offerBanner = view.findViewById(R.id.offer_banner);
        userList = view.findViewById(R.id.user_list);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        userList.setLayoutManager(gridLayoutManager);

        tv_all = view.findViewById(R.id.tv_all);
        tv_lan1 = view.findViewById(R.id.tv_lan1);
        tv_lan2 = view.findViewById(R.id.tv_lan2);
        tv_lan3 = view.findViewById(R.id.tv_lan3);
        tv_lan4 = view.findViewById(R.id.tv_lan4);
        tv_lan5 = view.findViewById(R.id.tv_lan5);
        tv_lan6 = view.findViewById(R.id.tv_lan6);
        tv_more = view.findViewById(R.id.tv_more);

        tv_popular = view.findViewById(R.id.tv_popular);
        tv_newone = view.findViewById(R.id.tv_newone);

        tv_all.setTextColor(getResources().getColor(R.color.colorPink));


        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_all.setTextColor(getResources().getColor(R.color.colorPink));
                tv_lan1.setTextColor(getResources().getColor(R.color.black));
                tv_lan2.setTextColor(getResources().getColor(R.color.black));
                tv_lan3.setTextColor(getResources().getColor(R.color.black));
                tv_lan4.setTextColor(getResources().getColor(R.color.black));
                tv_lan5.setTextColor(getResources().getColor(R.color.black));
                tv_lan6.setTextColor(getResources().getColor(R.color.black));
                isLastPage = false;
                new SessionManager(getContext()).setLangState(0);
                apiManager.getUserList(String.valueOf(currentPage), "");
            }
        });

        tv_lan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_all.setTextColor(getResources().getColor(R.color.black));
                tv_lan1.setTextColor(getResources().getColor(R.color.colorPink));
                tv_lan2.setTextColor(getResources().getColor(R.color.black));
                tv_lan3.setTextColor(getResources().getColor(R.color.black));
                tv_lan4.setTextColor(getResources().getColor(R.color.black));
                tv_lan5.setTextColor(getResources().getColor(R.color.black));
                tv_lan6.setTextColor(getResources().getColor(R.color.black));
                isLastPage = false;
                new SessionManager(getContext()).setLangState(1);
                apiManager.getUserList(String.valueOf(currentPage), "");
            }
        });

        tv_lan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_all.setTextColor(getResources().getColor(R.color.black));
                tv_lan1.setTextColor(getResources().getColor(R.color.black));
                tv_lan2.setTextColor(getResources().getColor(R.color.colorPink));
                tv_lan3.setTextColor(getResources().getColor(R.color.black));
                tv_lan4.setTextColor(getResources().getColor(R.color.black));
                tv_lan5.setTextColor(getResources().getColor(R.color.black));
                tv_lan6.setTextColor(getResources().getColor(R.color.black));
                isLastPage = false;
                new SessionManager(getContext()).setLangState(2);
                apiManager.getUserList(String.valueOf(currentPage), "");
            }
        });

        tv_lan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_all.setTextColor(getResources().getColor(R.color.black));
                tv_lan1.setTextColor(getResources().getColor(R.color.black));
                tv_lan2.setTextColor(getResources().getColor(R.color.black));
                tv_lan3.setTextColor(getResources().getColor(R.color.colorPink));
                tv_lan4.setTextColor(getResources().getColor(R.color.black));
                tv_lan5.setTextColor(getResources().getColor(R.color.black));
                tv_lan6.setTextColor(getResources().getColor(R.color.black));
                isLastPage = false;
                new SessionManager(getContext()).setLangState(3);
                apiManager.getUserList(String.valueOf(currentPage), "");
            }
        });

        tv_lan4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_all.setTextColor(getResources().getColor(R.color.black));
                tv_lan1.setTextColor(getResources().getColor(R.color.black));
                tv_lan2.setTextColor(getResources().getColor(R.color.black));
                tv_lan3.setTextColor(getResources().getColor(R.color.black));
                tv_lan4.setTextColor(getResources().getColor(R.color.colorPink));
                tv_lan5.setTextColor(getResources().getColor(R.color.black));
                tv_lan6.setTextColor(getResources().getColor(R.color.black));
                isLastPage = false;
                new SessionManager(getContext()).setLangState(4);
                apiManager.getUserList(String.valueOf(currentPage), "");
            }
        });
        tv_lan5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_all.setTextColor(getResources().getColor(R.color.black));
                tv_lan1.setTextColor(getResources().getColor(R.color.black));
                tv_lan2.setTextColor(getResources().getColor(R.color.black));
                tv_lan3.setTextColor(getResources().getColor(R.color.black));
                tv_lan4.setTextColor(getResources().getColor(R.color.black));
                tv_lan5.setTextColor(getResources().getColor(R.color.colorPink));
                tv_lan6.setTextColor(getResources().getColor(R.color.black));
                isLastPage = false;
                new SessionManager(getContext()).setLangState(5);
                apiManager.getUserList(String.valueOf(currentPage), "");
            }
        });
        tv_lan6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_all.setTextColor(getResources().getColor(R.color.black));
                tv_lan1.setTextColor(getResources().getColor(R.color.black));
                tv_lan2.setTextColor(getResources().getColor(R.color.black));
                tv_lan3.setTextColor(getResources().getColor(R.color.black));
                tv_lan4.setTextColor(getResources().getColor(R.color.black));
                tv_lan5.setTextColor(getResources().getColor(R.color.black));
                tv_lan6.setTextColor(getResources().getColor(R.color.colorPink));
                isLastPage = false;
                new SessionManager(getContext()).setLangState(6);
                apiManager.getUserList(String.valueOf(currentPage), "");
            }
        });
        tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.mylanguagecustom, viewGroup, false);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView);

                final AlertDialog alertDialog = builder.create();

                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
                layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
                //   layoutParams.x=100;
                //      layoutParams.y = 120;
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                GridView lanGridViewCust;
                lanGridViewCust = dialogView.findViewById(R.id.lanGridView);
                lanGridViewCust.setAdapter(languageAdapter);
                lanGridViewCust.setSelector(new ColorDrawable(Color.TRANSPARENT));
                lanGridViewCust.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                       /* TextView currentLetter = (TextView) view.findViewById(R.id.tv_landata);
                        currentLetter.setTextColor(getResources().getColor(R.color.colorPink));*/

                        switch (i) {
                            case 0:
                                tv_all.performClick();
                                break;
                            case 1:
                                tv_lan1.performClick();
                                break;
                            case 2:
                                tv_lan2.performClick();
                                break;
                            case 3:
                                tv_lan3.performClick();
                                break;
                            case 4:
                                tv_lan4.performClick();
                                break;
                            case 5:
                                tv_lan5.performClick();
                                break;
                            case 6:
                                tv_lan6.performClick();
                                break;
                            default:
                                tv_all.setTextColor(getResources().getColor(R.color.black));
                                tv_lan1.setTextColor(getResources().getColor(R.color.black));
                                tv_lan2.setTextColor(getResources().getColor(R.color.black));
                                tv_lan3.setTextColor(getResources().getColor(R.color.black));
                                tv_lan4.setTextColor(getResources().getColor(R.color.black));
                                tv_lan5.setTextColor(getResources().getColor(R.color.black));
                                tv_lan6.setTextColor(getResources().getColor(R.color.black));

                                if (languageResponceArrayList.get(i).getLanguage().equals("search")) {
                                    ((MainActivity) getActivity()).loadSearchFragement();
                                } else {
                                    new SessionManager(getContext()).setLangState(i);
                                    apiManager.getUserList(String.valueOf(currentPage), "");
                                }
                        }

                        alertDialog.dismiss();

                    }
                });

            }
        });


  /*      userList.addOnItemTouchListener(new RecyclerTouchListener(getContext(), userList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    Intent intent = new Intent(getContext(), ViewProfile.class);
                    Bundle bundle = new Bundle();
                    //hide intent sending data homefragment to ViewprofileActivity...
                    //  bundle.putSerializable("user_data", list.get(position));
                    bundle.putSerializable("id", list.get(position).getId());
                    bundle.putSerializable("profileId", list.get(position).getProfile_id());

                    intent.putExtras(bundle);

                    startActivity(intent);

                    //  ((MainActivity) getActivity()).startCountDown();

                } catch (Exception e) {
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
*/
        apiManager = new ApiManager(getContext(), this);
        // apiManager.getPromotionBanner();

        userList.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                showProgress();
                // mocking network delay for API call
                new Handler().postDelayed(() -> apiManager.getUserListNextPage(String.valueOf(currentPage), ""), 500);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        apiManager.getUserLanguage();
        apiManager.getProfileDetails();


        // showProgress();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // mSwipeRefreshLayout.setRefreshing(false);
                currentPage = 1;
                isLastPage = false;
                list.clear();
                apiManager.getUserList(String.valueOf(currentPage), "");


            }
        });

        //  markerForLang();
        checkFreeGift();

        tv_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_popular.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_popular.setTextSize(18);

                // Typeface typeface = getResources().getFont(R.font.lobster);
                //or to support all versions use
                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.lato_bold);
                tv_popular.setTypeface(typeface);

                tv_newone.setTextColor(getResources().getColor(R.color.black));
                tv_newone.setTextSize(14);

                typeface = ResourcesCompat.getFont(getContext(), R.font.lato_regular);
                tv_newone.setTypeface(typeface);

                apiManager.getPopularList(String.valueOf(currentPage), "");

            }
        });

        tv_newone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_newone.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_newone.setTextSize(18);
                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.lato_bold);
                tv_newone.setTypeface(typeface);
                tv_popular.setTextColor(getResources().getColor(R.color.black));
                tv_popular.setTextSize(14);
                typeface = ResourcesCompat.getFont(getContext(), R.font.lato_regular);
                tv_popular.setTypeface(typeface);
                apiManager.getUserList(String.valueOf(currentPage), "");

            }
        });

        tv_newone.performClick();

     /*   ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
          //  private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.background));

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
             //   adapter.showMenu(viewHolder.getAdapterPosition());
                Log.e("direction",direction+"");
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(userList);
*/

       /* ((LinearLayout)view.findViewById(R.id.homeMain)).setOnTouchListener(new OnSwipeTouchListener(getContext(), new TouchListener() {
            @Override
            public void onSingleTap() {
        //        Log.e("TAG", ">> Single tap");

            }

            @Override
            public void onDoubleTap() {
         //       Log.e("TAG", ">> Double tap");
            }

            @Override
            public void onLongPress() {
                Log.e("TAG", ">> Long press");
            }

            @Override
            public void onSwipeLeft() {
                Log.e("TAG", ">> Swipe left");
            }

            @Override
            public void onSwipeRight() {
                Log.e("TAG", ">> Swipe right");

            }
        }));
*/

        return view;
    }

    private void checkFreeGift() {
        if (new SessionManager(getContext()).getGender().equals("male")) {
            apiManager.getRemainingGiftCardDisplayFunction();
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        // resetPages();
        // apiManager.getUserList(String.valueOf(currentPage), "");
        //  apiManager.getUserLanguage();
        apiManager.getPaymentData();
        //   Log.e("onlineState", new SessionManager(getContext()).getOnlineState() + "");
        if (new SessionManager(getContext()).getUserLoaddata().equals("yes")) {
//            apiManager.getUserList(String.valueOf(currentPage), "");
            new SessionManager(getContext()).setUserLoaddataNo();
        }
        if (new SessionManager(getContext()).getOnlineState() == 0) {
            showProgress();
            //  markerForLang();
            new SessionManager(getContext()).setOnlineState(1);
            //new SessionManager(getContext()).setLangState(0);
        }

        //  Log.e("onlineState2", new SessionManager(getContext()).getOnlineState() + "");

    }

    public void markerForLang() {
        switch (new SessionManager(getContext()).gettLangState()) {
            case 0:
                tv_all.performClick();
                break;
            case 1:
                tv_lan1.performClick();
                break;
            case 2:
                tv_lan2.performClick();
                break;
            case 3:
                tv_lan3.performClick();
                break;
            case 4:
                tv_lan4.performClick();
                break;
            case 5:
                tv_lan5.performClick();
                break;
            case 6:
                tv_lan6.performClick();
                break;
            default:
                apiManager.getUserList(String.valueOf(currentPage), "");
        }
    }

    @Override
    public void isError(String errorCode) {
        if (errorCode.equals("227")) {
            new InsufficientCoins(getContext(), 2, Integer.parseInt(callRate));
        } else {
            Toast.makeText(getContext(), errorCode, Toast.LENGTH_SHORT).show();
        }
    }

    //lateinit var dialog1: Dialog
    private Dialog dialog1;
    private boolean success;
    private int remGiftCard = 0;
    private String freeSeconds;
    private int walletBalance = 0;

    @Override
    public void isSuccess(Object response, int ServiceCode) {

        try {
            if (ServiceCode == Constant.GET_REMAINING_GIFT_CARD_DISPLAY) {
                RemainingGiftCardResponce rsp = (RemainingGiftCardResponce) response;
                try {
                    int remGiftCard = rsp.getTotalGift();
                    updatecoins = rsp.getTotalGift();
                    Log.e("update1", String.valueOf(updatecoins));
                    int updatewallet = new SessionManager(getContext()).getUserWallet();
                    Log.e("newballance", String.valueOf(updatewallet));

                    if (remGiftCard > 0 && updatewallet == 0) {
                        dialog1 = new Dialog(getContext());
                        dialog1.setContentView(R.layout.freecard_layout_new);
                        dialog1.setCancelable(false);
                        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationWindMill;
                        TextView tv_freecardcount = dialog1.findViewById(R.id.tv_freecardcount);
                        Button btn_gotit = dialog1.findViewById(R.id.btn_gotit);
                        tv_freecardcount.setText(remGiftCard + " gift cards received. Enjoy your free chance of video call.");

                        btn_gotit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog1.dismiss();
                            }
                        });
                        dialog1.show();
                    }

                } catch (Exception e) {
                }

            }
            if (ServiceCode == Constant.GET_REMAINING_GIFT_CARD) {
                RemainingGiftCardResponce rsp = (RemainingGiftCardResponce) response;

                try {
                    try {
                        success = rsp.getSuccess();
                        remGiftCard = rsp.getTotalGift();
                        freeSeconds = rsp.getFreeSecond();
                        if (remGiftCard > 0) {
                            apiManager.searchUser(profileId, "1");
                            return;
                        }
                    } catch (Exception e) {
                    }

                    if (new SessionManager(getContext()).getUserWallet() >= Integer.parseInt(callRate)) {
                        apiManager.searchUser(profileId, "1");
                    } else {
                        new InsufficientCoins(getContext(), 2, Integer.parseInt(callRate));
                    }
                } catch (Exception e) {
                    apiManager.searchUser(profileId, "1");
                }

            }


            if (ServiceCode == Constant.NEW_GENERATE_AGORA_TOKEN) {
                ResultCall rsp = (ResultCall) response;

                //Log.e("newWalletValue", rsp.getResult().getPoints().getTotalPoint() + "");
                //int walletBalance = rsp.getResult().getPoints().getTotalPoint();
                walletBalance = rsp.getResult().getPoints().getTotalPoint();
                int talkTime = walletBalance / Integer.parseInt(callRate) * 1000 * 60;

                // int talkTime2 = userData.getCall_rate() * 1000 * 60;
                // Minus 2 sec to prevent balance goes into minus
                int canCallTill = talkTime - 2000;
                Intent intent = new Intent(getContext(), VideoChatActivity.class);
                intent.putExtra("TOKEN", rsp.getResult().getData().getToken());
                intent.putExtra("ID", profileId);
                intent.putExtra("UID", String.valueOf(userId));
                intent.putExtra("CALL_RATE", callRate);
                intent.putExtra("UNIQUE_ID", rsp.getResult().getData().getUniqueId());

                if (remGiftCard > 0 && walletBalance <= 20) {
                    int newFreeSec = Integer.parseInt(freeSeconds) * 1000;
                    intent.putExtra("AUTO_END_TIME", newFreeSec);
                    intent.putExtra("is_free_call", "true");

                    Log.e("cardValueIfHA", String.valueOf(remGiftCard));
                    Log.e("walBalIfHA", String.valueOf(walletBalance));

                } else {
                    intent.putExtra("AUTO_END_TIME", canCallTill);
                    intent.putExtra("is_free_call", "false");

                    Log.e("cardValueElseHA", String.valueOf(remGiftCard));
                    Log.e("walBalElseHA", String.valueOf(walletBalance));
                }
                intent.putExtra("receiver_name", hostName);
                intent.putExtra("converID", "convId");

                intent.putExtra("receiver_image", hostImage);
                startActivity(intent);

            }
            if (ServiceCode == Constant.SEARCH_USER) {
                UserListResponse rsp = (UserListResponse) response;

                Log.e("updatewallet", String.valueOf(walletBalance));
                if (rsp != null) {
                    try {
                        int onlineStatus = rsp.getResult().getData().get(0).getIs_online();
                        int busyStatus = rsp.getResult().getData().get(0).getIs_busy();

                        if (onlineStatus == 1 && busyStatus == 0) {
                            // Check wallet balance before going to make a video call
                            //     apiManager.getWalletAmount();
                            walletBalance = new SessionManager(getContext()).getUserWallet();
                            if (callType.equals("video")) {
                                if (remGiftCard > 0 && walletBalance <= 20) {
                                    Log.e("cardValueIfH", String.valueOf(remGiftCard));
                                    Log.e("walBalIfH", String.valueOf(walletBalance));
                                    updatecoins = remGiftCard;
                                    Log.e("update2", String.valueOf(updatecoins));
                                    apiManager.generateCallRequest(userId, String.valueOf(System.currentTimeMillis()), "0", Integer.parseInt(callRate),
                                            Boolean.parseBoolean("true"), String.valueOf(remGiftCard));
                                } else if (remGiftCard == 0 && walletBalance >=20) {
                                    Log.e("cardValueElseH", String.valueOf(remGiftCard));
                                    Log.e("walBalElseH", String.valueOf(walletBalance));

                                    apiManager.generateCallRequest(userId, String.valueOf(System.currentTimeMillis()), "0", Integer.parseInt(callRate),
                                            Boolean.parseBoolean("false"), String.valueOf(remGiftCard));
                                } else if (remGiftCard > 0 && walletBalance >=20) {

                                    apiManager.generateCallRequest(userId, String.valueOf(System.currentTimeMillis()), "0", Integer.parseInt(callRate),
                                            Boolean.parseBoolean("false"), String.valueOf(remGiftCard));
                                } else {
                                    new InsufficientCoins(getActivity(), 2,Integer.parseInt(callRate));

                                }
                            } else if (callType.equals("audio")) {
                    /*            apiManager.dailVoiceCallUser(String.valueOf(userData.get(0).getAudioCallRate()), String.valueOf(userId),
                                        String.valueOf(System.currentTimeMillis()));
                    */
                            }
                        } else if (onlineStatus == 1) {
                            Toast.makeText(getContext(), hostName + " is Busy", Toast.LENGTH_SHORT).show();

                        } else if (onlineStatus == 0) {
                            Toast.makeText(getContext(), hostName + " is Offline", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "User is Offline!", Toast.LENGTH_SHORT).show();
                        new SessionManager(getContext()).setOnlineState(0);
                        //     finish();
                    }
                }
            }
            if (ServiceCode == Constant.USER_LIST) {
                UserListResponse rsp = (UserListResponse) response;

                mSwipeRefreshLayout.setRefreshing(false);


                list = rsp.getResult().getData();
                TOTAL_PAGES = rsp.getResult().getLast_page();
                if (list.size() > 0) {
                    homeUserAdapter = new HomeUserAdapter(getActivity(), HomeFragment.this, "dashboard", HomeFragment.this);
                    // userList.setItemAnimator(new DefaultItemAnimator());
                    userList.setAdapter(homeUserAdapter);

                    // Shuffle Data

                    // Collections.shuffle(list);

                    // Set data in adapter
                    homeUserAdapter.addAll(list);
                    Log.e("listSize", String.valueOf(list.size()));

                    if (currentPage < TOTAL_PAGES) {
                        homeUserAdapter.addLoadingFooter();
                    } else {
                        isLastPage = true;
                    }
                }

                consentReminder();
            }

            if (ServiceCode == Constant.USER_LIST_NEXT_PAGE) {
                UserListResponse rsp = (UserListResponse) response;
                mSwipeRefreshLayout.setRefreshing(false);
                homeUserAdapter.removeLoadingFooter();
                isLoading = false;
                List<UserListResponse.Data> results = rsp.getResult().getData();
                // Shuffle Data
                //  Collections.shuffle(results);
                list.addAll(results);
                homeUserAdapter.addAll(results);
                if (currentPage != TOTAL_PAGES) homeUserAdapter.addLoadingFooter();
                else isLastPage = true;
            }
            if (ServiceCode == Constant.GET_BANNER) {
                BannerResponse rsp = (BannerResponse) response;

                if (rsp.getResult() != null && rsp.getResult().size() > 0) {
                    // Banner Code
                    offerImageAdapter = new OfferImageAdapter(getActivity(), rsp.getResult());
                    offerBanner.setAdapter(offerImageAdapter);
                    offerBanner.setClipChildren(false);
                    offerBanner.setClipToPadding(false);
                    offerBanner.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
                    offerBanner.setPadding(4, 0, 4, 0);
                    offerBanner.setInterval(3000);
                    offerBanner.startAutoScroll();
                    offerBanner.setVisibility(View.VISIBLE);
                }
            }

            if (ServiceCode == Constant.LAN_DATA) {
                LanguageResponce rsp = (LanguageResponce) response;
                if (!rsp.getSuccess()) {
                    //      Log.e("errCode", rsp.getError());
                    logoutDialog();
                    return;
                }
                if (rsp.getResult() != null) {
                    languageResponceArrayList = new ArrayList<>();
                    LanguageData languageData = new LanguageData();
                    languageData.setId(0);
                    languageData.setLanguage("All");
                    languageResponceArrayList.add(languageData);
                    languageResponceArrayList.addAll(rsp.getResult());
                    languageData = new LanguageData();
                    languageData.setId(0);
                    languageData.setLanguage("search");
                    languageResponceArrayList.add(languageData);
                    //    Log.e("arrayData", new Gson().toJson(languageResponceArrayList));
                    languageAdapter = new LanguageAdapter(getContext(), languageResponceArrayList);
                    try {
                        tv_lan1.setText(languageResponceArrayList.get(1).getLanguage());
                        tv_lan1.setVisibility(View.VISIBLE);
                        tv_lan2.setText(languageResponceArrayList.get(2).getLanguage());
                        tv_lan2.setVisibility(View.VISIBLE);
                        tv_lan3.setText(languageResponceArrayList.get(3).getLanguage());
                        tv_lan3.setVisibility(View.VISIBLE);
                        tv_lan4.setText(languageResponceArrayList.get(4).getLanguage());
                        tv_lan4.setVisibility(View.VISIBLE);
                        tv_lan5.setText(languageResponceArrayList.get(5).getLanguage());
                        tv_lan5.setVisibility(View.VISIBLE);
                        tv_lan6.setText(languageResponceArrayList.get(6).getLanguage());
                        tv_lan6.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                    }

                }

            }

            // PAY_DETAILS
            if (ServiceCode == Constant.PAY_DETAILS) {
                PaymentGatewayResponce rsp = (PaymentGatewayResponce) response;
                new SessionManager(getContext()).setUserWall(rsp.getData().getBalance().getTotalPoint());

                new SessionManager(getContext()).setUserStriepKS(rsp.getData().getStripe().getPublishableKey(),
                        rsp.getData().getStripe().getSecretKey());

                new SessionManager(getContext()).setUserRazKS(rsp.getData().getRazorpay().getKeyId(),
                        rsp.getData().getRazorpay().getKeySecret());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ServiceCode == Constant.PROFILE_DETAILS) {
            ProfileDetailsResponse rsp = (ProfileDetailsResponse) response;

            if (rsp.getSuccess().getProfile_images() != null && rsp.getSuccess().getProfile_images().size() > 0) {
                new SessionManager(getContext()).setUserProfilepic(rsp.getSuccess().getProfile_images().get(0).getImage_name());
            }
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
            new SessionManager(getContext()).logoutUser();
            apiManager.getUserLogout();
            getActivity().finish();
        });
    }

    @Override
    public void retryPageLoad() {
        //   apiManager.getUserListNextPage(String.valueOf(currentPage), "");
    }

    void resetPages() {
        // Reset Current page when refresh data
        this.currentPage = 1;
        this.isLastPage = false;
    }

    void consentReminder() {
        if (!new SessionManager(getContext()).getConsent()) {
            // new ConsentDialog(this);
        }
    }

    public void showProgress() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private String callType = "", profileId = "", callRate = "", hostName = "", hostImage = "";
    private int userId;

    public void startVideoCall(String profileId, String callRate, int userId, String hostName, String hostImage) {
        callType = "video";
        this.profileId = profileId;
        this.callRate = callRate;
        this.userId = userId;
        this.hostName = hostName;
        this.hostImage = hostImage;
        apiManager.getRemainingGiftCardFunction();
    }


}