package com.gayelak.gayelakandroid;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BrowsingItemActivity extends AppCompatActivity {

    MultiTouchViewPager viewPager;
    private LottieAnimationView animationView;
    public static String itemId;
    private DatabaseReference itemRef;
    Item item;

    TextView priceTextView;
    TextView distanceTextView;
    TextView titleTextView;
    TextView descriptionTextView;

    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browsing_item);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        viewPager = (MultiTouchViewPager) findViewById(R.id.viewPager);
        ItemViewPagerAdapter itemViewPagerAdapter = new ItemViewPagerAdapter(this);
        viewPager.setAdapter(itemViewPagerAdapter);
        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);
        playAnimation();
        itemRef = FirebaseDatabase.getInstance().getReference().child("items").child(itemId);
        getItemdData();
    }

    private void getItemdData()
    {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
               item = dataSnapshot.getValue(Item.class);




               // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(BrowsingItemActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

      itemRef.addValueEventListener(postListener);
    }

    private void playAnimation()
    {
        animationView.playAnimation();
        animationView.loop(true);
        animationView.setVisibility(View.VISIBLE);
    }
}
