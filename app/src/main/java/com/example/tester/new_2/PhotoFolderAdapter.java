package com.example.tester.new_2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/4/10.
 */
public class PhotoFolderAdapter extends MyBaseAdapter<PhotoAlbumInfo> {
    private Context mContext;

    public PhotoFolderAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.photo_folder_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PhotoAlbumInfo photoAlbumInfo = getItem(position);
        ImageLoader.getInstance().displayImage(photoAlbumInfo.pathFile, holder.image,
                ImageOptionFactory.getPhotoPickImageOptions());
        holder.info.setText(photoAlbumInfo.nameAlbum);
        holder.num.setText(String.format(mContext.getResources().getString(R.string.img_album_photo_num), photoAlbumInfo.photoCount));
        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
        public TextView info;
        public TextView num;

        public ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.img);
            info = (TextView) view.findViewById(R.id.info);
            num = (TextView) view.findViewById(R.id.num);
        }
    }
}