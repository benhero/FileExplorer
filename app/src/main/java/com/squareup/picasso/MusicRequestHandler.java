package com.squareup.picasso;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.benhero.fileexplorer.common.file.FileType;
import com.benhero.fileexplorer.common.file.FileTypeUtil;

import java.io.IOException;

import static android.content.ContentResolver.SCHEME_FILE;
import static android.media.ExifInterface.ORIENTATION_NORMAL;

/**
 * 音频文件解析器
 *
 * @author benhero
 */
public class MusicRequestHandler extends RequestHandler {

    private final Context mContext;

    public MusicRequestHandler(Context context) {
        mContext = context;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        return SCHEME_FILE.equals(data.uri.getScheme())
                && FileTypeUtil.getType(data.uri.getPath()).equals(FileType.AUDIO);
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        return new Result(getImage(request), null, Picasso.LoadedFrom.DISK, ORIENTATION_NORMAL);
    }

    private Bitmap getImage(Request request) {
        Cursor currentCursor = getCursor(request.uri.getPath());
        if (currentCursor == null) {
            return null;
        }
        int albumId = 0;
        try {
            albumId = currentCursor.getInt(currentCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            currentCursor.close();
        }
        if (albumId == 0) {
            return null;
        }
        String albumArt = getAlbumArt(albumId);
        if (TextUtils.isEmpty(albumArt)) {
            return null;
        } else {
            return ImageDecodeUtil.decodeSampledBitmapFromResource(albumArt,
                    request.targetWidth, request.targetHeight);
        }
    }

    private Cursor getCursor(String filePath) {
        String path;
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                path = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                if (path.equals(filePath)) {
                    break;
                }
            } while (cursor.moveToNext());
        }
        return cursor;
    }

    private String getAlbumArt(int albumId) {
        Cursor cur = mContext.getContentResolver().query(
                Uri.parse(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI + "/"
                        + Integer.toString(albumId)),
                new String[]{"album_art"}, null, null, null);
        String album_art = null;
        if (cur != null) {
            if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
                cur.moveToNext();
                album_art = cur.getString(0);
            }
            cur.close();
        }
        return album_art;
    }
}
