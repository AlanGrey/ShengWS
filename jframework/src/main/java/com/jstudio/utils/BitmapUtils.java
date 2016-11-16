package com.jstudio.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

/**
 * Bitmap工具类
 * <p/>
 * Created by Jason
 */
public class BitmapUtils {

    /**
     * 根据传入的长宽，得到最佳inSimpleSize
     *
     * @param options       在调用此方法前通过BitmapFactory decode中的参数BitmapFactory.Options
     * @param requestWidth  输出宽度
     * @param requestHeight 输出高度
     * @return BitmapFactory.Options 中 inSimpleSize的值
     */
    public static int getBitmapSampleSize(BitmapFactory.Options options, int requestWidth, int requestHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > requestHeight || width > requestWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > requestHeight && (halfWidth / inSampleSize) > requestWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 解析路径类型图片得到Bitmap
     *
     * @param path          图片的路径
     * @param requestWidth  输出的宽，高宽任一为0输出原图
     * @param requestHeight 输出的高，高宽任一为0输出原图
     * @return 解析到的Bitmap对象
     */
    public static Bitmap decodeFromPath(String path, int requestWidth, int requestHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        if (requestHeight != 0 && requestWidth != 0) {
            options.inSampleSize = getBitmapSampleSize(options, requestWidth, requestHeight);
        }
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 解析FileDescriptor类型图片得到Bitmap
     *
     * @param fd            图片的FileDescriptor
     * @param rect          图片的外边距
     * @param requestWidth  输出的宽，高宽任一为0输出原图
     * @param requestHeight 输出的高，高宽任一为0输出原图
     * @return 解析到的Bitmap对象
     */
    public static Bitmap decodeFromFileDescriptor(FileDescriptor fd, Rect rect, int requestWidth, int requestHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, rect, options);

        if (requestHeight != 0 && requestWidth != 0) {
            options.inSampleSize = getBitmapSampleSize(options, requestWidth, requestHeight);
        }
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fd, rect, options);
    }

    /**
     * 解析资源类型图片得到Bitmap
     *
     * @param context       Context
     * @param resId         图片的资源id
     * @param requestWidth  输出的宽，高宽任一为0输出原图
     * @param requestHeight 输出的高，高宽任一为0输出原图
     * @return 解析到的Bitmap对象
     */
    public static Bitmap decodeFromDrawable(Context context, int resId, int requestWidth, int requestHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);

        if (requestHeight != 0 && requestWidth != 0) {
            options.inSampleSize = getBitmapSampleSize(options, requestWidth, requestHeight);
        }
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    /**
     * 解析InputStream类型图片得到Bitmap
     *
     * @param inputStream   图片的InputStream
     * @param requestWidth  输出的宽，高宽任一为0输出原图
     * @param requestHeight 输出的高，高宽任一为0输出原图
     * @return 解析到的Bitmap对象
     */
    public static Bitmap decodeInputStream(InputStream inputStream, int requestWidth, int requestHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Rect outPadding = new Rect(1, 1, 1, 1);
        BitmapFactory.decodeStream(inputStream, outPadding, options);

        if (requestHeight != 0 && requestWidth != 0) {
            options.inSampleSize = getBitmapSampleSize(options, requestWidth, requestHeight);
        }
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(inputStream, outPadding, options);
    }

    /**
     * 解析Uri类型图片得到Bitmap
     *
     * @param uri           图片的Uri
     * @param context       Context
     * @param requestWidth  输出的宽，高宽任一为0输出原图
     * @param requestHeight 输出的高，高宽任一为0输出原图
     * @return 解析到的Bitmap对象
     */
    public static Bitmap decodeFromUri(Uri uri, Context context, int requestWidth, int requestHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        String url = UriUtils.getRealPathFromURI(context, uri);
        BitmapFactory.decodeFile(url, options);
        if (requestHeight != 0 && requestWidth != 0) {
            options.inSampleSize = getBitmapSampleSize(options, requestWidth, requestHeight);
        }
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(url, options);
    }

    /**
     * 给现有的Bitmap对象提供缩略图
     *
     * @param bitmap 原图
     * @param width  缩略图的宽
     * @param height 缩略图的高
     * @return 缩略图Bitmap对象
     */
    public static Bitmap getBitmapThumbnail(Bitmap bitmap, int width, int height) {
        return ThumbnailUtils.extractThumbnail(bitmap, width, height);
    }

    /**
     * 获取指定View的屏幕截图
     *
     * @param view 准备截取的View
     * @return View的截图
     */
    public static Bitmap getScreenShotByView(View view) {
        if (view == null) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }

    /**
     * 获取整个屏幕的截图
     *
     * @param activity 要截取的Activity
     * @return Bitmap 截取到的界面
     */
    public static Bitmap getScreenShot(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        return decorView.getDrawingCache();
    }

    /**
     * 读取图片的方向
     *
     * @param path 图片路径
     * @return 返回图片的角度
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

    /**
     * 创建新的旋转过的Bitmap
     *
     * @param angle  旋转角度
     * @param bitmap 原图
     * @return Bitmap 新的旋转过的Bitmap对象
     */
    public static Bitmap rotateImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 自动旋转图片到正确的角度
     *
     * @param file 图片文件
     * @return 正确角度的Bitmap
     */
    public static Bitmap correctImgDegres(File file) {
        int degree = readPictureDegree(file.getAbsolutePath());

        BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
        opts.inSampleSize = 2;
        Bitmap temp = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

        /**
         * 把图片旋转为正的方向
         */
        return rotateImageView(degree, temp);
    }

    /**
     * 将Bitmap对象转为Base64字符串
     *
     * @param bitmap          需要转为Base64的bitmap
     * @param compressQuality Bitmap转为字节数组的压缩质量，质量最好为100
     * @return Base64字符串
     */
    public static String bitmap2Base64(Bitmap bitmap, int compressQuality) {
        String base64Str = null;
        ByteArrayOutputStream outputStream = null;
        try {
            if (bitmap != null) {
                outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream);
                byte[] bytes = outputStream.toByteArray();
                base64Str = Base64.encodeToString(bytes, Base64.NO_WRAP);
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return base64Str;
    }

    /**
     * 从Base64字符串转为Bitmap对象
     *
     * @param base64Str 需要被转换的字符串
     * @return 从字符串转来的Bitmap对象
     */
    public static Bitmap bitmapFromBase64(String base64Str) {
        if (TextUtils.isEmpty(base64Str)) {
            return null;
        } else {
            byte[] bytes = Base64.decode(base64Str, Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }



}
