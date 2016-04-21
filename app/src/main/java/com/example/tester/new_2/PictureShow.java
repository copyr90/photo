package com.example.tester.new_2;

import android.content.Context;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/10.
 */
public class PictureShow implements Serializable {
    public String smallUrl;
    public String bigUrl;
    public int width;
    public int height;
    public int locationX;
    public int locationY;
    public long id;//图片唯一ID

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /* 根据View和大小图片url生成PictureShow对象 */
    public static PictureShow createPictureShow(String smallUrl, String bigUrl, View view) {
        PictureShow pictureShow = new PictureShow();
        pictureShow.smallUrl = smallUrl;
        pictureShow.bigUrl = bigUrl;
        pictureShow.width = view.getWidth();
        pictureShow.height = view.getHeight();

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        pictureShow.locationX = location[0];
        pictureShow.locationY = location[1];
        return pictureShow;
    }

    public static List<PictureShow> createPhotoNetShows(Context context, List<String> smallUrls, List<String> bigUrls, View... views) {
        List<PictureShow> pictureShowList = new ArrayList<>();
        for (int i = 0, n = smallUrls.size(); i < n; i++) {
            PictureShow pictureShow = new PictureShow();
            pictureShow.smallUrl = smallUrls.get(i);
            pictureShow.bigUrl = bigUrls.get(i);

            int[] location = new int[2];
            if (views.length > i) {
                views[i].getLocationOnScreen(location);
                pictureShow.width = views[i].getWidth();
                pictureShow.height = views[i].getHeight();
            } else {
                views[0].getLocationOnScreen(location);
                pictureShow.width = views[0].getWidth();
                pictureShow.height = views[0].getHeight();
            }
            pictureShow.locationX = location[0];
            pictureShow.locationY = location[1] + DensityUtils.getStatusBarHeight(context);
            pictureShowList.add(pictureShow);
        }

        return pictureShowList;
    }

    public static List<PictureShow> createPictureShows(List<PhotoInfo> photoInfos, View... views) {
        List<PictureShow> pictureShowList = new ArrayList<PictureShow>();
        for (int i = 0, n = photoInfos.size(); i < n; i++) {
            PictureShow pictureShow = new PictureShow();
            if (StringUtils.isNotEmpty(photoInfos.get(i).imageUrl)) {
                pictureShow.smallUrl = photoInfos.get(i).thumImgUrl;
                pictureShow.bigUrl = photoInfos.get(i).imageUrl;
            } else {
                pictureShow.smallUrl = photoInfos.get(i).filePath;
                pictureShow.bigUrl = photoInfos.get(i).filePath;
            }

            int[] location = new int[2];
            if (views.length > i) {
                views[i].getLocationOnScreen(location);
                pictureShow.width = views[i].getWidth();
                pictureShow.height = views[i].getHeight();
            } else {
                views[0].getLocationOnScreen(location);
                pictureShow.width = views[0].getWidth();
                pictureShow.height = views[0].getHeight();
            }
            pictureShow.locationX = location[0];
            pictureShow.locationY = location[1];
            pictureShowList.add(pictureShow);
        }
        return pictureShowList;
    }
}
