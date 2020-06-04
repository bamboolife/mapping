package com.project.mapping.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;


import java.util.HashMap;
import java.util.Map;


public final class ScreenUtils {
    private static Map sMapMargin = new HashMap();
    public static void addMapMargin(int top, int left, int right, int bottom) {
        int sTop = getMapMargin("top") + top;
        int sLeft = getMapMargin("left") + left;
        int sRight = getMapMargin("right") + right;
        int sBottom = getMapMargin("bottom") + bottom;
        sMapMargin.put("top", sTop);
        sMapMargin.put("right", sRight);
        sMapMargin.put("left", sLeft);
        sMapMargin.put("bottom", sBottom);
    }

    public static int getMapMargin(String key) {
        return sMapMargin.get(key) != null ? (int) sMapMargin.get(key) : 0;
    }

    // 获取屏幕宽度
    public static int getScreenWidth(Context context) {
        WindowManager windowmanager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowmanager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.widthPixels;// - getMapMargin("left") - getMapMargin("right");
    }

    // 得到屏幕高度
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels - getMapMargin("top") - getMapMargin("bottom");
    }

    /**
     * 基于屏幕宽度，传入上下文和对应的view及view所需设置的宽高对比屏幕宽度的比例
     */

    public static LayoutParams getParmsFromWidth(Context context, View v, float width,
                                                 float height) {
        LayoutParams params = v.getLayoutParams();
        params.width = (int) (getScreenWidth(context) * width);
        params.height = (int) (getScreenWidth(context) * height);
        return params;
    }

    /**
     * 基于屏幕宽高，传入上下文和对应的view及view所需设置的宽高对比屏幕宽高的比例
     */
    public static LayoutParams getParms(Context context, View v, float width,
                                        float height) {
        LayoutParams params = v.getLayoutParams();
        params.width = (int) (getScreenWidth(context) * width);
        params.height = (int) (getScreenHeight(context) * height);
        return params;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int tp2px(Context context, int dimenTp){
        return context.getResources().getDimensionPixelSize(dimenTp);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float getDensity(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float getDpi(Context context) {
        final float dpi = context.getResources().getDisplayMetrics().densityDpi;
        return dpi;
    }



    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }


    public static float getMatchDp(int sourceDp, Context context) {
        float currentDensity = getDensity(context);
        float densityRatio = 2 / currentDensity;
        return (sourceDp * densityRatio + 0.5F);
    }

    /**
     * 获取 View 的坐标
     * <p>
     * RectF rect = calcViewScreenLocation(view);
     * boolean isInViewRect = rect.contains(x, y);
     *
     * @param view
     * @return
     */
    public static RectF calcViewScreenLocation(View view) {
        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
    }
}
