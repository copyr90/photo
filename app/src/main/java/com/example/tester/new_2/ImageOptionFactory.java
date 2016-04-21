package com.example.tester.new_2;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Administrator on 2016/4/10.
 */
public class ImageOptionFactory {
    static DisplayImageOptions DISPLAY_AVATER_OPTIONS;

    public static DisplayImageOptions getAvaterImageOptions() {
        if (DISPLAY_AVATER_OPTIONS == null) {
            DISPLAY_AVATER_OPTIONS = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.mipmap.ic_error)
                    .showImageOnLoading(R.mipmap.ic_error)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();
        }
        return DISPLAY_AVATER_OPTIONS;
    }

    static DisplayImageOptions DISPLAY_DEFAULT_OPTIONS;

    public static DisplayImageOptions getDefaultImageOptions() {
        if (DISPLAY_DEFAULT_OPTIONS == null) {
            DISPLAY_DEFAULT_OPTIONS = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.mipmap.ic_error)
                    .showImageOnLoading(R.mipmap.ic_error)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();
        }
        return DISPLAY_DEFAULT_OPTIONS;
    }

    static DisplayImageOptions DISPLAY_PHOTOPICK_OPTIONS;
    public static DisplayImageOptions getPhotoPickImageOptions() {
        if (DISPLAY_PHOTOPICK_OPTIONS == null) {

            DISPLAY_PHOTOPICK_OPTIONS = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(R.color.grey)
                    .showImageForEmptyUri(R.mipmap.pic_main)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .considerExifParams(true)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
        }
        return DISPLAY_PHOTOPICK_OPTIONS;
    }

    static DisplayImageOptions DISPLAY_BIGPHOTO_OPTIONS;
    public static DisplayImageOptions getNoChangePhotoOptions() {
        if (DISPLAY_BIGPHOTO_OPTIONS == null) {
            DISPLAY_BIGPHOTO_OPTIONS = new DisplayImageOptions.Builder()
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();
        }
        return DISPLAY_BIGPHOTO_OPTIONS;
    }
}
