package com.benhero.fileexplorer.util;

import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Parcelable;


/**
 * 快捷方式工具类
 *
 * @author benhero
 */
public class ShortcutUtil {
    // 安装快捷方式
    private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    /**
     * 创建快捷方式
     *
     * @param shortcutName 图标名
     * @param iconId       图标资源
     * @param cls          打开的Activity类名
     */
    public static void sendShortcut(Context context, String shortcutName,
                                    int iconId, Class<?> cls) {
        context.sendBroadcast(getShortCutIntent(context, shortcutName, iconId, cls));
    }

    /**
     * 获取创建快捷方式的Intent
     *
     * @param shortcutName 图标名
     * @param iconId       图标资源
     * @param cls          打开的Activity类名
     */
    public static Intent getShortCutIntent(Context context, String shortcutName,
                                           int iconId, Class<?> cls) {
        return getShortCutIntent(context, shortcutName, iconId, new Intent(context.getApplicationContext(), cls));
    }

    /**
     * 获取创建快捷方式的Intent
     *
     * @param shortcutName   图标名
     * @param iconId         图标资源
     * @param shortcutIntent 快捷方式触发事件
     */
    public static Intent getShortCutIntent(Context context, String shortcutName,
                                           int iconId, Intent shortcutIntent) {
        Intent intent = new Intent(ACTION_INSTALL_SHORTCUT);
        // 不允许重复创建
        intent.putExtra("duplicate", false);
        // 需要现实的名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                shortcutName);
        // 快捷图片
        Parcelable icon = ShortcutIconResource.fromContext(
                context.getApplicationContext(), iconId);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 点击快捷图片，运行的程序主入口
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        return intent;
    }
}
