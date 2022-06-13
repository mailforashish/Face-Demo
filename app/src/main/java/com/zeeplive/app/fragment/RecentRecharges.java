package com.zeeplive.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zeeplive.app.R;
import com.zeeplive.app.adapter.RecentRechargesAdapter;
import com.zeeplive.app.databinding.FragmentRecentRechargesBinding;
import com.zeeplive.app.response.RecentRechargeResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;

public class RecentRecharges extends Fragment implements ApiResponseInterface {

    FragmentRecentRechargesBinding binding;
    ApiManager apiManager;

    public RecentRecharges() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recent_recharges, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        apiManager = new ApiManager(getContext(), this);
        apiManager.getRecentRecharges();

        binding.searchLoader.setVisibility(View.VISIBLE);
        binding.cvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.searchLoader.setVisibility(View.VISIBLE);
                apiManager.getRecentRecharges();
            }
        });
    }


    @Override
    public void isError(String errorCode) {
        Toast.makeText(getContext(), errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.RECENT_RECHARGES) {
            //   Log.e("RECENT_RECHARGES", new Gson().toJson(response));
            binding.searchLoader.setVisibility(View.GONE);
            RecentRechargeResponse rsp = (RecentRechargeResponse) response;
            RecentRechargesAdapter rechargesAdapter = new RecentRechargesAdapter(getContext(), rsp.getResult());
            binding.recyclerview.setItemAnimator(new DefaultItemAnimator());
            binding.recyclerview.setAdapter(rechargesAdapter);
        }
    }
}