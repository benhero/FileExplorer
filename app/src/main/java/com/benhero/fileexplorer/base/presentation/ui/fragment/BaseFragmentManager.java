package com.benhero.fileexplorer.base.presentation.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

/**
 * FragmentManager基类
 *
 * @author benhero
 */
public class BaseFragmentManager extends FragmentManager {

    @Override
    public FragmentTransaction beginTransaction() {
        return null;
    }

    @Override
    public boolean executePendingTransactions() {
        return false;
    }

    @Override
    public Fragment findFragmentById(int id) {
        return null;
    }

    @Override
    public Fragment findFragmentByTag(String tag) {
        return null;
    }

    @Override
    public void popBackStack() {

    }

    @Override
    public boolean popBackStackImmediate() {
        return false;
    }

    @Override
    public void popBackStack(String name, int flags) {

    }

    @Override
    public boolean popBackStackImmediate(String name, int flags) {
        return false;
    }

    @Override
    public void popBackStack(int id, int flags) {

    }

    @Override
    public boolean popBackStackImmediate(int id, int flags) {
        return false;
    }

    @Override
    public int getBackStackEntryCount() {
        return 0;
    }

    @Override
    public BackStackEntry getBackStackEntryAt(int index) {
        return null;
    }

    @Override
    public void addOnBackStackChangedListener(OnBackStackChangedListener listener) {

    }

    @Override
    public void removeOnBackStackChangedListener(OnBackStackChangedListener listener) {

    }

    @Override
    public void putFragment(Bundle bundle, String key, Fragment fragment) {

    }

    @Override
    public Fragment getFragment(Bundle bundle, String key) {
        return null;
    }

    @Override
    public List<Fragment> getFragments() {
        return null;
    }

    @Override
    public Fragment.SavedState saveFragmentInstanceState(Fragment f) {
        return null;
    }

    @Override
    public boolean isDestroyed() {
        return false;
    }

    @Override
    public void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks fragmentLifecycleCallbacks, boolean b) {

    }

    @Override
    public void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks fragmentLifecycleCallbacks) {

    }

    @Override
    public Fragment getPrimaryNavigationFragment() {
        return null;
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {

    }

    @Override
    public boolean isStateSaved() {
        return false;
    }

    public void dispatchBackEvent() {
        
    }


}
