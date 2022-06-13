package com.zeeplive.app.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.zeeplive.app.R;
import com.zeeplive.app.activity.LocationSelection;
import com.zeeplive.app.activity.MainActivity;
import com.zeeplive.app.activity.SocialLogin;
import com.zeeplive.app.response.LoginResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;


public class GenderDialog extends Dialog implements ApiResponseInterface, View.OnClickListener {
    private SocialLogin context;
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
    private String login_type;
    private String mHash = "";
    private int Dob;
    private String finalDob;


    public GenderDialog(@NonNull SocialLogin context, String login_type, String deviceId, String mHash) {
        super(context);
        this.context = context;
        this.login_type = login_type;
        this.deviceId = deviceId;
        this.mHash = mHash;
        setContentView(R.layout.activity_gender);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.setCanceledOnTouchOutside(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(context.getResources().getColor(R.color.titlebar));
        }

        apiManager = new ApiManager(getContext(), this);

        show();
        init();
        child = new Dialog(context);
    }


    void init() {
        Log.e("parent_here", "complete profile dialog");

        dr = context.getResources().getDrawable(R.drawable.gender_male_bg);
        dr.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

        dra = context.getResources().getDrawable(R.drawable.gender_female_bg);
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
                dismiss();
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
                btn_male.setTextColor(context.getResources().getColor(R.color.busyBackground));
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
                btn_female.setTextColor(context.getResources().getColor(R.color.busyBackground));
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, date, myCalendar
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
                    Toast.makeText(context, "Select One", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, "Age below 18", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("GenderDialog", "age above 18 " + y);
            tv_birth.setText(finalDob);
        }

    }


    public void showChildDialog() {
        child = new Dialog(context);
        child.setContentView(R.layout.attention_layout);
        child.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        child.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            child.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            child.getWindow().setStatusBarColor(context.getResources().getColor(R.color.titlebar));
        }
        child.show();
        tv_selected_gender = child.findViewById(R.id.tv_selected_gender);
        tv_think = child.findViewById(R.id.tv_think);
        btn_conform = child.findViewById(R.id.btn_conform);

        if (gender.equals("male")) {
            tv_selected_gender.setText("Male" + "?");
            tv_selected_gender.setTextColor(context.getResources().getColor(R.color.busyBackground));
            tv_selected_gender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gender_male, 0, 0, 0);
        } else {
            tv_selected_gender.setText("Female" + "?");
            tv_selected_gender.setTextColor(context.getResources().getColor(R.color.busyBackground));
            tv_selected_gender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gender_female, 0, 0, 0);
        }
        btn_conform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("GenderDialogT", "Valuelogin_type " + login_type);
                Log.e("GenderDialogT", "ValuedeviceId " + deviceId);
                Log.e("GenderDialogT", "Valuetv_selected_gender " + gender);
                Log.e("GenderDialogT", "ValueDob " + finalDob);
                Log.e("GenderDialogT", "ValuemHash " + mHash);

                apiManager.guestRegister(login_type, deviceId, gender, finalDob, mHash);
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

    @Override
    public void isError(String errorCode) {
        Toast.makeText(context, errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        String c_name = new SessionManager(context).getUserLocation();
        if (ServiceCode == Constant.REGISTER) {
            LoginResponse rsp = (LoginResponse) response;
            new SessionManager(context).saveGuestStatus(Integer.parseInt(rsp.getAlready_registered()));

            if (c_name.equals("null")) {
                Log.e("userLocationLog", c_name);
                if (rsp.getResult().getAllow_in_app_purchase() == 0) {
                    new SessionManager(context).createLoginSession(rsp);
                    Intent intent = new Intent(context, MainActivity.class);
                    context.finishAffinity();
                    Log.e("GenderDialogT", "ValueINTENT1 ");
                    context.startActivity(intent);

                } else {
                    new SessionManager(context).createLoginSession(rsp);
                    Log.e("GenderDialogT", "ValueINTENT2 ");
                    context.startActivity(new Intent(context, LocationSelection.class));
                }
            } else {
                new SessionManager(context).createLoginSession(rsp);
                Intent intent = new Intent(context, MainActivity.class);
                Log.e("GenderDialogT", "ValueINTENT3 ");
                context.finishAffinity();
                context.startActivity(intent);

            }
        }
        if (ServiceCode == Constant.GUEST_REGISTER) {
            LoginResponse rsp = (LoginResponse) response;

            if (rsp.getResult() != null && rsp.getResult().getUsername() != null && rsp.getResult().getDemo_password() != null) {

                new SessionManager(context).saveGuestStatus(Integer.parseInt(rsp.getAlready_registered()));
                new SessionManager(context).saveGuestPassword(rsp.getResult().getDemo_password());
                apiManager.login(rsp.getResult().getUsername(), rsp.getResult().getDemo_password());
            }
        }
        if (ServiceCode == Constant.LOGIN) {
            LoginResponse rsp = (LoginResponse) response;
            if (c_name.equals("null")) {
                if (rsp.getResult().getAllow_in_app_purchase() == 0) {
                    new SessionManager(context).createLoginSession(rsp);
                    Intent intent = new Intent(context, MainActivity.class);
                    context.finishAffinity();
                    context.startActivity(intent);
                    Log.e("GenderDialogT", "ValueINTENT4 ");

                } else {
                    new SessionManager(context).createLoginSession(rsp);
                    Log.e("GenderDialogT", "ValueINTENT5 ");
                    context.finishAffinity();
                    context.startActivity(new Intent(context, MainActivity.class));

                }
            } else {
                new SessionManager(context).createLoginSession(rsp);
                Intent intent = new Intent(context, MainActivity.class);
                Log.e("GenderDialogT", "ValueINTENT6 ");
                context.finishAffinity();
                context.startActivity(intent);

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        context.finish();
        dismiss();

    }
}