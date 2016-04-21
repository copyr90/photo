package com.example.tester.new_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Administrator on 2016/4/10.
 */
public class PhotoUtils{

    private static PhotoUtils sInstance = null;

    public static final Uri EXTERNAL_CONTENT_URI = FileUtils.getLocalFileUri(
            MyApplication.getInstance(), "/temp.jpg");

    private static boolean mCropPhoto = false;
    private static boolean mFullScreenCrop = false;
    private static float mAspectRatio = 1f;
    private static boolean mCompressPhoto = true;

    public PhotoUtils() {
        super();
    }

    public synchronized static PhotoUtils getInstance() {
        if (sInstance == null) {
            sInstance = new PhotoUtils();
        }
        return sInstance;
    }

    /* 多选图片 */
    public static void pickPhoto(Context context, int maxSelecteNum) {
        pickPhoto(context, false, true, 1f, maxSelecteNum);
    }

    /* cropPhoto = true 默认按照1比1的进行裁剪取单图
     * cropPhoto = false 不裁剪图片*/
    public static void pickPhoto(final Context context, boolean cropPhoto) {
        pickPhoto(context, cropPhoto, 1f);
    }

    /* 获取不压缩不裁剪的原始图片 */
    public static void pickUnCompressPhoto(Context context, boolean compressPhoto) {
        pickPhoto(context, false, compressPhoto, 1f, 0);
    }

    /* 指定裁剪比例进行取单图 */
    public static void pickPhoto(final Context context, boolean cropPhoto, float aspectRatio) {
        pickPhoto(context, cropPhoto, true, aspectRatio, 0);
    }

    /**
     * 选择图片
     *
     * @param context
     * @param cropPhoto     是否对图片进行裁剪
     * @param compressPhoto 是否对图片进行压缩处理
     * @param aspectRatio   裁剪图片的长宽比率
     * @param maxSelecteNum 是否为多选图片及多选图最多的张数
     */
    public static void pickPhoto(final Context context, boolean cropPhoto, boolean compressPhoto, float aspectRatio, final int maxSelecteNum) {
        mCropPhoto = cropPhoto;
        mAspectRatio = aspectRatio;
        mCompressPhoto = compressPhoto;
        SelectPhotoDialog dialog = new SelectPhotoDialog(context, R.style.dialogButtom);
        dialog.setOnActionListener(new SelectPhotoDialog.onActionListener() {
            @Override
            public void onAlbumClick() {
                if (maxSelecteNum != 0) { // 多选图
                    getPhotoMultipleFromAlbum(context, QuesCode.RequestCodeMultipleAlbum, maxSelecteNum);
                } else {                  // 选单图
                    getPhotoFromAlbum(context, QuesCode.RequestCodeAlbum, mCropPhoto);
                }
            }

            @Override
            public void onCarmeraClick() {
                getPhotoFromCamera(context, QuesCode.RequestCodeCamera, mCropPhoto);
            }
        });
        dialog.show();
    }

    /* 调用系统相机拍照取图 */
    public static void getPhotoFromCamera(Context context, int requestCode, boolean cropPhoto) {
        mCropPhoto = cropPhoto;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, EXTERNAL_CONTENT_URI);
        intent.putExtra("return-data", false);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /* 调用系统相册取图 */
    public static void getPhotoFromAlbum(Context context, int requestCode, boolean cropPhoto) {
        mCropPhoto = cropPhoto;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, EXTERNAL_CONTENT_URI);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /* 调起多图选择模块 */
    public static void getPhotoMultipleFromAlbum(Context context, int requestCode, int maxSelect) {
        Intent intent = new Intent(context, PhotoPickerActivity.class);
        intent.putExtra(Parameters.PARAM_PHOTO_MAX_SELECT_NUM, maxSelect);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /* 打开裁剪图片页面 */
    private static void cropImageUri(Context context, String path) {
        Intent intent = new Intent(context, CropImageActivity.class);
        intent.putExtra(CropImageActivity.IMAGE_PATH, path);
        intent.putExtra(CropImageActivity.FULL_SCREEN_CROP, mFullScreenCrop);
        intent.putExtra(CropImageActivity.ASPECTRATIO, mAspectRatio);
        ((Activity) context).startActivityForResult(intent, QuesCode.RequesCodeCropPhoto);
    }

    /**
     * 对返回图片进行处理
     *
     * @param context
     * @param data
     * @param requestCode
     * @param resultCode
     * @return
     */
    public static String handlePhotoResult(Context context, Intent data, int requestCode, int resultCode) {
        Bitmap bitmap = null;
        String path = null;
        switch (requestCode) {
            case QuesCode.RequestCodeCamera:
                if (resultCode != Activity.RESULT_OK) {
                    return null;
                }

                if (FileUtils.checkSaveLocationExists()) {
                    path = FileUtils.getPath(MyApplication.getInstance(), PhotoUtils.EXTERNAL_CONTENT_URI);
                    if (mCropPhoto) {
                        PhotoUtils.cropImageUri(context, path);
                        path = null;
                    } else {
                        path = getSavePath(path);
                        if (mCompressPhoto) {
                            bitmap = BitmapUtils.decodeSampledBitmapFromFileName(path, PhotoUtils.getRequireSize(), PhotoUtils.getRequireSize());
                            int degree = BitmapUtils.readPictureDegree(path);
                            if (bitmap != null && degree != ExifInterface.ORIENTATION_NORMAL) {
                                bitmap = BitmapUtils.setRotate(bitmap, degree, false);
                            }
                            FileUtils.writeImage(bitmap, path, 70);
                            bitmap.recycle();
                        }
                    }
                } else {
                    MyProgressHUD.showToast(context, context.getString(R.string.pick_photo_no_storage_error));
                }
                break;

            case QuesCode.RequestCodeAlbum:
                if (resultCode != Activity.RESULT_OK) {
                    return null;
                }

                path = FileUtils.getPath(MyApplication.getInstance(), data.getData());
                if (mCropPhoto) {
                    PhotoUtils.cropImageUri(context, path);
                    path = null;
                } else {
                    if (path.startsWith("http")) {
                        MyProgressHUD.showToast(context, context.getString(R.string.pick_photo_handle_webphoto_error));
                        return null;
                    } else {
                        if (BitmapUtils.isBitmapEmpty(path)) { // 判断图片是否为空
                            MyProgressHUD.showToast(context, context.getString(R.string.pick_photo_error));
                            return null;
                        }

                        path = getSavePath(path);

                        if (mCompressPhoto) {
                            bitmap = BitmapUtils.decodeSampledBitmapFromFileName(path, PhotoUtils.getRequireSize(), PhotoUtils.getRequireSize());

                            if (bitmap == null) {
                                MyProgressHUD.showToast(context, context.getString(R.string.pick_photo_error));
                                return null;
                            }

                            int degree = BitmapUtils.readPictureDegree(path);
                            if (degree != ExifInterface.ORIENTATION_NORMAL) {
                                bitmap = BitmapUtils.setRotate(bitmap, degree, false);
                            }
                            FileUtils.writeImage(bitmap, path, 70);
                            BitmapUtils.recycleBitmap(bitmap);
                        }
                    }
                }

                break;

            case QuesCode.RequesCodeCropPhoto:
                if (data == null) {
                    return null;
                }
                path = data.getStringExtra(CropImageActivity.IMAGE_PATH);
                break;
            default:
                break;
        }
        return path;
    }

    /* 复制图片，避免对原图进行裁剪 */
    public static String getSavePath(String path) {
        String finalPath = Constants.CameraImageFileDictory.concat(String.valueOf(System.currentTimeMillis()).concat(".jpg"));

        File fileOrigin = new File(path);
        if (!fileOrigin.exists())
            fileOrigin.mkdirs();

        File fileFinal = new File(finalPath);
        if (!fileFinal.exists())
            fileFinal.getParentFile().mkdirs();

        FileUtils.fileChannelCopy(fileOrigin, fileFinal);
        return finalPath;
    }

    public static int getRequireSize() {
        return MyApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.photo_crop_size);
    }

}