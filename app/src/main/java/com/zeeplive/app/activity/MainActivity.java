package com.zeeplive.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zeeplive.app.R;
import com.zeeplive.app.dialog.CompleteProfileDialog;
import com.zeeplive.app.dialog.PermissionDialog;
import com.zeeplive.app.fragment.FemaleHomeFragment;
import com.zeeplive.app.fragment.HomeFragment;
import com.zeeplive.app.fragment.InboxFragment;
import com.zeeplive.app.fragment.MatchOnCamFragment;
import com.zeeplive.app.fragment.MessageEmployeeFragment;
import com.zeeplive.app.fragment.MyAccountFragment;
import com.zeeplive.app.fragment.MyFavourite;
import com.zeeplive.app.fragment.RecentRecharges;
import com.zeeplive.app.fragment.SearchFragment;
import com.zeeplive.app.helper.MyCountDownTimer;
import com.zeeplive.app.helper.NetworkCheck;
import com.zeeplive.app.response.Ban.BanResponce;
import com.zeeplive.app.response.Chat.ChatList;
import com.zeeplive.app.response.Chat.RequestChatList;
import com.zeeplive.app.response.Chat.ResultChatList;
import com.zeeplive.app.response.OnlineStatusResponse;
import com.zeeplive.app.response.ProfileDetailsResponse;
import com.zeeplive.app.response.Token;
import com.zeeplive.app.retrofit.ApiClientChat;
import com.zeeplive.app.retrofit.ApiInterface;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        ApiResponseInterface {

    String TAG = "MainActivity";
    boolean doubleBackToExitPressedOnce = false;
    SessionManager sessionManager;

    Fragment fragment = null;
   // BottomNavigationView navigation;

    DatabaseReference chatRef;
    ApiManager apiManager;
    String fcmToken;

    /* In-app Update Variables */
    AppUpdateManager mAppUpdateManager;
    InstallStateUpdatedListener installStateUpdatedListener;
    int MY_REQUEST_CODE = 2020;
    int REQUEST_CODE_CHECK_SETTINGS = 2021;
    private NetworkCheck networkCheck;
    //fragment new Code

    final Fragment homeFragment = new HomeFragment();
    final Fragment femaleHomeFragment = new FemaleHomeFragment();
    //Fragment onCamFragment = new OnCamFragment();
    Fragment matchOnCamFragment = new MatchOnCamFragment();
    Fragment searchFragment = new SearchFragment();
    Fragment myFavourite = new MyFavourite();
    Fragment recentRecharges = new RecentRecharges();
    //Fragment messageFragment = new MessageFragment();

    Fragment messageFragment = new InboxFragment();
    Fragment messageEmployeeFragment = new MessageEmployeeFragment();
    final Fragment myAccountFragment = new MyAccountFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active;

    int isGuest = 0;
    private static final int PICK_IMAGE_CAMERA_REQUEST_CODE = 0;
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 1;
    private String country_name = "";
    private String city_name = "";
    private String state_name = "";
    private String facebook_name = "";
    private String guest_name;
    private PowerManager.WakeLock wakeLock;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.content_main);
        Toolbar toolbar = findViewById(R.id.toolbar);


        networkCheck = new NetworkCheck();
        setSupportActionBar(toolbar);
        sessionManager = new SessionManager(this);

        // Setting Temp location
        //sessionManager.setUserLocation("India");


        // Facebook Analaytics
        AppEventsLogger.newLogger(this);


        //fm.beginTransaction().add(R.id.fragment_view, onCamFragment, "2").hide(onCamFragment).commit();
        fm.beginTransaction().add(R.id.fragment_view, matchOnCamFragment, "2").hide(matchOnCamFragment).commit();
        fm.beginTransaction().add(R.id.fragment_view, searchFragment, "3").hide(searchFragment).commit();
        fm.beginTransaction().add(R.id.fragment_view, myFavourite, "4").hide(myFavourite).commit();
        fm.beginTransaction().add(R.id.fragment_view, recentRecharges, "5").hide(recentRecharges).commit();
        fm.beginTransaction().add(R.id.fragment_view, messageFragment, "6").hide(messageFragment).commit();
        fm.beginTransaction().add(R.id.fragment_view, messageEmployeeFragment, "7").hide(messageEmployeeFragment).commit();
        fm.beginTransaction().add(R.id.fragment_view, myAccountFragment, "8").hide(myAccountFragment).commit();

        if (sessionManager.getGender().equals("male")) {
            //fragment = new HomeFragment();
            sessionManager.setLangState(0);
            sessionManager.setOnlineState(0);
            addFragment(homeFragment, "1");

            ((ImageView) findViewById(R.id.img_newMenuOnCam)).setVisibility(View.VISIBLE);
        } else {
//            fragment = new FemaleHomeFragment();
            addFragment(femaleHomeFragment, "1");
            ((ImageView) findViewById(R.id.img_newMenuRecentRecharge)).setVisibility(View.VISIBLE);
        }
        // loadFragment(fragment);


        apiManager = new ApiManager(this, this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            fcmToken = instanceIdResult.getToken();

            // I am commenting this line to prevent token expire problem
            //  if (new SessionManager(this).getFcmToken() == null) {
            apiManager.registerFcmToken(fcmToken);
            // }
        });

        chatRef = FirebaseDatabase.getInstance().getReference().child("Users");
        apiManager.getProfileDetails();

        /* In app update */
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                if (mAppUpdateManager != null) {
                    mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                }

            } else {
                Log.i("ProductList", "InstallStateUpdatedListener: state: " + state.installStatus());
            }
        };

        //checkForUpdates();


        //    getChatList();
        //    initSocket();

        //  sessionManager.setOnlineState(1);
        // sessionManager.setLangState(0);
        drawBadge();



      /* if (sessionManager.getUserName().contains("guest")) {
            new CompleteProfileDialog(this);
        }*/
        facebook_name = sessionManager.getUserFacebookName();
        //Log.e("userfacebookname", facebook_name);
        checkGuestLogin();

        getPermission();

        if (sessionManager.getGender().equals("male")) {
            //  getPermission();
            if (canGetLocation()) {
                //DO SOMETHING USEFUL HERE. ALL GPS PROVIDERS ARE CURRENTLY ENABLED
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    autoCountrySelect();
                }
            } else {
                //SHOW OUR SETTINGS ALERT, AND LET THE USE TURN ON ALL THE GPS PROVIDERS
                //showSettingsAlert();
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    enableLocationSettings();
                }
                // new SessionManager(this).setUserLocation("India");
            }
        }

    }

    private void getPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        try {
                            if (report.areAllPermissionsGranted()) {
                            }

                            if (report.isAnyPermissionPermanentlyDenied()) {
                            }

                            if (report.getGrantedPermissionResponses().get(0).getPermissionName().equals("android.permission.ACCESS_FINE_LOCATION")) {
                                autoCountrySelect();
                                //  enableLocationSettings();
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkGuestLogin() {


        if (sessionManager.getGender().equals("male")) {
            isGuest = sessionManager.getGuestStatus();
            // Log.e("inActivity", "" + new SessionManager(this).getGuestStatus());
            if (isGuest != 1) {
                if (isGuest == 0) {
                    new CompleteProfileDialog(this, facebook_name);
                    isGuest = 1;
                }/* else {
            isGuest = 0;
        }*/ else if (facebook_name != null) {
                    new CompleteProfileDialog(this, facebook_name);
                }
                if (!sessionManager.getUserAskpermission().equals("no")) {
                    // new PermissionDialog(MainActivity.this);

                }
            }
        }

    }


    private void autoCountrySelect() {
        // Log.e("LocationDetection", "m here");
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(getApplicationContext());
        for (String provider : lm.getAllProviders()) {
            @SuppressWarnings("ResourceType")
            Location location = lm.getLastKnownLocation(provider);
            if (location != null) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        //       c_name = addresses.get(0).getCountryName();
                        city_name = addresses.get(0).getLocality();
/*
                        Log.e("countryname", addresses.get(0).getCountryName());
                        Log.e("cityname", city_name);
*/
                        sessionManager.setUserLocation(addresses.get(0).getCountryName());
                        sessionManager.setUserAddress(city_name);
/*
                        Log.e("cityname1", addresses.get(0).getAddressLine(0).toLowerCase());
                        Log.e("cityname2", addresses.get(0).getAdminArea());
                        Log.e("cityname3", addresses.get(0).getSubLocality());
*/

                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //   autoCountrySelect();
                enableLocationSettings();
            }
        }

    }

    public boolean canGetLocation() {
        boolean result = true;
        LocationManager lm;
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // exceptions will be thrown if provider is not permitted.
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            networkEnabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gpsEnabled && networkEnabled;
    }


    public void enableLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(100)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                    // startUpdatingLocation(...);
                    //   autoCountrySelect();

                })
                .addOnFailureListener(this, ex -> {
                    Log.e("LocationService", "Failure");
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(MainActivity.this, REQUEST_CODE_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                });
    }

    TextView badgeText;

    private void drawBadge() {
       /* BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) navigation.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(3);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;*/

        /*View badge = LayoutInflater.from(this)
                .inflate(R.layout.notification_badge, itemView, true);
        badgeText = badge.findViewById(R.id.notificationsinbag);
        badgeText.setText("0");*/
    }

    void checkOnlineAvailability(String uid, String name, String image) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);

                if (connected) {
                    // Change online status when user comes back on app
                    HashMap<String, String> details = new HashMap<>();
                    details.put("uid", uid);
                    details.put("name", name);
                    details.put("image", image);
                    details.put("status", "Online");
                    chatRef.child(uid).setValue(details);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Listener was cancelled");
            }
        });
    }

    void checkForUpdates() {
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                try {
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, MainActivity.this, MY_REQUEST_CODE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                MainActivity.this.popupSnackbarForCompleteUpdate();
            } else {
                Log.e("ProductList", "checkForAppUpdateAvailability: something else");
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("selectedImage", "selectedImage:" + data);

        if (REQUEST_CODE_CHECK_SETTINGS == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                //user clicked OK, you can startUpdatingLocation(...);
                //          autoCountrySelect();
                enableLocationSettings();
            } else {
                //user clicked cancel: informUserImportanceOfLocationAndPresentRequestAgain();
            }
        }
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("ProductList", "onActivityResult: app download failed");
            }
        } else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null || requestCode == PICK_IMAGE_CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK) {
                        Intent Intent = new Intent("FBR-USER-IMAGE");
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        // call this method to get the URI from the bitmap
                        Uri selectedCamera = getImageUri(getApplicationContext(), photo);
                        // call this method to get the actual path of capture image
                        String finalCameraImage = getProfileImagePath(this, selectedCamera);
                        Log.e("selectedCameraImage", "selectedCameraImage:" + finalCameraImage);
                        if (!finalCameraImage.equals("Not found")) {
                            Intent.putExtra("uri", finalCameraImage);
                            Intent.putExtra("fromCam", "yes");
                            this.sendBroadcast(Intent);
                        }

                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        Intent myIntent = new Intent("FBR-USER-IMAGE");
                        Uri selectedImage = data.getData();
                        // Log.e("selectedImage", "selectedImage:" + selectedImage);
                        String picturePath = getProfileImagePath(this, selectedImage);
                        if (!picturePath.equals("Not found")) {
                            myIntent.putExtra("uri", picturePath);
                            myIntent.putExtra("fromCam", "no");
                            this.sendBroadcast(myIntent);
                            // Log.e("selectedImage", "selectedImage:" + picturePath);

                        }

                    }
                    break;
            }
        }

    }

    public static String getProfileImagePath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Capture", null);
        return Uri.parse(path);
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New app is ready!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        });

        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
        startCountDown();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myReceiver, new IntentFilter("FBR-IMAGE"));

        Log.e("MainActivoity","getGenderDatyaa "+sessionManager.getGender());
        if (sessionManager.getGender().equals("male")) {
            //   menu.findItem(R.id.navigation_favourite).setVisible(false);
        } else {
            //    menu.findItem(R.id.navigation_search).setVisible(false);
        }

        // Change status when user open app
        if (sessionManager.getGender().equals("male")) {
            if (myCountDownTimer != null) {
                inCount = false;
                myCountDownTimer.cancel();
            }



            // if male user is offline hit api to change status
            apiManager.changeOnlineStatus(1);

        } else {



            apiManager.getBanList();

        }


    }


    void verifyUserRegisteredFirebase(String uid, String name, String image) {
        chatRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.child("name").exists()) {

                        // Set for female user only(prevent server load from php)
                        if (!sessionManager.getGender().equals("male")) {

                            // Prepare offline status for existing user
                            HashMap<String, String> details = new HashMap<>();
                            details.put("uid", uid);
                            details.put("name", name);
                            details.put("image", image);
                            details.put("status", "Offline");

                            // for disconnected state
                            chatRef.child(uid).onDisconnect().setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        checkOnlineAvailability(uid, name, image);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Not Working", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    } else {
                        HashMap<String, String> details = new HashMap<>();
                        details.put("uid", uid);
                        details.put("name", name);
                        details.put("image", image);

                        // Set for female user only(prevent server load from php)
                        if (!sessionManager.getGender().equals("male")) {
                            details.put("status", "Offline");
                        }

                        chatRef.child(uid).setValue(details).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                // Set for female user only(prevent server load for php)
                                if (!sessionManager.getGender().equals("male")) {
                                    // for disconnected state
                                    chatRef.child(uid).onDisconnect().setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                checkOnlineAvailability(uid, name, image);
                                            }
                                        }
                                    });
                                }

                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private MyCountDownTimer myCountDownTimer;
    private boolean inCount = false;

    public void startCountDown() {
        //hide code startCountDown 9/04/21
       /* if (!inCount) {
            myCountDownTimer = new MyCountDownTimer(10000, 1000, getApplicationContext());
            inCount = true;
            myCountDownTimer.start();
        }*/
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //   badgeText.setBackground(getResources().getDrawable(R.drawable.circularbadgeunselect));
        // Handles unnecessary click on already selected item

        /* Bottom Navigation */
        if (id == R.id.navigation_home) {
            if (sessionManager.getGender().equals("male")) {
                //   fragment = new HomeFragment();
                showFragment(homeFragment);
                if (myCountDownTimer != null) {
                    inCount = false;
                    myCountDownTimer.cancel();
                }
                detachOncam();
                homeFragment.onResume();

            } else {
                showFragment(femaleHomeFragment);
                femaleHomeFragment.onResume();
//                fragment = new FemaleHomeFragment();
                apiManager.getBanList();
            }
            //  loadFragment(fragment);

        } else if (id == R.id.navigation_search) {
            if (sessionManager.getGender().equals("male")) {
                //onCamFragment = new OnCamFragment();
                matchOnCamFragment = new MatchOnCamFragment();
                fm.beginTransaction().add(R.id.fragment_view, matchOnCamFragment, "2").hide(matchOnCamFragment).commit();

                showFragment(matchOnCamFragment);

                startCountDown();
//      fragment = new OnCamFragment();
            } else {
                loadSearchFragement();
                //showFragment(searchFragment);

//                fragment = new SearchFragment();
            }
            //       loadFragment(fragment);

        } else if (id == R.id.navigation_favourite) {
            if (sessionManager.getGender().equals("male")) {
                // showFragment(myFavourite);
                loadSearchFragement();
                showFollowers();
                // myFavourite.onResume();
                startCountDown();
                detachOncam();

//    fragment = new MyFavourite();
            } else {
                showFollowers();
                item.setIcon(R.drawable.ic_recent_recharges);
                recentRecharges = new RecentRecharges();
                fm.beginTransaction().add(R.id.fragment_view, recentRecharges, "2").hide(recentRecharges).commit();
                item.setTitle("Recent");
                showFragment(recentRecharges);
//      fragment = new RecentRecharges();
            }
            //      loadFragment(fragment);

        } else if (id == R.id.navigation_inbox) {
            // fragment = new InboxFragment();

            // badgeText.setBackground(getResources().getDrawable(R.drawable.circularbadge));
            //  if (sessionManager.getGender().equals("male")) {


            showFragment(messageFragment);


            //  messageFragment = new MessageFragment();
            //  fm.beginTransaction().add(R.id.fragment_view, messageFragment, "2").hide(messageFragment).commit();
            //  showFragment(messageFragment);
            startCountDown();
            detachOncam();

           /* } else {

                //         showFragment(messageEmployeeFragment);
                messageEmployeeFragment = new MessageEmployeeFragment();
                fm.beginTransaction().add(R.id.fragment_view, messageEmployeeFragment, "2").hide(messageEmployeeFragment).commit();
                showFragment(messageEmployeeFragment);

//                messageEmployeeFragment.onResume();
//               fragment = new MessageEmployeeFragment();
            }*/


        } else if (id == R.id.navigation_user_profile) {
            showFragment(myAccountFragment);
            if (sessionManager.getGender().equals("male")) {
                detachOncam();
                startCountDown();
            }
            // myAccountFragment.onResume();
//   fragment = new MyAccountFragment();
            //   loadFragment(fragment);

        } else if (id == R.id.nav_wallet) {
            startCountDown();

            Intent intent = new Intent(this, MaleWallet.class);
            startActivity(intent);
        }
        return true;
    }

    private void addFragment(Fragment fragment, String tag) {
        fm.beginTransaction().add(R.id.fragment_view, fragment, tag).commit();
        active = fragment;
    }

    private void showFragment(Fragment fragment) {
        fm.beginTransaction().hide(active).show(fragment).commit();
        active = fragment;
    }

    public void showFollowers() {
        /*Intent myIntent = new Intent("FBR");
        myIntent.putExtra("action", "reload");
        this.sendBroadcast(myIntent);
        */
        showFragment(myFavourite);
    }

    private void detachOncam() {
        try {
            getSupportFragmentManager().beginTransaction().remove(matchOnCamFragment).commitAllowingStateLoss();
        } catch (Exception e) {
        }
    }


    public void loadSearchFragement() {
        searchFragment = new SearchFragment();
        fm.beginTransaction().add(R.id.fragment_view, searchFragment, "3").hide(searchFragment).commit();

        showFragment(searchFragment);
        if (sessionManager.getGender().equals("male")) {
            startCountDown();
        }
/*      fragment = new SearchFragment();
        loadFragment(fragment);
  */
    }

    @Override
    public void onBackPressed() {
        unselectAllMenu();
        ((ImageView) findViewById(R.id.img_newMenuHome)).setImageResource(R.drawable.heartattackselected);

        if (active instanceof HomeFragment || active instanceof FemaleHomeFragment) {
            if (doubleBackToExitPressedOnce) {

                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "click BACK again to go Exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        } else {
            if (sessionManager.getGender().equals("male")) {
                showFragment(homeFragment);
                if (myCountDownTimer != null) {
                    inCount = false;
                    myCountDownTimer.cancel();
                }
                homeFragment.onResume();
//       fragment = new HomeFragment();
            } else {
                showFragment(femaleHomeFragment);
//     fragment = new FemaleHomeFragment();
            }

            //     loadFragment(fragment);

            setTitle("1");
        }
    }

    public void loadHomeFragment() {
        detachOncam();
        matchOnCamFragment = new MatchOnCamFragment();
        fm.beginTransaction().add(R.id.fragment_view, matchOnCamFragment, "2").hide(matchOnCamFragment).commit();
        showFragment(matchOnCamFragment);
    }

    @Override
    protected void onDestroy() {
        // Change status when user open app
        if (sessionManager != null && sessionManager.getGender() != null) {
            if (sessionManager.getGender().equals("male")) {
                apiManager.changeOnlineStatus(0);
            }
        }
        super.onDestroy();
        //wakeLock.release();
    }

    private void saveUserTokenIntoFirebase(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(sessionManager.getUserId()).setValue(token1);
    }


    @Override
    public void isError(String errorCode) {
        Toast.makeText(this, errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.REGISTER_FCM_TOKEN) {
            sessionManager.saveFcmToken(fcmToken);

            saveUserTokenIntoFirebase(fcmToken);
        }


        if (ServiceCode == Constant.MANAGE_ONLINE_STATUS) {
            OnlineStatusResponse reportResponse = (OnlineStatusResponse) response;
        }
        if (ServiceCode == Constant.PROFILE_DETAILS) {
            ProfileDetailsResponse rsp = (ProfileDetailsResponse) response;
            if (rsp.getSuccess() != null) {

                String img = "";
                if (rsp.getSuccess().getProfile_images() != null && rsp.getSuccess().getProfile_images().size() > 0) {
                    img = rsp.getSuccess().getProfile_images().get(0).getImage_name();
                }

                // Register User into firebase
                if (sessionManager.getGender().equals("male")) {
                    verifyUserRegisteredFirebase(String.valueOf(rsp.getSuccess().getProfile_id()), rsp.getSuccess().getName(), img);
                }
            }
        }

        if (ServiceCode == Constant.BAN_DATAP) {
            BanResponce reportResponse = (BanResponce) response;
            if (reportResponse.getResult() != null) {
                if (reportResponse.getResult().getIsBanned() == 1) {

                    ((CardView) findViewById(R.id.cv_ban)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tv_banmsg)).setText("You are ban, please contact your manager.");
                    isBlock = 1;

                } else {
                    ((CardView) findViewById(R.id.cv_ban)).setVisibility(View.GONE);
                }
            }
        }
    }

    int isBlock = 0;

    public int isBlockFunction() {
        return isBlock;
    }

    public ArrayList<ChatList> chatListArrayList;
    public boolean nxtPageMsg = false;


    public void updateMessageCount(int msgCount) {
        if (msgCount >= 99) {

            badgeText.setText("99+");
            //((TextView) findViewById(R.id.tv_unreadmain)).setText("99+");
        } else {
            badgeText.setText(String.valueOf(msgCount));
            // ((TextView) findViewById(R.id.tv_unreadmain)).setText(String.valueOf(msgCount));
        }
    }


    public void getChatList() {
        if (networkCheck.isNetworkAvailable(this)) {
            ApiInterface apiservice = ApiClientChat.getClient().create(ApiInterface.class);

            RequestChatList requestChatList = new RequestChatList(Long.parseLong(new SessionManager(getApplicationContext()).getUserId()));

            // RequestChatList requestChatList = new RequestChatList(Long.parseLong("2147483648"));
            //     Log.e("userId", new SessionManager(getApplicationContext()).getUserId());
            Map<String, String> data = new HashMap<>();
            data.put("page", String.valueOf(1));

            Call<ResultChatList> call = apiservice.getChatList(data, requestChatList);
            //  Log.e("user_id: ", String.valueOf(SharedPrefManager.getInstance(this).getUser().getId()));
            call.enqueue(new Callback<ResultChatList>() {
                @Override
                public void onResponse(Call<ResultChatList> call, Response<ResultChatList> response) {
                    //   Log.e("onChatList: ", new Gson().toJson(response.body()));
                    try {
                        nxtPageMsg = response.body().getPaging().isNextPage();

                        chatListArrayList = new ArrayList<ChatList>();
                        chatListArrayList.addAll(response.body().getData());
                        long totalunread = 0;
                        for (int i = 0; i < chatListArrayList.size(); i++) {
                            //              Log.e("unread", String.valueOf(chatListArrayList.get(i).getUnread()));
                            totalunread = totalunread + chatListArrayList.get(i).getUnread();
                        }
                        if (totalunread >= 99) {
                            badgeText.setText("99+");
                        } else {
                            badgeText.setText(String.valueOf(totalunread));
                            //             ((TextView) findViewById(R.id.tv_unreadmain)).setText(String.valueOf(totalunread));
                        }
                    } catch (Exception e) {
                        Log.e("fromAdapter", "inError");
                    }
                }

                @Override
                public void onFailure(Call<ResultChatList> call, Throwable t) {
                    //  Log.e("onChatListError: ", t.getMessage());
                }
            });
        }

    }


    public void logoutDialog() {

        Dialog dialog = new Dialog(MainActivity.this);
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

            String eMail = new SessionManager(getApplicationContext()).getUserEmail();
            String passWord = new SessionManager(getApplicationContext()).getUserPassword();
            new SessionManager(getApplicationContext()).logoutUser();
            new ApiManager(getApplicationContext(), this).getUserLogout();
            new SessionManager(getApplicationContext()).setUserEmail(eMail);
            new SessionManager(getApplicationContext()).setUserPassword(passWord);
            finish();
        });
    }

    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            if (action.equals("logout")) {
                logoutDialog();
            }
        }
    };


    public void newProfileMenu(View v) {
        unselectAllMenu();
        ((ImageView) findViewById(R.id.img_newMenuProfile)).setImageResource(R.drawable.avatarselected);

        showFragment(myAccountFragment);
        if (sessionManager.getGender().equals("male")) {
            detachOncam();
        }

    }

    public void newChatMenu(View v) {
        unselectAllMenu();
        ((ImageView) findViewById(R.id.img_newMenuMessage)).setImageResource(R.drawable.conversationselected);

        showFragment(messageFragment);
        detachOncam();
    }

    public void recentRechargeNewMenu(View v) {
        unselectAllMenu();
        ((ImageView) findViewById(R.id.img_newMenuRecentRecharge)).setImageResource(R.drawable.recentrechargeselect);

        recentRecharges = new RecentRecharges();
        fm.beginTransaction().add(R.id.fragment_view, recentRecharges, "2").hide(recentRecharges).commit();
        showFragment(recentRecharges);
    }

    public void maleOnCamMenu(View v) {
        unselectAllMenu();
        ((ImageView) findViewById(R.id.img_newMenuOnCam)).setImageResource(R.drawable.playbuttonselected);

        if (sessionManager.getGender().equals("male")) {
            matchOnCamFragment = new MatchOnCamFragment();
            fm.beginTransaction().add(R.id.fragment_view, matchOnCamFragment, "2").hide(matchOnCamFragment).commit();

            showFragment(matchOnCamFragment);

            startCountDown();
        } else {
            loadSearchFragement();
        }

    }

    public void newHomeMenu(View v) {

        unselectAllMenu();
        ((ImageView) findViewById(R.id.img_newMenuHome)).setImageResource(R.drawable.heartattackselected);

        if (sessionManager.getGender().equals("male")) {
            showFragment(homeFragment);
            if (myCountDownTimer != null) {
                inCount = false;
                myCountDownTimer.cancel();
            }
            detachOncam();
            homeFragment.onResume();

        } else {
            showFragment(femaleHomeFragment);
            femaleHomeFragment.onResume();
            apiManager.getBanList();
        }
    }

    private void unselectAllMenu() {
        ((ImageView) findViewById(R.id.img_newMenuHome)).setImageResource(R.drawable.heartattackunselect);
        ((ImageView) findViewById(R.id.img_newMenuOnCam)).setImageResource(R.drawable.playbuttonunselect);
        ((ImageView) findViewById(R.id.img_newMenuRecentRecharge)).setImageResource(R.drawable.recentrechargeunselect);
        ((ImageView) findViewById(R.id.img_newMenuMessage)).setImageResource(R.drawable.conversationunselect);
        ((ImageView) findViewById(R.id.img_newMenuProfile)).setImageResource(R.drawable.avatarunselect);
    }

    public void rankMenu(View v) {
        Toast.makeText(this, "Comming Soon", Toast.LENGTH_SHORT).show();
    }
}