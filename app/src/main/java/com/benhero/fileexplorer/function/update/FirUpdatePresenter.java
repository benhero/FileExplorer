package com.benhero.fileexplorer.function.update;

import android.app.Activity;
import android.os.Environment;

import com.sunfusheng.FirUpdater;

import java.io.File;

/**
 * Fir.im版本升级处理器
 *
 * @author Benhero
 * @date 2018/7/24
 */
public class FirUpdatePresenter {
    private static final String API_TOKEN = "e21b7440a6e5d9ee12f30fe5e02f7801";
    private static final String APP_ID = "5b56e569ca87a82d80540afc";

    /**
     * 检测升级
     */
    public static void checkUpdate(Activity activity) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        new FirUpdater(activity, API_TOKEN, APP_ID)
                .apkPath(path + File.separator)
                .checkVersion();
    }

}
