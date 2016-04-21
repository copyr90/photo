package com.example.tester.new_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/4/10.
 */
public class OpenActivityManager {
    private static OpenActivityManager instance;

        public static OpenActivityManager getInstance() {
            if (instance == null) {
                instance = new OpenActivityManager();
            }
            return instance;
        }

        /* 打开浏览网页大图的界面（多图） */
        public void startPhotoNormalBrowserActivity(Context context, List<PictureShow> pictureShows, int index) {
            Intent intent = new Intent();
            intent.putExtra(Parameters.PARAM_PICTURE_SHOW, (Serializable) pictureShows);
            intent.putExtra(Parameters.PARAM_START_INDEX, index);
            intent.setClass(context, PhotoBrowserActivity.class);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(0, 0);
        }

        /* 预览本地大图的界面（多图） */
        public void startPhotoPreViewActivity(Context context, List<PictureShow> pictureShows,int index){
            Intent intent = new Intent();
            intent.putExtra(Parameters.PARAM_PICTURE_SHOW, (Serializable) pictureShows);
            intent.putExtra(Parameters.PARAM_START_INDEX, index);
            intent.setClass(context, PhotoPreviewActivity.class);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(0, 0);
        }
}
