package com.benhero.fileexplorer.function.home.event;

/**
 * ActionMode事件
 *
 * @author benhero
 */
public class ActionModeEvent {
    private boolean mIsInActionMode;

    public ActionModeEvent(boolean isInActionMode) {
        mIsInActionMode = isInActionMode;
    }

    public boolean isInActionMode() {
        return mIsInActionMode;
    }

    public void setIsInActionMode(boolean isInActionMode) {
        mIsInActionMode = isInActionMode;
    }
}
