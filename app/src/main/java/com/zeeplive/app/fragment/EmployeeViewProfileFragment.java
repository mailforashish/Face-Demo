package com.zeeplive.app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zeeplive.app.R;
import com.zeeplive.app.activity.InboxDetails;
import com.zeeplive.app.adapter.GiftCountDisplayAdapter;
import com.zeeplive.app.response.AddRemoveFavResponse;
import com.zeeplive.app.response.DisplayGiftCount.GiftCountResult;
import com.zeeplive.app.response.DisplayGiftCount.GiftDetails;
import com.zeeplive.app.response.DisplayGiftCount.Result;
import com.zeeplive.app.response.RecentRechargeResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class EmployeeViewProfileFragment extends Fragment implements ApiResponseInterface {

    String prodileId, recProfileId;
    String name;
    String image;
    String isFav;
    int position;

    //giftcount
    RecyclerView rv_giftshow;
    GiftCountDisplayAdapter giftCountDisplayAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<GiftDetails> giftDetailsArrayList;
    ArrayList<Result> resultArrayList;
    FragmentManager childFragmentManager ;

    public EmployeeViewProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_view_profile, container, false);
        name = getArguments().getString("recName");
        prodileId = getArguments().getString("recProfileId");

        childFragmentManager = getChildFragmentManager();

        ImageView img_back = view.findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStackImmediate();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDataFromRecent();
        initControls();
    }


    private void getDataFromRecent() {
        //Intent in = getActivity().getIntent();
        //Bundle b = in.getArgu();
        Bundle b = getArguments();

        if (b != null) {
            name = b.getString("recName");
            recProfileId = b.getString("recId");
            ((TextView) getActivity().findViewById(R.id.user_name_emp)).setText(name);
            prodileId = b.getString("recId");
            ((TextView) getActivity().findViewById(R.id.user_id_emp)).setText("ID :" + prodileId);
            // ((TextView) findViewById(R.id.call_rate_tv)).setText(b.getString("recPoints"));
            image = b.getString("recImage");

            Glide.with(getApplicationContext())
                    .load(b.getString("recImage"))
                    .centerCrop()
                    .error(R.drawable.maleplaceholder)
                    .into(((ImageView) getActivity().findViewById(R.id.img_user)));

        }
    }


    private void initControls() {
        rv_giftshow = getActivity().findViewById(R.id.rv_giftshow);
        giftDetailsArrayList = new ArrayList<>();
        resultArrayList = new ArrayList<>();
        giftCountDisplayAdapter = new GiftCountDisplayAdapter(getApplicationContext(), giftDetailsArrayList, resultArrayList);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_giftshow.setLayoutManager(linearLayoutManager);
        rv_giftshow.setAdapter(giftCountDisplayAdapter);

        new ApiManager(getApplicationContext(), this).getGiftCountForHost(String.valueOf(prodileId));


        ((Button) getActivity().findViewById(R.id.add_friend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ApiManager(getApplicationContext()).doFavourite(Integer.parseInt(prodileId));
            }
        });

        ((Button) getActivity().findViewById(R.id.chat_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                gotoChatConversation();

                gotoFirebaseChatConversation();

            }
        });
    }


    public void gotoFirebaseChatConversation() {
        Intent intent = new Intent(getContext(), InboxDetails.class);
        intent.putExtra("receiver_id", recProfileId);
        intent.putExtra("receiver_name", name);


        if (image == null) {
            intent.putExtra("receiver_image", "empty");
        } else {
            intent.putExtra("receiver_image", image);
        }
        startActivity(intent);

        new SessionManager(getContext()).isRecentChatListUpdateNeeded(true);
    }

    @Override
    public void isError(String errorCode) {

    }

    List<RecentRechargeResponse.Result> resultList;

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        try {

            if (ServiceCode == Constant.GET_GIFT_COUNT) {
                GiftCountResult rsp = (GiftCountResult) response;
                try {
                    resultArrayList.addAll(rsp.getResult());

                    ((TextView) getActivity().findViewById(R.id.call_rate_tv)).setText(rsp.getPoints());
                    if (resultArrayList.size() == 0) {
                        ((TextView) getActivity().findViewById(R.id.tv_gifrecmsg)).setVisibility(View.GONE);
                        rv_giftshow.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < rsp.getResult().size(); i++) {
                        giftDetailsArrayList.add(rsp.getResult().get(i).getGiftDetails());
                    }
                    //giftDetailsArrayList.add(rsp.getResult().get(0).getGiftDetails());
                    giftCountDisplayAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }

            if (ServiceCode == Constant.DO_FAVOURITE) {
                AddRemoveFavResponse rsp = (AddRemoveFavResponse) response;
                Toast.makeText(getContext(), rsp.getResult(), Toast.LENGTH_SHORT).show();

                Intent myIntent = new Intent("FBR");
                myIntent.putExtra("action", "reload");
                getContext().sendBroadcast(myIntent);
            }
        } catch (Exception e) {
        }
    }
}