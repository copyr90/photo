package com.example.tester.new_2;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/4/10.
 */
public class PhotoPreviewActivity extends BaseActivity implements View.OnClickListener {

    List<PictureShow> pictureShowList = new ArrayList<PictureShow>();
    public int index = 0;

    public MyViewPager mViewPager;
    private PhotoAdapter mAdapter;
    private TextView tvPhotoNum;

    private RelativeLayout rlHead;
    private List<ViewHolder> viewHolderList = new ArrayList<ViewHolder>();

    private AnimatorSet animationSet;

    @Override
    protected int getLayoutResourceId() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        return R.layout.activity_photo_preview;
    }

    @Override
    protected void initial() {
        initPictureData();
        initView();
        initListener();
    }

    protected void initView() {
        mViewPager = (MyViewPager) findViewById(R.id.view_pager);
        tvPhotoNum = (TextView) findViewById(R.id.tv_photo_guide);
        rlHead = (RelativeLayout) findViewById(R.id.layout);

        for (int i = 0; i < pictureShowList.size(); i++) {
            ViewHolder holder = new ViewHolder();
            if (i == index) {
                holder.firstIn = true;
            }
            viewHolderList.add(holder);
        }

        resetHeadView();
        initUnderView();
        initAdapter();
        initAnimation();
    }

    /* 重设标题栏高度 */
    private void resetHeadView() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlHead.getLayoutParams();
        layoutParams.setMargins(0, DensityUtils.getStatusBarHeight(this), 0, 0);
        rlHead.setLayoutParams(layoutParams);
    }

    /* 初始化下标 */
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
        mViewPager.setDrawingCacheEnabled(true);
        mViewPager.setCurrentItem(index);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setPhotoNum();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /* 设置底部提醒数字 */
    private void setPhotoNum() {
        if (pictureShowList.size() > 1) {
            tvPhotoNum.setText(String.format(getString(R.string.img_guide_num), mViewPager.getCurrentItem() + 1, pictureShowList.size()));
        }
    }

    private void initAnimation() {
        animationSet = new AnimatorSet();
        animationSet.setDuration(300);
        animationSet.setInterpolator(new AccelerateInterpolator());
    }

    protected void initListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_delete).setOnClickListener(this);
    }

    public void initPictureData() {
        pictureShowList = (List<PictureShow>) getIntent().getSerializableExtra(Parameters.PARAM_PICTURE_SHOW);
        index = getIntent().getIntExtra(Parameters.PARAM_START_INDEX, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.txt_delete:
                cancelEditAlert();
                break;
        }
    }

    public void cancelEditAlert() {
        getAlertDialog().setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.insure_delete_photos))
                .setRightBtn(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAlertDialog().dismissDialog();

                        PhotoInfoEvent photoInfoEvent = new PhotoInfoEvent();
                        photoInfoEvent.state = PhotoInfoEvent.DELETE;
                        PhotoInfoEvent.position = mViewPager.getCurrentItem();
                        photoInfoEvent.id = String.valueOf(pictureShowList.get(mViewPager.getCurrentItem()).getId());
                        EventBus.getDefault().post(photoInfoEvent);

                        pictureShowList.remove(mViewPager.getCurrentItem());
                        if (pictureShowList.size() == 1) {
                            tvPhotoNum.setVisibility(View.GONE);
                        }
                        if (pictureShowList.size() == 0) {
                            finish();
                        }

                        setPhotoNum();
                        mAdapter.notifyDataSetChanged();
                    }
                });
        getAlertDialog().setCanceledOnTouchOutside(false);
        getAlertDialog().showDialog(CommAlertDialog.Style.TWO_BUTTON);
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
                    if (hasShowBar)
                        hidTitleBar();
                    else
                        showTitlebar();
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


            container.addView(cell, DensityUtils.getWindowWidth(PhotoPreviewActivity.this), DensityUtils.getWindowHeight(PhotoPreviewActivity.this));

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

    public class ViewHolder {
        SmoothImageView photoView;
        MyProgress progress_bar;
        boolean firstIn = false;
    }

    private boolean hasShowBar = true;

    /* 展示标题栏 */
    private void showTitlebar() {
        if (animationSet.isRunning())
            return;
        animationSet.removeAllListeners();
        animationSet.playTogether(
                ObjectAnimator.ofFloat(rlHead, "alpha", 0, 1),
                ObjectAnimator.ofFloat(rlHead, "translationY", -rlHead.getHeight(), 0),
                ObjectAnimator.ofFloat(tvPhotoNum, "translationY", (tvPhotoNum.getHeight() + getResources().getDimension(R.dimen.dp_20)), 0)

        );
        animationSet.setStartDelay(200);
        animationSet.start();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hasShowBar = true;
    }

    /* 隐藏标题栏 */
    private void hidTitleBar() {
        if (animationSet.isRunning())
            return;
        animationSet.removeAllListeners();
        animationSet.setStartDelay(0);
        animationSet.playTogether(
                ObjectAnimator.ofFloat(rlHead, "alpha", 1, 0),
                ObjectAnimator.ofFloat(rlHead, "translationY", 0, -rlHead.getHeight()),
                ObjectAnimator.ofFloat(tvPhotoNum, "translationY", 0, (tvPhotoNum.getHeight() + getResources().getDimension(R.dimen.dp_20)))

        );
        animationSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animationSet.start();
        hasShowBar = false;
    }
}