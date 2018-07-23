package com.benhero.fileexplorer.base;

import android.app.Application;
import android.content.Context;

import com.benhero.fileexplorer.common.crash.CrashHandler;

import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class AndroidApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().init(this);
        // initiate Timber
        Timber.plant(new DebugTree());
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }
}
