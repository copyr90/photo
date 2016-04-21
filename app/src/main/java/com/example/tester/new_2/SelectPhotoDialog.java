package com.example.tester.new_2;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2016/4/10.
 */
public class SelectPhotoDialog extends Dialog implements View.OnClickListener {
    private Button btnAlbum;
    private Button btnCarmera;
    protected Button btnCancle;

    public interface onActionListener {
        void onAlbumClick();

        void onCarmeraClick();
    }

    private onActionListener onActionListener;

    public void setOnActionListener(onActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    public SelectPhotoDialog(Context context) {
        super(context);
        initDialog(context);
    }

    public SelectPhotoDialog(Context context, int theme) {
        super(context, theme);
        initDialog(context);
    }

    protected SelectPhotoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void initDialog(Context context) {
        setContentView(R.layout.dialog_select_photo);
        initView();
        initListener();
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(-1, -2);
    }

    private void initView() {
        btnCarmera = (Button) findViewById(R.id.btn_carmera);
        btnAlbum = (Button) findViewById(R.id.btn_album);
        btnCancle = (Button) findViewById(R.id.btn_cancel);
    }

    private void initListener() {
        btnCarmera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_carmera:
                if (onActionListener != null)
                    onActionListener.onCarmeraClick();
                dismiss();
                break;
            case R.id.btn_album:
                if (onActionListener != null)
                    onActionListener.onAlbumClick();
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

}