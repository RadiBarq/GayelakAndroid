package com.gayelak.gayelakandroid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by radibarq on 1/31/18.
 */

public class ViewPagerPostItemAdapter extends PagerAdapter {

    Context mContext;

    public ViewPagerPostItemAdapter(Context context)
    {
        mContext = context;

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.post_item_view_pager_layout, null);
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public float getPageWidth(int position) {
        return 0.25f;
    }


}
