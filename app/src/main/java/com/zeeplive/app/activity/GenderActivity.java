package com.zeeplive.app.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zeeplive.app.GetAppVersion;
import com.zeeplive.app.R;
import com.zeeplive.app.dialog.GenderDialog;
import com.zeeplive.app.response.LoginResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.ErrorDialog;
import com.zeeplive.app.utils.SessionManager;

import org.json.JSONException;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GenderActivity extends AppCompatActivity implements View.OnClickListener, ApiResponseInterface {
    private String gender;
    private ImageView img_back_gender;
    private Button btn_male, btn_female, btn_next, btn_conform;
    private TextView tv_birth, tv_selected_gender, tv_think;
    private ApiManager apiManager;
    private Dialog child;
    private Drawable dr;
    private Drawable dra;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar myCalendar;
    private String deviceId;

    private int Dob;
    private String finalDob;

    ConnectivityManager cm;
    NetworkInfo activeNetwork;
    ErrorDialog errorDialog;
    private String clickButtonType = "";

    /*------------------- Facebook variables -----------------------*/
    LoginButton loginButton;
    ImageView fb, gmail_btn;
    CallbackManager callbackManager;
    public static final int REQ_CODE = 9001;
    public static final int FACEBOOK_REQ_CODE = 64206;
    /*------------------- Facebook variables ends here -------------*/

    /* ----- Google+ Login Variables ------- */
    GoogleSignInClient googleApiClient;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        apiManager = new ApiManager(this, this);

        clickButtonType = getIntent().getStringExtra("clickButtonType");
        deviceId = getIntent().getStringExtra("deviceId");
        loginButton = findViewById(R.id.facebook_login_button_gender);
        errorDialog = new ErrorDialog(this, "Please Check Your Internet Connection");

        /*------------ Callback Method Of Facebook ---------------*/
        callbackManager = CallbackManager.Factory.create();
        // loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFbInfo();
            }

            @Override
            public void onCancel() {
                Toast.makeText(GenderActivity.this, "cancel...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(GenderActivity.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
        /*------------ Callback Method Of Facebook ends here ---------------*/

        /*-------------- Google+ ----------------*/
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleApiClient = GoogleSignIn.getClient(this, googleSignInOptions);


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            autoCountrySelect();
        } else {
            getPermission();
        }
        hash();
        init();
        child = new Dialog(this);
    }

    void init() {
        Log.e("parent_here", "complete profile dialog");

        dr = this.getResources().getDrawable(R.drawable.gender_male_bg);
        dr.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

        dra = this.getResources().getDrawable(R.drawable.gender_female_bg);
        dra.setColorFilter(Color.parseColor("#be7af1"), PorterDuff.Mode.SRC_ATOP);

        try {
            img_back_gender = findViewById(R.id.img_back_gender);
            btn_male = findViewById(R.id.btn_male);
            btn_female = findViewById(R.id.btn_female);
            tv_birth = findViewById(R.id.tv_birth);
            btn_next = findViewById(R.id.btn_next);

            img_back_gender.setOnClickListener(this);
            btn_male.setOnClickListener(this);
            btn_female.setOnClickListener(this);
            tv_birth.setOnClickListener(this);
            btn_next.setOnClickListener(this);
        } catch (Exception e) {

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back_gender:
                onBackPressed();
                break;
            case R.id.btn_male:
                gender = "male";
                if (btn_male == null) {
                    btn_male = (Button) findViewById(view.getId());
                } else {
                    btn_male.setBackgroundResource(R.drawable.gender_male_bg);
                    btn_male = (Button) findViewById(view.getId());
                }
                btn_male.setBackgroundDrawable(dr);
                btn_male.setTextColor(this.getResources().getColor(R.color.busyBackground));
                btn_male.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gender_male, 0, 0, 0);

                btn_female.setBackgroundDrawable(dra);
                btn_female.setTextColor(Color.WHITE);
                btn_female.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gender0, 0, 0, 0);

                break;
            case R.id.btn_female:
                gender = "female";
                if (btn_female == null) {
                    btn_female = (Button) findViewById(view.getId());
                } else {
                    btn_female.setBackgroundResource(R.drawable.gender_female_bg);
                    btn_female = (Button) findViewById(view.getId());
                }
                btn_female.setBackgroundDrawable(dr);
                btn_female.setTextColor(this.getResources().getColor(R.color.busyBackground));
                btn_female.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gender_female, 0, 0, 0);

                btn_male.setBackgroundDrawable(dra);
                btn_male.setTextColor(Color.WHITE);
                btn_male.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gender1, 0, 0, 0);
                break;
            case R.id.tv_birth:
                myCalendar = Calendar.getInstance();
                date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }

                };
                myCalendar.add(Calendar.YEAR, -18);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                //following line to restrict future date selection
                datePickerDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
                datePickerDialog.show();
                //myCalendar.add(Calendar.YEAR, -18);
                Dob = myCalendar.get(Calendar.YEAR);
                Log.e("GenderDialog", "dataeage18 " + myCalendar.get(Calendar.YEAR));

                break;
            case R.id.btn_next:
                if (TextUtils.isEmpty(gender)) {
                    Toast.makeText(this, "Select One", Toast.LENGTH_SHORT).show();
                } else {
                    showChildDialog();
                }

                break;
            default:
                break;

        }
    }


    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        finalDob = sdf.format(myCalendar.getTime());
        finalDob.split("/");
        String[] items1 = finalDob.split("/");
        String d1 = items1[0];
        String m1 = items1[1];
        String y1 = items1[2];
        int d = Integer.parseInt(d1);
        int m = Integer.parseInt(m1);
        int y = Integer.parseInt(y1);
        Log.e("GenderDialog", "Selected Year " + y);
        if (y > Dob) {
            Log.e("GenderDialog", "age below 18 " + y);
            Toast.makeText(this, "Age below 18", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("GenderDialog", "age above 18 " + y);
            tv_birth.setText(finalDob);
        }

    }


    public void showChildDialog() {
        child = new Dialog(this);
        child.setContentView(R.layout.attention_layout);
        child.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        child.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            child.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            child.getWindow().setStatusBarColor(this.getResources().getColor(R.color.titlebar));
        }
        child.show();
        tv_selected_gender = child.findViewById(R.id.tv_selected_gender);
        tv_think = child.findViewById(R.id.tv_think);
        btn_conform = child.findViewById(R.id.btn_conform);

        if (gender.equals("male")) {
            tv_selected_gender.setText("Male" + "?");
            tv_selected_gender.setTextColor(this.getResources().getColor(R.color.busyBackground));
            tv_selected_gender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gender_male, 0, 0, 0);
        } else {
            tv_selected_gender.setText("Female" + "?");
            tv_selected_gender.setTextColor(this.getResources().getColor(R.color.busyBackground));
            tv_selected_gender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gender_female, 0, 0, 0);
        }
        btn_conform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("GenderDialogT", "Valuelogin_type " + "guest");
                Log.e("GenderDialogT", "ValuedeviceId " + deviceId);
                Log.e("GenderDialogT", "Valuetv_selected_gender " + gender);
                Log.e("GenderDialogT", "ValueDob " + finalDob);
                Log.e("GenderDialogT", "ValuemHash " + mHash);

                if (clickButtonType.equals("guest_login")) {
                    apiManager.guestRegister("guest", deviceId, gender, finalDob, "sXIteqZGAelGDidpsEcIEN+GsOk=");
                } else if (clickButtonType.equals("fb_btn")) {
                    if (((CheckBox) findViewById(R.id.chkTT)).isChecked()) {
                        checkInternetConnection();
                        if (activeNetwork != null) {
                            loginButton.performClick();
                        } else {
                            errorDialog.show();
                        }
                    } else {
                        Toast.makeText(GenderActivity.this, "Please accept the agreement first", Toast.LENGTH_SHORT).show();
                    }
                } else if (clickButtonType.equals("gmail_btn")) {
                    if (((CheckBox) findViewById(R.id.chkTT)).isChecked()) {
                        checkInternetConnection();
                        if (activeNetwork != null) {
                            Intent signInIntent = googleApiClient.getSignInIntent();
                            startActivityForResult(signInIntent, REQ_CODE);
                        } else {
                            errorDialog.show();
                        }
                    } else {
                        Toast.makeText(GenderActivity.this, "Please accept the agreement first", Toast.LENGTH_SHORT).show();
                    }
                }
                child.dismiss();
            }
        });
        tv_think.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                child.dismiss();
            }
        });

    }

    private void getPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        try {
                            if (report.areAllPermissionsGranted()) {
                            }
                            if (report.isAnyPermissionPermanentlyDenied()) {
                            }
                            if (report.getGrantedPermissionResponses().get(0).getPermissionName().equals("android.permission.ACCESS_FINE_LOCATION")) {
                                // autoCountrySelect();
                                enableLocationSettings();
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

    SessionManager sessionManager;
    int REQUEST_CODE_CHECK_SETTINGS = 2021;

    private void autoCountrySelect() {
        Log.e("LocationDetection", "m here");
        sessionManager = new SessionManager(this);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(getApplicationContext());
        for (String provider : lm.getAllProviders()) {
            @SuppressWarnings("ResourceType")
            Location location = lm.getLastKnownLocation(provider);
            if (location != null) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        String city_name = addresses.get(0).getLocality();
                        sessionManager.setUserLocation(addresses.get(0).getCountryName());
                        sessionManager.setUserAddress(city_name);
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            }
        }

    }

    public void enableLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(1000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                    // startUpdatingLocation(...);
                    autoCountrySelect();
                })
                .addOnFailureListener(this, ex -> {
                    Log.e("LocationService", "Failure");
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(GenderActivity.this, REQUEST_CODE_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            googleApiClient.signOut()
                    .addOnCompleteListener(this, task -> {
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_CHECK_SETTINGS == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                //user clicked OK, you can startUpdatingLocation(...);
                autoCountrySelect();
                // enableLocationSettings();
            } else {
                //user clicked cancel: informUserImportanceOfLocationAndPresentRequestAgain();
            }
        }

        if (requestCode == REQ_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        // Pass the activity result to the FacebookCallback.
        if (requestCode == FACEBOOK_REQ_CODE) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if (account != null) {
                new SessionManager(this).setUserFacebookName(account.getDisplayName());

                Log.e("GenderDialogG", "Valuelogin_type " + "google");
                Log.e("GenderDialogG", "Valuetv_selected_gender " + gender);
                Log.e("GenderDialogG", "ValueDob " + finalDob);
                Log.e("GenderDialogG", "ValuemHash " + account.getDisplayName());
                Log.e("GenderDialogG", "ValuemHash " + account.getId());
                apiManager.login_FbGoogle(account.getDisplayName(), "google", account.getId(), gender, finalDob, "sXIteqZGAelGDidpsEcIEN+GsOk=");
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("", "signInResult:failed code=" + e.getStatusCode());
            finish();
        }
    }

    /*------- Get Facebook Data ------------*/
    private void getFbInfo() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (object, response) -> {
                    try {
                        Log.e("LOG_TAG", "fb json object: " + object);
                        Log.e("LOG_TAG", "fb graph response: " + response);

                        String id = object.getString("id");
                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");
                        // 8/06/21 set user name in sheared prefrences
                        new SessionManager(this).setUserFacebookName(first_name + " " + last_name);
                        //Log.e("Userfacebooknamesocialp", first_name);
                        //String last_name = object.getString("last_name");
                        //String gender = object.getString("gender");
                        //String birthday = object.getString("birthday");
                        // String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";

                      /*String email = "";
                        if (object.has("email")) {
                            email = object.getString("email");
                        }*/
                        Log.e("GenderDialogF", "Valuelogin_type " + "facebook");
                        Log.e("GenderDialogF", "Valuetv_selected_gender " + gender);
                        Log.e("GenderDialogF", "ValueDob " + finalDob);
                        Log.e("GenderDialogF", "ValuemHash " + first_name);
                        Log.e("GenderDialogF", "ValuemHash " +  id);

                        apiManager.login_FbGoogle(first_name, "facebook", id,  gender, finalDob,"sXIteqZGAelGDidpsEcIEN+GsOk=");


                    } catch (JSONException e) {
                        e.printStackTrace();
                        finish();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
        request.setParameters(parameters);
        request.executeAsync();
    }
    /*------- Get Facebook Data ends here ------------*/

    void checkInternetConnection() {
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(this, errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        String c_name = new SessionManager(getApplicationContext()).getUserLocation();
        if (ServiceCode == Constant.REGISTER) {
            LoginResponse rsp = (LoginResponse) response;
            new SessionManager(this).saveGuestStatus(Integer.parseInt(rsp.getAlready_registered()));
            if (c_name.equals("null")) {
                if (rsp.getResult().getAllow_in_app_purchase() == 0) {
                    new SessionManager(this).createLoginSession(rsp);
                    Intent intent = new Intent(this, MainActivity.class);
                    finishAffinity();
                    startActivity(intent);
                } else {
                    new SessionManager(this).createLoginSession(rsp);
                    startActivity(new Intent(GenderActivity.this, LocationSelection.class));
                }
                startActivity(new Intent(GenderActivity.this, LocationSelection.class));
            } else {
                new SessionManager(this).createLoginSession(rsp);
                Intent intent = new Intent(GenderActivity.this, MainActivity.class);
                finishAffinity();
                startActivity(intent);
            }
        }
        if (ServiceCode == Constant.GUEST_REGISTER) {
            LoginResponse rsp = (LoginResponse) response;

            if (rsp.getResult() != null && rsp.getResult().getUsername() != null && rsp.getResult().getDemo_password() != null) {

                new SessionManager(this).saveGuestStatus(Integer.parseInt(rsp.getAlready_registered()));
                new SessionManager(this).saveGuestPassword(rsp.getResult().getDemo_password());
                //apiManager.login(rsp.getResult().getUsername(), rsp.getResult().getDemo_password(), mHash);
                apiManager.login(rsp.getResult().getUsername(), rsp.getResult().getDemo_password());
            }
        }
        if (ServiceCode == Constant.LOGIN) {
            LoginResponse rsp = (LoginResponse) response;
            if (c_name.equals("null")) {
                if (rsp.getResult().getAllow_in_app_purchase() == 0) {
                    new SessionManager(this).createLoginSession(rsp);
                    Intent intent = new Intent(this, MainActivity.class);
                    finishAffinity();
                    startActivity(intent);

                } else {
                    new SessionManager(this).createLoginSession(rsp);
                    startActivity(new Intent(GenderActivity.this, MainActivity.class));

                }
            } else {
                new SessionManager(this).createLoginSession(rsp);
                Intent intent = new Intent(this, MainActivity.class);
                finishAffinity();
                startActivity(intent);

            }
        }
    }

    private String mHash = "";

    private void hash() {
        PackageInfo info;
        try {

            info = getPackageManager().getPackageInfo(
                    this.getPackageName(), PackageManager.GET_SIGNATURES);

            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                //Log.e("Zeep_sha_key", md.toString());
                String something = new String(Base64.encode(md.digest(), 0));
                mHash = something;
                // Log.e("Zeep_Hash_key", something);
                System.out.println("Hash key" + something);
            }

        } catch (PackageManager.NameNotFoundException e1) {
            //Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            //Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            //Log.e("exception", e.toString());
        }
    }

    public void openAgreement(View view) {
        String urlString = "https://sites.google.com/view/zeeplive/terms";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed and open Kindle Browser
            intent.setPackage("com.amazon.cloud9");
            startActivity(intent);
        }
    }

    public void openPrivacyPolicy(View view) {
        String urlString = "https://sites.google.com/view/zeeplive/privacy";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed and open Kindle Browser
            intent.setPackage("com.amazon.cloud9");
            startActivity(intent);
        }
    }
}

