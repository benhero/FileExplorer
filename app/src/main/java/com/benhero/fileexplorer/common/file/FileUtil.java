package com.benhero.fileexplorer.common.file;

import java.io.File;

/**
 * 文件工具类
 *
 * @author benhero
 */
public class FileUtil {

    public static final String DOT = ".";

    /**
     * 获取文件的拓展名
     */
    public static String getExtension(String path) {
        int dotIndex = path.lastIndexOf(DOT);
        return dotIndex != -1 ? path.substring(dotIndex + 1) : "";
    }

    /**
     * 路径所指文件是否存在
     */
    public static boolean isExist(String path) {
        File file = new File(path);
        return file.exists();
    }

}
