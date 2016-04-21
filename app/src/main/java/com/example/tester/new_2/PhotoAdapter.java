package com.example.tester.new_2;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/4/10.
 */
public class PhotoAdapter extends CursorAdapter {

    /**
     * 列表数据按钮监听接口
     */
    public interface OnActionImageClickListener {
        void onImgPickerClick(PhotoInfo photoInfo, ViewHolder holder);

        void onImgClick(PhotoInfo photoInfo, ViewHolder holder);
    }

    private OnActionImageClickListener mActionListener;

    public void setOnActionImageClickListener(OnActionImageClickListener listener) {
        mActionListener = listener;
    }

    private Context mContext;
    private int size;

    public PhotoAdapter(Context context) {
        super(context, null, false);
        this.mContext = context;

        size = (DensityUtils.getWindowWidth(mContext) - mContext.getResources().getDimensionPixelSize(R.dimen.dp_4) * 5) / 4;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.photo_item, null);
        ViewHolder holder = new ViewHolder(view, size);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.position = cursor.getPosition();

        long id = cursor.getInt(PhotoPickerActivity.ID_INDEX);
        final String path = cursor.getString(PhotoPickerActivity.DATA_INDEX);

        final PhotoInfo photoInfo = new PhotoInfo(id, path);

        ImageLoader.getInstance().displayImage(photoInfo.filePath, holder.img, ImageOptionFactory.getPhotoPickImageOptions(), new AnimateDisplayListener());

        if (MultiplePhotoUtils.getInstance().isPhotoChoosed(photoInfo)) {
            holder.imgPicker.setSelected(true);
        } else {
            holder.imgPicker.setSelected(false);
        }

        holder.imgPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionListener.onImgPickerClick(photoInfo, holder);
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionListener.onImgClick(photoInfo, holder);
            }
        });
    }

    public void refreshView(ViewHolder holder) {
        holder.imgPicker.setSelected(!holder.imgPicker.isSelected());
    }

    @Override
    public int getCount() {
        return getCursor() != null ? getCursor().getCount() : 0;
    }

    public static class ViewHolder {
        ImageView img;
        ImageView imgPicker;
        int position;

        public ViewHolder(View view, int size) {
            img = (ImageView) view.findViewById(R.id.photo_item);
            imgPicker = (ImageView) view.findViewById(R.id.select_img);
            ViewHelper.setSize(img, size, size);
        }
    }
}