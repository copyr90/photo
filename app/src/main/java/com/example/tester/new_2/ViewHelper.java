package com.example.tester.new_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/4/10.
 */
public class ViewHelper {
    public static void setWidth(View view, int width) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (null == lp) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        lp.width = width;
        view.setLayoutParams(lp);
    }

    public static void setHeight(View view, int height) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (null == lp) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        lp.height = height;
        view.setLayoutParams(lp);
    }

    public static void setHeight(View view, float height) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (null == lp) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        lp.height = (int) height;
        view.setLayoutParams(lp);
    }

    public static void setSize(View view, int width, int height) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (null == lp) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        lp.width = width;
        lp.height = height;
        view.setLayoutParams(lp);
    }

    /**
     * 获取本地drawable 转换到bitmap
     *
     * @param resId
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitMapFromDrawable(Context context, int resId, int width, int height) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resId);
        if (width <= 0 || height <= 0) {
            return bm;
        }
        return Bitmap.createScaledBitmap(bm, width, height, false);
    }
}