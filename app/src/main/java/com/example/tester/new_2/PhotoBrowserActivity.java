package com.example.tester.new_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/4/10.
 */
public class PhotoBrowserActivity extends BaseActivity {
    @Override
    protected int getLayoutResourceId() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

        return R.layout.activity_normal_photo_browser_layout;
    }

    @Override
    protected void initial() {
        initPictureData();
        initView();
    }

    List<PictureShow> pictureShowList = new ArrayList<PictureShow>();
    public int index = 0;

    public MyViewPager mViewPager;
    private PhotoAdapter mAdapter;
    private TextView tvPhotoNum;

    private List<ViewHolder> viewHolderList = new ArrayList<ViewHolder>();


    public void initPictureData() {
        pictureShowList = (List<PictureShow>) getIntent().getSerializableExtra(Parameters.PARAM_PICTURE_SHOW);
        index = getIntent().getIntExtra(Parameters.PARAM_START_INDEX, 0);
    }

    protected void initView() {
        mViewPager = (MyViewPager) findViewById(R.id.view_pager);
        tvPhotoNum = (TextView) findViewById(R.id.tv_photo_guide);
        for (int i = 0; i < pictureShowList.size(); i++) {
            ViewHolder holder = new ViewHolder();
            if (i == index) {
                holder.firstIn = true;
            }
            viewHolderList.add(holder);
        }
        initUnderView();
        initAdapter();
    }

    public void initUnderView() {
        if (pictureShowList.size() == 1) {
            tvPhotoNum.setVisibility(View.GONE);
        } else {
            tvPhotoNum.setVisibility(View.VISIBLE);
            tvPhotoNum.setText(String.format(getString(R.string.img_current_chose_num), index + 1, pictureShowList.size()));
        }
    }

    public void initAdapter() {
        mAdapter = new PhotoAdapter(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(index);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (pictureShowList.size() > 1) {
                    tvPhotoNum.setText(String.format(getString(R.string.img_guide_num), position + 1, pictureShowList.size()));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    class PhotoAdapter extends PagerAdapter {
        private Context mContext;

        PhotoAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return pictureShowList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final PictureShow pictureShow = pictureShowList.get(position);
            View cell = LayoutInflater.from(mContext).inflate(R.layout.normal_photo_browser_item_view, null);
            final ViewHolder holder = viewHolderList.get(position);

            holder.photoView = (SmoothImageView) cell.findViewById(R.id.photo_view);
            holder.progress_bar = (MyProgress) cell.findViewById(R.id.progress_bar);
            holder.progress_bar.setBackgroundColor(getResources().getColor(R.color.white));
            holder.progress_bar.setProgressColor(getResources().getColor(R.color.blue));

            holder.photoView.setOriginalInfo(pictureShow.width, pictureShow.height, pictureShow.locationX, pictureShow.locationY);
            holder.photoView.setCanPaint(true);
            holder.photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    dismiss();
                }
            });

            if (index == position && holder.firstIn) {
                holder.firstIn = false;
                ImageLoader.getInstance().displayImage(pictureShow.smallUrl, holder.photoView, ImageOptionFactory.getDefaultImageOptions(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.photoView.transformIn();
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
                holder.photoView.setOnTransformListener(new SmoothImageView.TransformListener() {
                    @Override
                    public void onTransformComplete(int mode) {
                        loadBigPhoto(pictureShow, holder.photoView, holder.progress_bar);
                    }
                });

            } else {
                /* 先加载缩略图 */
                ImageLoader.getInstance().displayImage(pictureShow.smallUrl, holder.photoView, ImageOptionFactory.getDefaultImageOptions(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        /* 再加载大图 */
                        loadBigPhoto(pictureShow, holder.photoView, holder.progress_bar);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
            viewHolderList.add(holder);


            container.addView(cell, DensityUtils.getWindowWidth(PhotoBrowserActivity.this), DensityUtils.getWindowHeight(PhotoBrowserActivity.this));

            return cell;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    public class ViewHolder {
        SmoothImageView photoView;
        MyProgress progress_bar;
        boolean firstIn = false;
    }


    private void loadBigPhoto(final PictureShow pictureShow, final SmoothImageView smoothImageView, final MyProgress progressBar) {
        /* 再加载大图 */
        ImageLoader.getInstance().displayImage(pictureShow.bigUrl, smoothImageView, ImageOptionFactory.getNoChangePhotoOptions(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
                smoothImageView.resetBitmap();
                /* 加载大图成功后可以保存 */
//                smoothImageView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        new ActionListDialog(NormalPhotoBrowserActivity.this, R.style.TransparentDialog).addItem(getString(R.string.img_save_to_phone), new ActionListDialog.OnClickListener() {
//                            @Override
//                            public void didClick(ActionListDialog dialog, String itemTitle) {
//                                dialog.dismiss();
//                                if (mViewPager.isDrawingCacheEnabled()) {
//                                    mViewPager.buildDrawingCache();
//                                    String name = pictureShow.smallUrl.substring(pictureShow.smallUrl.lastIndexOf("/") + 1, pictureShow.smallUrl.lastIndexOf(".jpg") + 4);
//                                    ImageFileUtil.savePhotoFromNet(mViewPager.getDrawingCache(), name, NormalPhotoBrowserActivity.this);
//                                }
//                            }
//                        }).show();
//
//                        return true;
//                    }
//                });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                double progress_value = ((double) current / total) * 100;
                progressBar.setProgress((int) progress_value);

                if (progressBar.getProgress() >= 99) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void dismiss() {
        ViewHolder holder = viewHolderList.get(mViewPager.getCurrentItem());
        if ((pictureShowList.get(mViewPager.getCurrentItem()).locationX == pictureShowList.get(index).locationX) &&
                (pictureShowList.get(mViewPager.getCurrentItem()).locationY == pictureShowList.get(index).locationY) &&
                mViewPager.getCurrentItem() != index) {
            finish();
        } else {
            holder.photoView.setOnTransformListener(new SmoothImageView.TransformListener() {
                @Override
                public void onTransformComplete(int mode) {
                    if (mode == 2) {
                        finish();
                        overridePendingTransition(0, 0);
                    }
                }
            });
            holder.photoView.transformOut();
        }
    }
}
