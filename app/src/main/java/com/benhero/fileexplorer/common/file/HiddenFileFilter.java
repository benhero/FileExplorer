package com.benhero.fileexplorer.common.file;

import java.io.File;
import java.io.FileFilter;

/**
 * 隐藏文件过滤器
 *
 * @author benhero
 */
public class HiddenFileFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        return !file.isHidden();
    }
}
