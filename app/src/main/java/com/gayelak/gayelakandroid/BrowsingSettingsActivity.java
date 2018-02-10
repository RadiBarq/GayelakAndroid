package com.gayelak.gayelakandroid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class BrowsingSettingsActivity extends AppCompatActivity {

    ListView listView;
    // selected items static...
    public static int[] categoriesSettings = {1, 0, 0, 0, 0, 0, 0, 0, 0, 1};
    public static int destanceSettignsIndex = 3;
    public static int sortedSeettigsIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing_settings);
        listView = (ListView) findViewById(R.id.listVIew);
        final BrowsingSettingsAdapter browsingSettingsAdapter = new BrowsingSettingsAdapter(this);
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

                        BrowsingSettingsActivity.destanceSettignsIndex = position - 12;

                    } else {

                        BrowsingSettingsActivity.sortedSeettigsIndex = position - 17;
                    }

                    browsingSettingsAdapter.notifyDataSetChanged();
                }
            }

        });

    }

}
