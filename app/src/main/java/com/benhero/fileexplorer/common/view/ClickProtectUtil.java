package com.benhero.fileexplorer.common.view;

import android.util.SparseArray;
import android.view.View;

/**
 * 点击保护工具类
 *
 * @author benhero
 */
public class ClickProtectUtil {
    private static SparseArray<Long> sClickTimeArray = new SparseArray<>();
    private static long sProtectTime = 500;

    public static long getProtectTime() {
        return sProtectTime;
    }

    public static void setProtectTime(long protectTime) {
        sProtectTime = protectTime;
    }

    public static boolean isToClick(View view) {
        int id = view.getId();
        Long lastTime = sClickTimeArray.get(id);
        long now = System.currentTimeMillis();
        if (lastTime == null || now - lastTime > sProtectTime) {
            sClickTimeArray.append(id, now);
            return true;
        }
        return false;
    }
}
