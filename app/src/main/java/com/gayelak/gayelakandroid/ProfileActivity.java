package com.gayelak.gayelakandroid;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    public static boolean isItOwnerProfile = true;
    ViewPager viewPager;
    TabLayout tabLayout;
    TextView userName;
    ImageView profileImage;
    String userId;
    StorageReference profileImaegRef;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new sliderAdapter(getSupportFragmentManager()));
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        userName = (TextView) findViewById(R.id.userName);
        profileImage = (ImageView) findViewById(R.id.profileImageView);
        profileImaegRef = FirebaseStorage.getInstance().getReference();

        userId = LoginActivity.user.UserId;

        if (isItOwnerProfile == true)
        {
            userName.setText(LoginActivity.user.UserName);
            profileImaegRef = profileImaegRef.child("Profile_Pictures").child(userId).child("Profile.jpg");


            Glide.with(this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(profileImaegRef).animate(android.R.anim.fade_in).thumbnail(Glide.with(this).load(R.drawable.spinner_gif)).crossFade()
                    .into(profileImage);
        }

        else {

            //TODO coming from other profile my lord.

        }
    }

    public void onClickBackImage(View view)
    {
        finish();

    }

    private class sliderAdapter extends FragmentPagerAdapter
    {

         final String tabs[] = {"المبيوعات مسبقا", "المعروضات للبيع", "المفضلة"};
        public sliderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0)
            {
                return new SoldFragment();
            }

            else if (position == 1)
            {
                return new  SellingFragment();
            }

            else
            {
                return new FavouritesFragment();
            }

        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }

}
