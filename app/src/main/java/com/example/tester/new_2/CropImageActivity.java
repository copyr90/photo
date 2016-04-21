package com.example.tester.new_2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.view.View;

import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/4/10.
 */
public class CropImageActivity extends MonitoredActivity implements View.OnClickListener{

    public static final String FULL_SCREEN_CROP = "fullscreen-crop";
    public static final String IMAGE_PATH = "image-path";
    public static final String RETURN_DATA = "return-data";
    public static final String RETURN_DATA_AS_BITMAP = "data";
    public static final String ACTION_INLINE_DATA = "inline-data";
    public static final String ORIENTATION_IN_DEGREES = "orientation_in_degrees";
    public static final String ASPECTRATIO = "aspectRatio";   // 长宽比

    final int IMAGE_MAX_SIZE = 1024;

    private ClipImageView imageView;
    private ClipView clipView;

    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    private String mImagePath;
    private Uri mSaveUri = null;
    private Bitmap mBitmap;

    boolean mSaving;
    private boolean mCropFullScreen = false;
    private float mAspectRatio = 1f;

    private final Handler mHandler = new Handler();

    ContentResolver mContentResolver;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_crop;
    }

    @Override
    protected void initial() {
        showStorageToast(this);
        mContentResolver = getContentResolver();
        initView();
        initListener();
        initData();
    }

    protected void initView() {
        imageView = (ClipImageView) findViewById(R.id.src_pic);
        clipView = (ClipView) findViewById(R.id.clipview);
    }

    protected void initListener() {
        findViewById(R.id.btn_discard).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
    }

    protected void initData() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mImagePath = extras.getString(IMAGE_PATH);
            mCropFullScreen = extras.getBoolean(FULL_SCREEN_CROP);
            File file = new File(mImagePath);

            mSaveUri = getSaveImageUri(file.getName());

            mAspectRatio = extras.getFloat(ASPECTRATIO, 1f);

            imageView.setClipfullSceen(mCropFullScreen);
            if(mCropFullScreen){
                findViewById(R.id.clipview).setVisibility(View.GONE);
                findViewById(R.id.rl_toolbar).setBackgroundColor(getResources().getColor(R.color.transparent_30));
            }
            try {
                mBitmap = getBitmap(mImagePath);
                //处理旋转
                mBitmap = BitmapUtils.setRotate(mBitmap, BitmapUtils.readPictureDegree(mImagePath), false);
            }catch (OutOfMemoryError e){
                MyApplication.getInstance().onLowMemory();
                MyProgressHUD.showToast(this, getString(R.string.crop_cache_limit_waring));
                onBackPressed();
            }
        }

        if (mBitmap == null) {
            finish();
            return;
        }else {
            imageView.setImageBitmap(mBitmap);
        }

        clipView.setAspectRatio(mAspectRatio);
        imageView.setAspectRatio(mAspectRatio);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_discard:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_save:
                try {
                    onSaveClicked();
                } catch (Exception e) {
                    finish();
                }
                break;
        }
    }

    private Uri getSaveImageUri(String fileName) {
        File file = new File(Constants.CropImageFileDictory);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                L.d("getSaveImageUri", "Create file fail");
            }
        }
        String path = Constants.CropImageFileDictory.concat(String.valueOf(System.currentTimeMillis()).concat(fileName));
        return Uri.fromFile(new File(path));
    }

    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    private Bitmap getBitmap(String path) {

        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = mContentResolver.openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;
        } catch (FileNotFoundException e) {
            L.d("error","file " + path + " not found");
        } catch (IOException e) {
            L.d("error","file " + path + " not found");
        }
        return null;
    }

    private void onSaveClicked() throws Exception {
        // TODO this code needs to change to use the decode/crop/encode single
        // step api so that we don't require that the whole (possibly large)
        // bitmap doesn't have to be read into memory
        if (mSaving) return;

        mSaving = true;

        Bitmap croppedImage;
        try {
            croppedImage = imageView.clip();
        } catch (Exception e) {
            throw e;
        }
        if (croppedImage == null) {

            return;
        }

        // Return the cropped image directly or save it to the specified URI.
        Bundle myExtras = getIntent().getExtras();
        if (myExtras != null && (myExtras.getParcelable("data") != null
                || myExtras.getBoolean(RETURN_DATA))) {

            Bundle extras = new Bundle();
            extras.putParcelable(RETURN_DATA_AS_BITMAP, croppedImage);
            extras.putBoolean(FULL_SCREEN_CROP,mCropFullScreen);
            setResult(RESULT_OK,
                    (new Intent()).setAction(ACTION_INLINE_DATA).putExtras(extras));
            finish();
        } else {
            final Bitmap b = croppedImage;
            CropImageUtil.startBackgroundJob(this, null, getString(R.string.crop_image_saving_image),
                    new Runnable() {
                        public void run() {

                            saveOutput(b);
                        }
                    }, mHandler);
        }
    }

    private void saveOutput(Bitmap croppedImage) {

        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 70, outputStream);
                }
            } catch (IOException ex) {

                L.d("Cannot open file: " + mSaveUri, ex.toString());
                setResult(RESULT_CANCELED);
                finish();
                return;
            } finally {

                CropImageUtil.closeSilently(outputStream);
            }

            Bundle extras = new Bundle();
            Intent intent = new Intent(mSaveUri.toString());
            intent.putExtras(extras);
            intent.putExtra(FULL_SCREEN_CROP,mCropFullScreen);
            intent.putExtra(IMAGE_PATH, mSaveUri.getPath());
            intent.putExtra(ORIENTATION_IN_DEGREES, CropImageUtil.getOrientationInDegree(this));
            setResult(RESULT_OK, intent);
        } else {
            L.d("error","not defined image url");
        }
        croppedImage.recycle();
        finish();
    }

    public static final int NO_STORAGE_ERROR = -1;
    public static final int CANNOT_STAT_ERROR = -2;

    public static void showStorageToast(Activity activity) {
        showStorageToast(activity, calculatePicturesRemaining(activity));
    }

    public static void showStorageToast(Activity activity, int remaining) {
        String noStorageText = null;

        if (remaining == NO_STORAGE_ERROR) {
            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_CHECKING)) {
                noStorageText = activity.getString(R.string.crop_image_preparing_card);
            } else {
                noStorageText = activity.getString(R.string.crop_image_no_storage_card);
            }
        } else if (remaining < 1) {
            noStorageText = activity.getString(R.string.crop_image_not_enough_space);
        }
        if (noStorageText != null) {
            MyProgressHUD.showToast(activity,noStorageText);
        }
    }

    public static int calculatePicturesRemaining(Activity activity) {

        try {
            /*if (!ImageManager.hasStorage()) {
                return NO_STORAGE_ERROR;
            } else {*/
            String storageDirectory = "";
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                storageDirectory = Environment.getExternalStorageDirectory().toString();
            } else {
                storageDirectory = activity.getFilesDir().toString();
            }
            StatFs stat = new StatFs(storageDirectory);
            float remaining = ((float) stat.getAvailableBlocks()
                    * (float) stat.getBlockSize()) / 400000F;
            return (int) remaining;
            //}
        } catch (Exception ex) {
            // if we can't stat the filesystem then we don't know how many
            // pictures are remaining.  it might be zero but just leave it
            // blank since we really don't know.
            return CANNOT_STAT_ERROR;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }
}