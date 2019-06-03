package com.lvbing.mysplashview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;

public class ImageLoader {

    /**
     * 加载不同的图片设置宽高
     * @param context
     * @param imageView
     * @param url
     * @param <V>
     */
    public static void loadImageForWH(Context context, ImageView imageView, String url,int width,int height){
        RequestOptions requestOptions=new RequestOptions()
                .override(width,height)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop();
        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .transition(new DrawableTransitionOptions().crossFade(200))
                .into(imageView);
    }
    /**
     * 图片预加载
     */
    public static void preLoadImage(Context context,String url,int width,int height){
        RequestOptions requestOptions=new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop();
        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .preload(width,height);
    }



}