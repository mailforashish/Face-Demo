package com.zeeplive.app.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.zeeplive.app.Config;
import com.zeeplive.app.R;

import com.zeeplive.app.response.LoginResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.ui.BaseActivity;
import com.zeeplive.app.utils.CommonMethod;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends BaseActivity implements View.OnClickListener, ApiResponseInterface {
    TextView tv_sign, et_forgot_password;
    Button login_btn;
    EditText username, password;
    SessionManager session;
    ApiManager apiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(this);
        tv_sign = findViewById(R.id.tv_sign);
        et_forgot_password = findViewById(R.id.et_forgot_password);
        login_btn = findViewById(R.id.login_btn);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        tv_sign.setOnClickListener(this);
        et_forgot_password.setOnClickListener(this);
        login_btn.setOnClickListener(this);

        // Prevent auto open edittext
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        apiManager = new ApiManager(this, this);

        getLoginDetails();
        hash();
    }

    private void getLoginDetails() {
        if (!session.getUserEmail().equals("null")) {
            username.setText(session.getUserEmail());
        }
        if (!session.getUserPassword().equals("null")) {
            password.setText(session.getUserPassword());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.login_btn:
                if (validateAllDetails()) {
                    if (CommonMethod.isOnline(this)) {
                        //apiManager.login(username.getText().toString(), password.getText().toString(),mHash);
                        apiManager.login(username.getText().toString(), password.getText().toString());

                    } else {
                        Toast.makeText(this, "Internet not connected !", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.tv_sign:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;


            case R.id.et_forgot_password:
                Intent i1 = new Intent(this, ForgotActivity.class);
                startActivity(i1);
                break;
        }
    }

    private boolean validateAllDetails() {
        boolean checkFields = true;

        if (!validateUserName()) {
            checkFields = false;
        }
        if (!validatePassword()) {
            checkFields = false;
        }
        return checkFields;
    }

    private boolean validateUserName() {
        if (username.getText().toString().trim().length() < 10) {
            username.setError("Incorrect Username");
            requestFocus(username);
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        if (password.getText().toString().trim().length() < 6) {
            password.setError("Incorrect Password");
            requestFocus(password);
            return false;
        } else {
            //  inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(this, errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.LOGIN) {
            LoginResponse rsp = (LoginResponse) response;
            new SessionManager(this).saveGuestStatus(Integer.parseInt(rsp.getAlready_registered()));
            session.createLoginSession(rsp);
            session.setUserEmail(username.getText().toString());
            session.setUserPassword(password.getText().toString());
            Intent intent = new Intent(this, MainActivity.class);
            finishAffinity();

            Config.UserProfile profile = config().getUserProfile();
            profile.setToken(rsp.getResult().getToken());
            profile.setAgoraUid(Long.parseLong(rsp.getResult().getProfile_id()));
            preferences().edit().putString(SessionManager.KEY_TOKEN, rsp.getResult().getToken()).apply();
            login();
            startActivity(intent);

        }
    }

      private void login() {
        Config.UserProfile profile = config().getUserProfile();
        initUserFromStorage(profile);
        if (!profile.isValid()) {
            config().getUserProfile().setUserName(username.getText().toString());
            preferences().edit().putString(SessionManager.KEY_USER_NAME, username.getText().toString()).apply();
        }
    }

    private void initUserFromStorage(Config.UserProfile profile) {
        profile.setUserId(preferences().getString(SessionManager.KEY_PROFILE_UID, null));
        profile.setUserName(preferences().getString(SessionManager.KEY_USER_NAME, null));
        profile.setImageUrl(preferences().getString(SessionManager.KEY_IMAGE_URL, null));
        profile.setToken(preferences().getString(SessionManager.KEY_TOKEN, null));
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
                //       Log.e("Zeep_sha_key", md.toString());
                String something = new String(Base64.encode(md.digest(), 0));
                mHash = something;
                //  Log.e("Zeep_Hash_key", something);
                System.out.println("Hash key" + something);
            }

        } catch (PackageManager.NameNotFoundException e1) {
            //     Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            //     Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            //     Log.e("exception", e.toString());
        }
    }






}