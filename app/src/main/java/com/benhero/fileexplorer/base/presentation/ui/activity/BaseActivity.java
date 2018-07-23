package com.benhero.fileexplorer.base.presentation.ui.activity;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.benhero.fileexplorer.base.presentation.ui.fragment.BaseFragment;
import com.benhero.fileexplorer.base.presentation.ui.fragment.BaseFragmentManager;
import com.benhero.fileexplorer.base.presentation.ui.fragment.FragmentHelper;

/**
 * 基类Activity
 *
 * @author benhero
 */
public abstract class BaseActivity<T extends BaseFragmentManager> extends AppCompatActivity {
    private T mBaseFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseFragmentManager = onCreateBaseFragmentManager();
    }

    @Override
    public void onBackPressed() {
        boolean handlePress = false;
        Fragment fragment = getFragmentManager().findFragmentByTag(FragmentHelper.CUR_FRAGMENT);
        if (fragment instanceof BaseFragment) {
            handlePress = ((BaseFragment) fragment).onBackPress();
        }
        if (!handlePress) {
            onBackClick();
        }
    }

    public void onBackClick() {
        super.onBackPressed();
    }

    /**
     * 获取颜色值
     */
    public int getColorCompat(@ColorRes int id) throws Resources.NotFoundException {
        return ContextCompat.getColor(this, id);
    }

    protected abstract T onCreateBaseFragmentManager();

    public T getBaseFragmentManager() {
        return mBaseFragmentManager;
    }

}
