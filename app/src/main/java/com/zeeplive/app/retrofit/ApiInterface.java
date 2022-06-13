package com.zeeplive.app.retrofit;

import com.zeeplive.app.body.CallRecordBody;
import com.zeeplive.app.body.CallRecordBodyAudio;
import com.zeeplive.app.response.AddRemoveFavResponse;
import com.zeeplive.app.response.AgoraTokenResponse;
import com.zeeplive.app.response.Ban.BanResponce;
import com.zeeplive.app.response.BannerResponse;
import com.zeeplive.app.response.Broadcast.BroadcastCallRequest.AudiRequestForCall;
import com.zeeplive.app.response.Broadcast.BroadcastCallRequest.HostAcceptCallRequest;
import com.zeeplive.app.response.Broadcast.BroadcastList.BroadcastListResponce;
import com.zeeplive.app.response.Call.ResultCall;
import com.zeeplive.app.response.Chat.RequestChatList;
import com.zeeplive.app.response.Chat.ResultChatList;
import com.zeeplive.app.response.ChatPurchaseValidity;
import com.zeeplive.app.response.ChatRoom.RequestChatRoom;
import com.zeeplive.app.response.ChatRoom.ResultChatRoom;
import com.zeeplive.app.response.CreatePaymentResponse;
import com.zeeplive.app.response.DeleteImageResponse;
import com.zeeplive.app.response.DisplayGiftCount.GiftCountResult;
import com.zeeplive.app.response.DisplayRatingCount.RatingDataResponce;
import com.zeeplive.app.response.Employee.RequestIdForEmployee;
import com.zeeplive.app.response.Employee.ResultEmployeeId;
import com.zeeplive.app.response.EndCallData.EncCallResponce;
import com.zeeplive.app.response.EndCallData.EndCallData;
import com.zeeplive.app.response.FavNew.FavNewResponce;
import com.zeeplive.app.response.FavResponse;
import com.zeeplive.app.response.FcmTokenResponse;
import com.zeeplive.app.response.Friendship.RequestFriendShipStatus;
import com.zeeplive.app.response.Friendship.ResultFriendShipStatus;
import com.zeeplive.app.response.Friendship.ResultSendFriendRequest;
import com.zeeplive.app.response.GenderResponse.GenderStatus;
import com.zeeplive.app.response.GetUserLevelResponse.GetLevelResponce;
import com.zeeplive.app.response.LoginResponse;
import com.zeeplive.app.response.MessageCallData.MessageCallDataRequest;
import com.zeeplive.app.response.MessageCallData.MessageCallDataResponce;
import com.zeeplive.app.response.Misscall.RequestMissCall;
import com.zeeplive.app.response.Misscall.ResultMissCall;
import com.zeeplive.app.response.MyResponse;
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
import com.zeeplive.app.response.Report.RequestReport;
import com.zeeplive.app.response.Report.ResultReportIssue;
import com.zeeplive.app.response.ReportResponse;
import com.zeeplive.app.response.RequestGiftRequest.RequestGiftRequest;
import com.zeeplive.app.response.RequestGiftRequest.RequestGiftResponce;
import com.zeeplive.app.response.Sender;
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
import com.zeeplive.app.response.block.RequestBlockUnclock;
import com.zeeplive.app.response.block.ResultBlockUnblock;
import com.zeeplive.app.response.Broadcast.BroadcastStart.BroadCastResponce;
import com.zeeplive.app.response.coin.ResultCoinPlan;
import com.zeeplive.app.response.gift.ResultGift;
import com.zeeplive.app.response.gift.SendGiftRequest;
import com.zeeplive.app.response.gift.SendGiftResult;
import com.zeeplive.app.response.language.LanguageResponce;
import com.zeeplive.app.response.logout.LogoutResponce;
import com.zeeplive.app.response.message.RequestAllMessages;
import com.zeeplive.app.response.message.RequestMessageRead;
import com.zeeplive.app.response.message.ResultMessage;
import com.zeeplive.app.response.message.ResultMessageRead;
import com.zeeplive.app.response.message.ResultSendMessage;
import com.zeeplive.app.response.order.RequestPlaceOrder;
import com.zeeplive.app.response.order.ResultPlaceOrder;
import com.zeeplive.app.response.videoplay.VideoPlayResponce;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("loginlocal")
    Call<LoginResponse> loginUser(@Field("username") String username, @Field("password") String password);

   /*  @FormUrlEncoded
    @POST("loginLatest")
    Call<LoginResponse> loginUser(@Field("username") String username,
                                  @Field("password") String password,
                                  @Field("myhaskey") String hash);*/


    @FormUrlEncoded
    @POST("registerGenderStatus")
    Call<GenderStatus> genderStatus(@Field("login_type") String login_type,
                                    @Field("device_id") String device_id);


    @FormUrlEncoded
    @POST("aaoregistrationkare")
    Call<LoginResponse> registerUser(@Field("name") String name, @Field("email") String email,
                                     @Field("password") String password, @Field("c_password") String c_password,
                                     @Field("login_type") String login_type, @Field("username") String username,
                                     @Field("mobile") String mobile, @Field("gender") String gender);

    @FormUrlEncoded
    @POST("aaoregistrationkare")
    Call<LoginResponse> guestRegister(@Field("login_type") String login_type,
                                      @Field("device_id") String device_id,
                                      @Field("gender") String gender,
                                      @Field("dob") String dob,
                                      @Field("myhaskey") String hash);

    @FormUrlEncoded
    @POST("aaoregistrationkare")
    Call<LoginResponse> guestRegisterOld(@Field("login_type") String login_type,
                                      @Field("device_id") String device_id,
                                      @Field("myhaskey") String hash);


    @FormUrlEncoded
    @POST("aaoregistrationkare")
    Call<LoginResponse> loginFbGoogle(@Field("name") String name, @Field("login_type") String login_type,
                                      @Field("username") String username,
                                      @Field("gender") String gender,
                                      @Field("dob") String dob,
                                      @Field("myhaskey") String hash);

    @GET("getToken")
    Call<AgoraTokenResponse> getAgoraToken(@Header("Authorization") String token,
                                           @Header("Accept") String accept,
                                           @Query("connecting_user_id") int id,
                                           @Query("outgoing_time") String outgoingTime,
                                           @Query("convId") String convId);

    @GET("getEmployeeVideoRandomly")
    Call<VideoPlayResponce> getVideoplay(@Header("Authorization") String token,
                                         @Header("Accept") String accept,
                                         @Query("user_id") String convId);

    @GET("dialCall")
    Call<ResultCall> getDailCallRequest(@Header("Authorization") String token,
                                        @Header("Accept") String accept,
                                        @Query("connecting_user_id") int id,
                                        @Query("outgoing_time") String outgoingTime,
                                        @Query("convId") String convId,
                                        @Query("call_rate") int callRate,
                                        @Query("is_free_call") boolean isFreeCall,
                                        @Query("rem_gift_cards") String remGiftCards);
    //for testing server
  /*  @GET("dialCallcoinspersecond")
    Call<ResultCall> getDailCallRequest(@Header("Authorization") String token,
                                        @Header("Accept") String accept,
                                        @Query("connecting_user_id") int id,
                                        @Query("outgoing_time") String outgoingTime,
                                        @Query("convId") String convId,
                                        @Query("call_rate") int callRate,
                                        @Query("is_free_call") boolean isFreeCall,
                                        @Query("rem_gift_cards") String remGiftCards);
*/


    @POST("user-busy")
    Call<Object> sendCallRecord(@Header("Authorization") String token,
                                @Header("Accept") String accept,
                                @Body CallRecordBody callRecordBody);

   /* @POST("user-busy-demo")
    Call<Object> sendCallRecord(@Header("Authorization") String token,
                                @Header("Accept") String accept,
                                @Body CallRecordBody callRecordBody);*/

    @POST("audiocallStatus")
    Call<Object> sendCallRecordAudio(@Header("Authorization") String token,
                                     @Header("Accept") String accept,
                                     @Body CallRecordBodyAudio callRecordBody);

    @GET("dialaudioCall")
    Call<VoiceCallResponce> dailVoiceCall(@Header("Authorization") String token, @Header("Accept") String accept,
                                          @Query("audio_call_rate") String audio_call_rate, @Query("connecting_user_id") String connecting_user_id,
                                          @Query("outgoing_time") String outgoing_time, @Query("convId") String convId);


    @FormUrlEncoded
    @POST("updateFCMToken")
    Call<FcmTokenResponse> registerFcmToken(@Header("Authorization") String token,
                                            @Header("Accept") String accept, @Field("token") String fcmToken);

    @FormUrlEncoded
    @POST("sendOtp")
    Call<SentOtpResponse> sendOTP(@Field("email") String email, @Field("purpose") String purpose);

    @FormUrlEncoded
    @POST("verifyOtp")
    Call<LoginResponse> verifyOTP(@Field("email") String email, @Field("otp") String otp);

    //hide api for view profile page 10/5/21
    @GET("userlist")
    Call<UserListResponse> getUserList(@Header("Authorization") String token, @Header("Accept") String accept, @Query("q") String q,
                                       @Query("page") String p, @Query("per_page_records") String lim, @Query("language_id") String lanid);

    @GET("userlist")
    Call<UserListResponse> searchUser(@Header("Authorization") String token, @Header("Accept") String accept, @Query("q") String q,
                                      @Query("page") String p, @Query("per_page_records") String lim);

    @GET("getHostsList")
    Call<UserListResponse> matchHostList(@Header("Authorization") String token, @Header("Accept") String accept);


/*
   @GET("newuserlistforhomepage")
    Call<UserListResponse> getUserList(@Header("Authorization") String token, @Header("Accept") String accept, @Query("q") String q,
                                       @Query("page") String p, @Query("per_page_records") String lim, @Query("language_id") String lanid);

    @GET("newuserlistforhomepage")
    Call<UserListResponse> searchUser(@Header("Authorization") String token, @Header("Accept") String accept, @Query("q") String q,
                                      @Query("page") String p, @Query("per_page_records") String lim);
*/

    @GET("getPopularList")
    Call<UserListResponse> getPopulartList(@Header("Authorization") String token, @Header("Accept") String accept, @Query("q") String q,
                                           @Query("page") String p, @Query("per_page_records") String lim, @Query("language_id") String lanid);
    //use new api for view profile page 10/5/21

    @GET("getprofiledata")
    Call<UserListResponseNewData> getProfileData(@Header("Authorization") String token, @Header("Accept") String accept, @Query("q") String q,
                                                 @Query("page") String p, @Query("id") String id, @Query("language_id") String lanid);


    @GET("add_remove_fav")
    Call<AddRemoveFavResponse> doFavourite(@Header("Authorization") String token, @Query("favorite_id") int favorite_id);

    @GET("userfav")
    Call<FavResponse> getFavList(@Header("Authorization") String token, @Header("Accept") String accept);

    @GET("favorites-new")
    Call<FavNewResponce> getFavListNew(@Header("Authorization") String token, @Header("Accept") String accept, @Query("page") String page);

    @GET("planlist")
    Call<RechargePlanResponse> getRechargeList(@Header("Authorization") String token, @Header("Accept") String accept);

    @GET("planlist_stripe")
    Call<RechargePlanResponse> getRechargeListStripe(@Header("Authorization") String token, @Header("Accept") String accept);

    @GET("points")
    Call<WalletBalResponse> getWalletBalance(@Header("Authorization") String token, @Header("Accept") String accept);

    @GET("wallet-history-latest")
    Call<WalletResponce> getWalletHistory(@Header("Authorization") String token, @Header("Accept") String accept);

    @GET("wallet-history-latest")
    Call<WalletResponce> getWalletHistoryFemale(@Header("Authorization") String token, @Header("Accept") String accept
            , @Query("page") int page);

    // new api for get Wallet History Female new for income report 14/4/21
    @GET("wallet-history-incomereport")
    Call<WallateResponceFemale> getWalletHistoryFemaleNew(@Header("Authorization") String token, @Header("Accept") String accept);

    @GET("wallet-history-latest")
    Call<WalletfilterResponce> getWalletHistoryFilter(@Header("Authorization") String token, @Header("Accept") String accept
            , @Query("by_week") String filterData);


    @FormUrlEncoded
    @POST("point_update_new")
    Call<WalletRechargeResponse> rechargeWallet(@Header("Authorization") String token, @Header("Accept") String accept,
                                                @Field("razorpay_id") String id, @Field("plan_id") String planId, @Field("plan_type") String plan_type);

    @FormUrlEncoded
    @POST("point_update")
    Call<WalletRechargeResponse> redeemCoins(@Header("Authorization") String token, @Header("Accept") String accept, @Field("redeem_point") String points);


    /*@FormUrlEncoded
    @POST("point_update")
    Call<WalletRechargeResponse> deductCallCharge(@Header("Authorization") String token,
                                                  @Field("call_receiver_id") String id,
                                                  @Field("callrate") String call_rate,
                                                  @Field("call_duration") String duration);*/


    @POST("details")
    Call<ProfileDetailsResponse> getProfileDetails(@Header("Authorization") String token, @Header("Accept") String accept);

    @FormUrlEncoded
    @POST("change-password")
    Call<ReportResponse> changePassword(@Header("Authorization") String token, @Header("Accept") String accept, @Field("new_password") String pass);


    @Multipart
    @POST("update-profile")
    Call<UpdateProfileResponse> updateProfileDetails(@Header("Authorization") String token,
                                                     @Header("Accept") String accept,
                                                     @Part MultipartBody.Part picToUpload, @Part("is_album") boolean is_album);

    @FormUrlEncoded
    @POST("update-profile")
    Call<UpdateProfileResponse> updateProfileDetails(@Header("Authorization") String token,
                                                     @Header("Accept") String accept,
                                                     @Field("name") String name,
                                                     @Field("city") String city,
                                                     @Field("dob") String dob,
                                                     @Field("about_user") String about_user);

    @FormUrlEncoded
    @POST("compalint")
    Call<ReportResponse> sendComplaint(@Header("Authorization") String token,
                                       @Header("Accept") String accept,
                                       @Field("issue_heading") String heading,
                                       @Field("description") String des);

    @GET("compalint-list")
    Call<ViewTicketResponse> viewTicket(@Header("Authorization") String token, @Header("Accept") String accept);


    @Multipart
    @POST("upload-video-via-mobile")
    Call<VideoResponce> sendVideo(@Header("Authorization") String token,
                                  @Header("Accept") String accept,
                                  @Part MultipartBody.Part vdo);

    @Headers({"Accept: application/json"})
    @GET("delete-profile-image")
    Call<DeleteImageResponse> deleteProfileImage(@Header("Authorization") String token, @Header("Accept") String accept, @Query("id") String id);

    @Headers({"Accept: application/json"})
    @GET("select-profile-pic")
    Call<DeleteImageResponse.Result> setProfileImage(@Header("Authorization") String token, @Header("Accept") String accept, @Query("id") String id);

    @GET("report-user")
    Call<ReportResponse> reportUser(@Header("Authorization") String token, @Header("Accept") String accept,
                                    @Query("report_to") String id, @Query("is_user_block") String b,
                                    @Query("description") String des, @Query("input_description") String in_des);

    @GET("online-status-update")
    Call<OnlineStatusResponse> manageOnlineStatus(@Header("Authorization") String token, @Header("Accept") String accept, @Query("is_online") int is_online);

    @GET("promotions")
    Call<BannerResponse> getPromotions(@Header("Authorization") String token, @Header("Accept") String accept);

    @Headers({"Content-Type:application/json",
            "Authorization:key=AAAAp8dcL_E:APA91bEuvdc3xgr2tWVas358OFTMck3kn53KSk0GWzDGXdhfRZhvVvYNDRnqSYfXfkXyQakB5rlSWFBP917Ln8ksHpIZriq-7govX0uYtyjYV4UqMagoQczefDni83cF068E2rWWKpqp"})

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender sender);

    @POST("chat-validity")
    Call<ChatPurchaseValidity> isChatServicePurchased(@Header("Authorization") String token, @Header("Accept") String accept);

    @POST("latest-recharge")
    Call<RecentRechargeResponse> getRecentRecharges(@Header("Authorization") String token, @Header("Accept") String accept);

    @FormUrlEncoded
    @POST("createpayment")
    Call<CreatePaymentResponse> createPayment(@Header("Authorization") String token, @Header("Accept") String accept, @Field("plan_id") int plan_id);

    @FormUrlEncoded
    @POST("checkpayment")
    Call<ReportResponse> verifyPayment(@Header("Authorization") String token, @Header("Accept") String accept,
                                       @Field("transaction_id") String transaction_id, @Field("order_id") String order_id);

    @GET("video-list")
    Call<OnCamResponse> getVideoList(@Header("Authorization") String token, @Header("Accept") String accept);

    // @FormUrlEncoded
    @POST("check-user-ban_status")
    Call<BanResponce> getBanData(@Header("Authorization") String token, @Header("Accept") String accept);// @FormUrlEncoded

    @GET("languages")
    Call<LanguageResponce> getLanguageData(@Header("Authorization") String token, @Header("Accept") String accept);

    @POST("logout")
    Call<LogoutResponce> logout(@Header("Authorization") String token, @Header("Accept") String accept);


    //chat

    @POST("getEmployeeById")
    Call<ResultEmployeeId> getEmployeeDataByID(
            @Header("Auth-Token") String header1,
            @Header("Csrf-Token") String header2,
            @Body RequestIdForEmployee requestIdForEmployee
    );

    @Multipart
    @POST("send-message")
    Call<ResultSendMessage> sendMessageFromSocket(
            @Part("UserId") RequestBody UserId,
            @Part("conversationId") RequestBody conversationId,
            @Part("_id") RequestBody id,
            @Part("name_1") RequestBody name_1,
            @Part("senderProfilePic") RequestBody senderProfilePic,
            @Part("senderType") RequestBody senderType,
            @Part("receiverId") RequestBody receiverId,
            @Part("receiverName") RequestBody receiverName,
            @Part("receiverImageUrl") RequestBody receiverImageUrl,
            @Part("receiverType") RequestBody receiverType,
            @Part("body") RequestBody body,
            @Part("isFriendAccept") RequestBody isFriendAccept,
            @Part("mimeType") RequestBody mimeType,
            @Part("isBotMessage") RequestBody isBotMessage,
            @Part("from") RequestBody from,
            @Part("to") RequestBody to,
            @Part("action_name") RequestBody action_name,
            @Part("action_id") RequestBody action_id,
            @Part("is_bot_reply") RequestBody is_bot_reply,
            @Part("bot_action") RequestBody bot_action,
            @Part("bot_message_id") RequestBody botmessageid,
            @Part("reply_id") RequestBody reply_id
    );

    @PUT("update-read-messages")
    Call<ResultMessageRead> readMessagefunction(
            @Body RequestMessageRead requestMessageRead
    );

    @POST("checkFriendShipStatus")
    Call<ResultFriendShipStatus> getFriendShipStatus(
            @Header("Auth-Token") String header1,
            @Header("Csrf-Token") String header2,
            @Body RequestFriendShipStatus requestFriendShipStatus
    );


    @Multipart
    @POST("send-message")
    Call<ResultSendMessage> sendMessage(
            @Part("UserId") RequestBody UserId,
            @Part("conversationId") RequestBody conversationId,
            @Part("_id") RequestBody id,
            @Part("name_1") RequestBody name_1,
            @Part("senderProfilePic") RequestBody senderProfilePic,
            @Part("senderType") RequestBody senderType,
            @Part("receiverId") RequestBody receiverId,
            @Part("receiverName") RequestBody receiverName,
            @Part("receiverImageUrl") RequestBody receiverImageUrl,
            @Part("receiverType") RequestBody receiverType,
            @Part("body") RequestBody body,
            @Part("isFriendAccept") RequestBody isFriendAccept,
            @Part("mimeType") RequestBody mimeType
    );

    @POST("message-trails")
    Call<ResultMessage> getAllMessages(
            @Body RequestAllMessages requestAllMessages
    );

    @PUT("block-unblock-chat")
    Call<ResultBlockUnblock> blockUnblockUser(
            @Header("Auth-Token") String header1,
            @Header("Csrf-Token") String header2,
            @Body RequestBlockUnclock requestBlockUnclock
    );

    @POST("reportUser")
    Call<ResultSendFriendRequest> reportUser(
            @Header("Auth-Token") String header1,
            @Header("Csrf-Token") String header2,
            @Body RequestReport requestReport
    );

    @POST("getReportIssues")
    Call<ResultReportIssue> getReportIssues(
            @Header("Auth-Token") String header1,
            @Header("Csrf-Token") String header2
    );

    @Multipart
    @POST("send-message")
    Call<ResultSendMessage> sendMessageAudio(
            @Part("UserId") RequestBody UserId,
            @Part("conversationId") RequestBody conversationId,
            @Part("_id") RequestBody id,
            @Part("name_1") RequestBody name_1,
            @Part("senderProfilePic") RequestBody senderProfilePic,
            @Part("senderType") RequestBody senderType,
            @Part("receiverId") RequestBody receiverId,
            @Part("receiverName") RequestBody receiverName,
            @Part("receiverImageUrl") RequestBody receiverImageUrl,
            @Part("receiverType") RequestBody receiverType,
            @Part MultipartBody.Part files,
            @Part("isFriendAccept") RequestBody isFriendAccept,
            @Part("audio_duration") RequestBody audioDuration
    );

    @POST("getSubscriptionPlans")
    Call<ResultCoinPlan> getCoinPlan(
            @Header("Auth-Token") String header1,
            @Header("Csrf-Token") String header2
    );

    @POST("placeOrder")
    Call<ResultPlaceOrder> placeOrder(
            @Header("Auth-Token") String header1,
            @Header("Csrf-Token") String header2,
            @Body RequestPlaceOrder requestPlaceOrder
    );

    @GET("getGifts")
    Call<ResultGift> getGift(
            @Header("Authorization") String header1
    );

    @POST("create-chat-room")
    Call<ResultChatRoom> createChatRoom(
            @Header("Content-Type") String header,
            @Body RequestChatRoom requestChatRoom
    );

    @POST("my-chat-list")
    Call<ResultChatList> getChatList(
            @QueryMap Map<String, String> options,
            @Body RequestChatList requestChatList
    );

    @POST("send_gift")
    Call<SendGiftResult> sendGift(
            @Header("Authorization") String header1,
            @Body SendGiftRequest requestGift
    );


    @POST("ask_for_gift")
    Call<RequestGiftResponce> sendGiftRequestFromHost(
            @Header("Authorization") String header1,
            @Body RequestGiftRequest requestGiftRequest
    );


    @Multipart
    @POST("send-message")
    Call<ResultSendMessage> sendMessageGift(
            @Part("UserId") RequestBody UserId,
            @Part("conversationId") RequestBody conversationId,
            @Part("_id") RequestBody id,
            @Part("name_1") RequestBody name_1,
            @Part("senderProfilePic") RequestBody senderProfilePic,
            @Part("senderType") RequestBody senderType,
            @Part("receiverId") RequestBody receiverId,
            @Part("receiverName") RequestBody receiverName,
            @Part("receiverImageUrl") RequestBody receiverImageUrl,
            @Part("receiverType") RequestBody receiverType,
            @Part("body") RequestBody body,
            @Part("isFriendAccept") RequestBody isFriendAccept,
            @Part("gift_coins") RequestBody giftCoins,
            @Part("mimeType") RequestBody mimeType
    );

    @FormUrlEncoded
    @POST("request-call-alert")
    Call<ResultSendMessage> callRequest(
            @Field("UserId") String UserId,
            @Field("conversationId") String conversationId,
            @Field("_id") String id,
            @Field("name_1") String name_1,
            @Field("senderProfilePic") String senderProfilePic,
            @Field("senderType") String senderType,
            @Field("receiverId") String receiverId,
            @Field("receiverName") String receiverName,
            @Field("receiverImageUrl") String receiverImageUrl,
            @Field("receiverType") String receiverType,
            @Field("body") String body,
            @Field("isFriendAccept") String isFriendAccept,
            @Field("mimeType") String mimeType
    );

    @PUT("update-read-messages")
    Call<ResultMessageRead> readMessage(
            @Body RequestMessageRead requestMessageRead
    );

    @POST("call-alert")
    Call<ResultMissCall> sendMissCallData(
            @Body RequestMissCall requestMissCall
    );

    @POST("stripe-api-keys")
    Call<KeyResponce> getStripeKey();

    @POST("createpayment-by-stripe")
    Call<ServerResponce> getStripeInit(@Header("Accept") String accept, @Header("Content-Type") String contentType,
                                       @Body RequestModel requestModel);

    @POST("stripe-save-payment")
    Call<PayResponce> getStripeOnSucess(@Header("Authorization") String token,
                                        @Header("Accept") String accept,
                                        @Header("Content-Type") String contentType,
                                        @Body PayRequest payRequest);

    @GET("getApiKeysAndBalance")
    Call<PaymentGatewayResponce> getPaymentData(@Header("Authorization") String token,
                                                @Header("Accept") String accept);

    @POST("end-call-new")
    Call<EncCallResponce> sendEndCallTime(@Header("Authorization") String token,
                                          @Header("Accept") String accept,
                                          @Body ArrayList<EndCallData> endCallData);


    @GET("get-gift-count")
    Call<GiftCountResult> getGiftCountForHost(@Header("Authorization") String token, @Header("Accept") String accept,
                                              @Query("id") String convId);


    @POST("getuserdetails")
    Call<MessageCallDataResponce> getMessageCallData(@Header("Authorization") String token,
                                                     @Header("Accept") String accept,
                                                     @Body MessageCallDataRequest messageCallDataRequest);


    @GET("remaininggiftcard")
    Call<RemainingGiftCardResponce> getRemainingGiftCardResponce(@Header("Authorization") String token, @Header("Accept") String accept);

    @FormUrlEncoded
    @POST("addrating")
    Call<RatingResponce> getUserRating(@Header("Authorization") String token,
                                       @Header("Accept") String accept,
                                       @Field("host_id") String host_id,
                                       @Field("rate") String rate,
                                       @Field("tag") String tag);

    //new function for show rating female 6/5/21
    @GET("getprofiledata")
    Call<UserListResponseNewData> getRateCountForHost(@Header("Authorization") String token, @Header("Accept") String accept,
                                                      @Query("id") String host_profileId);

    @GET("getPaymentGatewayZeeplive")
    Call<PaymentSelectorResponce> getPaymentSelector(@Header("Authorization") String token, @Header("Accept") String accept);


    //new api for guest Edit image 29/5/21
    @Multipart
    @POST("update-guest-profile")
    Call<Object> upDateGuestProfile(@Header("Authorization") String token,
                                    @Header("Accept") String accept,
                                    @Part("name") RequestBody name,
                                    @Part MultipartBody.Part profile_pic);


    @POST("malelevelSystem")
    Call<UpgradedLevelResponce> UpgradeUserLevel(@Header("Authorization") String token,
                                                 @Header("Accept") String accept);

    //broad

    @GET("getLevels")
    Call<GetLevelResponce> getUserLevelHistory(@Header("Authorization") String token, @Header("Accept") String accept);


    @POST("startbroadcast")
    Call<BroadCastResponce> startBroadCast(@Header("Authorization") String token,
                                           @Header("Accept") String accept);

    @POST("broadcastlist")
    Call<BroadcastListResponce> getBroadList(@Header("Authorization") String token,
                                             @Header("Accept") String accept);

    @FormUrlEncoded
    @POST("stopbroadcast")
    Call<Object> stopBroadCast(@Header("Authorization") String token,
                               @Header("Accept") String accept,
                               @Field("broadcast_id") String broadcastId);

    @FormUrlEncoded
    @POST("joinbroadcast")
    Call<Object> audiJoinBroad(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Field("broadcast_id") String broadcastId);

    @FormUrlEncoded
    @POST("leavebroadcast")
    Call<Object> audiLeaveBroad(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Field("broadcast_id") String broadcastId);

    @POST("askToJoinOnCall")
    Call<Object> audiAskToJoinCallinBroad(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Body AudiRequestForCall audiRequestForCall);

    @POST("acceptBroadCallJoinRequest ")
    Call<Object> hostAcceptJoinCallinBroad(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Body HostAcceptCallRequest hostAcceptCallRequest);

    @POST("cash-free-payment")
    Call<Object> cashFreePayment(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Body CashFreePaymentRequest cashFreePaymentRequest);

    @GET("getCashFreeToken")
    Call<CfTokenResponce> getCfToken(@Header("Authorization") String token,
                                     @Header("Accept") String accept,
                                     @Query("amount") String amount,
                                     @Query("plan_id") String plan_id);

    @FormUrlEncoded
    @POST("updateWalletAndCallToZero")
    Call<Object> callCutByHost(@Header("Authorization") String token,
                               @Header("Accept") String accept,
                               @Field("unique_id") String unique_id);
}