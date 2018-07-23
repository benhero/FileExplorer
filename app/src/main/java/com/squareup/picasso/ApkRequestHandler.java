package com.squareup.picasso;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.benhero.fileexplorer.common.file.FileType;
import com.benhero.fileexplorer.common.file.FileTypeUtil;

import java.io.IOException;

import static android.content.ContentResolver.SCHEME_FILE;
import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static com.squareup.picasso.Picasso.LoadedFrom.DISK;

/**
 * APK文件解析器
 *
 * @author benhero
 */
public class ApkRequestHandler extends ContentStreamRequestHandler {

    ApkRequestHandler(Context context) {
        super(context);
    }

    @Override
    public boolean canHandleRequest(Request data) {
        return SCHEME_FILE.equals(data.uri.getScheme())
                && FileTypeUtil.getType(data.uri.getPath()).equals(FileType.APK);
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        return new Result(getIcon(request.uri.getPath()), null, DISK, ORIENTATION_NORMAL);
    }

    private Bitmap getIcon(String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null) {
            return null;
        }
        ApplicationInfo appInfo = packageInfo.applicationInfo;
        appInfo.sourceDir = path;
        appInfo.publicSourceDir = path;
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) appInfo.loadIcon(pm);
            return bitmapDrawable.getBitmap();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

}
