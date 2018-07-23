package com.benhero.fileexplorer.common.time;

/**
 * 时间保护助手类接口
 *
 * @author benhero
 */
public interface ITimeProtectHelper {
    /**
     * 是否在保护时间之外
     */
    boolean isNeedToUpdate();
}
