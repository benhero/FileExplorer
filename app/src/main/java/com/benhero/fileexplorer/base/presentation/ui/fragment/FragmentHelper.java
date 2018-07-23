package com.benhero.fileexplorer.base.presentation.ui.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.benhero.fileexplorer.R;
import com.benhero.fileexplorer.base.presentation.ui.activity.BaseActivity;

/**
 * Fragment管理助手类
 *
 * @author benhero
 */
public class FragmentHelper {
    public static final String CUR_FRAGMENT = "cur";

    public static void addCommonFragment(BaseActivity activity, BaseFragment fragment) {
        activity.setContentView(R.layout.common_fragment_layout);
        FragmentManager manager = new BaseFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.common_fragment_layout_root, fragment);
        transaction.commitAllowingStateLoss();
    }

    public static void addFragment(BaseActivity activity, int id, BaseFragment fragment) {
        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
        transaction.add(id, fragment, CUR_FRAGMENT);
        transaction.commitAllowingStateLoss();
    }

    public static void replaceFragment(BaseActivity activity, int id, BaseFragment fragment) {
        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
        transaction.replace(id, fragment, CUR_FRAGMENT);
        transaction.commitAllowingStateLoss();
    }

}
