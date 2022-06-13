package com.zeeplive.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zeeplive.app.R;
import com.zeeplive.app.adapter.ViewTicketAdapter;
import com.zeeplive.app.databinding.ActivityViewTicketBinding;
import com.zeeplive.app.response.ViewTicketResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;

public class ViewTicketActivity extends AppCompatActivity implements ApiResponseInterface {

    //  TransactionAdapter adapter;
    ActivityViewTicketBinding binding;
    ApiManager apiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_ticket);

        binding.heading.setText("View Ticket");
        binding.ticketRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.setClickListener(new EventHandler(this));

        apiManager = new ApiManager(this, this);
        apiManager.viewTicket();
    }

    public class EventHandler {
        Context mContext;

        public EventHandler(Context mContext) {
            this.mContext = mContext;
        }

        public void closeActivity() {
            finish();
        }
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(this, errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.VIEW_TICKET) {

            ViewTicketResponse rsp = (ViewTicketResponse) response;
            binding.ticketRecyclerview.setAdapter(new ViewTicketAdapter(this, rsp.getResult()));
        }
    }
}