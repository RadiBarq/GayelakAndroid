package com.gayelak.gayelakandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by radibarq on 1/23/18.
 */

public class CategoriesGridViewAdapter extends BaseAdapter {

    private Context mContext;

    private  LayoutInflater mInflater;


    public CategoriesGridViewAdapter(Context c)
    {
        mContext = c;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return imagesArray.length;
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
        view = mInflater.inflate(R.layout.browsing_category_layout, parent, false);
        view.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 600));
        TextView textView = (TextView) view.findViewById(R.id.text);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        textView.setText(textArray[position]);
        imageView.setImageResource(imagesArray[position]);
        return view;
    }

    private  Integer[] imagesArray = {

            R.drawable.category_car,
            R.drawable.phone_category,
            R.drawable.category_aparment,
            R.drawable.category_home,
            R.drawable.category_dog,
            R.drawable.category_sport,
            R.drawable.category_clothes,
            R.drawable.category_kids,
            R.drawable.category_books,
            R.drawable.category_others
    };

    private  String[] textArray = {

            "سيارات", "الكترونيات", "شقق و اراضي", "البيت و الحديقة", "حيوانات", "الرياضة و الالعاب", "ملابس و اكسسوارات",
            "الاطفال", "افلام، كتب و اغاني", "اغراض اخرى"

    };
}
