package com.gayelak.gayelakandroid;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.image.ImageInfo;

import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by radibarq on 1/23/18.
 */

public class ItemViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;


    public ItemViewPagerAdapter(Context context)
    {
        this.context = context;
        Fresco.initialize(context);


    }

    @Override
    public int getCount() {

        return images.length;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view == object;

    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        //layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final PhotoDraweeView photoDraweeView = new PhotoDraweeView(container.getContext());
        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        photoDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
        controller.setUri(Uri.parse("res:///" + images[position]));


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
        photoDraweeView.setController(controller.build());

        try {
            container.addView(photoDraweeView, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);




        } catch (Exception e) {
            e.printStackTrace();
        }


        return photoDraweeView;


    }

    private Integer[] images = {

            R.drawable.nike_shoes,
            R.drawable.nike_shoes_2,
            R.drawable.nike_shoes

    };

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
         ViewPager viewPager = (ViewPager) container;
         View view = (View) object;
         viewPager.removeView(view);
    }
}
