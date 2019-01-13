package com.gayelak.gayelakandroid;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;


public class CategoryFragment extends Fragment {


    DisplayMetrics displayMetrics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_category, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gridView.setAdapter(new CategoriesGridViewAdapter(getActivity(), displayMetrics.heightPixels, displayMetrics.widthPixels));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                onClickItem(position);
            }
        });

        return view;
    }

    private void onClickItem(Integer position)
    {

        BrowsingSettingsActivity.categoriesSettings = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        BrowsingSettingsActivity.categoriesSettings[position] = 1;
        BrowsingFragment.queryChanged = true;
        BrowsingActivity.viewPager.setCurrentItem(0);
    }

}
