package com.benhero.fileexplorer.common.file;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.benhero.fileexplorer.base.AndroidApplication;

import java.io.File;

/**
 * FileIntentUtil
 *
 * @author benhero
 */
public class FileIntentUtil {

    public static Intent openFile(String filePath) {
        String end = FileUtil.getExtension(filePath);
        switch (end) {
            case "m4a":
            case "mp3":
            case "mid":
            case "xmf":
            case "ogg":
            case "wav":
            case "3gp":
                return getAudioFileIntent(filePath);
            case "mp4":
            case "avi":
            case "rmvb":
            case "rm":
                return getVideoFileIntent(filePath);
            case "jpg":
            case "gif":
            case "png":
            case "jpeg":
            case "bmp":
                return getImageFileIntent(filePath);
            case "apk":
                return getApkFileIntent(filePath);
            case "ppt":
                return getPptFileIntent(filePath);
            case "xls":
                return getExcelFileIntent(filePath);
            case "doc":
                return getWordFileIntent(filePath);
            case "pdf":
                return getPdfFileIntent(filePath);
            case "chm":
                return getChmFileIntent(filePath);
            case "txt":
                return getTextFileIntent(filePath);
            default:
                return getAllIntent(filePath);
        }
    }

    /**
     * 获取指定类型的Intent
     *
     * @param path 路径
     * @param type 类型参数
     */
    private static Intent getIntentByType(String path, String type) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(AndroidApplication.getContext(),
                    AndroidApplication.getContext().getPackageName() + ".file_provider", file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    public static Intent getAllIntent(String path) {
        return getIntentByType(path, "*/*");
    }

    /**
     * 获取一个用于打开APK文件的intent
     */
    public static Intent getApkFileIntent(String path) {
        return getIntentByType(path, "application/vnd.android.package-archive");
    }

    /**
     * 获取一个用于打开VIDEO文件的intent
     */
    public static Intent getVideoFileIntent(String path) {
        Intent intent = getIntentByType(path, "video/*");
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        return intent;
    }

    /**
     * 获取一个用于打开AUDIO文件的intent
     */
    public static Intent getAudioFileIntent(String path) {
        Intent intent = getIntentByType(path, "audio/*");
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        return intent;
    }

    /**
     * 获取一个用于打开图片文件的intent
     */
    public static Intent getImageFileIntent(String path) {
        return getIntentByType(path, "image/*");
    }

    /**
     * 获取一个用于打开PPT文件的intent
     */
    public static Intent getPptFileIntent(String path) {
        return getIntentByType(path, "application/vnd.ms-powerpoint");
    }

    /**
     * 获取一个用于打开Excel文件的intent
     */
    public static Intent getExcelFileIntent(String path) {
        return getIntentByType(path, "application/vnd.ms-excel");
    }

    /**
     * 获取一个用于打开Word文件的intent
     */
    public static Intent getWordFileIntent(String path) {
        return getIntentByType(path, "application/msword");
    }

    /**
     * 获取一个用于打开CHM文件的intent
     */
    public static Intent getChmFileIntent(String path) {
        return getIntentByType(path, "application/x-chm");
    }

    /**
     * 获取一个用于打开文本文件的intent
     */
    public static Intent getTextFileIntent(String path) {
        return getIntentByType(path, "text/plain");
    }

    /**
     * 获取一个用于打开PDF文件的intent
     */
    public static Intent getPdfFileIntent(String path) {
        return getIntentByType(path, "application/pdf");
    }
}
