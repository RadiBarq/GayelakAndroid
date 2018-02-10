package com.gayelak.gayelakandroid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.io.IOException;

import static com.gayelak.gayelakandroid.MakePhotoActivity.REQUEST_IMAGE_CAPTURE;

/**
 * Created by radibarq on 1/31/18.
 */

public class ViewPagerPostItemAdapter extends PagerAdapter {

    Context mContext;
    final int GALLERY_REQUEST = 1;
    final int CAMERA_REQUEST = 0;

    public ViewPagerPostItemAdapter(Context context)
    {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.post_item_view_pager_layout, null);
        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        if (PostItemActivity.postedItemImages[position] == null)
        {
            //@drawable/ic_add_a_photo_white_36dp
            imageView.setImageResource(R.drawable.ic_add_a_photo_white_36dp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        else
        {
            imageView.setImageBitmap(PostItemActivity.postedItemImages[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        view.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //this will log the page number that was click
               // Log.i("TAG", "This page was clicked: " + position);
                CharSequence items[];

                if (PostItemActivity.postedItemImages[position] != null)
                {
                    items = new CharSequence[] {"الكاميرة", "مكتبة الصور", "حذف الصورة"};
                }

                else
                {
                    items = new CharSequence[] {"الكاميرة", "مكتبة الصور"};

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("اختيار الصورة بواسطة")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                PostItemActivity.clickedItem = position;

                                if (which == 0)
                                {
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    ((Activity) mContext).startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                                }

                                 else if (which == 1)
                                {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setType("image/*");
                                    ((Activity) mContext).startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                                }

                                else if (which == 2)
                                {
                                    PostItemActivity.postedItemImages[position]= null;
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                    imageView.setImageResource(R.drawable.ic_add_a_photo_white_36dp);
                                }
                            }
                        });

                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public float getPageWidth(int position) {
        return 0.25f;
    }
}
