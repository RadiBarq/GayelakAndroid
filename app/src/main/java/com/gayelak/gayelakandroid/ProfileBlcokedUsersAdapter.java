package com.gayelak.gayelakandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by radibarq on 1/23/18.
 */

public class ProfileBlcokedUsersAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    ProfileBlcokedUsersAdapter(Context context)
    {

        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = mInflater.inflate(R.layout.profile_blocked_users_layout, parent, false);
        return view;
    }
}
