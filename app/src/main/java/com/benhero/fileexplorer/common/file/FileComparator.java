package com.benhero.fileexplorer.common.file;

import java.io.File;
import java.util.Comparator;

/**
 * 文件比较器
 *
 * @author benhero
 */
public class FileComparator implements Comparator<File> {

    @Override
    public int compare(File lhs, File rhs) {
        boolean isLDirectory = lhs.isDirectory();
        boolean isRDirectory = rhs.isDirectory();
        if (isLDirectory && !isRDirectory) {
            return -1;
        } else if (!isLDirectory && isRDirectory) {
            return 1;
        }
        return compareCharacter(lhs.getName(), rhs.getName());
    }

    /**
     * 按字母排序</br>
     * 相同字母按大写在前，小写在后排序
     */
    private int compareCharacter(String l, String r) {
        int result;
        int end = l.length() < r.length() ? l.length() : r.length();
        char c1, c2;
        for (int i = 0; i < end; ++i) {
            if ((c1 = l.charAt(i)) == (c2 = r.charAt(i))) {
                // 字符相同
                continue;
            }
            // 转小写字母
            char cLower1 = foldCase(c1);
            char cLower2 = foldCase(c2);
            if ((result = cLower1 - cLower2) == 0) {
                // 若小写一致，则按从大到小排序
                return c1 - c2;
            } else {
                // 若小写不一致，则返回结果
                return result;
            }
        }
        return l.length() - r.length();
    }

    private char foldCase(char ch) {
        if (ch < 128) {
            if ('A' <= ch && ch <= 'Z') {
                // 大写字母转小写
                return (char) (ch + ('a' - 'A'));
            }
            return ch;
        }
        return Character.toLowerCase(Character.toUpperCase(ch));
    }
}
