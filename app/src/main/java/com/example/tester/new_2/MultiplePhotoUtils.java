package com.example.tester.new_2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/10.
 */
public class MultiplePhotoUtils {
    private static MultiplePhotoUtils sInstance = null;
    private List<PhotoInfo> mPhotoList = new ArrayList<PhotoInfo>();

    public MultiplePhotoUtils() {
        super();
    }

    public synchronized static MultiplePhotoUtils getInstance() {
        if (sInstance == null) {
            sInstance = new MultiplePhotoUtils();
        }

        return sInstance;
    }

    public void choosePhoto(PhotoInfo photoInfo, boolean choose) {
        if (photoInfo == null) {
            return;
        }

        //此处最多遍历 maxSelect次，所以先用这种array过滤
        boolean find = false;
        for (int i = 0; i < mPhotoList.size(); i++) {
            PhotoInfo choosePhoto = mPhotoList.get(i);
            if (choosePhoto.imageId == photoInfo.imageId) {
                if (choose) {
                    mPhotoList.add(photoInfo);
                } else {
                    mPhotoList.remove(choosePhoto);
                }
                find = true;
                break;
            }
        }
        if (!find) {
            if (choose) {
                mPhotoList.add(photoInfo);
            }
        }
    }

    public boolean isPhotoChoosed(PhotoInfo photoInfo) {
        if (photoInfo == null) {
            return false;
        }

        boolean find = false;
        for (int i = 0; i < mPhotoList.size(); i++) {
            PhotoInfo choosePhoto = mPhotoList.get(i);
            if (choosePhoto.imageId == photoInfo.imageId) {
                find = true;
                break;
            }
        }

        return find;
    }

    public List<PhotoInfo> getPhotoList() {
        return mPhotoList;
    }

    public boolean isPhotoListEmpty() {
        return null == mPhotoList || mPhotoList.size() == 0 ? true : false;
    }

    public void clearPhotoList() {
        mPhotoList.clear();
    }

}

