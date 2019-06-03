package com.lvbing.mysplashview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.githun.lvbing.mysplashview.Advert;
import com.githun.lvbing.mysplashview.ContextUtils;
import com.githun.lvbing.mysplashview.Glideloader;
import com.githun.lvbing.mysplashview.SplashView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void show(View view) {
        List<Advert> advertList = new ArrayList<>();
        Advert advert = new Advert();
        advert.setDuration(5);
        advert.setImageurl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2746975318,973909212&fm=26&gp=0.jpg");
        Advert advert1 = new Advert();
        advert1.setDuration(4);
        advert1.setImageurl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1226171028,3121416593&fm=26&gp=0.jpg");

        advertList.add(advert1);
        advertList.add(advert);
        SplashView.showSplashView(MainActivity.this, advertList, new SplashView.OnSplashViewActionListener() {
            @Override
            public void onSplashImageClick(String actionUrl, SplashView splashView) {

            }
            //initiativeDismiss表示是否手动点击跳过
            @Override
            public void onSplashViewDismiss(boolean initiativeDismiss) {

            }
        }, new GlideImageLoader());
    }

    public class GlideImageLoader extends Glideloader {
        //加载图片
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            int screenWidth = ContextUtils.getSreenWidth(context);
            int screenHeight = ContextUtils.getSreenHeight(context);
            ImageLoader.loadImageForWH(context,imageView,path.toString(),screenWidth,screenHeight);
        }
        //预加载下一张图片
        @Override
        public void preDisplayImage(Context context, Object path) {
            int screenWidth = ContextUtils.getSreenWidth(context);
            int screenHeight = ContextUtils.getSreenHeight(context);
            ImageLoader.preLoadImage(context,path.toString(),screenWidth,screenHeight);
        }
    }
}
