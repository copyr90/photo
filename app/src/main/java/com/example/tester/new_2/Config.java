package com.example.tester.new_2;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2016/4/10.
 */
public class Config {
    /**
     * 数据目录
     */
    public final static String APP_DATA_PATH = "firesafe";

    /**
     * 图片目录
     */
    public final static String APP_IMAGES_PATH = "images";

    /**
     * 临时目录名称
     */
    public final static String APP_TEMP_PATH = "temp";

    /**
     * 获取程序图片目录
     *
     * @return
     */
    public static String getAppImagesPath() {
        String imgPath =  getAppDataPath() + APP_IMAGES_PATH + File.separator;
        return imgPath;
    }

    /**
     * 获取app数据目录
     *
     * @return
     */
    public static String getAppDataPath() {
        // 判断是否挂载了SD卡
        String dataPath = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            dataPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + File.separator
                    + APP_DATA_PATH
                    + File.separator;
        } else {

            File basePath = MyApplication.getInstance().getFilesDir();
            if (basePath == null) {
                basePath = MyApplication.getInstance().getCacheDir();
            }
            dataPath = basePath.getAbsolutePath()
                    + File.separator
                    + APP_DATA_PATH
                    + File.separator;
        }
        File file = new File(dataPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dataPath;
    }

    /**
     * 获取程序临时目录
     *
     * @return
     */
    public static String getAppTempPath() {
        return getAppDataPath() + APP_TEMP_PATH + File.separator;
    }
}
