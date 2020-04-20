package com.githun.lvbing.mysplashview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SplashView extends FrameLayout {

    View splashImageView;
    TextView skipButton;

    private static final String IMG_URL = "splash_img_url";
    private static final String ACT_URL = "splash_act_url";
    private static String IMG_PATH = null;
    private static final String SP_NAME = "splash";
    private static final int skipButtonSizeInDip = 50;
    private static final int skipButtonMarginInDip = 16;
    private Integer duration = 6;
    private static final int delayTime = 1000;   // 每隔1000 毫秒执行一次

    private List<Advert> advertList = new ArrayList<>();
    private int currentPosition = 0;

    private ImageLoaderInterface mImageLoader;
    private boolean isActionBarShowing = true;
    private int previousSysytemUi = SYSTEM_UI_FLAG_FULLSCREEN;
    private int previousStatusColor;
    private WeakReference<Activity> weakReference = null;
    private OnSplashViewActionListener mOnSplashViewActionListener = null;

    private Handler handler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (duration >= 3600) {handler.removeCallbacks(timerRunnable); return;}
            if (weakReference.get() == null || weakReference.get().isDestroyed()){dismissSplashView(false); return;}
            handleDuration();
            if (!(0 == duration && currentPosition == advertList.size() - 1)){
                handler.postDelayed(timerRunnable, delayTime);
            }else {
                dismissSplashView(false);
            }
        }

        private void handleDuration() {
            if (0 == duration && currentPosition == advertList.size() - 1) {
                dismissSplashView(false);
            } else if (duration == 0){//一张图片结束进入下一张
                currentPosition++;
                skipButton.setVisibility(advertList.get(currentPosition).getIs_superscript() == 1 ? VISIBLE : GONE);
                int nextDuration = advertList.get(currentPosition).getDuration();
                if (nextDuration <= 0){ handleDuration(); return;}
                setDuration(nextDuration);
                setImage(advertList.get(currentPosition).getImageurl() == null ? "" : advertList.get(currentPosition).getImageurl());
            }else {
                setDuration(--duration);
            }
        }
    };

    private void setImage(String imageUrl) {
        int screenWidth = ContextUtils.getSreenWidth(weakReference.get());
        int screenHeight = ContextUtils.getSreenHeight(weakReference.get());
        try {
            if (weakReference.get() != null && !weakReference.get().isDestroyed()){
                mImageLoader.displayImage(weakReference.get(),imageUrl,splashImageView);
                if (currentPosition < advertList.size() - 1)
                    mImageLoader.preDisplayImage(weakReference.get(),advertList.get(currentPosition+1).getImageurl());
            }
        }catch (Exception e){
            Log.i("Splashview","e="+e.getMessage());
        }


    }

    public SplashView(Activity context,List<Advert> adverts,ImageLoaderInterface imageLoaderInterface) {
        super(context);
        weakReference = new WeakReference<>(context);
        advertList = adverts;
        mImageLoader = imageLoaderInterface;
        initComponents();
    }

    private GradientDrawable splashSkipButtonBg = new GradientDrawable();
    void initComponents() {
        splashSkipButtonBg.setColor(Color.parseColor("#40000000"));
        splashSkipButtonBg.setCornerRadius(40);
        splashImageView = mImageLoader.createImageView(weakReference.get());
        if (splashImageView instanceof ImageView) ((ImageView)splashImageView).setScaleType(ImageView.ScaleType.CENTER_CROP);
        splashImageView.setBackgroundColor(weakReference.get().getResources().getColor(android.R.color.white));
        LayoutParams imageViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(splashImageView, imageViewLayoutParams);
        splashImageView.setClickable(true);

        skipButton = new TextView(weakReference.get());
        int skipButtonSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, skipButtonSizeInDip, weakReference.get().getResources().getDisplayMetrics());
        LayoutParams skipButtonLayoutParams = new LayoutParams(skipButtonSize, skipButtonSize);
        skipButtonLayoutParams.gravity = Gravity.TOP|Gravity.RIGHT;
        int skipButtonMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, skipButtonMarginInDip, weakReference.get().getResources().getDisplayMetrics());
        skipButtonLayoutParams.setMargins(0, skipButtonMargin, skipButtonMargin, 0);
        skipButtonLayoutParams.width= 150;
        skipButtonLayoutParams.height= 80;
        skipButton.setGravity(Gravity.CENTER);
        skipButton.setTextColor(weakReference.get().getResources().getColor(android.R.color.white));
        if (advertList != null && advertList.size() > 0) skipButton.setVisibility(advertList.get(currentPosition).getIs_superscript() == 1 ? VISIBLE : GONE);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            skipButton.setBackground(splashSkipButtonBg);
        }else{
            skipButton.setBackgroundDrawable(splashSkipButtonBg);
        }
        skipButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);

        this.addView(skipButton, skipButtonLayoutParams);

        skipButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSplashView(true);
            }
        });

        setDuration(duration);
        if (duration >= 3600){
            skipButton.setVisibility(GONE);
        }else{
            handler.postDelayed(timerRunnable, delayTime);
        }
    }

    private SplashView mSplashView;
    private void setSplashView (SplashView splashView){
        this.mSplashView = splashView;
    }

    private void setDuration(Integer duration) {
        this.duration = duration;
        skipButton.setText(String.format(" %d 跳过 ", duration));
    }

    private void setOnSplashImageClickListener(@Nullable final OnSplashViewActionListener listener) {
        if (null == listener) return;
        mOnSplashViewActionListener = listener;
        splashImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (advertList.size() > currentPosition){
                    String linkUrl = advertList.get(currentPosition).getLinkurl();
                    listener.onSplashImageClick(linkUrl == null ? "" :linkUrl,mSplashView);
                }

            }
        });
    }
    /**
     * start
     * @param activity 上下文
     * @param advertList 数据
     * @param listener 监听
     */
    @SuppressLint("RestrictedApi")
    public static void showSplashView(@NonNull Activity activity,
                                      @Nullable List<Advert> advertList,
                                      @Nullable OnSplashViewActionListener listener,
                                      @Nullable ImageLoaderInterface imageLoader) {

        //设置splashView放在布局最顶层
        ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        if (null == contentView || 0 == contentView.getChildCount()) {
            throw new IllegalStateException("You should call showSplashView() after setContentView() in Activity instance");
        }
        SplashView splashView = new SplashView(activity,advertList,imageLoader);
        splashView.setSplashView(splashView);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        splashView.setOnSplashImageClickListener(listener);
        if (advertList == null || advertList.size() == 0) {
            splashView.dismissSplashView(false);
            return;
        }
        //开始加载图片
        int durationTime = advertList.get(0).getDuration();
        String firstImage = advertList.get(0).getImageurl();
        splashView.setDuration(durationTime);
        splashView.setImage(firstImage == null ? "" : firstImage);
        //设置上下状态栏
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            splashView.previousStatusColor = activity.getWindow().getStatusBarColor();
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            View decorView = activity.getWindow().getDecorView();
            if (isNavigationBarAvailable()){//判断是否有华为虚拟键，隐藏虚拟键
                splashView.previousSysytemUi = decorView.getSystemUiVisibility();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
        }else{
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }*/
        if (activity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (null != supportActionBar) {
                supportActionBar.setShowHideAnimationEnabled(false);
                splashView.isActionBarShowing = supportActionBar.isShowing();
                supportActionBar.hide();
            }
        } else if (activity instanceof Activity) {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (null != actionBar) {
                splashView.isActionBarShowing = actionBar.isShowing();
                actionBar.hide();
            }
        }


        contentView.addView(splashView, param);
    }

    public void setImageLoader(ImageLoaderInterface imageLoader) {
        this.mImageLoader = imageLoader;
    }
    public void dismissSplashView(){
        handler.removeCallbacks(timerRunnable);
    }
    public void dismissSplashView(final boolean initiativeDismiss) {
        handler.removeCallbacks(timerRunnable);
        final ViewGroup parent = (ViewGroup) this.getParent();
        showSystemUi(initiativeDismiss);
        if (null != parent) {
            @SuppressLint("ObjectAnimatorBinding") ObjectAnimator animator = ObjectAnimator.ofFloat(SplashView.this, "scale", 0.0f, 0.5f).setDuration(600);
            animator.start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float cVal = (Float) animation.getAnimatedValue();
                    SplashView.this.setAlpha(1.0f - 2.0f * cVal);
                    /*SplashView.this.setScaleX(1.0f + cVal);
                    SplashView.this.setScaleY(1.0f + cVal);*/
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    parent.removeView(SplashView.this);
                    //showSystemUi(initiativeDismiss);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    parent.removeView(SplashView.this);
                    //showSystemUi(initiativeDismiss);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }
    //是否有下方虚拟栏
    private static boolean isNavigationBarAvailable() {
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        return (!(hasBackKey && hasHomeKey));
    }
    private void showSystemUi(boolean initiativeDismiss) {
        weakReference.get().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (weakReference.get() instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) weakReference.get()).getSupportActionBar();
            if (null != supportActionBar) {
                if (isActionBarShowing) supportActionBar.show();
            }
        } else if (weakReference.get() instanceof Activity) {
            android.app.ActionBar actionBar = weakReference.get().getActionBar();
            if (null != actionBar) {
                if (isActionBarShowing) actionBar.show();
            }
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            View decorView = weakReference.get().getWindow().getDecorView();
            if (isNavigationBarAvailable()){
                decorView.setSystemUiVisibility(previousSysytemUi);
                decorView.setFitsSystemWindows(true);
            }
            if (previousStatusColor != 0)
                weakReference.get().getWindow().setStatusBarColor(previousStatusColor);
        }else{
            weakReference.get().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }*/
        if (null != mOnSplashViewActionListener) mOnSplashViewActionListener.onSplashViewDismiss(initiativeDismiss);
    }

    private static boolean isExistsLocalSplashData(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String imgUrl = sp.getString(IMG_URL, null);
        return !TextUtils.isEmpty(imgUrl) && isFileExist(IMG_PATH);
    }

    /**
     * static method, update splash view data
     * @param imgUrl - url of image which you want to set as splash image
     * @param actionUrl - related action url, such as webView etc.
     */
    public static void updateSplashData(@NonNull Activity activity, @NonNull String imgUrl, @Nullable String actionUrl) {
        IMG_PATH = activity.getFilesDir().getAbsolutePath().toString() + "/splash_img.jpg";
        SharedPreferences.Editor editor = activity.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(IMG_URL, imgUrl);
        editor.putString(ACT_URL, actionUrl);
        editor.apply();
        getAndSaveNetWorkBitmap(imgUrl);
    }
    public static boolean isUpdate(@NonNull Activity activity,@NonNull String imgUrl){
        SharedPreferences sp = activity.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String imgPath = sp.getString(IMG_URL, null);
        if (imgPath == null || imgPath == "")
            return true;
        if (!imgUrl.equals(imgPath)){
            return true;
        }else{
            return false;
        }

    }

    public interface OnSplashViewActionListener {
        void onSplashImageClick(String actionUrl,SplashView splashView);
        void onSplashViewDismiss(boolean initiativeDismiss);
    }

    private static void getAndSaveNetWorkBitmap(final String urlString) {
        Runnable getAndSaveImageRunnable = new Runnable() {
            @Override
            public void run() {
                URL imgUrl = null;
                Bitmap bitmap = null;
                try {
                    imgUrl = new URL(urlString);
                    HttpURLConnection urlConn = (HttpURLConnection) imgUrl.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.connect();
                    InputStream is = urlConn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();

                    saveBitmapFile(bitmap, IMG_PATH);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(getAndSaveImageRunnable).start();
    }

    private static void saveBitmapFile(Bitmap bm, String filePath) throws IOException {
        File myCaptureFile = new File(filePath);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        /*SharedPreferences.Editor editor = activity.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(IMG_URL, imgUrl);*/
    }

    public static boolean isFileExist(String filePath) {
        if(TextUtils.isEmpty(filePath)) {
            return false;
        } else {
            File file = new File(filePath);
            return file.exists() && file.isFile();
        }
    }
}