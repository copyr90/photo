package com.example.tester.new_2;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/9.
 */
public class MainActivity2 extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button button_1;
    private GridView mPhotoGradView;
    private SaveInspectionPreViewAdapter mAdapter;
    private int inspectionId = 0;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_2;
    }

    @Override
    protected void initial() {
        initView();
        initClick();
    }

    private void initView() {
        mPhotoGradView = (GridView) findViewById(R.id.gridview);
    }

    private void initClick() {
        mAdapter = new SaveInspectionPreViewAdapter(this);
        mPhotoGradView.setAdapter(mAdapter);
        mPhotoGradView.setOnItemClickListener(this);

        /* 网络图片 */
        List<PhotoInfo> photoInfos = new ArrayList<>();


        List<InspectionPicture> inspectionPictures = InspectionPicture.getInspectionPicture(inspectionId);

        for (InspectionPicture inspectionPicture : inspectionPictures) {
            PhotoInfo photoInfo = new PhotoInfo(inspectionPicture.filePath);
            photoInfo.lat = inspectionPicture.lat;
            photoInfo.lng = inspectionPicture.lng;
            photoInfos.add(photoInfo);
        }
        mAdapter.setItems(photoInfos);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == mAdapter.getItems().size()) {
            PhotoUtils.pickPhoto(MainActivity2.this, Constants.MAX_VALUE_PHOTOS - mAdapter.getItems().size());
        } else {
            return;
        }
    }
}
