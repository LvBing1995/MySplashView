package com.githun.lvbing.mysplashview;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

public interface ImageLoaderInterface<T extends View> extends Serializable {

    void displayImage(Context context, Object path, T imageView);
    void preDisplayImage(Context context, Object path);
    T createImageView(Context context);
}