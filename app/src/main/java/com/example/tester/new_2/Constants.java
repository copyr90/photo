package com.example.tester.new_2;

/**
 * Created by Administrator on 2016/4/10.
 */
public class Constants {
    /* 半秒 */
    public final static int HALF_SECOND = 500;

    /* 网络请求一次条数 */
    public final static int PAGESIZE = 20;

    /*选择图片的最大数目 */
    public static final int MAX_VALUE_PHOTOS = 30;

    // TODO 临时记录在这个文件夹，后期统一整理
    /* 拍摄图片保存文件夹 */
    public final static String CameraImageFileDictory = Config.getAppTempPath() + "cameraImage/";
    /* 裁剪图片保存文件夹 */
    public final static String CropImageFileDictory = Config.getAppTempPath() + "cropImage/";
}
