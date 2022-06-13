package com.zeeplive.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeeplive.app.R;
import com.zeeplive.app.dialog.MyProgressDialog;
import com.zeeplive.app.utils.CommonMethod;

import java.util.regex.Pattern;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_sign_up;
    Button btn_reset;
    EditText et_phonenumber;
    MyProgressDialog myProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        et_phonenumber = findViewById(R.id.et_phonenumber);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        tv_sign_up = findViewById(R.id.tv_sign_up);

        btn_reset.setOnClickListener(this);
        tv_sign_up.setOnClickListener(this);

        myProgressDialog = new MyProgressDialog(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:

                if (validateEmail()) {
                    if (CommonMethod.isOnline(this)) {
                        Intent intent = new Intent(this, VerifyOtp.class);
                        intent.putExtra("email_id", et_phonenumber.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Internet not connected !", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.tv_sign_up:
                Intent i = new Intent(this, SignUpActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    private boolean validateEmail() {
        if (et_phonenumber.getText().toString().trim().isEmpty()) {
            et_phonenumber.setError("Email can't be empty");
            requestFocus(et_phonenumber);
            return false;

        } else if (!isValidEmail(et_phonenumber.getText().toString().trim())) {
            et_phonenumber.setError("You have entered wrong Email Id.");
            requestFocus(et_phonenumber);
            return false;

        }
        return true;
    }

    private boolean isValidEmail(String username) {
        return Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(username).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
