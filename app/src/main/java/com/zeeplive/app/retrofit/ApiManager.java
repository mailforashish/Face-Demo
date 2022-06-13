package com.zeeplive.app.retrofit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeeplive.app.body.CallRecordBody;
import com.zeeplive.app.body.CallRecordBodyAudio;
import com.zeeplive.app.dialog.MyProgressDialog;
import com.zeeplive.app.response.AddRemoveFavResponse;
import com.zeeplive.app.response.AgoraTokenResponse;
import com.zeeplive.app.response.Ban.BanResponce;
import com.zeeplive.app.response.BannerResponse;
import com.zeeplive.app.response.Broadcast.BroadcastCallRequest.AudiRequestForCall;
import com.zeeplive.app.response.Broadcast.BroadcastCallRequest.HostAcceptCallRequest;
import com.zeeplive.app.response.Broadcast.BroadcastList.BroadcastListResponce;
import com.zeeplive.app.response.Broadcast.BroadcastStart.BroadCastResponce;
import com.zeeplive.app.response.Call.ResultCall;
import com.zeeplive.app.response.ChatPurchaseValidity;
import com.zeeplive.app.response.CreatePaymentResponse;
import com.zeeplive.app.response.DeleteImageResponse;
import com.zeeplive.app.response.DisplayGiftCount.GiftCountResult;
import com.zeeplive.app.response.EndCallData.EncCallResponce;
import com.zeeplive.app.response.EndCallData.EndCallData;
import com.zeeplive.app.response.FavNew.FavNewResponce;
import com.zeeplive.app.response.FavResponse;
import com.zeeplive.app.response.FcmTokenResponse;
import com.zeeplive.app.response.GenderResponse.GenderStatus;
import com.zeeplive.app.response.GetUserLevelResponse.GetLevelResponce;
import com.zeeplive.app.response.LoginResponse;
import com.zeeplive.app.response.MessageCallData.MessageCallDataRequest;
import com.zeeplive.app.response.MessageCallData.MessageCallDataResponce;
import com.zeeplive.app.response.NewWallet.WalletResponce;
import com.zeeplive.app.response.NewWalletResponce.WallateResponceFemale;
import com.zeeplive.app.response.OnCamResponse;
import com.zeeplive.app.response.OnlineStatusResponse;
import com.zeeplive.app.response.PaymentGatewayDetails.CashFree.CFToken.CfTokenResponce;
import com.zeeplive.app.response.PaymentGatewayDetails.CashFree.CashFreePayment.CashFreePaymentRequest;
import com.zeeplive.app.response.PaymentGatewayDetails.PaymentGatewayResponce;
import com.zeeplive.app.response.PaymentSelector.PaymentSelectorResponce;
import com.zeeplive.app.response.ProfileDetailsResponse;
import com.zeeplive.app.response.Rating.RatingResponce;
import com.zeeplive.app.response.RecentRechargeResponse;
import com.zeeplive.app.response.RechargePlanResponse;
import com.zeeplive.app.response.RemainingGiftCard.RemainingGiftCardResponce;
import com.zeeplive.app.response.ReportResponse;
import com.zeeplive.app.response.RequestGiftRequest.RequestGiftRequest;
import com.zeeplive.app.response.RequestGiftRequest.RequestGiftResponce;
import com.zeeplive.app.response.SentOtpResponse;
import com.zeeplive.app.response.Stripe.RequestModel;
import com.zeeplive.app.response.Stripe.ServerResponce;
import com.zeeplive.app.response.Stripe.key.KeyResponce;
import com.zeeplive.app.response.Stripe.paysucess.PayRequest;
import com.zeeplive.app.response.Stripe.paysucess.PayResponce;
import com.zeeplive.app.response.UpdateProfileResponse;
import com.zeeplive.app.response.UpgradedLevel.UpgradedLevelResponce;
import com.zeeplive.app.response.UserListResponse;
import com.zeeplive.app.response.UserListResponseNew.UserListResponseNewData;
import com.zeeplive.app.response.Video.VideoResponce;
import com.zeeplive.app.response.ViewTicketResponse;
import com.zeeplive.app.response.VoiceCall.VoiceCallResponce;
import com.zeeplive.app.response.WalletBalResponse;
import com.zeeplive.app.response.WalletRechargeResponse;
import com.zeeplive.app.response.Walletfilter.WalletfilterResponce;
import com.zeeplive.app.response.gift.ResultGift;
import com.zeeplive.app.response.gift.SendGiftRequest;
import com.zeeplive.app.response.gift.SendGiftResult;
import com.zeeplive.app.response.language.LanguageResponce;
import com.zeeplive.app.response.logout.LogoutResponce;
import com.zeeplive.app.response.videoplay.VideoPlayResponce;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiManager {
    private Context mContext;
    private MyProgressDialog dialog;
    private ApiResponseInterface mApiResponseInterface;
    private ApiInterface apiService;
    String authToken;

    public ApiManager(Context context, ApiResponseInterface apiResponseInterface) {
        this.mContext = context;
        this.mApiResponseInterface = apiResponseInterface;
        apiService = ApiClient.getClient().create(ApiInterface.class);
        dialog = new MyProgressDialog(mContext);
        authToken = Constant.BEARER + new SessionManager(context).getUserToken();
        //   Log.e("authToken", authToken);
    }

    public ApiManager(Context context) {
        this.mContext = context;
        apiService = ApiClient.getClient().create(ApiInterface.class);
        dialog = new MyProgressDialog(mContext);
        authToken = Constant.BEARER + new SessionManager(context).getUserToken();
    }

    public void registerUser(String name, String email, String password, String c_password,
                             String login_type, String username, String mobile, String gender) {
        showDialog();
        Call<LoginResponse> call = apiService.registerUser(name, email, password, c_password, login_type, username, mobile, gender);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.REGISTER);
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(mContext, "User already registered. please login", Toast.LENGTH_LONG).show();
                } else if (response.code() == 500) {
                    Toast.makeText(mContext, "Internal Server Error", Toast.LENGTH_LONG).show();
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void guestRegister(String login_type, String device_id,String gender,String dob, String hash) {
        showDialog();
        Call<LoginResponse> call = apiService.guestRegister(login_type, device_id, gender,dob, hash);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.e("Guestregister", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.GUEST_REGISTER);
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(mContext, "User already registered. please login", Toast.LENGTH_LONG).show();
                } else if (response.code() == 500) {
                    Toast.makeText(mContext, "Internal Server Error", Toast.LENGTH_LONG).show();
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("error", t.getMessage());
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void guestRegisterOld(String login_type, String device_id, String hash) {
        showDialog();
        Call<LoginResponse> call = apiService.guestRegisterOld(login_type, device_id, hash);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.e("Guestregister", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.GUEST_REGISTER);
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(mContext, "User already registered. please login", Toast.LENGTH_LONG).show();
                } else if (response.code() == 500) {
                    Toast.makeText(mContext, "Internal Server Error", Toast.LENGTH_LONG).show();
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("error", t.getMessage());
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void login_FbGoogle(String name, String login_type, String username,String gender,String dob, String hash) {
        showDialog();
        Call<LoginResponse> call = apiService.loginFbGoogle(name, login_type, username,gender,dob, hash);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.REGISTER);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } else if (response.code() == 401) {
                    Gson gson = new GsonBuilder().create();
                    ReportResponse mError;
                    try {
                        mError = gson.fromJson(response.errorBody().string(), ReportResponse.class);
                        Toast.makeText(mContext, mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        // handle failure to read error
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

   /* public void login(String email, String password, String hash) {
        showDialog();
        Call<LoginResponse> call = apiService.loginUser(email, password, hash);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.e("loginResponce", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.LOGIN);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } else if (response.code() == 401) {
                    Log.e("errorResponce", response.body().getError());
                    Toast.makeText(mContext, "Wrong Password", Toast.LENGTH_SHORT).show();
                    //    Toast.makeText(mContext, response.body().getError(), Toast.LENGTH_SHORT).show();

                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                closeDialog();
                //     Log.e("loginResponceError", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }*/

    public void genderStatus(String login_type, String device_id) {
        Call<GenderStatus> call = apiService.genderStatus(login_type, device_id);
        call.enqueue(new Callback<GenderStatus>() {
            @Override
            public void onResponse(Call<GenderStatus> call, Response<GenderStatus> response) {
                Log.e("GenderStatusResponce", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.GENDER_STATUS);
                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(mContext, "GenderStatus Wrong ", Toast.LENGTH_SHORT).show();
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<GenderStatus> call, Throwable t) {
                Log.e("GenderStatusError", t.getMessage());
                //Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }





    public void login(String email, String password) {
        showDialog();
        Call<LoginResponse> call = apiService.loginUser(email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.e("loginResponce", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.LOGIN);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } else if (response.code() == 401) {
                    Log.e("errorResponce", response.body().getError());
                    Toast.makeText(mContext, "Wrong Password", Toast.LENGTH_SHORT).show();
                    //    Toast.makeText(mContext, response.body().getError(), Toast.LENGTH_SHORT).show();

                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                closeDialog();
                //     Log.e("loginResponceError", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }






    public void generateAgoraToken(int id, String outgoingTime, String convId) {
        showDialog();
        //Log.e("connnnnnId", convId);
        Call<AgoraTokenResponse> call = apiService.getAgoraToken(authToken, "application/json", id, outgoingTime, convId);
        call.enqueue(new Callback<AgoraTokenResponse>() {
            @Override
            public void onResponse(Call<AgoraTokenResponse> call, Response<AgoraTokenResponse> response) {
                //          Log.e("tokenResponce", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.GENERATE_AGORA_TOKEN);
                } else {

                    //  mApiResponseInterface.isError(response.body().getError());
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<AgoraTokenResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void generateCallRequest(int id, String outgoingTime, String convId, int callRate, boolean isFreeCall, String remGiftCards) {
        //Log.e("freeCall", "isFreeCallApi="+ isFreeCall);
        showDialog();
        Call<ResultCall> call = apiService.getDailCallRequest(authToken, "application/json", id, outgoingTime, convId, callRate, isFreeCall, remGiftCards);
        call.enqueue(new Callback<ResultCall>() {
            @Override
            public void onResponse(Call<ResultCall> call, Response<ResultCall> response) {

                Log.e("request", call.request().toString());
                Log.e("generateCallRequest", new Gson().toJson(response.body()));

                if (response.body().getSuccess()) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.NEW_GENERATE_AGORA_TOKEN);
                } else {
                    //Toast.makeText(mContext, response.body().getError(), Toast.LENGTH_SHORT).show();
                    mApiResponseInterface.isError("227");
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ResultCall> call, Throwable t) {
                closeDialog();
                //  Log.e("generateCallError", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void registerFcmToken(String token) {
        //  showDialog();
        Call<FcmTokenResponse> call = apiService.registerFcmToken(authToken, "application/json", token);
        call.enqueue(new Callback<FcmTokenResponse>() {
            @Override
            public void onResponse(Call<FcmTokenResponse> call, Response<FcmTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (!response.body().getResult().isEmpty()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.REGISTER_FCM_TOKEN);
                    }
                }
                //        closeDialog();
            }

            @Override
            public void onFailure(Call<FcmTokenResponse> call, Throwable t) {
                //      closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getUserList(String pageNumber, String search) {
        //showDialog();
        Call<UserListResponse> call = apiService.getUserList(authToken, "application/json", search, pageNumber, "16", String.valueOf(new SessionManager(mContext).gettLangState()));

        // Log.e("lanID", String.valueOf(new SessionManager(mContext).gettLangState()));

        call.enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                //Log.e("userList", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getResult().getData() != null) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.USER_LIST);
                    }
                }
                //   closeDialog();
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                //     closeDialog();
                //      Log.e("userListErr", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void matchHostList() {
        Call<UserListResponse> call = apiService.matchHostList(authToken, "application/json");
        call.enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                //   if (response.isSuccessful() && response.body() != null) {
                // Log.e("MatchHostList",new Gson().toJson(response.body()));
                mApiResponseInterface.isSuccess(response.body(), Constant.MATCH_HOST_LIST);
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void getPopularList(String pageNumber, String search) {
        //showDialog();
        Call<UserListResponse> call = apiService.getPopulartList(authToken, "application/json", search, pageNumber, "16", String.valueOf(new SessionManager(mContext).gettLangState()));

        // Log.e("lanID", String.valueOf(new SessionManager(mContext).gettLangState()));

        call.enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                //Log.e("userList", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getResult().getData() != null) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.USER_LIST);
                    }
                }
                //   closeDialog();
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                //     closeDialog();
                //      Log.e("userListErr", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getProfileData(String id, String search) {
        showDialog();
        Log.e("newProfileFemaleId", id);
        Call<UserListResponseNewData> call = apiService.getProfileData(authToken, "application/json", search, "", id, String.valueOf(new SessionManager(mContext).gettLangState()));
        // Log.e("lanID", String.valueOf(new SessionManager(mContext).gettLangState()));
        call.enqueue(new Callback<UserListResponseNewData>() {
            @Override
            public void onResponse(Call<UserListResponseNewData> call, Response<UserListResponseNewData> response) {
                //Log.e("getprofiledata", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {

                    //if (response.body().getResult().get(0) != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.GET_PROFILE_DATA);
                    // }
                }
                //   closeDialog();
            }

            @Override
            public void onFailure(Call<UserListResponseNewData> call, Throwable t) {
                //     closeDialog();
                Log.e("getprofiledataerror", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getUserListNextPage(String pageNumber, String search) {
        //  showDialog();
        Call<UserListResponse> call = apiService.getUserList(authToken, "application/json", search, pageNumber, "16", String.valueOf(new SessionManager(mContext).gettLangState()));

       /* Log.e("authToken", authToken);
        Log.e("pageNumber", pageNumber);
        Log.e("lanID", String.valueOf(new SessionManager(mContext).gettLangState()));
*/
        call.enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                Log.e("userListNXT", new Gson().toJson(response.body()));

                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getResult().getData() != null) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.USER_LIST_NEXT_PAGE);
                    }
                }
                //  closeDialog();
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                //   closeDialog();
                //        Log.e("userListErrNXT", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getFavList() {
        //   showDialog();
        Call<FavResponse> call = apiService.getFavList(authToken, "application/json");
        call.enqueue(new Callback<FavResponse>() {
            @Override
            public void onResponse(Call<FavResponse> call, Response<FavResponse> response) {
              /*  if (response.isSuccessful() && response.body() != null) {

                    mApiResponseInterface.isSuccess(response.body(), Constant.FAVOURITE_LIST);

                }*/
                //         closeDialog();
            }

            @Override
            public void onFailure(Call<FavResponse> call, Throwable t) {
                //          closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getFavListNew(String page) {
        //   showDialog();
        Call<FavNewResponce> call = apiService.getFavListNew(authToken, "application/json", page);
        call.enqueue(new Callback<FavNewResponce>() {
            @Override
            public void onResponse(Call<FavNewResponce> call, Response<FavNewResponce> response) {
                // Log.e("FavlistNew", new Gson().toJson(response.body()));
                if (response.isSuccessful() & response.body() != null) {
                    try {
                        mApiResponseInterface.isSuccess(response.body(), Constant.FAVOURITE_LIST_NEW);
                    } catch (Exception e) {
                    }
                }
                //         closeDialog();
            }

            @Override
            public void onFailure(Call<FavNewResponce> call, Throwable t) {
                //          closeDialog();
                Log.e("FavlistError", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void doFavourite(int id) {
        Call<AddRemoveFavResponse> call = apiService.doFavourite(authToken, id);
        call.enqueue(new Callback<AddRemoveFavResponse>() {
            @Override
            public void onResponse(Call<AddRemoveFavResponse> call, Response<AddRemoveFavResponse> response) {
                //Log.e("auth", authToken);
                // Log.e("doFav", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {

                    if (!response.body().getResult().isEmpty()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.DO_FAVOURITE);
                    }
                }
            }

            @Override
            public void onFailure(Call<AddRemoveFavResponse> call, Throwable t) {
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void searchUser(String keyword, String pageNumber) {

     /*   Log.e("keyword", keyword);
        Log.e("pageNumber", pageNumber);
        Log.e("authToken", authToken);*/


        Call<UserListResponse> call = apiService.searchUser(authToken, "application/json", keyword, pageNumber, "16");
        call.enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                //   if (response.isSuccessful() && response.body() != null) {

                // Log.e("callTest", new Gson().toJson(response.body()));
                mApiResponseInterface.isSuccess(response.body(), Constant.SEARCH_USER);

            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void getRechargeList() {
        showDialog();
        Call<RechargePlanResponse> call = apiService.getRechargeList(authToken, "application/json");
        call.enqueue(new Callback<RechargePlanResponse>() {
            @Override
            public void onResponse(Call<RechargePlanResponse> call, Response<RechargePlanResponse> response) {

                Log.e("RechargeListData", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.RECHARGE_LIST);

                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<RechargePlanResponse> call, Throwable t) {
                closeDialog();
                //      Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getRechargeListStripe() {
        showDialog();
        Call<RechargePlanResponse> call = apiService.getRechargeListStripe(authToken, "application/json");
        call.enqueue(new Callback<RechargePlanResponse>() {
            @Override
            public void onResponse(Call<RechargePlanResponse> call, Response<RechargePlanResponse> response) {

                Log.e("RechargeListDataStripe", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.RECHARGE_LIST);

                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<RechargePlanResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getWalletAmount() {
        //  showDialog();
        Call<WalletBalResponse> call = apiService.getWalletBalance(authToken, "application/json");
        call.enqueue(new Callback<WalletBalResponse>() {
            @Override
            public void onResponse(Call<WalletBalResponse> call, Response<WalletBalResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.WALLET_AMOUNT);
                }
                //        closeDialog();
            }

            @Override
            public void onFailure(Call<WalletBalResponse> call, Throwable t) {
                //       closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getWalletAmount2() {
        //   showDialog();
        Call<WalletBalResponse> call = apiService.getWalletBalance(authToken, "application/json");
        call.enqueue(new Callback<WalletBalResponse>() {
            @Override
            public void onResponse(Call<WalletBalResponse> call, Response<WalletBalResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.WALLET_AMOUNT2);

                }
                //       closeDialog();
            }

            @Override
            public void onFailure(Call<WalletBalResponse> call, Throwable t) {
                //         closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getTransactionHistory() {
        showDialog();
        Call<WalletResponce> call = apiService.getWalletHistory(authToken, "application/json");
        call.enqueue(new Callback<WalletResponce>() {
            @Override
            public void onResponse(Call<WalletResponce> call, Response<WalletResponce> response) {
                //   Log.e("wallHisResponce", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.TRANSACTION_HISTORY);

                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<WalletResponce> call, Throwable t) {
                //     Log.e("wallHisResponce", t.getMessage());
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getTransactionHistoryFemale(int page) {
        showDialog();
        Call<WalletResponce> call = apiService.getWalletHistoryFemale(authToken, "application/json", page);
        call.enqueue(new Callback<WalletResponce>() {
            @Override
            public void onResponse(Call<WalletResponce> call, Response<WalletResponce> response) {
                // Log.e("wallHisResponce", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.TRANSACTION_HISTORY);

                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<WalletResponce> call, Throwable t) {
                //  Log.e("wallHisResponce", t.getMessage());
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    // new Api for female history new data for income Report 14/4/21
    public void getWalletHistoryFemaleNew() {
        showDialog();
        Call<WallateResponceFemale> call = apiService.getWalletHistoryFemaleNew(authToken, "application/json");
        call.enqueue(new Callback<WallateResponceFemale>() {
            @Override
            public void onResponse(Call<WallateResponceFemale> call, Response<WallateResponceFemale> response) {
                Log.e("wallHisResponceData", new Gson().toJson(response.body()));
                if (response.body().getSuccess()) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.TRANSACTION_HISTORY_NEW);

                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<WallateResponceFemale> call, Throwable t) {
                Log.e("wallHisResponceNew", t.getMessage());
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getWalletHistroyFilter(String filterData) {
        showDialog();
        Call<WalletfilterResponce> call = apiService.getWalletHistoryFilter(authToken, "application/json", filterData);
        call.enqueue(new Callback<WalletfilterResponce>() {
            @Override
            public void onResponse(Call<WalletfilterResponce> call, Response<WalletfilterResponce> response) {
                //      Log.e("walletFilter", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.FILTER_DATA);

                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<WalletfilterResponce> call, Throwable t) {
                //     Log.e("walletFilter", t.getMessage());
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void rechargeWallet(String transactionId, String planId) {
        showDialog();
        Log.e("transactionId", transactionId);
        Log.e("planId", planId);

        Call<WalletRechargeResponse> call = apiService.rechargeWallet(authToken, "application/json", transactionId, planId, "IAP");
        call.enqueue(new Callback<WalletRechargeResponse>() {
            @Override
            public void onResponse(Call<WalletRechargeResponse> call, Response<WalletRechargeResponse> response) {
                Log.e("IAP-Recharge", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.RECHARGE_WALLET);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<WalletRechargeResponse> call, Throwable t) {
                closeDialog();
                Log.e("IAP-Recharge-Error", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reportUser(String id, String isBlock, String des, String inputDes) {
        showDialog();
        Call<ReportResponse> call = apiService.reportUser(authToken, "application/json", id, isBlock, des, inputDes);
        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.REPORT_USER);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void redeemCoins(String points) {
        showDialog();
        Call<WalletRechargeResponse> call = apiService.redeemCoins(authToken, "application/json", points);
        call.enqueue(new Callback<WalletRechargeResponse>() {
            @Override
            public void onResponse(Call<WalletRechargeResponse> call, Response<WalletRechargeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.REDEEM_EARNING);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<WalletRechargeResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

  /*  public void deductCallCharge(String callerId, String callRate, String callDuration) {
        showDialog();
        Call<WalletRechargeResponse> call = apiService.deductCallCharge(authToken, callerId, callRate, callDuration);
        call.enqueue(new Callback<WalletRechargeResponse>() {
            @Override
            public void onResponse(Call<WalletRechargeResponse> call, Response<WalletRechargeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.DEDUCT_CALL_CHARGE);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<WalletRechargeResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }*/

    public void getProfileDetails() {
        //   showDialog();
        Call<ProfileDetailsResponse> call = apiService.getProfileDetails(authToken, "application/json");
        call.enqueue(new Callback<ProfileDetailsResponse>() {
            @Override
            public void onResponse(Call<ProfileDetailsResponse> call, Response<ProfileDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //    Log.e("profileDetail: ", new Gson().toJson(response.body()));

                    //  if (response.body().isSuccess()) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.PROFILE_DETAILS);

                    // } else {
                    //      mApiResponseInterface.isError(response.body().getError());
                    //  }
                }
                //         closeDialog();
            }

            @Override
            public void onFailure(Call<ProfileDetailsResponse> call, Throwable t) {
                //        closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateProfileDetails(String name, String city, String dob, String aboutUser, MultipartBody.Part part, boolean is_album) {
        // Log.e("authToken",authToken);
        showDialog();
        Call<UpdateProfileResponse> call;

        if (city.isEmpty() && dob.isEmpty() && aboutUser.isEmpty()) {
            Log.e("currentnamefirst", name);
            //  RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            //  MultipartBody.Part picToUpload = MultipartBody.Part.createFormData("profile_pic[]", file.getName(), requestBody);
            call = apiService.updateProfileDetails(authToken, "application/json", part, is_album);

        } else {
            Log.e("currentnamesecond", name);
            call = apiService.updateProfileDetails(authToken, "application/json", name, city, dob, aboutUser);
        }

        call.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                Log.e("updateProfile", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.UPDATE_PROFILE);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                Log.e("profileError", t.getMessage());
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendVideo(MultipartBody.Part part) {

        Call<VideoResponce> call;

        //  RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        //  MultipartBody.Part picToUpload = MultipartBody.Part.createFormData("profile_pic[]", file.getName(), requestBody);
        call = apiService.sendVideo(authToken, "application/json", part);

        call.enqueue(new Callback<VideoResponce>() {
            @Override
            public void onResponse(Call<VideoResponce> call, Response<VideoResponce> response) {
                Log.e("vdoResponce", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getSuccess()) {
                        //       Toast.makeText(mContext, response.body().getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoResponce> call, Throwable t) {
                Log.e("vdoERROR", t.getMessage());
                //         Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteProfileImage(String id) {
        showDialog();

        Call<DeleteImageResponse> call = apiService.deleteProfileImage(authToken, "application/json", id);
        call.enqueue(new Callback<DeleteImageResponse>() {
            @Override
            public void onResponse(Call<DeleteImageResponse> call, Response<DeleteImageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.DELETE_PICTURE);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<DeleteImageResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setProfilePicture(String id) {
        showDialog();

        Call<DeleteImageResponse.Result> call = apiService.setProfileImage(authToken, "application/json", id);
        call.enqueue(new Callback<DeleteImageResponse.Result>() {
            @Override
            public void onResponse(Call<DeleteImageResponse.Result> call, Response<DeleteImageResponse.Result> response) {
                if (response.isSuccessful() && response.body() != null) {

                    //   if (response.body().isSuccess()) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.SET_PROFILE_PICTURE);

                    // } else {
                    //     mApiResponseInterface.isError(response.body().getError());
                    //}
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<DeleteImageResponse.Result> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendComplaint(String heading, String des) {
        showDialog();
        Call<ReportResponse> call = apiService.sendComplaint(authToken, "application/json", heading, des);
        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.RAISE_COMPLIANT);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void viewTicket() {
        showDialog();
        Call<ViewTicketResponse> call = apiService.viewTicket(authToken, "application/json");
        call.enqueue(new Callback<ViewTicketResponse>() {
            @Override
            public void onResponse(Call<ViewTicketResponse> call, Response<ViewTicketResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.VIEW_TICKET);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ViewTicketResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void changeOnlineStatus(int status) {
        //  showDialog();
        Call<OnlineStatusResponse> call = apiService.manageOnlineStatus(authToken, "application/json", status);

        call.enqueue(new Callback<OnlineStatusResponse>() {
            @Override
            public void onResponse(Call<OnlineStatusResponse> call, Response<OnlineStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("changeOnlineStatus", new Gson().toJson(response.body()));
                    if (response.body().getResult() != null) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.MANAGE_ONLINE_STATUS);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                //  closeDialog();
            }

            @Override
            public void onFailure(Call<OnlineStatusResponse> call, Throwable t) {
                //closeDialog();
                //Toast.makeText(mContext, "Network Error MANAGE_ONLINE_STATUS", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void changeOnlineStatusBack(int status) {
        //  showDialog();
        Call<OnlineStatusResponse> call = apiService.manageOnlineStatus(authToken, "application/json", status);

        call.enqueue(new Callback<OnlineStatusResponse>() {
            @Override
            public void onResponse(Call<OnlineStatusResponse> call, Response<OnlineStatusResponse> response) {
                Log.e("onlineSatusBack", new Gson().toJson(response.body()));
                //closeDialog();
                new SessionManager(mContext).setOnlineFromBack(0);
            }

            @Override
            public void onFailure(Call<OnlineStatusResponse> call, Throwable t) {
                //closeDialog();
            }
        });
    }

    public void sendCallRecord(CallRecordBody callRecordBody) {
        showDialog();
        // Log.e("callStartFun", new Gson().toJson(callRecordBody));
        Call<Object> call = apiService.sendCallRecord(authToken, "application/json", callRecordBody);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("callStart", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {

                    //  if (response.body().isSuccess()) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.SEND_CALL_RECORD);

                    //   } else {
                    //    mApiResponseInterface.isError(response.body().getError());
                    //  }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void endCall(CallRecordBody callRecordBody) {
        showDialog();
        Call<Object> call = apiService.sendCallRecord(authToken, "application/json", callRecordBody);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("callEnd", new Gson().toJson(response.body()));

                //   Log.e("callEndArrayData", new Gson().toJson(new SessionManager(mContext).getUserEndcalldata()));

                if (response.isSuccessful() && response.body() != null) {
                    new SessionManager(mContext).setUserGetendcalldata("success");
                    mApiResponseInterface.isSuccess(response.body(), Constant.END_CALL);
                } else {
                    new SessionManager(mContext).setUserGetendcalldata("error");
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                closeDialog();
                //  Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
                //   Log.e("callEndError", t.getMessage());
                new SessionManager(mContext).setUserGetendcalldata("error");
            }
        });
    }

    public void VoiceCallData(CallRecordBodyAudio callRecordBody) {
        Log.e("callData", new Gson().toJson(callRecordBody));

        showDialog();
        Call<Object> call = apiService.sendCallRecordAudio(authToken, "application/json", callRecordBody);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("callStatus", new Gson().toJson(response.body()));

                //   Log.e("callEndArrayData", new Gson().toJson(new SessionManager(mContext).getUserEndcalldata()));

                if (response.isSuccessful() && response.body() != null) {
                    new SessionManager(mContext).setUserGetendcalldata("success");
                    mApiResponseInterface.isSuccess(response.body(), Constant.END_CALL);
                } else {
                    new SessionManager(mContext).setUserGetendcalldata("error");
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                closeDialog();
                //  Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
                //   Log.e("callEndError", t.getMessage());
                new SessionManager(mContext).setUserGetendcalldata("error");
            }
        });
    }

    public void getPromotionBanner() {
        showDialog();
        Call<BannerResponse> call = apiService.getPromotions(authToken, "application/json");
        call.enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.GET_BANNER);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendOtp(String emailId, String purpose) {
        showDialog();
        Call<SentOtpResponse> call = apiService.sendOTP(emailId, purpose);
        call.enqueue(new Callback<SentOtpResponse>() {
            @Override
            public void onResponse(Call<SentOtpResponse> call, Response<SentOtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.SEND_OTP);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(mContext, "UnAuthorized", Toast.LENGTH_SHORT).show();
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<SentOtpResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void verifyOtp(String emailId, String otp) {
        showDialog();
        Call<LoginResponse> call = apiService.verifyOTP(emailId, otp);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.VERIFY_OTP);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(mContext, "Wrong OTP", Toast.LENGTH_SHORT).show();
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void changePassword(String password) {
        showDialog();
        Call<ReportResponse> call = apiService.changePassword(authToken, "application/json", password);
        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.CHANGE_PASSWORD);

                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(mContext, "Unauthorized", Toast.LENGTH_SHORT).show();
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void isChatServicePurchased() {
        //   showDialog();
        Call<ChatPurchaseValidity> call = apiService.isChatServicePurchased(authToken, "application/json");
        call.enqueue(new Callback<ChatPurchaseValidity>() {
            @Override
            public void onResponse(Call<ChatPurchaseValidity> call, Response<ChatPurchaseValidity> response) {
                if (response.isSuccessful() && response.body() != null) {

                    mApiResponseInterface.isSuccess(response.body(), Constant.IS_CHAT_SERVICE_ACTIVE);

                }
                //        closeDialog();
            }

            @Override
            public void onFailure(Call<ChatPurchaseValidity> call, Throwable t) {
                //         closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createPayment(int plan_id) {
        Log.e("createPaymentPlainId", plan_id + "");
        showDialog();
        Call<CreatePaymentResponse> call = apiService.createPayment(authToken, "application/json", plan_id);
        call.enqueue(new Callback<CreatePaymentResponse>() {
            @Override
            public void onResponse(Call<CreatePaymentResponse> call, Response<CreatePaymentResponse> response) {
                Log.e("createPayment", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.CREATE_PAYMENT);
                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<CreatePaymentResponse> call, Throwable t) {
                closeDialog();
//                Log.e("createPaymentError", t.getMessage());

                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void getUserRating(String host_id, String rate, String tag) {
        showDialog();
        Call<RatingResponce> call = apiService.getUserRating(authToken, "application/json", host_id, rate, tag);
        call.enqueue(new Callback<RatingResponce>() {
            @Override
            public void onResponse(Call<RatingResponce> call, Response<RatingResponce> response) {
                Log.e("rateValue", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.GET_USER_RATING);
                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<RatingResponce> call, Throwable t) {
                closeDialog();
                Log.e("rateValueError", t.getMessage());

                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    //new function for show rating female on view profile page 6/5/21
    public void getRateCountForHost(String id) {
        //showDialog();
        // Call<RatingDataResponce> call = apiService.getRateCountForHost(authToken, "application/json", "397445");
        Call<UserListResponseNewData> call = apiService.getRateCountForHost(authToken, "application/json", id);
        call.enqueue(new Callback<UserListResponseNewData>() {
            @Override
            public void onResponse(Call<UserListResponseNewData> call, Response<UserListResponseNewData> response) {
                try {
                    if (response.body().getSuccess()) {
                        Log.e("getRateCountForHostNew", new Gson().toJson(response.body()));
                        mApiResponseInterface.isSuccess(response.body(), Constant.GET_RATING_COUNT);
                    }
                } catch (Exception e) {
                }

            }

            @Override
            public void onFailure(Call<UserListResponseNewData> call, Throwable t) {
                //Log.e("getrateCountForHostnewError", t.getMessage());
                // closeDialog();
            }
        });
    }

    public void getPaymentSelector() {
        //showDialog();
        Call<PaymentSelectorResponce> call = apiService.getPaymentSelector(authToken, "application/json");
        call.enqueue(new Callback<PaymentSelectorResponce>() {
            @Override
            public void onResponse(Call<PaymentSelectorResponce> call, Response<PaymentSelectorResponce> response) {
                try {
                    if (response.body().getSuccess()) {
                        Log.e("getPaymentSelectorData", new Gson().toJson(response.body()));
                        mApiResponseInterface.isSuccess(response.body(), Constant.GET_PAYMENT_SELECTOR);
                    }
                } catch (Exception e) {
                }

            }

            @Override
            public void onFailure(Call<PaymentSelectorResponce> call, Throwable t) {
                Log.e("getPaymentSelectorError", t.getMessage());
                // closeDialog();
            }
        });
    }


    public void verifyPayment(String transaction_id, String orderId) {
        showDialog();

     /*   Log.e("transaction_id", transaction_id);
        Log.e("orderId", orderId);
        Log.e("plan_id", plan_id);*/

        Call<ReportResponse> call = apiService.verifyPayment(authToken, "application/json", transaction_id, orderId);
        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                //           Log.e("verifyPayment", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.VERIFY_PAYMENT);
                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                closeDialog();
                //         Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getRecentRecharges() {
        //  showDialog();
        Call<RecentRechargeResponse> call = apiService.getRecentRecharges(authToken, "application/json");
        call.enqueue(new Callback<RecentRechargeResponse>() {
            @Override
            public void onResponse(Call<RecentRechargeResponse> call, Response<RecentRechargeResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.RECENT_RECHARGES);
                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                //        closeDialog();
            }

            @Override
            public void onFailure(Call<RecentRechargeResponse> call, Throwable t) {
                //        closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getVideoList() {
        Call<OnCamResponse> call = apiService.getVideoList(authToken, "application/json");
        call.enqueue(new Callback<OnCamResponse>() {
            @Override
            public void onResponse(Call<OnCamResponse> call, Response<OnCamResponse> response) {
                //   Log.e("getVideoList", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.GET_ONCAM_LIST);
                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                }
                //             closeDialog();
            }

            @Override
            public void onFailure(Call<OnCamResponse> call, Throwable t) {
                //              closeDialog();
                mApiResponseInterface.isError("Network Error");
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getBanList() {
        Call<BanResponce> call = apiService.getBanData(authToken, "application/json");

        call.enqueue(new Callback<BanResponce>() {
            @Override
            public void onResponse(Call<BanResponce> call, Response<BanResponce> response) {
                //    Log.e("banResponce", new Gson().toJson(response.body()));
                try {
                    if (response.body().isSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.BAN_DATAP);
                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } catch (Exception e) {
                    mApiResponseInterface.isError("Network Error");
                }
            }

            @Override
            public void onFailure(Call<BanResponce> call, Throwable t) {
                //   Log.e("banError", t.getMessage());
                mApiResponseInterface.isError("Network Error");

                //                mApiResponseInterface.isError("Network Error");
            }
        });
    }

    public void getUserLanguage() {
        Call<LanguageResponce> call = apiService.getLanguageData(authToken, "application/json");

        call.enqueue(new Callback<LanguageResponce>() {
            @Override
            public void onResponse(Call<LanguageResponce> call, Response<LanguageResponce> response) {
                //     Log.e("Language", new Gson().toJson(response.body()));
                // Log.e("Auth", new SessionManager(mContext).getUserToken());

                try {
                    if (!response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.LAN_DATA);
                    }
                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.LAN_DATA);
                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } catch (Exception e) {
                    try {
                        mApiResponseInterface.isError(response.body().getError());
                    } catch (Exception ex) {

                    }
                }
            }

            @Override
            public void onFailure(Call<LanguageResponce> call, Throwable t) {
                //     Log.e("LanguageError", t.getMessage());
                mApiResponseInterface.isError(t.getMessage());
            }
        });

    }

    public void getUserLogout() {
        Call<LogoutResponce> call = apiService.logout(authToken, "application/json");
        //      Log.e("logoutReq", authToken + "application/json");

        call.enqueue(new Callback<LogoutResponce>() {
            @Override
            public void onResponse(Call<LogoutResponce> call, Response<LogoutResponce> response) {
                         //  Log.e("logout", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<LogoutResponce> call, Throwable t) {
                //            Log.e("logoutError", t.getMessage());

            }
        });
      /*  call.enqueue(new Callback<LanguageResponce>() {
            @Override
            public void onResponse(Call<LanguageResponce> call, Response<LanguageResponce> response) {
                Log.e("Language", new Gson().toJson(response.body()));
                //    Log.e("Auth", new SessionManager(mContext).getUserToken());

                try {

                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.LAN_DATA);
                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } catch (Exception e) {
                    mApiResponseInterface.isError("Network Error");
                }
            }

            @Override
            public void onFailure(Call<LanguageResponce> call, Throwable t) {
                Log.e("ResponceErr", t.getMessage());
                mApiResponseInterface.isError("Network Error");
            }
        });*/

    }

    public void sendUserGift(SendGiftRequest sendGiftRequest) {

        Call<SendGiftResult> call = apiService.sendGift(authToken, sendGiftRequest);

        // Log.e("sendGiftReq", authToken + new Gson().toJson(sendGiftRequest));
        call.enqueue(new Callback<SendGiftResult>() {
            @Override
            public void onResponse(Call<SendGiftResult> call, Response<SendGiftResult> response) {
                Log.e("SendGift", new Gson().toJson(response.body()));
                try {
                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.SEND_GIFT);
                    } else {
                        mApiResponseInterface.isError(response.body().getError());
                    }
                } catch (Exception e) {
                    mApiResponseInterface.isError("Network Error");
                }
            }

            @Override
            public void onFailure(Call<SendGiftResult> call, Throwable t) {
                //     Log.e("SendGiftError", t.getMessage());
                mApiResponseInterface.isError("Network Error");
            }
        });


    }

    public void hostSendGiftRequest(RequestGiftRequest requestGiftRequest) {

        Call<RequestGiftResponce> call = apiService.sendGiftRequestFromHost(authToken, requestGiftRequest);
        //      Log.e("hostSendGiftRequest", authToken + new Gson().toJson(requestGiftRequest));
        call.enqueue(new Callback<RequestGiftResponce>() {
            @Override
            public void onResponse(Call<RequestGiftResponce> call, Response<RequestGiftResponce> response) {
                // Log.e("hostSendGiftRequest", new Gson().toJson(response.body()));
                Toast.makeText(mContext, "Gift request send.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<RequestGiftResponce> call, Throwable t) {
                //     Log.e("SendGiftError", t.getMessage());
                //    mApiResponseInterface.isError("Network Error");
            }
        });

    }


    public void getVideoForProfile(String profileId) {

        Call<VideoPlayResponce> call = apiService.getVideoplay(authToken, "application/json", profileId);
        //  Log.e("sendGiftReq", authToken + new Gson().toJson(sendGiftRequest));
        call.enqueue(new Callback<VideoPlayResponce>() {
            @Override
            public void onResponse(Call<VideoPlayResponce> call, Response<VideoPlayResponce> response) {
                // Log.e("getVideoForProfile", new Gson().toJson(response.body()));

                try {
                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.PLAY_VIDEO);
                    }
                } catch (Exception e) {
                    //  mApiResponseInterface.isError("Network Error");
                }
            }

            @Override
            public void onFailure(Call<VideoPlayResponce> call, Throwable t) {
                //Log.e("getVideoForProfileError", t.getMessage());
                //mApiResponseInterface.isError("Network Error");
            }
        });
    }

    public void getStripeKey() {
        Call<KeyResponce> call = apiService.getStripeKey();

        call.enqueue(new Callback<KeyResponce>() {
            @Override
            public void onResponse(Call<KeyResponce> call, Response<KeyResponce> response) {
                //    Log.e("getStripeKey", new Gson().toJson(response.body()));
                try {
                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.STRIPE_KEY);
                    }
                } catch (Exception e) {
                    //  mApiResponseInterface.isError("Network Error");
                }
            }

            @Override
            public void onFailure(Call<KeyResponce> call, Throwable t) {

                //     Log.e("getStripeKeyError", t.getMessage());

            }
        });
    }

    public void getStripePaymentInit(RequestModel requestModel) {

        Call<ServerResponce> call = apiService.getStripeInit("application/json", "application/json",
                requestModel);
        //  Log.e("sendGiftReq", authToken + new Gson().toJson(sendGiftRequest));
        call.enqueue(new Callback<ServerResponce>() {
            @Override
            public void onResponse(Call<ServerResponce> call, Response<ServerResponce> response) {
                //               Log.e("getStripeKey", new Gson().toJson(response.body()));

                try {
                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.STRIPE_INIT);
                    }
                } catch (Exception e) {
                    //  mApiResponseInterface.isError("Network Error");
                }
            }

            @Override
            public void onFailure(Call<ServerResponce> call, Throwable t) {
                //Log.e("getVideoForProfileError", t.getMessage());
                //mApiResponseInterface.isError("Network Error");
            }
        });
    }

    public void sendDataFromStripe(PayRequest payRequest) {

        Call<PayResponce> call = apiService.getStripeOnSucess(authToken, "application/json", "application/json",
                payRequest);

      /*  Log.e("authToken", authToken);
        Log.e("payRequestinFUN", new Gson().toJson(payRequest));*/

        call.enqueue(new Callback<PayResponce>() {
            @Override
            public void onResponse(Call<PayResponce> call, Response<PayResponce> response) {

                //   Log.e("getStripeSuccess", new Gson().toJson(response.body()));


                try {
                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.STRIPE_SUCESS);
                    }
                } catch (Exception e) {
                    //  mApiResponseInterface.isError("Network Error");
                }
            }

            @Override
            public void onFailure(Call<PayResponce> call, Throwable t) {

            }
        });
    }

    public void getPaymentData() {

        Call<PaymentGatewayResponce> call = apiService.getPaymentData(authToken, "application/json");

      /*  Log.e("authToken", authToken);
        Log.e("payRequestinFUN", new Gson().toJson(payRequest));*/

        call.enqueue(new Callback<PaymentGatewayResponce>() {
            @Override
            public void onResponse(Call<PaymentGatewayResponce> call, Response<PaymentGatewayResponce> response) {
                //  Log.e("getOnePayData", new Gson().toJson(response.body()));
                try {
                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.PAY_DETAILS);
                    }
                } catch (Exception e) {
                    //  mApiResponseInterface.isError("Network Error");
                }
            }

            @Override
            public void onFailure(Call<PaymentGatewayResponce> call, Throwable t) {
                //Log.e("getOnePayDataError", t.getMessage());

            }
        });
    }

    public void submitEndCallData(ArrayList<EndCallData> endCallData) {

        Call<EncCallResponce> call = apiService.sendEndCallTime(authToken, "application/json", endCallData);

        call.enqueue(new Callback<EncCallResponce>() {
            @Override
            public void onResponse(Call<EncCallResponce> call, Response<EncCallResponce> response) {
                //   Log.e("submitEndCallData", new Gson().toJson(response.body()));
                try {
                    if (response.body().getSuccess()) {
                        new SessionManager(mContext).setUserGetendcalldata("success");
                    } else {
                        new SessionManager(mContext).setUserGetendcalldata("error");
                    }
                } catch (Exception e) {
                    new SessionManager(mContext).setUserGetendcalldata("error");
                }
            }

            @Override
            public void onFailure(Call<EncCallResponce> call, Throwable t) {
                // Log.e("submitEndCallError", t.getMessage());
                new SessionManager(mContext).setUserGetendcalldata("error");

            }
        });
    }


    public void getGiftCountForHost(String id) {
        // showDialog();
        Call<GiftCountResult> call = apiService.getGiftCountForHost(authToken, "application/json", id);

        call.enqueue(new Callback<GiftCountResult>() {
            @Override
            public void onResponse(Call<GiftCountResult> call, Response<GiftCountResult> response) {
                // Log.e("getGiftCountForHost", new Gson().toJson(response.body()));
                closeDialog();
                try {
                    if (response.body().getSuccess()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.GET_GIFT_COUNT);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<GiftCountResult> call, Throwable t) {
                //Log.e("getGiftCountForHostError", t.getMessage());
                closeDialog();
            }
        });
    }

    public void dailVoiceCallUser(String audio_call_rate, String connecting_user_id, String outgoing_time) {
        showDialog();
        Call<VoiceCallResponce> call = apiService.dailVoiceCall(authToken, "application/json", audio_call_rate, connecting_user_id, outgoing_time, "");
        call.enqueue(new Callback<VoiceCallResponce>() {
            @Override
            public void onResponse(Call<VoiceCallResponce> call, Response<VoiceCallResponce> response) {
                Log.e("dailVoiceCallUser", new Gson().toJson(response.body()));

                if (response.body().getSuccess()) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.GENERATE_VOICE_CALL_TOKEN);
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<VoiceCallResponce> call, Throwable t) {
                closeDialog();
                //  Log.e("generateCallError", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getGiftList() {
        Call<ResultGift> call = apiService.getGift(authToken);
        call.enqueue(new Callback<ResultGift>() {
            @Override
            public void onResponse(Call<ResultGift> call, Response<ResultGift> response) {
                Log.e("getgift", new Gson().toJson(response.body()));
                if (response.body().isStatus()) {
                    if (response.body().isStatus()) {
                        mApiResponseInterface.isSuccess(response.body(), Constant.GET_GIFT_LIST);
                    } else {
                        mApiResponseInterface.isError(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultGift> call, Throwable t) {
                Toast.makeText(mContext, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getMessageCallDataFunction(MessageCallDataRequest messageCallDataRequest) {
        Call<MessageCallDataResponce> call = apiService.getMessageCallData(authToken, "application/json", messageCallDataRequest);
        call.enqueue(new Callback<MessageCallDataResponce>() {
            @Override
            public void onResponse(Call<MessageCallDataResponce> call, Response<MessageCallDataResponce> response) {

                Log.e("getMessag", new Gson().toJson(response.body()));
                try {
                    if (response.body().getSuccess()) {

                        mApiResponseInterface.isSuccess(response.body(), Constant.GET_MESSAGE_CALL_DETAILS);

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<MessageCallDataResponce> call, Throwable t) {
                Log.e("getMessagError", t.getMessage());
                Toast.makeText(mContext, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getRemainingGiftCardFunction() {
        Call<RemainingGiftCardResponce> call = apiService.getRemainingGiftCardResponce(authToken, "application/json");
        call.enqueue(new Callback<RemainingGiftCardResponce>() {
            @Override
            public void onResponse(Call<RemainingGiftCardResponce> call, Response<RemainingGiftCardResponce> response) {
                Log.e("freeGiftCardValue", new Gson().toJson(response.body()));
                // if (response.body().getSuccess()) {
                mApiResponseInterface.isSuccess(response.body(), Constant.GET_REMAINING_GIFT_CARD);
                // }
            }

            @Override
            public void onFailure(Call<RemainingGiftCardResponce> call, Throwable t) {
                Log.e("freeGiftCardError", t.getMessage());
            }
        });
    }

    public void getRemainingGiftCardDisplayFunction() {
        Call<RemainingGiftCardResponce> call = apiService.getRemainingGiftCardResponce(authToken, "application/json");
        call.enqueue(new Callback<RemainingGiftCardResponce>() {
            @Override
            public void onResponse(Call<RemainingGiftCardResponce> call, Response<RemainingGiftCardResponce> response) {
                Log.e("freeGiftCardData", new Gson().toJson(response.body()));
                // if (response.body().getSuccess()) {
                mApiResponseInterface.isSuccess(response.body(), Constant.GET_REMAINING_GIFT_CARD_DISPLAY);
                // }
            }

            @Override
            public void onFailure(Call<RemainingGiftCardResponce> call, Throwable t) {
                Log.e("freeGiftCardError", t.getMessage());
            }
        });
    }


    //new api for Guest edit name after guest login 29/05/21
    public void upDateGuestProfile(RequestBody name, MultipartBody.Part part) {
        // Log.e("authToken",authToken);
        Call<Object> call;
        Log.e("currentGuestName", new Gson().toJson(name));
        call = apiService.upDateGuestProfile(authToken, "application/json", name, part);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("updateGuestProfile", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.UPDATE_GUEST_PROFILE);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("GuestprofileError", t.getMessage());
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void UpgradeUserLevel() {
        Call<UpgradedLevelResponce> call = apiService.UpgradeUserLevel(authToken, "application/json");
        call.enqueue(new Callback<UpgradedLevelResponce>() {
            @Override
            public void onResponse(Call<UpgradedLevelResponce> call, Response<UpgradedLevelResponce> response) {
                Log.e("userUpgradedLevel", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.UPGRADED_LEVELS);
                }
            }

            @Override
            public void onFailure(Call<UpgradedLevelResponce> call, Throwable t) {
                Log.e("userUpgradedLevelError", t.getMessage());
            }
        });


    }

    // new Api for User levels history  22/06/21
    public void getUserLevelHistory() {
        Log.e("here User Level", "here User Level");
        showDialog();
        Call<GetLevelResponce> call = apiService.getUserLevelHistory(authToken, "application/json");
        call.enqueue(new Callback<GetLevelResponce>() {
            @Override
            public void onResponse(Call<GetLevelResponce> call, Response<GetLevelResponce> response) {
                Log.e("userlevelData", new Gson().toJson(response.body()));
                if (response.body().getSuccess()) {
                    mApiResponseInterface.isSuccess(response.body(), Constant.GET_USER_LEVELS);

                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<GetLevelResponce> call, Throwable t) {
                Log.e("userleveldataerror", t.getMessage());
                closeDialog();
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void startBroadCastFunction() {

        Call<BroadCastResponce> call = apiService.startBroadCast(authToken, "application/json");
        call.enqueue(new Callback<BroadCastResponce>() {
            @Override
            public void onResponse(Call<BroadCastResponce> call, Response<BroadCastResponce> response) {

                Log.e("startBroadCastFunction", new Gson().toJson(response.body()));

                mApiResponseInterface.isSuccess(response.body(), Constant.GET_BROADCAST_START);
                try {

                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<BroadCastResponce> call, Throwable t) {
                Log.e("startBroadCastError", t.getMessage());
                Toast.makeText(mContext, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void stopBroadCastFunction(String broadcastId) {
        Call<Object> call = apiService.stopBroadCast(authToken, "application/json", broadcastId);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                Log.e("stopBroadCastFunction", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("stopBroadCastError", t.getMessage());
            }
        });
    }

    public void audiJoinBroadFunction(String broadcastId) {
        Call<Object> call = apiService.audiJoinBroad(authToken, "application/json", broadcastId);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("audiJoinBroadFunction", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("audiJoinBroadError", t.getMessage());
            }
        });
    }

    public void audiLeaveBroadFunction(String broadcastId) {
        Call<Object> call = apiService.audiLeaveBroad(authToken, "application/json", broadcastId);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("audiLeaveBroadFunction", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("audiLeaveBroadError", t.getMessage());
            }
        });
    }

    public void getBroadCastListFunction() {

        Call<BroadcastListResponce> call = apiService.getBroadList(authToken, "application/json");
        call.enqueue(new Callback<BroadcastListResponce>() {
            @Override
            public void onResponse(Call<BroadcastListResponce> call, Response<BroadcastListResponce> response) {

                Log.e("BroadCastList", new Gson().toJson(response.body()));

                try {
                    mApiResponseInterface.isSuccess(response.body(), Constant.GET_BROADCAST_LIST);
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<BroadcastListResponce> call, Throwable t) {
                Log.e("BroadCastListError", t.getMessage());
            }
        });
    }

    public void audiAskToJoinCallinBroadFunction(AudiRequestForCall audiRequestForCall) {

        Call<Object> call = apiService.audiAskToJoinCallinBroad(authToken, "application/json", audiRequestForCall);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("JoinCallinBroad", new Gson().toJson(response.body()));

                try {
                    //  mApiResponseInterface.isSuccess(response.body(), Constant.GET_BROADCAST_LIST);
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("JoinCallinBroadtError", t.getMessage());
            }
        });
    }

    public void hostAcceptJoinCallinBroadFunction(HostAcceptCallRequest hostAcceptCallRequest) {

        Call<Object> call = apiService.hostAcceptJoinCallinBroad(authToken, "application/json", hostAcceptCallRequest);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("AcceptJoinCallinBroad", new Gson().toJson(response.body()));

                try {
                    //  mApiResponseInterface.isSuccess(response.body(), Constant.GET_BROADCAST_LIST);
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("AcceptCallinBroadError", t.getMessage());
            }
        });
    }

    public void cashFreePayment(CashFreePaymentRequest cashFreePaymentRequest) {
        Call<Object> call = apiService.cashFreePayment(authToken, "application/json", cashFreePaymentRequest);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("cashFreePaymentResponce", new Gson().toJson(response.body()));

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("cashFreePaymentError", t.getMessage());
            }
        });
    }

    public void getCfToken(String amount, String plan_id) {
        Log.e("CfTokenAmount", amount);
        Log.e("CfTokenPlanId", plan_id);
        dialog.show();
        Call<CfTokenResponce> call = apiService.getCfToken(authToken, "application/json", amount, plan_id);
        call.enqueue(new Callback<CfTokenResponce>() {
            @Override
            public void onResponse(Call<CfTokenResponce> call, Response<CfTokenResponce> response) {
                Log.e("CfTokenResponce", new Gson().toJson(response.body()));
                dialog.cancel();
                try {
                    mApiResponseInterface.isSuccess(response.body(), Constant.GET_CFTOKEN);
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<CfTokenResponce> call, Throwable t) {
                dialog.cancel();
                Log.e("CfTokenError", t.getMessage());
            }
        });
    }

    public void getcallCutByHost(String unique_id) {
        Call<Object> call = apiService.callCutByHost(authToken, "application/json", unique_id);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("getcallCutByHost", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("getcallCutByHostError", t.getMessage());
            }
        });
    }


    public void showDialog() {
        try {
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}