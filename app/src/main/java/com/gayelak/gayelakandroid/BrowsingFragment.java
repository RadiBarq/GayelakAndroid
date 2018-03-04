package com.gayelak.gayelakandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class BrowsingFragment extends Fragment {

    GridView gridView;
    ArrayList<String> finalItems;
    DatabaseReference geoFireDatabaseRef;
    GeoFire geoFire;
    DisplayMetrics displayMetrics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_selling, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        geoFireDatabaseRef = FirebaseDatabase.getInstance().getReference().child("items-location");
        geoFire = new GeoFire(geoFireDatabaseRef);
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getItems();
        return view;

    }

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

                gridView.setAdapter(new BrowsingImageAdapter(getActivity(), finalItems, displayMetrics.heightPixels, displayMetrics.widthPixels));
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

    private  void intentToItem()
    {
        Intent intent  = new Intent(getActivity(), BrowsingItemActivity.class);
        startActivity(intent);
    }


}
