package com.gayelak.gayelakandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

/**
 * Created by radibarq on 1/28/18.
 */

public class NotificationsAdapter extends BaseAdapter{

    Context context;
    LayoutInflater layoutInflater;


    public NotificationsAdapter(Context context)
    {

        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return 20;
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

        // here where we want to return the item.
        View view = layoutInflater.inflate(R.layout.notifications_layout, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 300;
        view.setLayoutParams(params);
        return view;
    }



}
