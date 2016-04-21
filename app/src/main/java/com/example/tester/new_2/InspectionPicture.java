package com.example.tester.new_2;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Roy
 * 图片
 * Date: 16/1/4
 */
@Table(name = "InspectionPicture")
public class InspectionPicture extends Model {

    @Column(name = "inspectionId")
    public int inspectionId;

    @Column(name = "filePath")
    public String filePath;

    /* 经度 */
    @Column(name = "lat")
    public double lat;
    /* 纬度 */
    @Column(name = "lng")
    public double lng;

    /* 获取所有未上传图片 */
    public static List<InspectionPicture> getInspectionPicture(int id) {
        return new Select().from(InspectionPicture.class).where("inspectionId=" + id).execute();
    }

    /* 新增图片保存至数据库 */
    public static void saveInspectionPicture(int inspectionId, String filePath, double lat, double lng) {
        InspectionPicture inspectionPicture = new InspectionPicture();
        inspectionPicture.inspectionId = inspectionId;
        inspectionPicture.filePath = filePath;
        inspectionPicture.lat = lat;
        inspectionPicture.lng = lng;
        inspectionPicture.save();
    }

    /* 删除图片 */
    public static void delInspectionPicture(int inspectionId, String filePath) {
        InspectionPicture inspectionPicture = new Select().from(InspectionPicture.class).where("inspectionId=" + inspectionId + " AND filePath like " + "\"" + filePath + "\"").executeSingle();
        if (inspectionPicture != null)
            inspectionPicture.delete();
    }
}