package com.benhero.fileexplorer.common.time;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author benhero
 */
public class DateUtil {
    private static SimpleDateFormat mDetailFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String simpleFormatter(Context context, long time) {
        return DateUtils.formatDateTime(context,
                time,
                DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_YEAR);
    }

    public static String detailFormatter(long time) {
        return mDetailFormatter.format(new Date(time));
    }
}
