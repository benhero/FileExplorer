package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import com.benhero.fileexplorer.common.file.FileType;
import com.benhero.fileexplorer.common.file.FileTypeUtil;

import java.io.IOException;

import static android.content.ContentResolver.SCHEME_FILE;
import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static com.squareup.picasso.Picasso.LoadedFrom.DISK;

/**
 * 视频文件解析器
 *
 * @author benhero
 */
public class VideoRequestHandler extends ContentStreamRequestHandler {

    VideoRequestHandler(Context context) {
        super(context);
    }

    @Override
    public boolean canHandleRequest(Request data) {
        return SCHEME_FILE.equals(data.uri.getScheme())
                && FileTypeUtil.getType(data.uri.getPath()).equals(FileType.VIDEO);
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        return new Result(getVideoThumbnail(request.uri), null, DISK, ORIENTATION_NORMAL);
    }

    public Bitmap getVideoThumbnail(Uri uri) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            bitmap = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        } catch (RuntimeException | OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
