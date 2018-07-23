package com.benhero.fileexplorer.common.time;

/**
 * 时间保护助手类
 *
 * @author benhero
 */
public class TimeProtectHelper implements ITimeProtectHelper {
    private long mLastUpdateTime;
    private final long mProtectDuration;

    public TimeProtectHelper(long protectDuration) {
        mProtectDuration = protectDuration;
    }

    @Override
    public boolean isNeedToUpdate() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastUpdateTime > mProtectDuration) {
            mLastUpdateTime = curTime;
            return true;
        }
        return false;
    }
}
