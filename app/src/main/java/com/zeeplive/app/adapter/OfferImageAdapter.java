package com.zeeplive.app.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zeeplive.app.R;
import com.zeeplive.app.response.BannerResponse;

import java.util.List;

public class OfferImageAdapter extends PagerAdapter {

    //   private LayoutInflater inflater;
    private Context activity;
    List<BannerResponse.Result> list;


    public OfferImageAdapter(Context activity, List<BannerResponse.Result> result) {
        this.activity = activity;
        this.list = result;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = LayoutInflater.from(activity).inflate(R.layout.adapter_banner, view, false);

        //assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.imageView);


        Glide.with(activity)
                .load(list.get(position).getImage())
                .apply(new RequestOptions().placeholder(R.drawable.ic_photo_library_gray_100dp).
                        error(R.drawable.ic_photo_library_gray_100dp))
                .into(imageView);

        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}