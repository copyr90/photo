package com.example.tester.new_2;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/4/10.
 */
public class DensityUtils {
    private static float scale = -1;
    private static int statusBarHeight = -1;
    private static int windowWidth = -1;
    private static int windowHeight = -1;

    private static float getScale(Context context) {
        if (scale == -1) {
            scale = context.getResources().getDisplayMetrics().density;
        }

        return scale;
    }

    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight < 0)
        {
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int sbar = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {

            }
        }

        return statusBarHeight;
    }

    public static int getWindowWidth(Context context) {
        if (windowWidth == -1)
        {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            windowWidth = dm.widthPixels;
        }

        return windowWidth;
    }

    public static int getWindowHeight(Context context) {
        if (windowHeight == -1)
        {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            windowHeight = dm.heightPixels;
        }

        return windowHeight;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        return (int) (dpValue * getScale(context) + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / getScale(context) + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    //    /** 屏幕宽度   */
    private static int DisplayWidthPixels = 0;
    //    /** 屏幕高度   */
    private static int DisplayheightPixels = 0;

    private static void getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        DisplayWidthPixels = dm.widthPixels;// 宽度
        DisplayheightPixels = dm.heightPixels;// 高度
    }

    public static int getDisplayWidthPixels(Context context) {
        if (context == null) {
            return -1;
        }
        if (DisplayWidthPixels == 0) {
            getDisplayMetrics(context);
        }
        return DisplayWidthPixels;
    }
}
