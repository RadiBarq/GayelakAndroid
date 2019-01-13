package com.gayelak.gayelakandroid;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class BrowsingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    NavigationView navigationView;
    com.arlib.floatingsearchview.FloatingSearchView mSearchView;
    TextView navSlideshow, navGallery;
    static ViewPager viewPager;
    static TabLayout tabLayout;
    String[] ownProfileTabs = {"المبيوعات مسبقا", "المعروضات للبيع", "المفضلة"};
    public static TextView notificationsBadge;
    public static TextView messagesBadge;
    private BadgeView badgeView;
    String notificationIntentExtra;
    BrowsingFragment browsingFragment = new BrowsingFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSearchView = (com.arlib.floatingsearchview.FloatingSearchView) findViewById(R.id.floating_search_view);
        notificationIntentExtra = getIntent().getStringExtra("notificationType");
        initializeTabLayout();

        setSupportActionBar(toolbar);
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        mSearchView.attachNavigationDrawerToMenuButton(drawer);
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {



         //   latitude = lastKnownLocation.getLatitude();


            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {

                BrowsingActivity.viewPager.setCurrentItem(0);
                browsingFragment.onSearchButtonClicked(currentQuery);
            }
        });

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                 if(newQuery.matches(""))
                 {
                     browsingFragment.clearSearch();
                 }
            }
        });

        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);

        ImageView headerProfilePicture = (ImageView) headerview.findViewById(R.id.imageView);
        headerProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileActivity.userId = LoginActivity.user.UserId;
                ProfileActivity.userName = LoginActivity.user.UserName;
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent profileActivity = new Intent(BrowsingActivity.this, ProfileActivity.class);
                startActivity(profileActivity);
            }
        });

        initializeSearchView();
        getSupportActionBar().hide();
        navigationView.setNavigationItemSelectedListener(this);
        initializeHeaderItems();
        // this when coming from notification clicked my lord...


    }

    private void initializeTabLayout()
    {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager.setAdapter(new sliderAdapter(getSupportFragmentManager(), ownProfileTabs));
        viewPager.setOffscreenPageLimit(4);

        View tabLayoutBrowseView =  LayoutInflater.from(this).inflate(R.layout.tab_custom_icon, null);
        View tabLayoutDiscoverView = LayoutInflater.from(this).inflate(R.layout.tab_custom_icon, null);
        View tabLayoutChatView = LayoutInflater.from(this).inflate(R.layout.tab_custom_icon, null);
        View tabLayoutNotificationsView = LayoutInflater.from(this).inflate(R.layout.tab_custom_icon, null);
        ImageView browseImage = (ImageView) tabLayoutBrowseView.findViewById(R.id.tab_icon);
        ImageView discoverImage = (ImageView) tabLayoutDiscoverView.findViewById(R.id.tab_icon);
        ImageView chatImage = (ImageView) tabLayoutChatView.findViewById(R.id.tab_icon);
        ImageView notificationsImage = (ImageView) tabLayoutNotificationsView.findViewById(R.id.tab_icon);

        TextView browseText = (TextView) tabLayoutBrowseView.findViewById(R.id.tab_badge);
        TextView discoverText = (TextView) tabLayoutDiscoverView.findViewById(R.id.tab_badge);
       // TextView chatText = (TextView) tabLayoutChatView.findViewById(R.id.tab_badge);
       // TextView notificationsText = (TextView) tabLayoutNotificationsView.findViewById(R.id.tab_badge);


        browseText.setVisibility(View.GONE);
        discoverText.setVisibility(View.GONE);

        notificationsBadge = (TextView) tabLayoutNotificationsView.findViewById(R.id.tab_badge);
        messagesBadge = (TextView) tabLayoutChatView.findViewById(R.id.tab_badge);
        notificationsBadge.setVisibility(View.GONE);
        messagesBadge.setVisibility(View.GONE);

        if(MyFirebaseMessagingService.notificationsNumber != 0)
         {
            notificationsBadge.setVisibility(View.VISIBLE);
            notificationsBadge.setText(String.valueOf(MyFirebaseMessagingService.messagesNumber));
         }

       // testImage.getDrawable().setColorFilter(Color.parseColor("#80FFFFFF"), PorterDuff.Mode.SRC_IN);

       // BadgeView bv1 = new BadgeView(this, ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {

                tabLayout.setupWithViewPager(viewPager);
                tabLayout.getTabAt(0).setCustomView(tabLayoutBrowseView);
                tabLayout.getTabAt(1).setCustomView(tabLayoutDiscoverView);
                tabLayout.getTabAt(2).setCustomView(tabLayoutChatView);
                tabLayout.getTabAt(3).setCustomView(tabLayoutNotificationsView);

                browseImage.setImageResource(R.drawable.ic_home_white_36dp);
                discoverImage.setImageResource(R.drawable.ic_explore_white_36dp);
                chatImage.setImageResource(R.drawable.ic_chat_bubble_outline_white_36dp);
                notificationsImage.setImageResource(R.drawable.ic_notifications_none_white_36dp);

                //tabLayout.getTabAt(0).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
               // browseImage.getDrawable().setColorFilter(Color.parseColor("#80FFFFFF"), PorterDuff.Mode.SRC_IN);
                discoverImage.getDrawable().setColorFilter(Color.parseColor("#80FFFFFF"), PorterDuff.Mode.SRC_IN);
                chatImage.getDrawable().setColorFilter(Color.parseColor("#80FFFFFF"), PorterDuff.Mode.SRC_IN);
                notificationsImage.getDrawable().setColorFilter(Color.parseColor("#80FFFFFF"), PorterDuff.Mode.SRC_IN);


                tabLayout.addOnTabSelectedListener(
                        new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                            @Override
                           public void onTabSelected(TabLayout.Tab tab) {
                                int position = tab.getPosition();

                                if (position == 0)
                                {
                                  //  tab.setIcon(R.drawable.ic_home_white_48dp);
                                    browseImage.setImageResource(R.drawable.ic_home_white_36dp);
                                    browseImage.getDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                                }

                                else if(position == 1)
                                {
                                   // tab.setIcon(R.drawable.ic_explore_white_48dp);
                                    discoverImage.setImageResource(R.drawable.ic_explore_white_36dp);
                                    discoverImage.getDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                                    navigationView.getMenu().getItem(2).setChecked(true);
                                }

                                else if(position == 2)
                                {
                                    //tab.setIcon(R.drawable.ic_chat_bubble_white_48dp);
                                    chatImage.setImageResource(R.drawable.ic_chat_bubble_white_36dp);
                                    chatImage.getDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                                    messagesBadge.setVisibility(View.GONE);
                                    MyFirebaseMessagingService.messagesCountForEachUser = new HashMap<String, Integer>();
                                    MyFirebaseMessagingService.messagesCounter =300;
                                    MyFirebaseMessagingService.messagesCount = new HashMap<String, Integer>();

                                }

                                else if (position == 3) {

                                    notificationsImage.setImageResource(R.drawable.ic_notifications_white_36dp);
                                    notificationsImage.getDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                                    notificationsBadge.setVisibility(View.GONE);
                                    MyFirebaseMessagingService.notificationsNumber = 0;
                                }
                            }
                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {

                                int position = tab.getPosition();
                                if (position == 0)
                                {
                                    browseImage.setImageResource(R.drawable.ic_home_white_48dp);
                                    browseImage.getDrawable().setColorFilter(Color.parseColor("#80FFFFFF"), PorterDuff.Mode.SRC_IN);
                                }

                                else if(position == 1)
                                {
                                    discoverImage.setImageResource(R.drawable.ic_explore_white_36dp);
                                    discoverImage.getDrawable().setColorFilter(Color.parseColor("#80FFFFFF"), PorterDuff.Mode.SRC_IN);
                                    navigationView.getMenu().getItem(0).setChecked(true);

                                }

                                else if(position == 2)
                                {
                                    chatImage.setImageResource(R.drawable.ic_chat_bubble_outline_white_36dp);
                                    chatImage.getDrawable().setColorFilter(Color.parseColor("#80FFFFFF"), PorterDuff.Mode.SRC_IN);
                                    BrowsingActivity.messagesBadge.setVisibility(View.GONE);

                                    if(MessagesListViewAdapter.messagesChanged == true)
                                    {
                                        unrecentMessages();
                                    }
                                }

                                else {

                                    notificationsImage.setImageResource(R.drawable.ic_notifications_none_white_36dp);
                                    notificationsImage.getDrawable().setColorFilter(Color.parseColor("#80FFFFFF"), PorterDuff.Mode.SRC_IN);
                                    BrowsingActivity.notificationsBadge.setVisibility(View.GONE);

                                    if (NotificationsAdapter.notificationsChanged == true)
                                    {
                                        unRecentNotifications();
                                    }

                                }
                            }
                        }
                );

                if (notificationIntentExtra != null)
                {
                    if (notificationIntentExtra.matches("normal"))
                    {
                        viewPager.setCurrentItem(3);
                    }

                    else if (notificationIntentExtra.matches("message"))
                    {
                        viewPager.setCurrentItem(2);
                    }
                }
            }
        });
    }


    private void unrecentMessages()
    {
        ValueEventListener messagesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot item: dataSnapshot.getChildren())
                {
                    String messageKey = item.getKey();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat").child(messageKey).child("lastMessage").child("recent").setValue("false");
                }


                MessagesListViewAdapter.messagesChanged = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(databaseError.getMessage(), databaseError.getDetails());

            }
        };

        FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat").addListenerForSingleValueEvent(messagesListener);
    }

    private void unRecentNotifications()
    {

        ValueEventListener notificationsListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot item: dataSnapshot.getChildren())
                {
                    String notifciationKey = item.getKey();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("notifications").child(notifciationKey).child("recent").setValue("false");
                }

                NotificationsAdapter.notificationsChanged = false;


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(databaseError.getMessage(), databaseError.getDetails());
            }
        };

        FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("notifications").addListenerForSingleValueEvent(notificationsListener);
    }
    private void initializeSearchView()
    {

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                int id = item.getItemId();
                switch (id)
                {
                    case R.id.action_filter:
                        Intent browsingSettingsIntent = new Intent(BrowsingActivity.this, BrowsingSettingsActivity.class);
                        startActivity(browsingSettingsIntent);
                        break;
                }
            }

        });
    }


    @Override
    public boolean onSearchRequested() {
        return true;

    }
        @Override
    protected void onResume() {

        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browsing, menu);
       /// MenuItem searchItem = menu.findItem(R.id.action_search);
       // SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

     //   SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
       // searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

       // searchView.setOnQueryTextListener(
//
           // new SearchView.OnQueryTextListener(
           // )
           // {
               // @Override
               // public boolean onQueryTextSubmit(String query) {

                   // System.out.print("submit");
                   // doMySearch(query);
                    //return false;
               // }

               // @Override
                //public boolean onQueryTextChange(String newText) {
          //          return false;
                //}
            //}
      //  );
      return  true;
    }

    private void doMySearch(String query)
    {
        System.out.println(query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_home:
                break;

            case R.id.nav_sell:
                Intent sellActivity = new Intent(BrowsingActivity.this, PostItemActivity.class);
                startActivity(sellActivity);
                break;

            case R.id.nav_discover:
                viewPager.setCurrentItem(1);
                break;


            case R.id.nav_profile:
                ProfileActivity.userId = LoginActivity.user.UserId;
                ProfileActivity.userName = LoginActivity.user.UserName;
                Intent profileActivity = new Intent(BrowsingActivity.this, ProfileActivity.class);
                startActivity(profileActivity);
                break;


            case R.id.nav_invite_facebook_friends:
                break;

            case R.id.nav_help:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeHeaderItems()
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile_Pictures").child(LoginActivity.user.UserId).child("Profile.jpg");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView headerImage =  (ImageView) headerView.findViewById(R.id.imageView);
        TextView headerUserName = (TextView) headerView.findViewById(R.id.user_name);
        TextView headerEmail = (TextView) headerView.findViewById(R.id.userName);
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(storageReference).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(headerImage);
        headerUserName.setText(LoginActivity.user.UserName);
        headerEmail.setText(LoginActivity.user.Email);
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
                return browsingFragment;
            }

            else if (position == 1)
            {
                return new CategoryFragment();
            }

            else if(position == 2)
            {
                return new MessagesFragment();
            }

            else
            {
                return new NotificationsFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
