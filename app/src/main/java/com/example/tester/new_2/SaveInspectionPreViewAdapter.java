package com.example.tester.new_2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/9.
 */
public class SaveInspectionPreViewAdapter extends MyBaseAdapter<PhotoInfo> {

    public final static int TYPE_NORMAL = 0; // 正常显示的照片
    public final static int TYPE_TAKE_PHOTO = 1; // 拍照按钮
    public final static int TYPE_COUNT = 2;
    private Context mContext;

    public SaveInspectionPreViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItems().size() && getItems().size() < Constants.MAX_VALUE_PHOTOS) {
            return TYPE_TAKE_PHOTO;
        } else
            return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        if (getItems().size() == Constants.MAX_VALUE_PHOTOS)
            return getItems().size();
        else
            return getItems() != null ? getItems().size() + 1 : 1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PhotoInfo photoInfo = getItem(position);

        final PhotoHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_published_grid, null);
            holder = new PhotoHolder(convertView, mContext);
            convertView.setTag(holder);
        } else {
            holder = (PhotoHolder) convertView.getTag();
        }
        switch (getItemViewType(position)) {
            case TYPE_NORMAL:
                if (photoInfo != null) {
                    if (StringUtils.isNotEmpty(photoInfo.thumImgUrl)) {
                        ImageLoader.getInstance().displayImage(photoInfo.thumImgUrl, holder.image);
                        holder.imgRed.setVisibility(View.GONE);
                    } else {
                        ImageLoader.getInstance().displayImage(photoInfo.filePath, holder.image);
                        holder.imgRed.setVisibility(View.VISIBLE);
                    }
                }
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<PictureShow> pictureShows = PictureShow.createPictureShows(getItems(), holder.image);
                        OpenActivityManager.getInstance().startPhotoPreViewActivity(mContext, pictureShows, position);

                    }
                });
                break;
            case TYPE_TAKE_PHOTO:
                holder.image.setImageResource(R.mipmap.icon_camera_click);
                holder.imgRed.setVisibility(View.GONE);
                break;
        }

        return convertView;
    }

    public class PhotoHolder {
        public ImageView image;
        public ImageView imgRed;

        public PhotoHolder(View view, Context context) {
            image = (ImageView) view.findViewById(R.id.item_grida_image);
            imgRed = (ImageView) view.findViewById(R.id.icon_img_selected);
            ViewHelper.setSize(image, context.getResources().getDimensionPixelSize(R.dimen.dp_70), context.getResources().getDimensionPixelSize(R.dimen.dp_70));
        }
    }

    @Override
    public void remove(PhotoInfo item) {
        super.remove(item);
        clearUploadPhotos();
    }

    @Override
    public void addItem(PhotoInfo item) {
        super.addItem(item);
        clearUploadPhotos();
    }

    @Override
    public void addItems(List<PhotoInfo> items) {
        super.addItems(items);
        clearUploadPhotos();
    }

    List<PhotoInfo> uploadPhotos = new ArrayList<>();

    public List<PhotoInfo> getUploadItems() {
        if (uploadPhotos.size() == 0) {
            for (PhotoInfo photoInfo : getItems()) {
                if (StringUtils.isNotEmpty(photoInfo.filePath) && StringUtils.isEmpty(photoInfo.imageUrl)) {
                    uploadPhotos.add(photoInfo);
                }
            }
        }
        return uploadPhotos;
    }

    public void clearUploadPhotos() {
        if (uploadPhotos != null)
            uploadPhotos.clear();
    }


}
