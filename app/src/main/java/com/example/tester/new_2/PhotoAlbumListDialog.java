package com.example.tester.new_2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Administrator on 2016/4/10.
 */
public class PhotoAlbumListDialog extends Dialog implements AdapterView.OnItemClickListener {

    private Context mContext;

    private ListView mListView = null;
    private PhotoFolderAdapter mAdapter;

    private onPhotoAlbumItemClickListener listener;

    public interface onPhotoAlbumItemClickListener {
        void onAlbumClick(PhotoAlbumInfo photoAlbumInfo);
    }

    public PhotoAlbumListDialog(Context context, onPhotoAlbumItemClickListener listener) {
        this(context, R.style.TransparentDialog, listener);
    }

    public PhotoAlbumListDialog(Context context, int theme, onPhotoAlbumItemClickListener listener) {
        super(context, theme);
        setContentView(R.layout.dialog_photo_album_list_layout);
        this.mContext = context;
        this.listener = listener;

        initView();
        getWindow().setWindowAnimations(R.style.pop_bottom_in_out_anim_style);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.getWindowHeight(context) / 4 * 3);
        setCanceledOnTouchOutside(true);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list_view);

        mAdapter = new PhotoFolderAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            listener.onAlbumClick(mAdapter.getItem(position));
        }
        dismissDialog();
    }

    public void setItems(List<PhotoAlbumInfo> items) {
        mAdapter.setItems(items);
    }

    public boolean showDialog() {
        if (isShowing() || ((Activity) mContext).isFinishing()) {
            return false;
        }

        show();
        return true;
    }

    public boolean dismissDialog() {
        if (isShowing()) {
            try {
                dismiss();
            } catch (Exception e) {
            }
            return true;
        }
        return false;
    }
}