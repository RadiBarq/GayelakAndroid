package com.gayelak.gayelakandroid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    public static boolean isItOwnerProfile = true;
    ViewPager viewPager;
    TabLayout tabLayout;
    TextView userNameTextView;
    ImageView profileImage;
    static String userId;
    static String userName;
    StorageReference profileImaegRef;
    String[] ownProfileTabs = {"المبيوعات مسبقا", "المعروضات للبيع", "المفضلة"};
    String[] notOwnProfileTabs = {"المبيوعات مسبقا", "المعروضات للبيع"};
    ImageView profileSettingsImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userNameTextView = (TextView) findViewById(R.id.passwordText);
        profileImage = (ImageView) findViewById(R.id.profileImageView);
        profileImaegRef = FirebaseStorage.getInstance().getReference();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        profileSettingsImageView = (ImageView) findViewById(R.id.profileSettingsImage);

        if (userId.matches(LoginActivity.user.UserId))
        {
            isItOwnerProfile = true;
            viewPager.setAdapter(new sliderAdapter(getSupportFragmentManager(), ownProfileTabs));
        }

        else
        {
            isItOwnerProfile = false;
            viewPager.setAdapter(new sliderAdapter(getSupportFragmentManager(), notOwnProfileTabs));
            profileSettingsImageView.setImageResource(R.drawable.ic_more_vert_white_36dp);
        }

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        userNameTextView.setText(userName);
        profileImaegRef = profileImaegRef.child("Profile_Pictures").child(userId).child("Profile.jpg");
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(profileImaegRef).animate(android.R.anim.fade_in).thumbnail(Glide.with(this).load(R.drawable.spinner_gif)).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profileImage);

        if (isItOwnerProfile == true)
        {
            // here all the work also

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
         String tabs[];

        public sliderAdapter(FragmentManager fm, String tabs[]) {
            super(fm);
            this.tabs = tabs;
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

    public void onClickSettings(View view)
    {

        if (isItOwnerProfile == true)
        {
            Intent settingsIntent = new Intent(ProfileActivity.this, ProfileSettingsActivity.class);
            startActivity(settingsIntent);
        }

        else
        {
            ValueEventListener checkBlockListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("العملية على المستخدم");

                    if(dataSnapshot.hasChild(userId))
                    {
                        final String[] userOperations = { "الغاء حذر المستخدم","التبليغ عن المستخدم"};
                        builder.setItems(userOperations, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                //PostItemActivity.clickedItem = position;

                                if (which == 0)
                                {
                                   //remove block from user
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("block").child(userId).removeValue();
                                }

                                else
                                {
                                    //report user.
                                    ReportUserActivity.reportedUserId = userId;
                                    Intent reportUserIntent = new Intent(ProfileActivity.this, ReportUserActivity.class);
                                    startActivity(reportUserIntent);

                                }
                            }
                        });

                        builder.setNegativeButton("اغلاق", null);
                        builder.show();
                    }

                    else {

                        final String[] userOperations = {"حذر المستخدم","التبليغ عن المستخدم"};
                        builder.setItems(userOperations, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                               // PostItemActivity.clickedItem = position;

                                if (which == 0)
                                {
                                    //remove block from user
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("block").child(userId).setValue(userName);
                                }

                                else
                                {
                                    //report user.
                                    ReportUserActivity.reportedUserId = userId;
                                    Intent reportUserIntent = new Intent(ProfileActivity.this, ReportUserActivity.class);
                                    startActivity(reportUserIntent);
                                }
                            }
                        });

                        builder.setNegativeButton("اغلاق", null);
                        builder.show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();

                }
            };

           DatabaseReference checkBlockRef =  FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("block");
           checkBlockRef.addListenerForSingleValueEvent(checkBlockListener);
        }
    }
}
