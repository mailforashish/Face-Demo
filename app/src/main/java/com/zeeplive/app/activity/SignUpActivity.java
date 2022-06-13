package com.zeeplive.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeeplive.app.R;
import com.zeeplive.app.response.LoginResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.CommonMethod;
import com.zeeplive.app.utils.Constant;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, ApiResponseInterface {

    LinearLayout tv_log;
    Button signUp;
    EditText et_mobile, et_name, et_email, et_password;
    /*et_address*/
    CheckBox policy_checkbox;
    ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        et_mobile = findViewById(R.id.et_mobile);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.email);
        //   et_address = findViewById(R.id.address);
        et_password = findViewById(R.id.password);
        signUp = findViewById(R.id.btn_sign_up);
        tv_log = findViewById(R.id.login);
        policy_checkbox = findViewById(R.id.policy_checkbox);

        tv_log.setOnClickListener(this);
        signUp.setOnClickListener(this);


        // Prevent auto open edittext
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        apiManager = new ApiManager(this, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.login:
                finish();
                return;

            case R.id.btn_sign_up:
                if (validateAllDetails()) {
                    if (CommonMethod.isOnline(this)) {

                        apiManager.registerUser(et_name.getText().toString(), et_email.getText().toString(),
                                et_password.getText().toString(), et_password.getText().toString(),
                                "manualy", et_email.getText().toString(), et_mobile.getText().toString(),
                                "male");

                    } else {
                        Toast.makeText(this, "Internet not available !", Toast.LENGTH_SHORT).show();
                    }
                }
                return;

        }
    }

    private boolean validateAllDetails() {
        boolean checkFields = true;


        if (!policy_checkbox.isChecked()) {
            policy_checkbox.setError("Check term & conditions");
            checkFields = false;
        }
        if (!validatePassword()) {
            checkFields = false;
        }
       /* if (!validateAddress()) {
            checkFields = false;
        }*/
        if (!mobileNumber()) {
            checkFields = false;
        }
        if (!validateEmail()) {
            checkFields = false;
        }
        if (!validateUserName()) {
            checkFields = false;
        }

        return checkFields;
    }

    private boolean validateUserName() {
        if (et_name.getText().toString().trim().isEmpty()) {
            et_name.setError("Please enter your name");
            requestFocus(et_name);
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        if (et_email.getText().toString().trim().isEmpty()) {
            et_email.setError("Email can't be empty");
            requestFocus(et_email);
            return false;

        } else if (!isValidEmail(et_email.getText().toString().trim())) {
            et_email.setError("Looks like you have entered wrong Email Id.");
            requestFocus(et_email);
            return false;

        }
        return true;
    }

    private boolean isValidEmail(String username) {
        return Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(username).matches();
    }

    private boolean mobileNumber() {
        if (et_mobile.getText().toString().trim().length() < 10) {
            et_mobile.setError("Please enter valid 10 digit mobile number");
            requestFocus(et_mobile);
            return false;
        }
        return true;
    }

  /*  private boolean validateAddress() {
        if (et_address.getText().toString().trim().length() < 1) {
            et_address.setError("Please enter valid address");
            requestFocus(et_address);
            return false;
        }
        return true;
    }*/

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().length() < 6) {
            et_password.setError("Password length must be 6 or greater");
            requestFocus(et_password);
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
        if (ServiceCode == Constant.REGISTER) {
            LoginResponse rsp = (LoginResponse) response;

            Intent intent = new Intent(this, VerifyOtp.class);
            intent.putExtra("email_id", et_email.getText().toString());
            startActivity(intent);
        }
    }
}