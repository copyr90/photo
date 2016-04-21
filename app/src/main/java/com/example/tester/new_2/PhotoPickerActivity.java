package com.example.tester.new_2;


import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/4/10.
 */
public class PhotoPickerActivity extends BaseActivity implements View.OnClickListener, PhotoAdapter.OnActionImageClickListener,
        LoaderCallbacks<Cursor>, PhotoAlbumListDialog.onPhotoAlbumItemClickListener {

    static final Uri MediaURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    static final String[] MediaPROJECTION = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
    static final String MediaSORTORDER = MediaStore.Images.Media._ID + " DESC";

    public static final int ID_INDEX = 0;
    public static final int DATA_INDEX = 1;
    public static final int BUCKETNAME_INDEX = 2;

    private static final int LOADER_ID = 0;

    private GridView mGridView;
    private PhotoAdapter mAdapter;

    private int maxSelect = 30;

    private TextView tvTitle;
    private TextView tvAlbum;
    private Button btnConfirm;
    private ProgressBar progressBar;

    private PhotoAlbumListDialog mDialog;

    private List<PhotoAlbumInfo> mAlbumList = new ArrayList<>();
    private MatrixCursor cursorTemp;
    private Cursor cursor;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_photo_picker_layout;
    }

    @Override
    protected void initial() {
        maxSelect = getIntent().getIntExtra(Parameters.PARAM_PHOTO_MAX_SELECT_NUM, Constants.MAX_VALUE_PHOTOS);

        initView();
        initListener();
        initData();
    }

    protected void initView() {
        mGridView = (GridView) findViewById(R.id.grid);
        mAdapter = new PhotoAdapter(this);
        mGridView.setAdapter(mAdapter);
        mAdapter.setOnActionImageClickListener(this);

        tvAlbum = (TextView) findViewById(R.id.tv_album);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mDialog = new PhotoAlbumListDialog(PhotoPickerActivity.this, this);

        updateTitle(getString(R.string.img_all_photo));
        updateChoseNum();
    }

    protected void initListener() {
        btnConfirm.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
    }

    protected void initData() {
        cursorTemp = new MatrixCursor(new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME});
        progressBar.setVisibility(View.VISIBLE);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MediaURI, MediaPROJECTION, null, null, MediaSORTORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            progressBar.setVisibility(View.GONE);
            mAdapter.swapCursor(data);

            cursor = data;
            if (null == cursor || !cursor.moveToFirst()) {
                return;
            }

            HashMap<String, PhotoAlbumInfo> myhash = new HashMap<String, PhotoAlbumInfo>();
            PhotoAlbumInfo photoAlbumInfo = null;

            do {
                int id = cursor.getInt(ID_INDEX);
                String path = cursor.getString(DATA_INDEX);
                String albumName = cursor.getString(BUCKETNAME_INDEX);

                int index = 0;

                if (!StringUtils.isNormalImagePath(path)) {
                    continue;
                }

                if (myhash.containsKey(albumName)) {
                    photoAlbumInfo = myhash.remove(albumName);
                    if (mAlbumList.contains(photoAlbumInfo))
                        index = mAlbumList.indexOf(photoAlbumInfo);
                    photoAlbumInfo.addPhotoCount();
                    mAlbumList.set(index, photoAlbumInfo);
                    myhash.put(albumName, photoAlbumInfo);
                } else {
                    photoAlbumInfo = new PhotoAlbumInfo();
                    photoAlbumInfo.imageId = id;
                    photoAlbumInfo.pathFile = (Scheme.FILE + path);
                    photoAlbumInfo.nameAlbum = albumName;
                    photoAlbumInfo.addPhotoCount();
                    mAlbumList.add(photoAlbumInfo);
                    myhash.put(albumName, photoAlbumInfo);
                }
            } while (cursor.moveToNext());

            /* 首部插入所有图片集合 */
            if (cursor.moveToFirst()) {
                photoAlbumInfo = new PhotoAlbumInfo();
                photoAlbumInfo.photoCount = cursor.getCount();
                photoAlbumInfo.nameAlbum = (getString(R.string.img_all_photo));
                photoAlbumInfo.imageId = cursor.getInt(ID_INDEX);
                photoAlbumInfo.pathFile = Scheme.FILE + cursor.getString(DATA_INDEX);
                mAlbumList.add(0, photoAlbumInfo);
            }

            mDialog.setItems(mAlbumList);
            tvAlbum.setEnabled(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                MultiplePhotoUtils.getInstance().clearPhotoList();
                setResult(QuesCode.ResultCodeMultipleCancle);
                finish();
                break;
            case R.id.btn_confirm:
                setResult(QuesCode.ResultCodeMultipleDone);
                finish();
                break;
            case R.id.tv_album:
                if (mDialog.isShowing())
                    mDialog.dismissDialog();
                else
                    mDialog.showDialog();
                break;
        }
    }

    @Override
    public void onAlbumClick(PhotoAlbumInfo photoAlbumInfo) {
        updateTitle(photoAlbumInfo.nameAlbum);

        cursorTemp.close();
        cursorTemp = new MatrixCursor(new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Thumbnails.DATA});
        if (photoAlbumInfo.nameAlbum.equals(getString(R.string.img_all_photo))) {
            mAdapter.swapCursor(cursor);
            return;
        }

        if (cursor.moveToFirst()) {
            do {
                String nameAlbum = cursor.getString(BUCKETNAME_INDEX);
                if (nameAlbum.equals(photoAlbumInfo.nameAlbum)) {
                    long id = cursor.getInt(ID_INDEX);
                    String path = cursor.getString(DATA_INDEX);// file path

                    cursorTemp.addRow(new Object[]{id, path, nameAlbum});
                } else {
                    continue;
                }
            } while (cursor.moveToNext());
        }
        mAdapter.swapCursor(cursorTemp);
    }

    /* 点击右上角小标标 */
    @Override
    public void onImgPickerClick(PhotoInfo photoInfo, PhotoAdapter.ViewHolder holder) {
        handlePhotoInfoChose(photoInfo, holder);
    }

    /* 点击图片 */
    @Override
    public void onImgClick(PhotoInfo photoInfo, PhotoAdapter.ViewHolder holder) {
        handlePhotoInfoChose(photoInfo, holder);
    }

    /* 处理点击选择图片后的逻辑业务 */
    private void handlePhotoInfoChose(PhotoInfo photoInfo, PhotoAdapter.ViewHolder holder) {
        if (MultiplePhotoUtils.getInstance().isPhotoChoosed(photoInfo)) {
            MultiplePhotoUtils.getInstance().choosePhoto(photoInfo, false);
        } else {
            if (MultiplePhotoUtils.getInstance().getPhotoList().size() == maxSelect) {
                MyProgressHUD.showToast(this, String.format(getString(R.string.img_max_select_toast), maxSelect));
                return;
            }
            MultiplePhotoUtils.getInstance().choosePhoto(photoInfo, true);
        }
        updateChoseNum();
        mAdapter.refreshView(holder);
    }

    /* 更新左下角数字提醒 */
    private void updateChoseNum() {
        btnConfirm.setText(String.format(getString(R.string.img_chose_complete), MultiplePhotoUtils.getInstance().getPhotoList().size(), maxSelect));
        btnConfirm.setEnabled(MultiplePhotoUtils.getInstance().getPhotoList().size() > 0 ? true : false);
    }

    /* 更新标题 */
    private void updateTitle(String text) {
        tvTitle.setText(text);
        tvAlbum.setText(text);
    }

    @Override
    public void onBackPressed() {
        MultiplePhotoUtils.getInstance().clearPhotoList();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        if (cursor != null)
            cursor.close();
    }
}