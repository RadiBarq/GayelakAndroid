package com.gayelak.gayelakandroid;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ProfileActivity extends AppCompatActivity {


    ViewPager viewPager;

    TabLayout tabLayout;

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
