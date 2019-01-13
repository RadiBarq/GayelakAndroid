package com.gayelak.gayelakandroid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import java.util.Random;

class BrowsingImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> itemsKeys;
    private LayoutInflater mInflater;
    double screenWidth;
    double screenHeight;

    // these are 12 colors
    String randomColors[] = {"#8C9EFF", "#536DFE", "#3D5AFE", "#304FFE", "#7C4DFF", "#651FFF", "#FF4081", "#F50057", "#29B6F6", "#03A9F4", "#69F0AE", "#00E676"};
    public BrowsingImageAdapter(Context c, ArrayList<String> itemsKeys, double screenHeight, double screenWidth)
    {
        mContext = c;
        this.itemsKeys = itemsKeys;
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
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        view = mInflater.inflate(R.layout.layout_browsing_image_adapter,parent,false);
        view.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT,  (int) (screenHeight/3)));
        ImageView imageView = view.findViewById(R.id.imageView);
      //  Random rand = new Random();
      //  int  n = rand.nextInt(12);
      //  String randomColor = randomColors[n];
        imageView.setBackgroundColor(Color.parseColor("#eef0f5"));
        StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference().child("Items_Photos").child(itemsKeys.get(position)).child("1.jpeg");
        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(imageStorageRef)
                .into(imageView);
        return  view;
    }
}
