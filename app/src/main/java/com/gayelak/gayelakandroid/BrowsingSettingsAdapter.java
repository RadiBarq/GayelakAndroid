package com.gayelak.gayelakandroid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import org.w3c.dom.Text;


public class BrowsingSettingsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    BrowsingSettingsAdapter(Context context)
    {

        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return settingItems.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        view = mInflater.inflate(R.layout.browsing_settings_layout, parent, false);
        // these are the headers my friend.
        if (position == 0 || position == 11 || position == 16 || position == 21)
        {
            view.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 200));
            TextView textView = view.findViewById(R.id.value);
            textView.setTextColor(view.getResources().getColor(R.color.colorPrimary));
            textView.setTextSize(20);
            textView.setText(settingItems[position]);
            ImageView imageView = view.findViewById(R.id.image);
            imageView.setVisibility(View.GONE);
        }
        // these are the normal items.
        else
        {
            view.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 150));
            ImageView imageView = view.findViewById(R.id.image);
            imageView.setVisibility(View.GONE);

            // Categories
            if (position <= 10 && position >= 1)
            {
               // if (BrowsingSettingsActivity.categoriesSettings[])

                if (BrowsingSettingsActivity.categoriesSettings[position - 1] == 1)
                {

                    imageView.setVisibility(View.VISIBLE);

                }

            }

            else if (position >= 12 && position <= 15)
            {

                if (position - 12 == BrowsingSettingsActivity.destanceSettignsIndex)
                {
                    imageView.setVisibility(View.VISIBLE);
                }

            }

            else
            {
                if (position - 17 == BrowsingSettingsActivity.sortedSeettigsIndex)
                {
                    imageView.setVisibility(View.VISIBLE);
                }

            }

            TextView textView = view.findViewById(R.id.value);
            textView.setTextSize(18);
            textView.setText(settingItems[position]);

        }

         return view;
    }


    private String settingItems[] ={

            "التصنيفات",
            "سيارات",
            "الكترونيات",
            "شقق و اراضي",
            "البيت و الحديقة",
            "حيوانات",
            "الرياضة و الالعاب",
            "ملابس و اكسسوارات",
            "الاطفال",
            "افلام، كتب و اغاني",
            "اغراض اخرى",
            "المسافة",
            "قريب جدا(اكم)",
            "في الاحياء القريبة(٥كم)",
            "في مدينتي(١٠كم)",
            "ابعد من ذلك(فوق ١٠كم)",
            "الترتيب حسب",
            "الاعجابات",
            "السعر من الاعلى الى الاقل",
            "السعر من الاقل الى الاعلى",
            "الاجدد اولا"
    };
}