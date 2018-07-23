package com.benhero.fileexplorer.common.file;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Locale;

/**
 * 文件类型工具类
 *
 * @author benhero
 */
@SuppressWarnings("SpellCheckingInspection")
public class FileTypeUtil {

    private static final String[] DOC = {"docx", "doc", "docm", "dotx",
            "dotm", "xls", "xlsx", "xlsm", "xltx", "xltm", "xlsb", "xlam",
            "pptx", "ppt", "pptm", "ppsx", "ppsm", "potx", "potm", "ppam",
            "pdf"};
    /**
     * TXT和DOCUMENT一样都属于文本类型，但不属于敏感类型，所以区分数组
     */
    private static final String[] TXT = {"txt", "log"};
    private static final String[] VIDEO = {"wmv", "asf", "asx", "rm", "rmvb",
            "mpg", "mpeg", "mpe", "vob", "dv", "3gp", "3g2", "mov", "avi",
            "mkv", "mp4", "m4v", "flv"};
    private static final String[] MUSIC = {"wav", "mp3", "aif", "cd", "midi",
            "wma"};
    private static final String[] APK = {"apk"};
    private static final String[] IMAGE = {"jpg", "bmp", "jpeg", "png", "gif"};
    private static final String[] COMPRESSION = {"rar", "gz", "gtar", "tar",
            "tgz", "z", "zip"};

    /**
     * 总文件后缀个数
     */
    private static final int TOTAL_FILE_TYPE = DOC.length + TXT.length
            + VIDEO.length + MUSIC.length + APK.length + IMAGE.length
            + COMPRESSION.length;

    /**
     * 所有文件类型的映射表
     */
    private static HashMap<String, FileType> sFileTypeMap = new HashMap<>(TOTAL_FILE_TYPE);


    /**
     * 将文件类型Set存入到HashMap中，再进行查找;<br>
     * HashMap的查找效率比直接遍历HashSet高，经测试有3.2倍的提速
     */
    static {
        // 消耗时间6ms，测试机型Nexus 5
        initFileTypeMap(VIDEO, FileType.VIDEO);
        initFileTypeMap(MUSIC, FileType.AUDIO);
        initFileTypeMap(DOC, FileType.DOC);
        initFileTypeMap(TXT, FileType.DOC);
        initFileTypeMap(APK, FileType.APK);
        initFileTypeMap(IMAGE, FileType.IMAGE);
        initFileTypeMap(COMPRESSION, FileType.COMPRESSION);
    }

    /**
     * 初始化所有文件的映射表
     *
     * @param array 文件后缀数组
     * @param type  文件类型
     */
    private static void initFileTypeMap(String[] array, FileType type) {
        for (String file : array) {
            sFileTypeMap.put(file, type);
        }
    }

    /**
     * 判断文件类型
     */
    public static FileType getType(String path) {
        return getFileTypeCommon(sFileTypeMap, path);
    }

    /**
     * 获取文件类型的公共方法
     *
     * @param map  查询的表
     * @param path 文件路径
     */
    private static FileType getFileTypeCommon(HashMap<String, FileType> map,
                                              String path) {
        String ext = FileUtil.getExtension(path).toLowerCase(Locale.US);
        if (TextUtils.isEmpty(ext)) {
            return FileType.OTHER;
        }
        FileType fileType = map.get(ext);
        if (fileType == null) {
            return FileType.OTHER;
        }
        return fileType;
    }

}
