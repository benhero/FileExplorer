package com.benhero.fileexplorer.function.home.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.benhero.fileexplorer.R;
import com.benhero.fileexplorer.base.presentation.ui.activity.BaseActivity;
import com.benhero.fileexplorer.base.presentation.ui.fragment.BaseFragmentManager;
import com.benhero.fileexplorer.base.presentation.ui.fragment.FragmentHelper;
import com.benhero.fileexplorer.function.fileexplorer.ui.fragment.FileExplorerFragment;
import com.benhero.fileexplorer.function.fileexplorer.ui.fragment.FileExplorerFragmentManager;
import com.benhero.fileexplorer.function.home.event.ActionModeEvent;
import com.benhero.fileexplorer.function.update.FirUpdatePresenter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * 跳转路径入口
     */
    public static final String EXTRA_PATH = "extra_file_path";
    public static final int BACK_PRESS_PROTECT_TIME = 1000;
    private long mLastBackPressTime = 0;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private FileExplorerFragment mFileExplorerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        EventBus.getDefault().register(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            if (savedInstanceState == null && mFileExplorerFragment == null) {
                mFileExplorerFragment = new FileExplorerFragment();
                FragmentHelper.addFragment(MainActivity.this, R.id.main_content, mFileExplorerFragment);
            } else {
                // 界面恢复处理
                mFileExplorerFragment = (FileExplorerFragment) getFragmentManager().findFragmentById(R.id.main_content);
            }
        } else {
            new TedPermission(this)
                    .setPermissionListener(mPermissionListener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at " +
                            "[Setting] > [Permission]")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFileExplorerFragment != null) {
            FirUpdatePresenter.checkUpdate(this);
        }
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FileExplorerFragment fileExplorerFragment = new FileExplorerFragment();
                    FragmentHelper.addFragment(MainActivity.this, R.id.main_content, fileExplorerFragment);
                }
            });
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            MainActivity.this.finish();
        }
    };

    public void setToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
        setSupportActionBar(mToolbar);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mToggle.syncState();
        if (mDrawer != null) {
            mDrawer.addDrawerListener(mToggle);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActionModeEvent(ActionModeEvent event) {
        mDrawer.setDrawerLockMode(event.isInActionMode() ?
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
        // 解决进入ActionMode淡入动画恶心的问题
        if (!event.isInActionMode()) {
            findViewById(R.id.action_mode_bar).setVisibility(View.INVISIBLE);
        }
        mToolbar.setBackgroundColor(
                getColorCompat(event.isInActionMode() ?
                        R.color.action_mode_primary_color : R.color.colorPrimary));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mDrawer.removeDrawerListener(mToggle);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mFileExplorerFragment != null) {
            mFileExplorerFragment.handleIntent(intent);
        }
    }

    @Override
    protected BaseFragmentManager onCreateBaseFragmentManager() {
        return new FileExplorerFragmentManager();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackClick() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mLastBackPressTime > BACK_PRESS_PROTECT_TIME) {
            mLastBackPressTime = currentTimeMillis;
            Toast.makeText(this, R.string.home_exit_tips, Toast.LENGTH_SHORT).show();
        } else {
            super.onBackClick();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_sdcard) {
            gotoPath(Environment.getExternalStorageDirectory().getPath());
        } else if (id == R.id.nav_photo) {
            gotoPath("/DCIM/Camera");
        } else if (id == R.id.nav_video) {

        } else if (id == R.id.nav_music) {

        } else if (id == R.id.nav_doc) {

        } else if (id == R.id.nav_download) {
            gotoPath(Environment.DIRECTORY_DOWNLOADS);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void gotoPath(String path) {
        if (mFileExplorerFragment != null) {
            mFileExplorerFragment.gotoPath(path);
        }
    }
}
