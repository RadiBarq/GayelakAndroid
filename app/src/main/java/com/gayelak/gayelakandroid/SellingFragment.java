package com.gayelak.gayelakandroid;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


public class SellingFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_selling, container, false);

        GridView gridview = (GridView) view.findViewById(R.id.sellingGridView);
        gridview.setAdapter(new SellingImageAdapter(view.getContext()));
        return view;
    }



}
