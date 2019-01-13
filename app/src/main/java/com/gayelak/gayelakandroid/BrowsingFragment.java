package com.gayelak.gayelakandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.AbsListViewOverScrollDecorAdapter;

import static android.content.Context.LOCATION_SERVICE;


public class BrowsingFragment extends Fragment  {

    GridView gridView;
    ArrayList<String> finalItems;
    ArrayList<GeoLocation> finalLocationItems;
    ArrayList<String> carCategoryArray = new ArrayList<String>();
    ArrayList<String> phoneCategoryArray = new ArrayList<String>();
    ArrayList<String> apartmentCategoryArray = new ArrayList<String>();
    ArrayList<String> homeCategoryArray = new ArrayList<String>();
    ArrayList<String> dogCategoryArray = new ArrayList<String>();
    ArrayList<String> sportCategoryArray = new ArrayList<String>();
    ArrayList<String> clothesCategoryArray = new ArrayList<String>();
    ArrayList<String> kidsCategoryArray = new ArrayList<String>();
    ArrayList<String> booksCategoryArray = new ArrayList<String>();
    ArrayList<String> otherCategoryArray = new ArrayList<String>();
    ArrayList<String> uncommonArray = new ArrayList<String>();
    ArrayList<String> searchKeys = new ArrayList<String>();
    ArrayList<String> fetchedKeys;
    HashMap<String, Item> itemsDictionary = new HashMap<String, Item>();
    HashMap<String, Integer> favouritesDictionary = new HashMap<String, Integer>();
    HashMap<String, Double> pricesHighToLowDictionary = new HashMap<String, Double>();
    HashMap<String, Double> pricesLowToHighDictionary = new HashMap<String, Double>();
    HashMap<String, Double> timestampDictionary = new HashMap<String, Double>();
    DatabaseReference geoFireDatabaseRef;
    GeoFire geoFire;
    DisplayMetrics displayMetrics;
    static boolean queryChanged = false;
    private LottieAnimationView loadingAnimationView;
    private LottieAnimationView noItemsAnimationView;

    private Button enableLocationButton;

    private LottieAnimationView locationAnimationView;
    private TextView noLocationAvailableTextView;
    TextView animationDescriptionTextView;
    int maximumRadius;
    int radius;
    ArrayList<String> itemKeys;
    ArrayList<GeoLocation> itemLocations;
    int currentItemsCount = 0;
    Boolean searchButtonClicked = false;
    static double longitude;
    static double latitude;
    LocationManager mLocationManager;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    boolean locationEnabled = false;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        LocationListener locationListener = new LocationListener() {

                            public void onLocationChanged(Location location) {
                                // Called when a new location is found by the network location provider.
                                //makeUseOfNewLocation(location);
                                stopLocationAnimation();
                                noLocationAvailableTextView.setVisibility(View.GONE);
                                enableLocationButton.setVisibility(View.GONE);
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                               //cation(locationProvider);
                                getItems();

                            }


                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                //String radi;
                            }

                            public void onProviderEnabled(String provider) {

                                stopLocationAnimation();
                                noLocationAvailableTextView.setVisibility(View.GONE);
                                locationEnabled = true;
                                //longitude = location.getLongitude();
                                //latitude = location.getLatitude();
                                //cation(locationProvider);
                                //mLocationManager.requestLocationUpdates(provider, 0, 0, locationListener);
                              //  getItems();

                            }

                            public void onProviderDisabled(String provider) {

                                Toast.makeText(getActivity(), "الرجاء السماح لجايلك باستخدام خدمة المواقع خاصتك لعرض لك المنتجات من واجهة الخصائص!", Toast.LENGTH_LONG).show();
                                locationEnabled = false;
                            }
                        };


                        //Request location updates:
                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                        //getItems();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    noLocationAvailableTextView.setVisibility(View.VISIBLE);
                    enableLocationButton.setVisibility(View.VISIBLE);
                    playLocationAnimation();
                    stopLoadingAnimation();
                }

                return;
            }
        }
    }


    private void showLocationDisabled()
    {






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_browsing, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        mLocationManager = (LocationManager)  getContext().getSystemService(Context.LOCATION_SERVICE);

        noLocationAvailableTextView = (TextView) view.findViewById(R.id.noLocationAvailableTextView);
        noLocationAvailableTextView.setVisibility(View.GONE);
        locationAnimationView = (LottieAnimationView) view.findViewById(R.id.locationAnimationView);
        // Acquire a reference to the system Location Manager
       /// mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);


        enableLocationButton = (Button) view.findViewById(R.id.enableLocationButton);

        enableLocationButton.setVisibility(View.GONE);

        enableLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                checkLocationPermission();

            }

        });


// Define a listener that responds to location updates

// Register the listener with the Location Manager to receive location updates
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//        }




//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////             TODO: Consider calling
////                ActivityCompat#requestPermissions
////             here to request the missing permissions, and then overriding
//
////               public void onRequestPermissionsResult(int requestCode, String[] permissions,
////                                                      int[] grantResults)
////                {
////
////                 }
////             to handle the case where the user grants the permission. See the documentation
////             for ActivityCompat#requestPermissions for more details.
//            LocationManager lm = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
//            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
//

//        }
//
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View strings,
                                           int position, long id) {

                // long press do nothing we need just the short click my friend
                return true;
            }
        });

        BrowsingActivity.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0)
                {
                    onResume();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // here to detect if the user reached the end of the gridView.
        geoFireDatabaseRef = FirebaseDatabase.getInstance().getReference().child("items-location");
        finalItems = new ArrayList<String>();
        itemKeys = new ArrayList<String>();
        itemLocations = new ArrayList<GeoLocation>();


        geoFire = new GeoFire(geoFireDatabaseRef);
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        loadingAnimationView = (LottieAnimationView) view.findViewById(R.id.loadingAnimationView);
        noItemsAnimationView = (LottieAnimationView) view.findViewById(R.id.noItemsAnimationView);

        animationDescriptionTextView = (TextView) view.findViewById(R.id.animationDescriptionTextView);
        locationAnimationView.setVisibility(View.GONE);
        noItemsAnimationView.setVisibility(View.GONE);
        loadingAnimationView.setVisibility(View.GONE);
        animationDescriptionTextView.setVisibility(View.GONE);
        maximumRadius = 50;
        radius = 5;
        playLoadingAnimation();


       // gridView.setOnScrollListener(new AbsListView.OnScrollListener(){
           // @Override
          //  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
           // {
               // if(firstVisibleItem + visibleItemCount >= finalItems.size()){
                    // End has been reached
                  //  if (radius <= maximumRadius)
                  //  {
                  //       radius = radius + 10;
                  //       playLoadingAnimation();
                 //        itemKeys = new ArrayList<String>();
                //         getItems();
               //     }
             //   }
          //  }

          //  @Override
          //  public void onScrollStateChanged(AbsListView view, int scrollState){

         //   }
        //});

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                BrowsingItemActivity.itemId = finalItems.get(position);
                BrowsingItemActivity.currentLat = latitude;
                BrowsingItemActivity.currentLong = longitude;

                intentToItem();
            }});


        checkLocationPermission();

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        if (queryChanged == true)
        {
           // noItemsAnimationView.setVisibility(View.GONE);
            stopNoItemsAnimation();
            stopLocationAnimation();
            animationDescriptionTextView.setVisibility(View.GONE);
            gridView.setAdapter(new BrowsingImageAdapter(getActivity(), new ArrayList<String>(), displayMetrics.heightPixels, displayMetrics.widthPixels));
            playLoadingAnimation();
            initializeItems();

            if (BrowsingSettingsActivity.distanceSettingsIndex == 0)
            {
                maximumRadius = 1;
            }

            else if (BrowsingSettingsActivity.distanceSettingsIndex == 1)
            {
                maximumRadius = 5;
            }

            else if (BrowsingSettingsActivity.distanceSettingsIndex == 2)
            {
                maximumRadius = 10;
            }

            else if (BrowsingSettingsActivity.distanceSettingsIndex == 3)
            {
                maximumRadius = 50;
            }

             getItems();

        }

    }

    public void clearSearch()
    {
        stopNoItemsAnimation();
        animationDescriptionTextView.setVisibility(View.GONE);
        queryChanged = true;
        gridView.setAdapter(new BrowsingImageAdapter(getActivity(), new ArrayList<String>(), displayMetrics.heightPixels, displayMetrics.widthPixels));
        searchButtonClicked = false;
        playLoadingAnimation();
        initializeItems();
        getItems();
    }

    
    public void onSearchButtonClicked(String query)
    {
        noItemsAnimationView.setVisibility(View.GONE);
        noItemsAnimationView.pauseAnimation();
        animationDescriptionTextView.setVisibility(View.GONE);
        // make the gridView empty
        gridView.setAdapter(new BrowsingImageAdapter(getActivity(), new ArrayList<String>(), displayMetrics.heightPixels, displayMetrics.widthPixels));
        String[] words = query.split(" ");
        searchKeys = new ArrayList<String>();
        searchKeys.addAll(Arrays.asList(words));
        searchButtonClicked = true;
        BrowsingSettingsActivity.categoriesSettings =  new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        itemKeys = new ArrayList<String>();
        itemLocations = new ArrayList<GeoLocation>();
        initializeItems();
        playLoadingAnimation();
        getItems();

    }

    private void getItems()
    {
        // TODO add the current location
        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                itemKeys.add(key);
                itemLocations.add(location);

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                //TODO here this is the finale step
              //  gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));
              //  gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   // public void onItemClick(AdapterView<?> parent, View v,
                                          //  int position, long id) {
                        //BrowsingItemActivity.itemId = finalItems.get(position);
                        //intentToItem();
                 //   }
               // });

                if (itemKeys.size() == 0 && radius > maximumRadius)
                {
                    stopLoadingAnimation();
                    initializeNoItemAvailable();
                }

                else if((itemKeys.size() - currentItemsCount) < 12 && radius <= maximumRadius )
                {
                    radius = radius + 10;
                    itemKeys = new ArrayList<String>();
                    itemLocations = new ArrayList<GeoLocation>();
                    getItems();
                }

                else{

                    carCategoryArray = new ArrayList<String>();
                    phoneCategoryArray = new ArrayList<String>();
                    apartmentCategoryArray = new ArrayList<String>();
                    homeCategoryArray = new ArrayList<String>();
                    dogCategoryArray = new ArrayList<String>();
                    sportCategoryArray = new ArrayList<String>();
                    clothesCategoryArray = new ArrayList<String>();
                    kidsCategoryArray = new ArrayList<String>();
                    booksCategoryArray = new ArrayList<String>();
                    otherCategoryArray = new ArrayList<String>();
                    uncommonArray = new ArrayList<String>(CollectionUtils.subtract(itemKeys, finalItems));
                    fetchGeofireDataToDictionary();
                    geoQuery.removeAllListeners();
                }
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void initializeItems()
    {
        itemKeys = new ArrayList<String>();
        itemLocations = new ArrayList<GeoLocation>();
        finalItems = new ArrayList<String>();
        finalLocationItems = new ArrayList<GeoLocation>();
        radius = 5;
        currentItemsCount = 0;
        carCategoryArray = new ArrayList<String>();
        phoneCategoryArray = new ArrayList<String>();
        apartmentCategoryArray = new ArrayList<String>();
        homeCategoryArray = new ArrayList<String>();
        dogCategoryArray = new ArrayList<String>();
        sportCategoryArray = new ArrayList<String>();
        clothesCategoryArray = new ArrayList<String>();
        kidsCategoryArray = new ArrayList<String>();
        booksCategoryArray = new ArrayList<String>();
        otherCategoryArray = new ArrayList<String>();
    }

    private void fetchGeofireDataToDictionary()
    {
        itemsDictionary = new HashMap<String, Item>();
        favouritesDictionary = new HashMap<String, Integer>();
        pricesHighToLowDictionary = new HashMap<String, Double>();
        pricesLowToHighDictionary = new HashMap<String, Double>();
        timestampDictionary = new HashMap<String, Double>();

        for ( int j = 0; j <  uncommonArray.size(); j++)
        {
            final Item oneItem = new Item();
            // cause it should be finale in this case;
            final int i = j;

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot item : dataSnapshot.getChildren())
                    {
                        String childValue = item.getValue().toString();
                        String childKey = item.getKey();

                        if (childKey.matches("category"))
                        {
                            oneItem.category = childValue.toString();

                            if (childValue.matches( "category-car"))
                            {
                                carCategoryArray.add(uncommonArray.get(i));
                            }

                            else if (childValue.matches( "phone-category"))
                            {
                                phoneCategoryArray.add(uncommonArray.get(i));
                            }

                            else if (childValue.matches("category-aparment"))
                            {
                                apartmentCategoryArray.add(uncommonArray.get(i));

                            }

                            else if (childValue.matches("category-home"))
                            {
                                homeCategoryArray.add(uncommonArray.get(i));
                            }

                            else if (childValue.matches("category-dog"))
                            {
                                dogCategoryArray.add(uncommonArray.get(i));
                            }

                            else if (childValue.matches( "category-sport"))
                            {
                                sportCategoryArray.add(uncommonArray.get(i));
                            }

                            else if (childValue.matches( "category-clothes"))
                            {
                                clothesCategoryArray.add(uncommonArray.get(i));
                            }

                            else if (childValue.matches( "category-kids"))
                            {
                                kidsCategoryArray.add(uncommonArray.get(i));
                            }

                            else if (childValue.matches( "category-books"))
                            {
                                booksCategoryArray.add(uncommonArray.get(i));
                            }

                            else if (childValue.matches("category-others"))
                            {
                                otherCategoryArray.add(uncommonArray.get(i));
                            }

                            else
                            {
                                Log.w("Error in browsing", "category type entered does not exist" );
                            }
                        }


                        else if (childKey.matches("currency"))
                        {
                            oneItem.currency = childValue.toString();

                        }

                        else if (childKey.matches("description"))
                        {

                            oneItem.description = childValue.toString();
                        }

                        else if (childKey.matches("displayName"))
                        {
                            oneItem.displayName = childValue.toString();
                        }

                        else if (childKey.matches("imagesCount"))
                        {
                            oneItem.imagesCount =  Integer.parseInt(childValue.toString());
                        }

                        else if (childKey.matches("price"))
                        {
                            oneItem.price = childValue.toString();

                            if (childValue.toString().matches("غير محدد"))
                            {
                                    childValue = "0";
                            }

                            double price = Double.parseDouble(childValue);
                            //TODO price multiplication with the local currency to get the price sorting right.


                            pricesHighToLowDictionary.put(uncommonArray.get(i), price);
                            pricesLowToHighDictionary.put(uncommonArray.get(i), price);
                        }

                        else if (childKey.matches("timestamp")){

                            oneItem.timestamp = Double.parseDouble(childValue.toString());
                            Double timestamp = Double.parseDouble(childValue);
                            timestampDictionary.put(uncommonArray.get(i), timestamp);
                        }

                        else if (childKey.matches("title"))
                        {
                            oneItem.title = childValue.toString();
                        }

                        else if (childKey.matches("userId"))
                        {
                            oneItem.userId = childValue.toString();
                        }

                        else if (childKey.matches("favourites"))
                        {
                            int numberOfFavourites = Integer.parseInt(childValue);
                            favouritesDictionary.put(uncommonArray.get(i), numberOfFavourites);
                            oneItem.price = childValue.toString();
                        }

                        else{

                            Log.w("Error in browsing", "one of the keys entered is wrong" );
                        }

                        itemsDictionary.put(uncommonArray.get(i), oneItem);
                    }

                    if (i == uncommonArray.size() - 1)
                    {
                        if (BrowsingSettingsActivity.sortedSettingsIndex == 0)
                        {
                            favouritesQuery();
                        }

                        else if (BrowsingSettingsActivity.sortedSettingsIndex == 1)
                        {
                            pricesHighToLowQuery();
                        }

                        else if (BrowsingSettingsActivity.sortedSettingsIndex == 2)
                        {
                            pricesLowToHighQuery();
                        }

                        else
                        {
                            newestQuery();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText( getActivity(),databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

           FirebaseDatabase.getInstance().getReference().child("items").child(uncommonArray.get(i)).addListenerForSingleValueEvent(valueEventListener);
        }
    }


    private void favouritesQuery()
    {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("items");
        ArrayList<String> tempArray = new ArrayList<String>();
        Boolean checkIfAtLeastOneChecked = false;
        HashMap<String, Integer> categoryFavouritesDic = new HashMap<String, Integer>();

        if (BrowsingSettingsActivity.categoriesSettings[0] == 1)
        {
            tempArray.addAll(carCategoryArray);
            checkIfAtLeastOneChecked = true;

        }

        if (BrowsingSettingsActivity.categoriesSettings[1] == 1)
        {
            tempArray.addAll(phoneCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (BrowsingSettingsActivity.categoriesSettings[2] == 1)
        {
            tempArray.addAll(apartmentCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[3] == 1)
        {
            tempArray.addAll(homeCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[4] == 1)
        {
            tempArray.addAll(dogCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[5] == 1)
        {
            tempArray.addAll(sportCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[6] == 1)
        {
            tempArray.addAll(clothesCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[7] == 1)
        {
            tempArray.addAll(kidsCategoryArray);
            checkIfAtLeastOneChecked = true;
        }


        if(BrowsingSettingsActivity.categoriesSettings[8] == 1)
        {
            tempArray.addAll(booksCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[9] == 1)
        {
            tempArray.addAll(otherCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (tempArray.size() != 0)
        {
                for (String item : tempArray)
                {
                    categoryFavouritesDic.put(item, favouritesDictionary.get(item));

                }

            uncommonArray = tempArray;
            Collections.sort(uncommonArray, (a, b) -> categoryFavouritesDic.get(b).compareTo(categoryFavouritesDic.get(a)));
            finalItems.addAll(uncommonArray);
            currentItemsCount = finalItems.size();
        }

        else
        {
            if(checkIfAtLeastOneChecked == true)
            {
                if(radius>maximumRadius&&finalItems.size() == 0)
                {
                    finalItems = tempArray;
                    initializeNoItemAvailable();
                    stopLocationAnimation();
                    stopLoadingAnimation();
                    gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                      int position, long id) {
                    BrowsingItemActivity.itemId = finalItems.get(position);
                    BrowsingItemActivity.currentLat = latitude;
                    BrowsingItemActivity.currentLong = longitude;
                    intentToItem();

                     }});

                    BrowsingFragment.queryChanged = false;
                    currentItemsCount = 0;
                    return;
                }

                else
                {
                    currentItemsCount = finalItems.size();
                }
            }

            else
            {
                Collections.sort(uncommonArray, (a, b) -> favouritesDictionary.get(b).compareTo(favouritesDictionary.get(a)));
                finalItems.addAll(uncommonArray);
                currentItemsCount = finalItems.size();
            }
        }

        if(finalItems.size() < 12 && radius<= maximumRadius)
        {
            radius = radius + 10;
            itemKeys = new ArrayList<String>();
            itemLocations = new ArrayList<GeoLocation>();
            getItems();
        }

        else
        {
            if(searchButtonClicked == true)
            {
                itemsSearchQuery();
            }

            else
            {
                gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));


                if (queryChanged == true)
                {
                    gridView.setSelection(0);
                    queryChanged = false;
                }

                stopLoadingAnimation();
            }
        }
    }

    private void pricesHighToLowQuery()
    {
        ArrayList<String> tempArray = new ArrayList<String>();
        Boolean checkIfAtLeastOneChecked = false;
        HashMap<String, Double> categoryPricesDic = new HashMap<String, Double>();

        if (BrowsingSettingsActivity.categoriesSettings[0] == 1)
        {
            tempArray.addAll(carCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (BrowsingSettingsActivity.categoriesSettings[1] == 1)
        {
            tempArray.addAll(phoneCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (BrowsingSettingsActivity.categoriesSettings[2] == 1)
        {
            tempArray.addAll(apartmentCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[3] == 1)
        {
            tempArray.addAll(homeCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[4] == 1)
        {
            tempArray.addAll(dogCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[5] == 1)
        {
            tempArray.addAll(sportCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[6] == 1)
        {
            tempArray.addAll(clothesCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[7] == 1)
        {
            tempArray.addAll(kidsCategoryArray);
            checkIfAtLeastOneChecked = true;
        }


        if(BrowsingSettingsActivity.categoriesSettings[8] == 1)
        {
            tempArray.addAll(booksCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[9] == 1)
        {
            tempArray.addAll(otherCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (tempArray.size() != 0)
        {
            for (String item : tempArray)
            {
                categoryPricesDic.put(item,   pricesHighToLowDictionary.get(item));

            }

            uncommonArray = tempArray;
            Collections.sort(uncommonArray, (a, b) -> categoryPricesDic.get(b).compareTo( categoryPricesDic.get(a)));
            finalItems.addAll(uncommonArray);
            currentItemsCount = finalItems.size();
        }

        else
        {
            if(checkIfAtLeastOneChecked == true)
            {
                if(radius>maximumRadius&&finalItems.size() == 0)
                {
                    finalItems = tempArray;
                    initializeNoItemAvailable();
                    stopLocationAnimation();
                    stopLoadingAnimation();
                    gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            BrowsingItemActivity.itemId = finalItems.get(position);
                            BrowsingItemActivity.currentLat = latitude;
                            BrowsingItemActivity.currentLong = longitude;
                            intentToItem();
                        }});

                    BrowsingFragment.queryChanged = false;
                    currentItemsCount = 0;
                    return;
                }

                else
                {
                    currentItemsCount = finalItems.size();

                }
            }

            else
            {
                Collections.sort(uncommonArray, (a, b) -> pricesHighToLowDictionary.get(b).compareTo(pricesHighToLowDictionary.get(a)));
                finalItems.addAll(uncommonArray);
                currentItemsCount = finalItems.size();
            }
        }

        if(finalItems.size() < 12 && radius<= maximumRadius)
        {
            radius = radius + 10;
            itemKeys = new ArrayList<String>();
            itemLocations = new ArrayList<GeoLocation>();
            getItems();
        }

        else
        {
            if(searchButtonClicked == true)
            {
                itemsSearchQuery();
            }

            else
            {
                gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));

                if (queryChanged == true)
                {
                    gridView.setSelection(0);
                    queryChanged = false;
                }

                stopLoadingAnimation();
            }
        }
    }

    private void pricesLowToHighQuery()
    {

        ArrayList<String> tempArray = new ArrayList<String>();
        Boolean checkIfAtLeastOneChecked = false;
        HashMap<String, Double> categoryPricesDic = new HashMap<String, Double>();

        if (BrowsingSettingsActivity.categoriesSettings[0] == 1)
        {
            tempArray.addAll(carCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (BrowsingSettingsActivity.categoriesSettings[1] == 1)
        {
            tempArray.addAll(phoneCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (BrowsingSettingsActivity.categoriesSettings[2] == 1)
        {
            tempArray.addAll(apartmentCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[3] == 1)
        {
            tempArray.addAll(homeCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[4] == 1)
        {
            tempArray.addAll(dogCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[5] == 1)
        {
            tempArray.addAll(sportCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[6] == 1)
        {
            tempArray.addAll(clothesCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[7] == 1)
        {
            tempArray.addAll(kidsCategoryArray);
            checkIfAtLeastOneChecked = true;
        }


        if(BrowsingSettingsActivity.categoriesSettings[8] == 1)
        {
            tempArray.addAll(booksCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[9] == 1)
        {
            tempArray.addAll(otherCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (tempArray.size() != 0)
        {
            for (String item : tempArray)
            {
                categoryPricesDic.put(item,   pricesLowToHighDictionary.get(item));
            }

            uncommonArray = tempArray;
            Collections.sort(uncommonArray, (a, b) -> categoryPricesDic.get(a).compareTo( categoryPricesDic.get(b)));
            finalItems.addAll(uncommonArray);
            currentItemsCount = finalItems.size();
        }

        else
        {
            if(checkIfAtLeastOneChecked == true)
            {
                if(radius>maximumRadius&&finalItems.size() == 0)
                {
                    finalItems = tempArray;
                    initializeNoItemAvailable();
                    stopLocationAnimation();
                    stopLoadingAnimation();
                    gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            BrowsingItemActivity.itemId = finalItems.get(position);
                            BrowsingItemActivity.currentLat = latitude;
                            BrowsingItemActivity.currentLong = longitude;
                            intentToItem();
                        }});

                    BrowsingFragment.queryChanged = false;
                    currentItemsCount = 0;
                    return;
                }

                else
                {
                    currentItemsCount = finalItems.size();
                }
            }

            else
            {
                Collections.sort(uncommonArray, (a, b) -> pricesLowToHighDictionary.get(a).compareTo(pricesLowToHighDictionary.get(b)));
                finalItems.addAll(uncommonArray);
                currentItemsCount = finalItems.size();
            }
        }

        if(finalItems.size() < 12 && radius<= maximumRadius)
        {
            radius = radius + 10;
            itemKeys = new ArrayList<String>();
            itemLocations = new ArrayList<GeoLocation>();
            getItems();
        }

        else
        {
            if(searchButtonClicked == true)
            {
                itemsSearchQuery();
            }


            else
            {
                gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));


                if (queryChanged == true)
                {
                    gridView.setSelection(0);
                    queryChanged = false;
                }

                stopLoadingAnimation();
            }
        }
    }

    private void newestQuery()
    {
        ArrayList<String> tempArray = new ArrayList<String>();
        Boolean checkIfAtLeastOneChecked = false;
        HashMap<String, Double> categoryTimeStampDic = new HashMap<String, Double>();

        if (BrowsingSettingsActivity.categoriesSettings[0] == 1)
        {
            tempArray.addAll(carCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (BrowsingSettingsActivity.categoriesSettings[1] == 1)
        {
            tempArray.addAll(phoneCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (BrowsingSettingsActivity.categoriesSettings[2] == 1)
        {
            tempArray.addAll(apartmentCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[3] == 1)
        {
            tempArray.addAll(homeCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[4] == 1)
        {
            tempArray.addAll(dogCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[5] == 1)
        {
            tempArray.addAll(sportCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[6] == 1)
        {
            tempArray.addAll(clothesCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[7] == 1)
        {
            tempArray.addAll(kidsCategoryArray);
            checkIfAtLeastOneChecked = true;
        }


        if(BrowsingSettingsActivity.categoriesSettings[8] == 1)
        {
            tempArray.addAll(booksCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if(BrowsingSettingsActivity.categoriesSettings[9] == 1)
        {
            tempArray.addAll(otherCategoryArray);
            checkIfAtLeastOneChecked = true;
        }

        if (tempArray.size() != 0)
        {
            for (String item : tempArray)
            {
                categoryTimeStampDic.put(item,   timestampDictionary.get(item));

            }

            uncommonArray = tempArray;
            Collections.sort(uncommonArray, (a, b) -> categoryTimeStampDic.get(b).compareTo( categoryTimeStampDic.get(a)));
            finalItems.addAll(uncommonArray);
            currentItemsCount = finalItems.size();
        }

        else
        {
            if(checkIfAtLeastOneChecked == true)
            {
                if(radius>maximumRadius&&finalItems.size() == 0)
                {
                    finalItems = tempArray;
                    initializeNoItemAvailable();
                    stopLocationAnimation();
                    stopLoadingAnimation();
                    gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));

                    BrowsingFragment.queryChanged = false;
                    currentItemsCount = 0;
                    return;
                }

                else
                {
                    currentItemsCount = finalItems.size();

                }
            }

            else
            {
                Collections.sort(uncommonArray, (a, b) -> timestampDictionary.get(b).compareTo(timestampDictionary.get(a)));
                finalItems.addAll(uncommonArray);
                currentItemsCount = finalItems.size();
            }
        }

        if(finalItems.size() < 12 && radius<= maximumRadius)
        {
            radius = radius + 10;
            itemKeys = new ArrayList<String>();
            itemLocations = new ArrayList<GeoLocation>();
            getItems();
        }


        else
        {
            if(searchButtonClicked == true)
            {
                itemsSearchQuery();
            }

            else
            {
                gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        BrowsingItemActivity.itemId = finalItems.get(position);
                        BrowsingItemActivity.currentLat = latitude;
                        BrowsingItemActivity.currentLong = longitude;
                        intentToItem();
                    }});

                if (queryChanged == true)
                {
                    gridView.setSelection(0);
                    queryChanged = false;
                }

                stopLoadingAnimation();
            }
        }
    }

    private void itemsSearchQuery()
    {
        fetchedKeys = new ArrayList<String>();
        DatabaseReference keysRef = FirebaseDatabase.getInstance().getReference().child("tags");
        int c = 0;

        for ( String key : searchKeys)
        {
            final int counter = c;
            ArrayList<String> keys = new ArrayList<String>();

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot item : dataSnapshot.getChildren())
                    {
                        String childKey = item.getKey();
                        keys.add(childKey);
                    }

                    if(counter == 0)
                    {
                        fetchedKeys = keys;
                    }

                    else
                    {
                        fetchedKeys.retainAll(keys);
                    }

                    if(counter == searchKeys.size() - 1)
                    {
                        finalItems.retainAll(fetchedKeys);

                        if(finalItems.size() < 12)
                        {
                            if(finalItems.size() < 12)
                            {
                                if(radius < maximumRadius)
                                {
                                    radius = radius + 10;
                                    itemKeys = new ArrayList<String>();
                                    itemLocations = new ArrayList<GeoLocation>();
                                    currentItemsCount = finalItems.size();
                                    getItems();
                                }
                                else{

                                    if(finalItems.size() == 0)
                                    {
                                        currentItemsCount = 0;
                                        initializeNoItemAvailable();
                                        stopLoadingAnimation();
                                        gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));
                                        queryChanged = false;
                                        //searchButtonClicked = false;
                                    }

                                    else
                                    {
                                        currentItemsCount = finalItems.size();
                                        gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));
                                        if (queryChanged == true)
                                        {
                                            gridView.setSelection(0);
                                            queryChanged = false;
                                        }

                                        stopLoadingAnimation();
                                       // searchButtonClicked = false;
                                    }
                                }
                            }

                            else
                            {
                                currentItemsCount = finalItems.size();
                                gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));

                                if (queryChanged == true)
                                {
                                    gridView.setSelection(0);
                                    queryChanged = false;
                                }

                                stopLoadingAnimation();
                                //searchButtonClicked = false;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

            keysRef.child(key).addListenerForSingleValueEvent(valueEventListener);
            c = c + 1;
        }
    }

    private void initializeNoItemAvailable(){

        // should do something related to the listview here
        animationDescriptionTextView.setVisibility(View.VISIBLE);
        playNoItemsAnimation();

    }

    private  void intentToItem()
    {
        Intent intent  = new Intent(getActivity(), BrowsingItemActivity.class);
        startActivity(intent);
    }

    private void playLoadingAnimation()
    {
        loadingAnimationView.playAnimation();
        loadingAnimationView.loop(true);
        loadingAnimationView.setVisibility(View.VISIBLE);
    }

    private void stopLoadingAnimation()
    {
        // If something goes wrong.
        loadingAnimationView.cancelAnimation();
        loadingAnimationView.setVisibility(View.GONE);
    }

    private  void playNoItemsAnimation()
    {
        noItemsAnimationView.playAnimation();
        noItemsAnimationView.loop(true);
        noItemsAnimationView.setVisibility(View.VISIBLE);
    }

    private void stopNoItemsAnimation()
    {
        noItemsAnimationView.cancelAnimation();
        noItemsAnimationView.setVisibility(View.GONE);
    }

    private void playLocationAnimation()
    {
        locationAnimationView.playAnimation();
        locationAnimationView.loop(true);
        locationAnimationView.setVisibility(View.VISIBLE);
    }



    private void stopLocationAnimation()
    {
       locationAnimationView.cancelAnimation();
        locationAnimationView.setVisibility(View.GONE);
    }


    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("تفعيل خدمة المواقع")
                        .setMessage("نود استخدام خدمة المواقع خاصتك حتي نعرض لك المنتجات الاقرب لك!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {

                // No explanation needed, we can request the permission.
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        }

        else {

            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(locationProvider);

            if (lastKnownLocation == null)
            {
                noLocationAvailableTextView.setVisibility(View.VISIBLE);
                playLocationAnimation();
                stopLoadingAnimation();
            }

            else {

                stopLocationAnimation();
                noLocationAvailableTextView.setVisibility(View.GONE);
                enableLocationButton.setVisibility(View.GONE);
                longitude = lastKnownLocation.getLongitude();
                locationEnabled = true;
                latitude = lastKnownLocation.getLatitude();
                getItems();
            }

            return true;
        }
    }


}
