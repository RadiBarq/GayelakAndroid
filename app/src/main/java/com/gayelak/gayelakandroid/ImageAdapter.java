package com.gayelak.gayelakandroid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by radibarq on 1/21/18.
 */

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    public ImageAdapter(Context c)
    {
        mContext = c;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            //maybe you should do this calculation not exactly in this method but put is somewhere else.
            Resources r = Resources.getSystem();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 165, r.getDisplayMetrics());


            imageView = new ImageView(mContext);

            imageView.setLayoutParams(new GridView.LayoutParams((int) px , (int) px));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // imageView.setPadding(0, 20, 0, 20);

        } else {

            imageView = (ImageView) convertView;
        }

        Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources() ,mThumbIds[position]);
        Bitmap circularBitmap1 = ImageConverter.getRoundedCornerBitmap(bitmap1, 15);
        imageView.setImageBitmap(circularBitmap1);
        return imageView;
    }


    private Integer[] mThumbIds = {

            R.drawable.nike_shoes_2, R.drawable.nike_shoes_2,
            R.drawable.nike_shoes_2, R.drawable.nike_shoes_2,
            R.drawable.nike_shoes_2,  R.drawable.nike_shoes_2,
            R.drawable.nike_shoes_2,  R.drawable.nike_shoes_2,
            R.drawable.nike_shoes_2,  R.drawable.nike_shoes_2,
            R.drawable.nike_shoes_2,  R.drawable.nike_shoes_2,
            R.drawable.nike_shoes_2,  R.drawable.nike_shoes_2,
            R.drawable.nike_shoes_2, R.drawable.nike_shoes_2,
            R.drawable.nike_shoes_2,  R.drawable.nike_shoes_2,
            R.drawable.nike_shoes_2,  R.drawable.nike_shoes_2,
            R.drawable.nike_shoes_2,  R.drawable.nike_shoes_2
    };

}
