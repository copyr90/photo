package com.example.tester.new_2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.TypedValue;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/4/10.
 */
public class BitmapUtils {
    private static final int MAX_HEIGHT = 1280;
    private static final int MAX_WIDTH = 720;

    //拉伸图片（不按比例）以填充View的宽高
    public static final int SCALE_TYPE_FIT_XY = 0;

    //按比例拉伸图片，拉伸后图片的高度为View的高度，且显示在View的左边
    public static final int SCALE_TYPE_FIT_START = 1;

    //按比例拉升，拉伸后图片的宽度为View的宽度
    public static final int SCALE_TYPE_FIT_Y = 2;

//    public static final int REQUIRE_SIZE = 1024;

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public synchronized static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Note: this method may return null
     *
     * @param fileName
     * @param reqWidth
     * @param reqHeight
     * @return a bitmap decoded from the specified file
     */
    public synchronized static Bitmap decodeSampledBitmapFromFileName(String fileName, int reqWidth, int reqHeight) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, options);
//            Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            L.d(">>>> decodeSampledBitmapFromFileName inSampleSize: " + options.inSampleSize);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(fileName, options);

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Note: this method may return null
     *
     * @param inputStream
     * @param reqWidth
     * @param reqHeight
     * @return a bitmap decoded from the specified file
     */
    public synchronized static Bitmap decodeSampledBitmapFromStream(InputStream inputStream, int reqWidth, int reqHeight) throws IOException {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream buffer = new BufferedInputStream(inputStream);
            buffer.mark(inputStream.available());
            BitmapFactory.decodeStream(buffer, null, options);
            buffer.reset();
            buffer.close();

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            L.d(">>>> decodeSampledBitmapFromFileName inSampleSize: " + options.inSampleSize);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            return bitmap;

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return null;
    }

    //    private static Paint ROUND_CORNER_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    public synchronized static Bitmap createRoundCornerBitmap(Bitmap srcBitmap, float roundPixel, int reqWidth, int reqHeight, boolean recycleOrig) {
        if (reqWidth == 0) reqWidth = srcBitmap.getWidth();

        if (reqHeight == 0) reqHeight = srcBitmap.getHeight();

        final Rect srcRect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
        final Rect dstRect = new Rect(0, 0, reqWidth, reqHeight);

        L.d(">>>> createRoundCornerBitmap: " + srcBitmap.getWidth() + ", " + srcBitmap.getHeight());

        Bitmap bmp = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        Paint roundCornerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        ROUND_CORNER_PAINT.reset();
        canvas.drawRoundRect(new RectF(dstRect), roundPixel, roundPixel, roundCornerPaint);
        roundCornerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, srcRect, dstRect, roundCornerPaint);

        if (recycleOrig && srcBitmap != null && !srcBitmap.isRecycled())
            srcBitmap.recycle();

        return bmp;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, int cornerDips, int borderDips, Context context, boolean recycleOrig) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
                context.getResources().getDisplayMetrics());
        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
                context.getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        // draw border
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        if (recycleOrig && bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();

        return output;
    }

    /**
     * create scaled bitmap with required width and height
     *
     * @param srcBitmap
     * @param reqWidth
     * @param reqHeight
     * @param recycleOrig
     * @param scaleType
     * @return
     */
    public synchronized static Bitmap createBitmap(Bitmap srcBitmap, int reqWidth, int reqHeight, boolean recycleOrig, int scaleType) {
        int bitmapWidth = srcBitmap.getWidth();
        int bitmapHeight = srcBitmap.getHeight();
        if (reqWidth == 0) reqWidth = bitmapWidth;
        if (reqHeight == 0) reqHeight = bitmapHeight;

//        final Rect srcRect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
//        final Rect dstRect = new Rect(0, 0, reqWidth, reqHeight);
        float scaleWidth = 1;
        float scaleHeight = 1;
        if (scaleType == SCALE_TYPE_FIT_START) {
            scaleWidth = (reqWidth / bitmapWidth < reqHeight
                    / bitmapHeight) ? (float) reqWidth / (float) bitmapWidth : (float) reqHeight
                    / (float) bitmapHeight;
            scaleHeight = scaleWidth;
        } else if (scaleType == SCALE_TYPE_FIT_XY) {
            scaleWidth = (float) reqWidth / (float) bitmapWidth;
            scaleHeight = (float) reqHeight / (float) bitmapHeight;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);

        if (recycleOrig) {
            srcBitmap.recycle();
        }

        return resizedBitmap;
    }

    //    private static Paint MASK_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    public synchronized static Bitmap createMaskedBitmap(Bitmap srcBitmap, int brightness, boolean recycleOrig) {
        Bitmap bmp = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        L.d("createMaskedBitmap: " + srcBitmap.getWidth() + ", " + srcBitmap.getHeight());

        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,// 改变亮度
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0
        });
        Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
        canvas.drawBitmap(srcBitmap, 0, 0, maskPaint);

        if (recycleOrig && srcBitmap != null && !srcBitmap.isRecycled())
            srcBitmap.recycle();

        return bmp;
    }

    public synchronized static Bitmap createRoundCornerAndMaskedBitmap(Bitmap srcBitmap, float roundPixel, int brightness, int reqWidth, int reqHeight, boolean recycleOrig) {
        try {
            if (reqWidth == 0) reqWidth = srcBitmap.getWidth();

            if (reqHeight == 0) reqHeight = srcBitmap.getHeight();

            final Rect srcRect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
            final Rect dstRect = new Rect(0, 0, reqWidth, reqHeight);

            L.d(">>>> createRoundCornerAndMaskedBitmap: " + srcBitmap.getWidth() + ", " + srcBitmap.getHeight());

            Bitmap bmp = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Paint roundCornerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            canvas.drawRoundRect(new RectF(dstRect), roundPixel, roundPixel, roundCornerPaint);
            roundCornerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(srcBitmap, srcRect, dstRect, roundCornerPaint);
            ColorMatrix cMatrix = new ColorMatrix();
            cMatrix.set(new float[]{1, 0, 0, 0, brightness,
                    0, 1, 0, 0, brightness,// 改变亮度
                    0, 0, 1, 0, brightness,
                    0, 0, 0, 1, 0
            });
            Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            maskPaint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
            canvas.drawBitmap(bmp, 0, 0, maskPaint);

            if (recycleOrig && srcBitmap != null && !srcBitmap.isRecycled())
                srcBitmap.recycle();

            return bmp;

        } catch (OutOfMemoryError e) {
            L.e("createRoundCornerAndMaskedBitmap :" + e);
            return srcBitmap;
        }
    }

    /**
     * 获取图片文件的信息，是否旋转了90度，如果是则反转
     *
     * @param bitmap 需要旋转的图片
     * @param path   图片的路径
     */
    public static Bitmap reviewPicRotate(Bitmap bitmap, String path) {
        int degree = readPictureDegree(path);
        if (degree != 0) {
            Matrix m = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            m.setRotate(degree); // 旋转angle度
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
        }
        return bitmap;
    }

    /**
     * 读取照片exif信息中的旋转角度<br>http://www.eoeandroid.com/thread-196978-1-1.html
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public synchronized static Bitmap setRotate(Bitmap bitmap, int degree, boolean recycleOrig) {
        try {
            Matrix matrix = new Matrix();
            //设置图像的旋转角度
            matrix.setRotate(degree);
            //旋转图像，并生成新的Bitmap对像
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            if (recycleOrig && bitmap != null && !bitmap.isRecycled())
                bitmap.recycle();

            return newBitmap;

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Bitmap 放大
     *
     * @param rect          Display对象
     * @param bitmap        图片
     * @param isResizeWidth true按宽度拉，false按高度拉
     * @return Bitmap
     */
    public synchronized static Bitmap resizeBitmap(Rect rect, Bitmap bitmap, boolean isResizeWidth, boolean recycleOrig) {
        int screenWidth = rect.width();
        int screenHeight = rect.height();

        try {
            if (isResizeWidth) {
                if (screenWidth != bitmap.getWidth()) {
                    float scale = (float) screenWidth / bitmap.getWidth();
                    int height = (int) (bitmap.getHeight() * scale);
                    Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, height, true);

                    if (recycleOrig && !bitmap.isRecycled()) bitmap.recycle();

                    bitmap = newBitmap;
                }

                // 按宽度拉伸后，高度超出screenHeight，所以要把超出的部分截取掉
                if (bitmap.getHeight() > screenHeight) {
                    int startY = (bitmap.getHeight() - screenHeight) / 2;
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, startY, screenWidth, screenHeight);

                    if (recycleOrig && !bitmap.isRecycled()) bitmap.recycle();

                    bitmap = newBitmap;
                }

            } else {
                if (screenHeight != bitmap.getHeight()) {  //
                    float scale = (float) screenHeight / bitmap.getHeight();
                    int width = (int) (bitmap.getWidth() * scale);

                    if (width > screenWidth) { //如果拉伸完宽度大于屏幕宽度，则按宽度拉伸
                        float widthScale = (float) screenWidth / bitmap.getWidth();
                        int height = (int) (bitmap.getHeight() * widthScale);
                        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, height, true);

                        if (recycleOrig && widthScale != 1 && !bitmap.isRecycled())
                            bitmap.recycle();

                        bitmap = newBitmap;

                    } else {
                        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width, screenHeight, true);

                        if (recycleOrig && !bitmap.isRecycled()) bitmap.recycle();

                        bitmap = newBitmap;
                    }
                }
            }

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static byte[] convertBitmapToByteArray(Bitmap image) {
        if (image == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 80;
        while (baos.toByteArray().length / 1024 > 1024 && options >= 80) {//循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 10;//每次都减少10
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        byte[] bytes = baos.toByteArray();
        IOUtils.closeCloseable(baos);
        return bytes;
    }

    //压缩图片大小
    public static Bitmap compressImage(Bitmap image) {
        if (image == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 80;
        while (baos.toByteArray().length / 1024 > 1024 && options >= 80) {//循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(bais, null, null);//把ByteArrayInputStream数据生成图片
        IOUtils.closeCloseable(bais);
        IOUtils.closeCloseable(baos);
        return bitmap;
    }

    // 强压图片至5kb
    public static Bitmap compressImageToSmallest(Bitmap image) {
        if (image == null) {
            return null;
        }
        int options = 50;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        while (baos.toByteArray().length / 1024 > 5 && options >= 50) {//循环判断如果压缩后图片是否大于5kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.PNG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(bais, null, null);//把ByteArrayInputStream数据生成图片
        IOUtils.closeCloseable(bais);
        IOUtils.closeCloseable(baos);
        return bitmap;
    }

    /*采用以下方法把图片压缩小至100KB，防止OOM*/
    public static Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }

    /* 通过Uri获取图片 */
    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /* 把Bitmap写进文件 */
    public static void writeBitmapToFile(String fileName, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap
    {
        int width = drawable.getIntrinsicWidth();// 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable,int width,int height) // drawable 转换成bitmap
    {
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }

    /* 将Bitmap转化为流 */
    public static InputStream Bitmap2IS(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        return sbs;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public static byte[] getBitmapByteArrayFromPath( String absolutePath) {
        Bitmap bitmap = null;
        byte[] bytes = null;

        File file = new File(absolutePath);
        if (null != file) {
            bitmap = getImageThumbnail(absolutePath, MAX_HEIGHT, MAX_WIDTH); // 根据限定最大尺寸进行b比特率压缩
            bitmap = reviewPicRotate(bitmap, absolutePath);
            bytes = convertBitmapToByteArrayByOptions(bitmap, 70);           // 根据70%压缩图片
            recycleBitmap(bitmap);
        }

        return bytes;
    }

    /* 根据文件路径转化成字符流 */
    public static String getBitmapStringFromPath(String absolutePath){
        Bitmap bitmap = null;
        String bitmapStrings = "";
        File file = new File(absolutePath);
        if (file != null && file.exists()){
            bitmap = getImageThumbnail(absolutePath, MAX_HEIGHT, MAX_WIDTH); // 根据限定最大尺寸进行b比特率压缩
            bitmap = reviewPicRotate(bitmap, absolutePath);
            bitmapStrings = bitmapToString(bitmap,70);
            recycleBitmap(bitmap);
        }
        return bitmapStrings;
    }

    private static Bitmap getImageThumbnail(String filePath, int imgHeight, int imgWidth) {
        Bitmap bitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(filePath, options);
        // 计算图片缩放比例
        final int minSideLength = Math.min(imgWidth, imgHeight);
        options.inSampleSize = computeSampleSize(options, minSideLength,
                imgWidth * imgHeight);

        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        try {
            bitmap = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError error) {
            ImageLoader.getInstance().clearMemoryCache();
            System.gc();

            bitmap = BitmapFactory.decodeFile(filePath, options);
        } finally {
            return bitmap;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /* 按照options %压缩质量 */
    public static byte[] convertBitmapToByteArrayByOptions(Bitmap image, int options) {
        if (image == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        byte[] bytes = baos.toByteArray();
        IOUtils.closeCloseable(baos);
        return bytes;
    }

    /* 根据Bitmap的option判断是否为空 */
    public static boolean isBitmapEmpty(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options.outHeight <= 0 || options.outWidth <= 0;
    }

    // 把bitmap转换成String
    public static String bitmapToString(Bitmap bm,int rate) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, rate, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
