# MySplashView
 SplashView.showSplashView(MainActivity.this, advertList, new SplashView.OnSplashViewActionListener() {
            @Override
            public void onSplashImageClick(String actionUrl, SplashView splashView) {

            }
            //initiativeDismiss表示是否手动点击跳过
            @Override
            public void onSplashViewDismiss(boolean initiativeDismiss) {

            }
        }, new GlideImageLoader());
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
