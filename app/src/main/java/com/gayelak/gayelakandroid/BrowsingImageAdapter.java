package com.gayelak.gayelakandroid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

class BrowsingImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> itemsKeys;
    private LayoutInflater mInflater;
    double screenWidth;
    double screenHeight;

    public BrowsingImageAdapter(Context c, ArrayList<String> itemsKeys, double screenHeight, double screenWidth)
    {
        this.itemsKeys = itemsKeys;
        mContext = c;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

    }

    @Override
    public int getCount() {

        return itemsKeys.size();
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

        View view;
        view = mInflater.inflate(R.layout.layout_browsing_image_adapter,parent,false);
        view.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT,  (int) (screenHeight/4 + 40)));

        ImageView imageView = view.findViewById(R.id.imageView);
        StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference().child("Items_Photos").child(itemsKeys.get(position)).child("1.jpeg");
        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(imageStorageRef).animate(android.R.anim.fade_in).thumbnail(Glide.with(mContext).load(R.drawable.spinner_gif)).crossFade()
                .into(imageView);
        return  view;
    }

}
