package com.benhero.fileexplorer.common.window;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * WindowUtil
 *
 * @author benhero
 */
public class WindowUtil {

    /**
     * 是否支持彩色通知栏
     */
    public static boolean isSupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 设置状态栏颜色
     */
    public static void setStatusBarColor(Activity activity, int color) {
        if (isSupport()) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    /**
     * 设置导航栏颜色
     */
    public static void setNavigationBarColor(Activity activity, int color) {
        if (isSupport()) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(color);
        }
    }

    /**
     * 设置状态栏和导航栏的颜色
     */
    public static void setSystemBarColor(Activity activity, int color) {
        if (isSupport()) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
            window.setNavigationBarColor(color);
        }
    }

}
