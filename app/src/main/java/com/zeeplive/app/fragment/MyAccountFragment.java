package com.zeeplive.app.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeplive.app.R;
import com.zeeplive.app.activity.CardActivity;
import com.zeeplive.app.activity.EarningActivity;
import com.zeeplive.app.activity.EditProfile;
import com.zeeplive.app.activity.InsufficientCoinActivity;
import com.zeeplive.app.activity.LevelUpActivity;
import com.zeeplive.app.activity.MainActivity;
import com.zeeplive.app.activity.MaleWallet;
import com.zeeplive.app.activity.PrivacyPolicy;
import com.zeeplive.app.activity.SettingActivity;
import com.zeeplive.app.activity.ViewTicketActivity;
import com.zeeplive.app.databinding.FragmentMyAccountBinding;
import com.zeeplive.app.dialog.AccountInfoDialog;
import com.zeeplive.app.dialog.ChangePasswordDialog;
import com.zeeplive.app.dialog.ComplaintDialog;
import com.zeeplive.app.dialog.OnlineStatusDialog;
import com.zeeplive.app.dialog.UpGradedLevelDialog;
import com.zeeplive.app.response.ProfileDetailsResponse;
import com.zeeplive.app.response.WalletBalResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.ui.live.LivePrepareActivity;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 **/
public class MyAccountFragment extends Fragment implements ApiResponseInterface {

    ApiManager apiManager;
    FragmentMyAccountBinding binding;
    public int onlineStatus;
    String username = "";
    SessionManager sessionManager;
    String guestPassword;
    DatabaseReference chatRef;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(getContext());
        guestPassword = sessionManager.getGuestPassword();


        binding.setClickListener(new EventHandler(getContext()));

        if (sessionManager.getGender().equals("male")) {
            binding.incomeReport.setVisibility(View.GONE);
            binding.onlineStatus.setVisibility(View.GONE);

            //    binding.myWallet.setVisibility(View.GONE);
        } else {
            binding.myWallet.setVisibility(View.GONE);
            binding.purchaseCoins.setVisibility(View.GONE);
            binding.onlineStatus.setVisibility(View.GONE);
            binding.upgradedLevel.setVisibility(View.GONE);
            binding.myCard.setVisibility(View.GONE);
        }

        apiManager = new ApiManager(getContext(), this);
        apiManager.getWalletAmount();
        chatRef = FirebaseDatabase.getInstance().getReference().child("Users");

        binding.rlFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).showFollowers();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        apiManager.getProfileDetails();

        if (new SessionManager(getContext()).getUserAddress().equals("null")) {
            binding.userLocation.setVisibility(View.GONE);
        } else {
            if (binding.userLocation.getVisibility() == View.GONE) {
                binding.userLocation.setVisibility(View.VISIBLE);
            }
            binding.userLocation.setText(new SessionManager(getContext()).getUserAddress());
        }
    }

    public class EventHandler {
        Context mContext;

        public EventHandler(Context mContext) {
            this.mContext = mContext;
        }

        public void editProfile() {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
            EditProfileFragment editProfileFragment = new EditProfileFragment();
            fragmentTransaction.add(R.id.fragmentContainer, editProfileFragment);
            fragmentTransaction.commit();

            /*Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);*/
        }

        public void purchaseCoins() {
           /* Intent recharge = new Intent(getActivity(), PurchaseCoins.class);
            startActivity(recharge);*/
            /*if (sessionManager.getGender().equals("male")) {
                ((MainActivity) getActivity()).enableLocationSettings();
            }*/
            // new InsufficientCoinsMyaccount(getActivity(), 2, Integer.parseInt(binding.availableCoins.getText().toString()));
            Intent recharge = new Intent(getActivity(), InsufficientCoinActivity.class);
            int coin = Integer.parseInt(binding.availableCoins.getText().toString());
            recharge.putExtra("current_coin", coin);
            recharge.putExtra("type", 2);
            startActivity(recharge);
            //getActivity().finish();

        }

        public void myCard() {
            Intent intent = new Intent(getActivity(), CardActivity.class);
            startActivity(intent);

        }

        public void maleWallet() {
            Intent my_wallet = new Intent(getActivity(), MaleWallet.class);
            startActivity(my_wallet);
        }

        public void onlineStatus() {
            new OnlineStatusDialog(MyAccountFragment.this, onlineStatus);
        }

        public void complaint() {
            new ComplaintDialog(getContext());
        }

        public void viewTicket() {
            Intent my_wallet = new Intent(getActivity(), ViewTicketActivity.class);
            startActivity(my_wallet);
        }

        public void privacyPolicy() {
            Intent intent = new Intent(getActivity(), PrivacyPolicy.class);
            startActivity(intent);
        }

        public void changePassword() {
            new ChangePasswordDialog(getContext());
        }

        public void myEarning() {
            Intent my_earning = new Intent(getActivity(), EarningActivity.class);
            startActivity(my_earning);
        }

        public void setting() {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
            SettingFragment settingFragment = new SettingFragment();
            fragmentTransaction.add(R.id.fragmentContainer, settingFragment);
            fragmentTransaction.commit();

           /* Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);*/
        }

        public void incomeReport() {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
            IncomeReportFragment incomeReportFragment = new IncomeReportFragment();
            fragmentTransaction.add(R.id.fragmentContainer, incomeReportFragment);
            fragmentTransaction.commit();
           /* Intent income = new Intent(getActivity(), IncomeReport.class);
            startActivity(income);*/
        }

        public void accountInfo() {
            new AccountInfoDialog(getContext(), username, guestPassword);
        }

        public void upGraded() {
            // show upgraded dialog here
            /*Intent intent = new Intent(getActivity(), LevelUpActivity.class);
            startActivity(intent);*/

            Intent intent = new Intent(getActivity(), LivePrepareActivity.class);
            startActivity(intent);
            // new UpGradedLevelDialog(getContext());
        }

        public void logout() {
            //  new ExitDialog(MyAccountFragment.this);
            logoutDialog();
        }
    }

    private void addFragment() {
        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.fragment_view);
        if (fragment instanceof MyAccountFragment) {
            fragment = new IncomeReportFragment();
        } else if (fragment instanceof IncomeReportFragment) {
            fragment = new SettingFragment();
        } else if (fragment instanceof SettingFragment) {
            fragment = new MyAccountFragment();
        } else {
            fragment = new MyAccountFragment();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //IncomeReportFragment incomeReportFragment = new IncomeReportFragment();
        //fragmentTransaction.add(R.id.fragmentContainer, fragment);
        fragmentTransaction.replace(R.id.fragment_view, fragment);
        fragmentTransaction.commit();
    }


    void logoutDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_exit);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        TextView closeDialog = dialog.findViewById(R.id.close_dialog);
        TextView logout = dialog.findViewById(R.id.logout);

        closeDialog.setOnClickListener(view -> dialog.dismiss());
        logout.setOnClickListener(view -> {
            dialog.dismiss();
            String cName = new SessionManager(getContext()).getUserLocation();
            String eMail = new SessionManager(getContext()).getUserEmail();
            String passWord = new SessionManager(getContext()).getUserPassword();
            new SessionManager(getContext()).logoutUser();
            apiManager.getUserLogout();
            checkOnlineAvailability(uid, nme, image);
            new SessionManager(getContext()).setUserLocation(cName);
            new SessionManager(getContext()).setUserEmail(eMail);
            new SessionManager(getContext()).setUserPassword(passWord);
            new SessionManager(getContext()).setUserAskpermission();
            //    sessionManager.setUserLocation("null");
            getActivity().finish();
        });
    }

    String uid, nme, image;

    void checkOnlineAvailability(String uid, String name, String image) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);

                if (connected) {
                    // Change online status when user comes back on app
                    try {
                        HashMap<String, String> details = new HashMap<>();
                        details.put("uid", uid);
                        details.put("name", name);
                        details.put("image", image);
                        details.put("status", "Offline");
                        chatRef.child(uid).setValue(details);

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("fireeBase", "Listener was cancelled");
            }
        });
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(getContext(), errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        try {
            if (ServiceCode == Constant.WALLET_AMOUNT) {
                WalletBalResponse rsp = (WalletBalResponse) response;
                binding.availableCoins.setText(String.valueOf(rsp.getResult().getTotal_point()));

            }
            if (ServiceCode == Constant.PROFILE_DETAILS) {
                ProfileDetailsResponse rsp = (ProfileDetailsResponse) response;

                if (rsp.getSuccess().getProfile_images() != null && rsp.getSuccess().getProfile_images().size() > 0) {
                    try {
                        if (!rsp.getSuccess().getProfile_images().get(0).getImage_name().equals("")) {
                            Glide.with(getContext()).load(rsp.getSuccess().getProfile_images().get(0).getImage_name())
                                    .circleCrop().placeholder(R.drawable.default_profile).into(binding.userImage);
                        }

                        String img = "";
                        if (rsp.getSuccess().getProfile_images() != null && rsp.getSuccess().getProfile_images().size() > 0) {
                            img = rsp.getSuccess().getProfile_images().get(0).getImage_name();
                        }
                        uid = String.valueOf(rsp.getSuccess().getProfile_id());
                        nme = rsp.getSuccess().getName();
                        this.image = img;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                binding.followers.setText(String.valueOf(rsp.getSuccess().getFavorite_count()));
                binding.name.setText(rsp.getSuccess().getName());
                binding.userId.setText("ID : " + rsp.getSuccess().getProfile_id());
                markOnlineStatus(rsp.getSuccess().getIs_online());

                if (rsp.getSuccess().getLogin_type().equals("manualy") && new SessionManager(getContext()).getGender().equals("male")) {
                    binding.changePassword.setVisibility(View.VISIBLE);
                    // binding.passwordSeprator.setVisibility(View.VISIBLE);
                }

                if (rsp.getSuccess().getUsername().startsWith("guest")) {
                    binding.changePassword.setVisibility(View.GONE);
                    // binding.accountInfo.setVisibility(View.VISIBLE);
                    username = rsp.getSuccess().getUsername();
                }

                apiManager.getWalletAmount();
            }
        } catch (Exception e) {
        }
    }

    public void markOnlineStatus(int is_online) {
        onlineStatus = is_online;

        if (is_online == 1) {
            binding.status.setText("Online");
        } else {
            binding.status.setText("Offline");
        }
    }


}