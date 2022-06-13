package com.zeeplive.app.fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.zeeplive.app.R;
import com.zeeplive.app.activity.EmployeeViewProfile;
import com.zeeplive.app.activity.ViewProfile;
import com.zeeplive.app.adapter.HomeUserAdapter;
import com.zeeplive.app.databinding.FragmentSearchBinding;
import com.zeeplive.app.response.UserListResponse;
import com.zeeplive.app.retrofit.ApiManager;
import com.zeeplive.app.retrofit.ApiResponseInterface;
import com.zeeplive.app.utils.Constant;
import com.zeeplive.app.utils.PaginationAdapterCallback;
import com.zeeplive.app.utils.PaginationScrollListener;
import com.zeeplive.app.utils.RecyclerTouchListener;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchFragment extends Fragment implements ApiResponseInterface, TextWatcher, PaginationAdapterCallback {

    FragmentSearchBinding binding;
    List<UserListResponse.Data> list = new ArrayList<>();
    ApiManager apiManager;
    HomeUserAdapter homeUserAdapter;

    GridLayoutManager gridLayoutManager;
    private static final int PAGE_START = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        View view = binding.getRoot();

        changeLoaderColor();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridLayoutManager = new GridLayoutManager(getContext(), 1);
        binding.searchList.setLayoutManager(gridLayoutManager);
        apiManager = new ApiManager(getContext(), this);
        binding.searchEd.addTextChangedListener(this);

        binding.searchList.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                binding.searchList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {

                    if (new SessionManager(getContext()).getGender().equals("male")) {
                        Intent intent = new Intent(getContext(), ViewProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user_data", list.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), EmployeeViewProfile.class);
                        intent.putExtra("recName", list.get(position).getName());
                        intent.putExtra("recProfileId", String.valueOf(list.get(position).getProfile_images().get(0).getUser_id()));
                        intent.putExtra("recId", String.valueOf(list.get(position).getProfile_images().get(0).getUser_id()));
                        intent.putExtra("recPoints", String.valueOf(list.get(position).getCall_rate()));
                        intent.putExtra("recImage", list.get(position).getProfile_images().get(0).getImage_name());
                        startActivity(intent);

                    }

                } catch (Exception e) {
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        binding.searchList.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(() -> apiManager.getUserListNextPage(String.valueOf(currentPage)
                        , binding.searchEd.getText().toString()), 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        resetPages();
        apiManager.searchUser(s.toString(), String.valueOf(currentPage));

        binding.placeholder.setVisibility(View.GONE);
        binding.searchLoader.setVisibility(View.VISIBLE);
    }

    void changeLoaderColor() {
        binding.searchLoader.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(getContext().getResources().getColor(R.color.colorPrimary),
                        PorterDuff.Mode.SRC_ATOP)
        );
    }

    @Override
    public void isError(String errorCode) {
        Toast.makeText(getContext(), errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isSuccess(Object response, int ServiceCode) {
        if (ServiceCode == Constant.SEARCH_USER) {
            UserListResponse rsp = (UserListResponse) response;

            if (rsp != null) {
                list.clear();
                list = rsp.getResult().getData();
                TOTAL_PAGES = rsp.getResult().getLast_page();
                if (list.size() > 0) {
                    homeUserAdapter = new HomeUserAdapter(getActivity(), SearchFragment.this, "search", SearchFragment.this);
                    binding.searchList.setItemAnimator(new DefaultItemAnimator());
                    binding.searchList.setAdapter(homeUserAdapter);

                    // Set data in adapter
                    homeUserAdapter.addAll(list);

                    if (currentPage < TOTAL_PAGES) {
                        homeUserAdapter.addLoadingFooter();
                    } else {
                        isLastPage = true;
                    }
                }
            } else {
                clearSearch();
                binding.placeholder.setVisibility(View.VISIBLE);
            }
        }
        if (ServiceCode == Constant.USER_LIST_NEXT_PAGE) {
            UserListResponse rsp = (UserListResponse) response;
            homeUserAdapter.removeLoadingFooter();
            isLoading = false;

            List<UserListResponse.Data> results = rsp.getResult().getData();
            list.addAll(results);
            homeUserAdapter.addAll(results);

            if (currentPage != TOTAL_PAGES) homeUserAdapter.addLoadingFooter();
            else isLastPage = true;
        }

        binding.searchLoader.setVisibility(View.GONE);
    }

    void clearSearch() {
        list.clear();
        if (homeUserAdapter != null) {
            homeUserAdapter.removeAll();
            homeUserAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void retryPageLoad() {
        apiManager.getUserListNextPage(String.valueOf(currentPage), binding.searchEd.getText().toString());
    }

    void resetPages() {
        // Reset Current page when refresh data
        this.currentPage = 1;
        this.isLastPage = false;
    }
}