package com.zeeplive.app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zeeplive.app.R;
import com.zeeplive.app.response.Broadcast.BroadcastList.BroadCastAudience;
import com.zeeplive.app.response.Broadcast.BroadcastList.BroadcastListData;
import com.zeeplive.app.response.Broadcast.BroadcastList.BroadcastListResponce;
import com.zeeplive.app.response.Broadcast.BroadcastStart.BroadCastResponce;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.RecyclerTouchListener;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;


public class BroadcastFragment extends Fragment implements ApiResponseInterface {


    private View rootView;
    private ArrayList<BroadcastListData> broadcastListData = new ArrayList<>();
    private RecyclerView rv_broadlist;
    private GridLayoutManager gridLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_broadcast, container, false);


        initControls();
        return rootView;
    }

    private void initControls() {

       /* if (new SessionManager(getContext()).getGender().equals("male")) {
            ((Button) rootView.findViewById(R.id.btn_startBroad)).setVisibility(View.GONE);
        } else {
            ((Button) rootView.findViewById(R.id.btn_startBroad)).setVisibility(View.VISIBLE);

        }*/

        rv_broadlist = rootView.findViewById(R.id.rv_broadlist);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rv_broadlist.setLayoutManager(gridLayoutManager);

        ((Button) rootView.findViewById(R.id.btn_startBroad)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ApiManager(getContext(), BroadcastFragment.this).startBroadCastFunction();
            }
        });

        rv_broadlist.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rv_broadlist, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    if (!broadcastListData.get(position).getChannelName().equals("")) {

                        new SessionManager(getContext()).setUserBroadtoken(broadcastListData.get(position).getToken());
                        new SessionManager(getContext()).setUserBroadtype("audi");
                        new SessionManager(getContext()).setUserBroadid(String.valueOf(broadcastListData.get(position).getId()));

                        new ApiManager(getContext()).audiJoinBroadFunction(
                                new SessionManager(getContext()).getUserBroadid()
                        );

                        ArrayList<BroadCastAudience> broadCastAudienceArrayList = new ArrayList<>();

                        broadCastAudienceArrayList.addAll(broadcastListData.get(position).getBroadCastAudiences());

                        String broadAudData = new Gson().toJson(broadCastAudienceArrayList);

                        Log.e("broadAudiinFr", new Gson().toJson(broadCastAudienceArrayList));


                    } else {
                        Toast.makeText(getContext(), "Something went here", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        new ApiManager(getContext(), BroadcastFragment.this).getBroadCastListFunction();
    }

    @Override
    public void isError(String errorCode) {

    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        try {
            if (ServiceCode == Constant.GET_BROADCAST_START) {
                BroadCastResponce broadCastResponce = (BroadCastResponce) response;
                if (!broadCastResponce.getBroadCastData().getChannelName().equals("")) {

                    new SessionManager(getContext()).setUserBroadtoken(broadCastResponce.getBroadCastData().getToken());
                    new SessionManager(getContext()).setUserBroadid(String.valueOf(broadCastResponce.getBroadCastData().getId()));

                    new SessionManager(getContext()).setUserBroadtype("host");

                    String hostId = new SessionManager(getContext()).getUserId();



                } else {
                    Toast.makeText(getContext(), "Something went here", Toast.LENGTH_SHORT).show();
                }
            }

            if (ServiceCode == Constant.GET_BROADCAST_LIST) {
                BroadcastListResponce broadcastListResponce = (BroadcastListResponce) response;
                broadcastListData.addAll(broadcastListResponce.getResult());
            }

        } catch (Exception e) {
        }
    }
}