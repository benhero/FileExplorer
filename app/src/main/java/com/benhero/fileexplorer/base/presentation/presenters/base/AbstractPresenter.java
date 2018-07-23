package com.benhero.fileexplorer.base.presentation.presenters.base;


import com.benhero.fileexplorer.base.domain.executor.Executor;
import com.benhero.fileexplorer.base.domain.executor.MainThread;

/**
 * This is a base class for all presenters which are communicating with interactors. This base class will hold a
 * reference to the Executor and MainThread objects that are needed for running interactors in a background thread.
 */
public abstract class AbstractPresenter {
    protected Executor   mExecutor;
    protected MainThread mMainThread;

    public AbstractPresenter(Executor executor, MainThread mainThread) {
        mExecutor = executor;
        mMainThread = mainThread;
    }
}
