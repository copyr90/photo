package com.example.tester.new_2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Roy
 * Date: 16/1/1
 * * <p/>
 * 本地相册图片bean<br>
 * {@link #imageId}图片id<br>
 * {@link #absolutePath} 绝对路径<br>
 * {@link #filePath} 用于显示的路径<br>
 * {@link #photoId} 网络PhotoId<br>
 * {@link #imageUrl} 网络图片url<br>
 */
public class PhotoInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public long imageId;
    public String filePath;
    public String absolutePath;
    public int position;

    public long photoId;
    public String imageUrl;
    public String thumImgUrl;

    public long pic;
    /* 经度 */
    public double lat;
    /* 纬度 */
    public double lng;

    public PhotoInfo() {
    }

    public PhotoInfo(String filePath) {
        this(0, filePath);
    }

    public PhotoInfo(long imageId, String filePath) {
        this.imageId = imageId;

        if (filePath.contains(Scheme.FILE)) {
            this.filePath = filePath;
            this.absolutePath = filePath.substring(Scheme.FILE.length());
        } else {
            this.filePath = Scheme.FILE + filePath;
            this.absolutePath = filePath;
        }
    }

    public static PhotoInfo insertOrReplace(JSONObject jsonObject) {
        PhotoInfo object = new PhotoInfo();

        String baseUrl = jsonObject.optString("photoUrl");

        String url = jsonObject.optString("url");



        if (jsonObject.has("photoes")) {
            JSONArray jsonArray = jsonObject.optJSONArray("photoes");
            try {
                JSONObject json = jsonArray.getJSONObject(0);

                object.photoId = json.getInt("photoId");
                object.thumImgUrl = "http://58.55.127.188" + baseUrl + object.photoId + com.example.tester.new_2.Scheme.TypeS;
                object.imageUrl = "http://58.55.127.188" + baseUrl + object.photoId + com.example.tester.new_2.Scheme.TypeL;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObject.has("pics")) {
            JSONArray jsonArray = jsonObject.optJSONArray("pics");
            try {
                JSONObject json = jsonArray.getJSONObject(0);

                object.pic = json.getInt("pic");
                object.imageUrl = "http://58.55.127.188" + url + object.pic;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return object;
    }
}
