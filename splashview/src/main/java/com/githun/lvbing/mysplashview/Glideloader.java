package com.githun.lvbing.mysplashview;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by lvbingisdad
 * on 2019/6/3
 */
public abstract class Glideloader implements ImageLoaderInterface<ImageView>{

    @Override
    public ImageView createImageView(Context context) {
        ImageView imageView = new ImageView(context);
        return imageView;
    }


}
