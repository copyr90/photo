package com.example.tester.new_2;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Administrator on 2016/4/10.
 */
public class FileUtils {
    public final static DecimalFormat ONE_DECIMAL_POINT_DF = new DecimalFormat("0.0");
    private final static int ONE_GIGABYTE = 1024 * 1024 * 1024;
    private final static int ONE_MEGABYTE = 1024 * 1024;
    private final static int ONE_KILOBYTE = 1024;

    private final static Map<String, Lock> LOCK_MAP = new HashMap<String, Lock>(0);
    private final static String IMAGE_DIR_NAME = "/ta_images/";

    private static Lock getLock(String path) {
        synchronized (LOCK_MAP) {
            Lock lock = LOCK_MAP.get(path);

            if (lock == null) {
                lock = new ReentrantLock();
                LOCK_MAP.put(path, lock);
            }

            return lock;
        }
    }

    public static String formatSizeInByte(long sizeInByte) {
        if (sizeInByte >= ONE_GIGABYTE)
            return ONE_DECIMAL_POINT_DF.format((double) sizeInByte / ONE_GIGABYTE) + "G";

        else if (sizeInByte >= ONE_MEGABYTE)
            return ONE_DECIMAL_POINT_DF.format((double) sizeInByte / ONE_MEGABYTE) + "M";

        else if (sizeInByte >= ONE_KILOBYTE)
            return ONE_DECIMAL_POINT_DF.format((double) sizeInByte / ONE_KILOBYTE) + "K";

        else
            return sizeInByte + "B";
    }

    /**
     * 采用递归方式
     * 获取某文件夹目录下所有文件大小
     *
     * @param directory 文件夹根目录
     * @return 文件大小(单位为bytes)
     */
    public static double getFolderSize(File directory) {
        double size = 0;

        for (File file : directory.listFiles()) {
            if (file.isFile())
                size += file.length();

            else
                size += getFolderSize(file);
        }

        return size;
    }

    public static boolean deleteDirectory(File file) {
        //File file = new File(path);
        boolean success = false;

        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();

                for (File subFile : files) {
                    if (subFile.isDirectory())
                        deleteDirectory(subFile);

                    else
                        subFile.delete();
                }
            }

            success = file.delete();
        }

        return success;
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void deleteFiles(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFiles = file.listFiles();

                if (childFiles == null || childFiles.length == 0) {
                    file.delete();
                    return;
                }
                for (File objFile : childFiles) {
                    deleteFiles(objFile);
                }

                file.delete();
            }
        }
    }

    /**
     * 删除指定路径的文件
     *
     * @param path
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.isFile())
            file.delete();
    }

    /**
     * 删除本项目的所有文件
     */
    public static void deleteDir() {
        File dir = new File(Config.getAppDataPath());
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    /**
     * 新建文件夹
     *
     * @param dirName
     * @return
     * @throws IOException
     */
    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(Config.getAppDataPath() + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

//    public static boolean deleteFiles(String file) {
//        ShellCommandUtils cm = new ShellCommandUtils();
//        return cm.sh.run("rm -r " + file) != null;
//    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 获取崩溃日志e的目录
     *
     * @param ctx Context
     * @return 目录File对象
     **/
    public static File getCrashLogDir(Context ctx) {
        File dir = new File(ctx.getExternalCacheDir(), "crash");
        dir.mkdirs();
        return dir;
    }

    public static int saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String dirPath = Environment.getExternalStorageDirectory() + IMAGE_DIR_NAME;
        File appDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIR_NAME);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        String destFileName = dirPath + fileName;

        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        updateGalleryPhoto(context, destFileName);

        return 0;
    }

    private static void updateGalleryPhoto(Context ctx, String imgFileName) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }

    public static String getCacheImagePath(Context con, String imageId) {
        File file = con.getExternalCacheDir();
        if (file == null) {
            file = new File(con.getFilesDir(), "cache");
        }

        if (!file.exists()) {
            file.mkdir();
        }

        File imageDir = new File(file.getPath(), "image");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }

        return imageDir.getPath() + "/" + imageId;
    }

    public static int copyFileData(String srcFilePath, String destFilePath) {
        int i = srcFilePath.indexOf("file://");
        if (i > -1) {
            srcFilePath = srcFilePath.substring(i + 7);
        }

        try {
            FileInputStream inStream = new FileInputStream(srcFilePath);
            FileOutputStream outStream = new FileOutputStream(destFilePath);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return -1;
        } catch (IOException e) {
            e.printStackTrace();

            return -1;
        }

        return 0;
    }

    public static byte[] getFileData(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 判断传进的路径的文件夹是否存在
     *
     * @param path
     * @return
     */
    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    /**
     * 文件转化为字节数组
     */
    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

//    /**
//     * 把byte数组转换为文件
//     *
//     * @param buff
//     * @return
//     * @author jwang
//     * @date 2013.4.24
//     */
//    public static boolean byteArrayToFile(byte[] buff, String destPath) {
//        OutputStream os = null;
//        try {
//            File file = new File(destPath);
//            if (file.exists()) {
//                file.delete();
//            }
//            os = new BufferedOutputStream(new FileOutputStream(file));
//            os.write(buff);
//            os.flush();
//            return true;
//        } catch (IOException ex) {
//            L.w(ex);
//        } finally {
//            try {
//                os.close();
//            } catch (IOException ex) {
//                L.w(ex);
//            }
//        }
//        return false;
//    }

    public static InputStream getInputStreamFromFilePath(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream inStream = new FileInputStream(file);
                return inStream;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void unzip(String zipName, String dir) throws IOException {
        File dest = new File(dir);
        if (!dest.exists()) {
            dest.mkdirs();
        }

        String zipFile = dir + zipName + ".zip";

        if (!dest.isDirectory())
            throw new IOException("Invalid Unzip destination " + dest);

        ZipInputStream zip = new ZipInputStream(getInputStreamFromFilePath(zipFile));

        ZipEntry ze;
        while ((ze = zip.getNextEntry()) != null) {
            final String path = dest.getAbsolutePath()
                    + File.separator + ze.getName();

            String zeName = ze.getName();
            char cTail = zeName.charAt(zeName.length() - 1);
            if (cTail == File.separatorChar) {
                File file = new File(path);
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        throw new IOException("Unable to create folder " + file);
                    }
                }
                continue;
            }

            FileOutputStream fout = new FileOutputStream(path);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = zip.read(bytes)) != -1) {
                fout.write(bytes, 0, c);
            }
            zip.closeEntry();
            fout.close();
        }
    }

    public static Uri getLocalFileUri(Context con, String path) {
        File file = new File(getLocalFilePath(con, path));
        return Uri.fromFile(file);
    }

    public static String getLocalFilePath(Context con, String path) {
        Uri uri = Uri.parse(path);
        String fileName = uri.getLastPathSegment();
        File file = con.getExternalCacheDir();
        if (file == null) {
            file = new File(con.getFilesDir(), "cache");
        }

        file.mkdir();
        String dirPath = file.getPath();
        return dirPath + "/" + fileName;
    }

    /**
     * 检查是否安装SD卡
     *
     * @return
     */
    public static boolean checkSaveLocationExists() {
        String sDCardStatus = Environment.getExternalStorageState();
        boolean status;
        status = sDCardStatus.equals(Environment.MEDIA_MOUNTED);
        return status;
    }

    public static void writeImage(Bitmap bitmap, String destPath, int quality) {
        try {
            FileUtils.deleteFile_2(destPath);
            if (FileUtils.createFile_2(destPath)) {
                FileOutputStream out = new FileOutputStream(destPath);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                    out.flush();
                    out.close();
                    out = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteFile_2(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建一个文件，创建成功返回true
     *
     * @param filePath
     * @return
     */
    public static boolean createFile_2(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                return file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 使用文件通道的方式复制文件
     *
     * @param s 源文件
     * @param t 复制到的新文件
     */
    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}