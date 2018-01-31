package com.gayelak.gayelakandroid;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gayelak.gayelakandroid.R;

/**
 * Created by radibarq on 1/30/18.
 */

public class MessagesListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    double screenHeight;



    MessagesListViewAdapter(Context context, double screenHeight)
    {

        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.screenHeight = screenHeight;

    }


    @Override
    public int getCount() {
        return messagesPhotos.length;
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

        View view;
        view = mLayoutInflater.inflate(R.layout.messages_list_view_layout, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();

        params.height = (int) (screenHeight / 10);
        view.setLayoutParams(params);
        return view;

    }

    private int[] messagesPhotos = {

            R.drawable.face_1,
            R.drawable.face_2,
            R.drawable.face_3
    };

}
