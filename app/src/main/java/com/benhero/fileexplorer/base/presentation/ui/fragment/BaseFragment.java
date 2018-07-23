package com.benhero.fileexplorer.base.presentation.ui.fragment;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

/**
 * 基类Fragment
 *
 * @author benhero
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * 处理返回事件
     *
     * @return 当前是否处理返回事件
     */
    public boolean onBackPress() {
        return false;
    }

    /**
     * 获取颜色值
     */
    public int getColorCompat(@ColorRes int id) throws Resources.NotFoundException {
        return ContextCompat.getColor(getActivity(), id);
    }
}
