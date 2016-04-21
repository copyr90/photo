package com.example.tester.new_2;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/10.
 */
/**
 * 相册bean<br>
 * {@link #imageId}图片id<br>
 * {@link #pathFile} 用于显示的路径<br>
 * {@link #nameAlbum} 相册名称<br>
 * {@link #photoCount} 相册所有照片数目<br>*
 */
public class PhotoAlbumInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public int imageId;
    public String pathFile;
    public String nameAlbum;
    public int photoCount;

    public void addPhotoCount() {
        this.photoCount++;
    }

}