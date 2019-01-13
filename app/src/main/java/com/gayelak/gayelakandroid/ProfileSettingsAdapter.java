package com.gayelak.gayelakandroid;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by radibarq on 1/22/18.
 */

public class ProfileSettingsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    String[] listTitles= {"الصورة الشخصية", "البريد الالكتروني", "اسم المستخدم", "كلمة المرور" ,"تسجيل الخروج"};
    String[] listValues = {"", "grayllow@gmail.com", "radi barq", "", ""};
    int profilePiture = R.drawable.profile_picture;
    double screenHeight;
    StorageReference profileImaegRef;


    ProfileSettingsAdapter(Context context, double screenHeight)
    {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.screenHeight = screenHeight;
        profileImaegRef = FirebaseStorage.getInstance().getReference();
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
        profileImaegRef = FirebaseStorage.getInstance().getReference();

        if (position == 0) {

            view = mInflater.inflate(R.layout.profile_settings_image_layout, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.userName);
            textView.setText("الصورة الشخصية");
            ImageView profileImage = (ImageView) view.findViewById(R.id.image);
            profileImaegRef = profileImaegRef.child("Profile_Pictures").child(LoginActivity.user.UserId).child("Profile.jpg");

            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(profileImaegRef).animate(android.R.anim.fade_in).thumbnail(Glide.with(mContext).load(R.drawable.spinner_gif)).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(profileImage);

        } else if (position == 1) {

            view = mInflater.inflate(R.layout.profile_settings_email_layout, parent, false);
            TextView text = (TextView) view.findViewById(R.id.userName);
            text.setText("البريد الالكتروني");
            TextView value = (TextView) view.findViewById(R.id.value);
            value.setText(LoginActivity.user.Email);
            //TextView textView = (TextView) view.findViewById(R.id.text);
            //TextView value = (TextView) view.findViewById(R.id.value);
        } else if (position == 2) {


            view = mInflater.inflate(R.layout.profile_settings_email_layout, parent, false);
            TextView text = (TextView) view.findViewById(R.id.userName);
            text.setText("اسم المستخدم");
            TextView value = (TextView) view.findViewById(R.id.value);
            value.setText(LoginActivity.user.UserName);

        } else if (position == 3) {

            view = mInflater.inflate(R.layout.profile_settings_image_layout, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.userName);
            textView.setText("كلمة المرور");
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            android.support.v7.widget.CardView cardView = (android.support.v7.widget.CardView) view.findViewById(R.id.cardView);
            cardView.setVisibility(View.GONE);

        } else if (position == 4) {
            view = mInflater.inflate(R.layout.profile_settings_image_layout, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.userName);
            textView.setText("قائمة المحذورين");
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            android.support.v7.widget.CardView cardView = (android.support.v7.widget.CardView) view.findViewById(R.id.cardView);
            cardView.setVisibility(View.GONE);

        } else {

            view = mInflater.inflate(R.layout.profile_settings_button_layout, parent, false);
            Button button = (Button) view.findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //logout button should be empty like that.
                    Intent i = mContext.getPackageManager()
                            .getLaunchIntentForPackage( mContext.getApplicationContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(i);
                    FirebaseAuth.getInstance().signOut();
                }
            });
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int) (screenHeight / 11);
        view.setLayoutParams(params);
        return view;

    }
}
