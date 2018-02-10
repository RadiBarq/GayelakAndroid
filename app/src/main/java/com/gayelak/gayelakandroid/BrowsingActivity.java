package com.gayelak.gayelakandroid;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.w3c.dom.Text;
import java.io.Console;
import java.util.ArrayList;

public class BrowsingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    ArrayList<String> finalItems;
    GeoFire geoFire;
    DatabaseReference geoFireDatabaseRef;
    GridView gridView;
    DisplayMetrics displayMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        finalItems = new ArrayList<String>();
        geoFireDatabaseRef = FirebaseDatabase.getInstance().getReference().child("items-location");
        geoFire = new GeoFire(geoFireDatabaseRef);


        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
         gridView = (GridView) findViewById(R.id.gridview);

        initializeHeaderItems();
        getItems();
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
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(

            new SearchView.OnQueryTextListener(
            )
            {
                @Override
                public boolean onQueryTextSubmit(String query) {

                   // System.out.print("submit");
                    doMySearch(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            }
        );

        return true;
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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

            case R.id.nav_chat:
                break;

            case R.id.nav_notifications:
                break;

            case R.id.nav_discover:
                break;

            case R.id.nav_profile:
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
        TextView headerEmail = (TextView) headerView.findViewById(R.id.email);

        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(headerImage);
        headerUserName.setText(LoginActivity.user.UserName);
        headerEmail.setText(LoginActivity.user.Email);
    }
    // This is just for testing
    //32.216382, 35.248197
    private void getItems()
    {
        // TODO add the current location
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(32.216382, 35.248197), 1);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                    finalItems.add(key);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                gridView.setAdapter(new BrowsingImageAdapter(BrowsingActivity.this, finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        BrowsingItemActivity.itemId = finalItems.get(position);

                        intentToItem();

                    }
                });


            }
            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    private void intentToItem()
    {
        Intent intent  = new Intent(this, BrowsingItemActivity.class);
        startActivity(intent);
    }
}
