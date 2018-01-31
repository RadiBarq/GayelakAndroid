package com.gayelak.gayelakandroid;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;


import com.airbnb.lottie.LottieAnimationView;

public class BrowsingItemActivity extends AppCompatActivity {

    MultiTouchViewPager viewPager;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browsing_item);
        viewPager = (MultiTouchViewPager) findViewById(R.id.viewPager);
        ItemViewPagerAdapter itemViewPagerAdapter = new ItemViewPagerAdapter(this);
        viewPager.setAdapter(itemViewPagerAdapter);
        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);
        playAnimation();

    }


    private void playAnimation()
    {

        animationView.playAnimation();
        animationView.loop(true);
        animationView.setVisibility(View.VISIBLE);

    }

}
