package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;

/**
 * 图片解析工具类
 *
 * @author benhero
 */
public class ImageDecodeUtil {

    /**
     * 解析图片
     *
     * @param pathName  图片路径
     * @param reqWidth  显示的宽度
     * @param reqHeight 显示的高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        float rotate = 0;
        try {
            File imageFile = new File(pathName);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 调用上面定义的方法计算inSampleSize值
        if (rotate == 90 || rotate == 270) {
            options.inSampleSize = calculateInSampleSize(options, reqHeight,
                    reqWidth);
        } else {
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
        }
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;

        try {
            // 防止爆内存
            Bitmap srcBitmap = BitmapFactory.decodeFile(pathName, options);
            if (rotate == 0) {
                return srcBitmap;
            } else {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                        srcBitmap.getHeight(), matrix, true);
            }
        } catch (OutOfMemoryError e) {
            for (int i = 0; i < 5; i++) {
                // 当爆内存时，使用逐步减少加载尺寸的方式尝试去
                try {
                    return oomDecode(options, pathName, rotate, 3);
                } catch (OutOfMemoryError ignore) {

                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 爆内存时，尝试缩小图片加载尺寸再次加载的方式
     * @param scaleSize 缩小倍数
     */
    private static Bitmap oomDecode(BitmapFactory.Options options, String pathName, float rotate, int scaleSize) {
        options.inSampleSize *= scaleSize;
        // 防止爆内存
        Bitmap srcBitmap = BitmapFactory.decodeFile(pathName, options);
        if (rotate == 0) {
            return srcBitmap;
        } else {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }
}
