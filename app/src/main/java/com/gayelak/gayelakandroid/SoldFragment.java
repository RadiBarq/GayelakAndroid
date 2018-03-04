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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SoldFragment extends Fragment {

    ArrayList<String> items;
    DisplayMetrics displayMetrics;
    GridView gridview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_sold, container, false);
        gridview = (GridView) view.findViewById(R.id.soldGridView);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemKey = items.get(position);
                BrowsingItemActivity.itemId = itemKey;
                BrowsingItemActivity.cameFromProfile = true;
                Intent itemIntent = new Intent(getActivity(), BrowsingItemActivity.class);
                startActivity(itemIntent);
            }
        });

        displayMetrics = new DisplayMetrics();
        items = new ArrayList<String>();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getItems(view);
        return view;

    }

    private void getItems(final View view)
    {
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot item: dataSnapshot.getChildren())
                {
                    items.add(item.getKey());
                }

                gridview.setAdapter(new SoldImageAdapter(view.getContext(),items, displayMetrics.heightPixels, displayMetrics.widthPixels));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(view.getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("sold-items").addListenerForSingleValueEvent(valueEventListener);
    }
}
