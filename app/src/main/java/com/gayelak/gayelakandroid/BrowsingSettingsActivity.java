package com.gayelak.gayelakandroid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class BrowsingSettingsActivity extends AppCompatActivity {

    ListView listView;
    // selected items static...
    public static int[] categoriesSettings = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static int distanceSettingsIndex = 3;
    public static int sortedSettingsIndex = 0;
    int[] copyCategoriesSettings = {};
    int copyDistanceSettingsIndex;
    int copySortedSettingsIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing_settings);
        listView = (ListView) findViewById(R.id.listVIew);
        final BrowsingSettingsAdapter browsingSettingsAdapter = new BrowsingSettingsAdapter(this);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }


        listView.setAdapter(browsingSettingsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // This is the position of the headers.
                if (!(position == 0 || position == 11 || position == 16 || position == 21))
                {

                    if (position >= 1 && position <= 10) {

                        if (BrowsingSettingsActivity.categoriesSettings[position - 1] == 1)
                        {
                            BrowsingSettingsActivity.categoriesSettings[position - 1] = 0;
                        }

                        else
                        {
                            BrowsingSettingsActivity.categoriesSettings[position - 1] = 1;

                        }

                    } else if (position >= 12 && position <= 15) {

                        BrowsingSettingsActivity.distanceSettingsIndex = position - 12;

                    } else {

                        BrowsingSettingsActivity.sortedSettingsIndex= position - 17;
                    }

                    browsingSettingsAdapter.notifyDataSetChanged();
                }
            }
        });

        copyCategoriesSettings = Arrays.copyOf(categoriesSettings, categoriesSettings.length);
        copyDistanceSettingsIndex = distanceSettingsIndex;
        copySortedSettingsIndex = sortedSettingsIndex;
    }
    @Override
    protected void onPause() {

        super.onPause();
        if (copyDistanceSettingsIndex != distanceSettingsIndex || copySortedSettingsIndex != sortedSettingsIndex ||   !Arrays.equals(copyCategoriesSettings,categoriesSettings))
        {
           BrowsingFragment.queryChanged = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_browsing_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        else
        {
            sortedSettingsIndex = 0;
            distanceSettingsIndex = 0;
            categoriesSettings = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            BrowsingFragment.queryChanged = true;
            finish();
            overridePendingTransition( 0, 0);
            startActivity(getIntent());
            overridePendingTransition( 0, 0);


        }

        return super.onOptionsItemSelected(item);
    }
}
