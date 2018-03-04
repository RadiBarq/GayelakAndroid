package com.gayelak.gayelakandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by radibarq on 1/23/18.
 */

public class ItemViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    StorageReference imagesRef;
    int imagesCount;
    String itemId;
    public static ArrayList<Uri> imagesUri;
    public static int selectedImageIndex;

    public ItemViewPagerAdapter(Context context, int imagesCount, String itemId, ArrayList<Uri> imagesUri)
    {
        this.context = context;
        this.imagesCount = imagesCount;
        this.itemId = itemId;
      //  imagesRef = FirebaseStorage.getInstance().getReference().child("Items_Photos").child(itemId);
        this.imagesUri = imagesUri;
        Fresco.initialize(context);

    }
    @Override
    public int getCount() {

        return imagesCount;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {

        //layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PhotoDraweeView photoDraweeView = new PhotoDraweeView(container.getContext());
        final PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        photoDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
                controller.setUri(imagesUri.get(position));
                controller.setOldController(photoDraweeView.getController());
                controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo == null) {
                            return;
                        }

                        photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                    }
                });

                photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
                    @Override public void onPhotoTap(View view, float x, float y) {

                        selectedImageIndex = position;
                        Intent intent = new Intent(context, BrowsingItemFullImageActivity.class);
                        context.startActivity(intent);
                    }
                });


                photoDraweeView.setController(controller.build());

                try {
                    container.addView(photoDraweeView, ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);


                } catch (Exception e) {
                    e.printStackTrace();
                }

        return photoDraweeView;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
         ViewPager viewPager = (ViewPager) container;
         View view = (View) object;
         viewPager.removeView(view);
    }
}
