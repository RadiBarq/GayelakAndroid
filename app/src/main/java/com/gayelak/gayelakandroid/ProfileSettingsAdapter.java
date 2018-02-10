package com.gayelak.gayelakandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by radibarq on 1/22/18.
 */

public class ProfileSettingsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    String[] listTitles= {"الصورة الشخصية", "البريد الالكتروني", "اسم المستخدم", "كلمة المرور" ,"تسجيل الخروج"};
    String[] listValues = {"", "grayllow@gmail.com", "radi barq", "", ""};
    int profilePiture = R.drawable.profile_picture;

    ProfileSettingsAdapter(Context context)
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

        View view;

        if (position == 0) {

            view = mInflater.inflate(R.layout.profile_settings_image_layout, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.email);
            textView.setText("الصورة الشخصية");
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setImageResource(R.drawable.profile_picture);


        } else if (position == 1) {

            view = mInflater.inflate(R.layout.profile_settings_email_layout, parent, false);
            TextView text = (TextView) view.findViewById(R.id.email);
            text.setText("البريد الالكتروني");

            TextView value = (TextView) view.findViewById(R.id.value);
            value.setText("test");
            //TextView textView = (TextView) view.findViewById(R.id.text);
            //TextView value = (TextView) view.findViewById(R.id.value);
        } else if (position == 2) {


            view = mInflater.inflate(R.layout.profile_settings_email_layout, parent, false);
            TextView text = (TextView) view.findViewById(R.id.email);
            text.setText("اسم المستخدم");
            TextView value = (TextView) view.findViewById(R.id.value);
            value.setText("test");

        } else if (position == 3) {

            view = mInflater.inflate(R.layout.profile_settings_image_layout, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.email);
            textView.setText("كلمة المرور");
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            android.support.v7.widget.CardView cardView = (android.support.v7.widget.CardView) view.findViewById(R.id.cardView);
            cardView.setVisibility(View.GONE);

        } else if (position == 4) {
            view = mInflater.inflate(R.layout.profile_settings_image_layout, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.email);
            textView.setText("قائمة المحذورين");
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            android.support.v7.widget.CardView cardView = (android.support.v7.widget.CardView) view.findViewById(R.id.cardView);
            cardView.setVisibility(View.GONE);

        } else {

            view = mInflater.inflate(R.layout.profile_settings_button_layout, parent, false);

        }

        return view;

    }

}
