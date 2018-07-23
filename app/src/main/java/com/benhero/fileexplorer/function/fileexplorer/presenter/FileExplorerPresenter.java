package com.benhero.fileexplorer.function.fileexplorer.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.benhero.fileexplorer.R;
import com.benhero.fileexplorer.base.presentation.ui.recyclerview.RecyclerViewScrollNode;
import com.benhero.fileexplorer.common.file.FileComparator;
import com.benhero.fileexplorer.common.file.FileUtil;
import com.benhero.fileexplorer.common.file.HiddenFileFilter;
import com.benhero.fileexplorer.common.io.FileUtils;
import com.benhero.fileexplorer.function.fileexplorer.FileExplorerContract;
import com.benhero.fileexplorer.function.fileexplorer.ui.fragment.FileExplorerFragment;
import com.benhero.fileexplorer.function.home.event.ActionModeEvent;
import com.benhero.fileexplorer.function.home.ui.activity.MainActivity;
import com.benhero.fileexplorer.util.ShortcutUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 文件管理器Presenter
 *
 * @author benhero
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileExplorerPresenter implements FileExplorerContract.Presenter {

    private Context mContext;
    private FileExplorerContract.View mView;
    private File mCurFile;
    private ArrayList<File> mFileList = new ArrayList<>();
    private ArrayList<File> mSelectedList = new ArrayList<>();
    private HiddenFileFilter mHiddenFileFilter = new HiddenFileFilter();
    private FileComparator mFileComparator = new FileComparator();
    private RecyclerViewScrollNode mScrollNode = new RecyclerViewScrollNode();
    private PasteAction mPasteAction = new PasteAction();
    private String mSDPath;


    public FileExplorerPresenter(FileExplorerContract.View view, Context context) {
        mView = view;
        mContext = context;
        mView.setPresenter(this);
        initData();
    }

    @Override
    public void start() {
        updateFileList();
    }

    private void initData() {
        mSDPath = Environment.getExternalStorageDirectory().getPath();
        mCurFile = new File(mSDPath);
        updateNavigationView();
        updateFileList();
    }

    private void updateNavigationView() {
        if (!mCurFile.exists()) {
            return;
        }
        mView.clearNavigationViewPath();
        String curPath = mCurFile.getAbsolutePath().replaceAll(mSDPath, "sdcard");
        String[] split = curPath.split(File.separator);
        for (String s : split) {
            mView.addNavigationViewPath(s);
        }
    }

    private void updateFileList() {
        mFileList.clear();
        try {
            File[] listFiles = mCurFile.listFiles(mHiddenFileFilter);
            mFileList.addAll(Arrays.asList(listFiles));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(mFileList, mFileComparator);
        mView.updateRecycleView(mFileList);
        onCurFolderChanged();
    }

    private void onCurFolderChanged() {
        int folderSize = 0;
        int fileSize = 0;
        for (File file : mFileList) {
            if (file.isFile()) {
                fileSize++;
            } else if (file.isDirectory()) {
                folderSize++;
            }
        }

        String folderString = mContext.getString(R.string.toolbar_subtitle_folder);
        String fileString = mContext.getString(R.string.toolbar_subtitle_file);
        if (folderSize > 0 && fileSize > 0) {
            mView.setSubtitle(folderSize + folderString
                    + mContext.getString(R.string.toolbar_subtitle_divider)
                    + fileSize + fileString);
        } else if (folderSize > 0) {
            mView.setSubtitle(folderSize + folderString);
        } else if (fileSize > 0) {
            mView.setSubtitle(fileSize + fileString);
        } else {
            mView.setSubtitle(mContext.getString(R.string.empty_folder));
        }
    }


    @Override
    public boolean onBackPress() {
        if (mCurFile == null) {
            return false;
        }
        if (!mSelectedList.isEmpty()) {
            mSelectedList.clear();
            mView.enterColorTheme();
            mView.updateRecycleView(mFileList);
            return true;
        } else if (Environment.getExternalStorageDirectory().getPath().equals(mCurFile.getPath())) {
            return false;
        } else {
            mView.removeNavigationViewLastPath();
            mCurFile = mCurFile.getParentFile();
            updateFileList();
            mScrollNode = mScrollNode.getParent();
            mView.scrollRecycleView(mScrollNode.getPosition());
            return true;
        }
    }

    /**
     * 粘贴动作
     */
    private static class PasteAction {
        List<File> mPasteList = new ArrayList<>();
        boolean mIsCutMode = false;
    }

    @Override
    public void gotoPath(String path) {
        if (TextUtils.isEmpty(path)) {
            // 检测路径合法性
            return;
        }
        // 以下对传入的路径进行适配性支持
        if (FileUtil.isExist(path)) {
            // 完整路径
            setCurFile(path);
            return;
        }
        File directory = Environment.getExternalStorageDirectory();
        if (FileUtil.isExist(directory.getPath() + path)) {
            // 不带SD卡路径
            setCurFile(directory.getPath() + path);
            return;
        }
        if (FileUtil.isExist(directory.getPath() + File.separator + path)) {
            // 不带SD卡路径，且不含路径分隔符
            setCurFile(directory.getPath() + File.separator + path);
        }
    }

    private void setCurFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            mCurFile = file;
        } else {
            mCurFile = file.getParentFile();
            mView.openFile(file);
        }
        updateNavigationView();
        updateFileList();
    }

    //*************************************************** Toolbar ******************************************************//

    @Override
    public void onCreateActionMode() {
        EventBus.getDefault().post(new ActionModeEvent(true));
    }

    @Override
    public void onDestroyActionMode() {
        EventBus.getDefault().post(new ActionModeEvent(false));
        mSelectedList.clear();
        mView.updateRecycleView(mFileList);
    }

    @Override
    public void onNodeRemove(int removeCount) {
        for (int i = 0; i < removeCount; i++) {
            mView.removeNavigationViewLastPath();
            mCurFile = mCurFile.getParentFile();
            mScrollNode = mScrollNode.getParent();
        }
        updateFileList();
        mView.scrollRecycleView(mScrollNode.getPosition());
    }

    //*************************************************** 列表 ******************************************************//

    @Override
    public boolean isFileSelected(File file) {
        return mSelectedList.contains(file);
    }

    @Override
    public void onAllSelectClick() {
        if (mSelectedList.size() == mFileList.size()) {
            mSelectedList.clear();
            mView.enterColorTheme();
        } else {
            mSelectedList.addAll(mFileList);
        }
        mView.updateRecycleView(mFileList);
    }

    @Override
    public void onItemClick(int position, FileExplorerFragment.FileExplorerRecyclerAdapter.ViewHolder holder) {
        if (mSelectedList.isEmpty()) {
            handlerItemClick(position);
        } else {
            updateSelectedState(position, holder);
        }
    }

    private void handlerItemClick(int position) {
        File clickItem = mFileList.get(position);
        if (clickItem.isDirectory()) {
            clickFolder(clickItem);
        } else if (clickItem.isFile()) {
            mView.openFile(clickItem);
        } else {
            mView.showUnknownTypeFileClick();
        }
    }

    private void clickFolder(File file) {
        mScrollNode.setPosition(mView.getCurrentPosition());
        RecyclerViewScrollNode node = new RecyclerViewScrollNode();
        node.setParent(mScrollNode);
        mScrollNode = node;
        mCurFile = file;
        mView.addNavigationViewPath(mCurFile.getName());
        updateFileList();
    }

    @Override
    public void updateSelectedState(int position, FileExplorerFragment.FileExplorerRecyclerAdapter.ViewHolder holder) {
        File file = mFileList.get(position);
        if (mSelectedList.contains(file)) {
            // 取消选中
            mSelectedList.remove(file);
            holder.itemView.setSelected(false);
            if (mSelectedList.isEmpty()) {
                mView.enterColorTheme();
            } else {
                mView.updateActionMode(mSelectedList);
            }
        } else {
            // 选中
            if (mSelectedList.isEmpty()) {
                mView.enterDarkTheme();
            }
            mSelectedList.add(file);
            mView.updateActionMode(mSelectedList);
            holder.itemView.setSelected(true);
        }
    }

    //*************************************************** 文件操作 ******************************************************//

    @Override
    public void onFloatButtonClick() {
        mView.showCreateFileDialog(mCurFile.getPath());
    }

    @Override
    public void onCreateDialogPositiveClick(String name, String currentPath, boolean isFile) {
        if (TextUtils.isEmpty(name)) {
            mView.showCreateDialogNameEmptyTips();
        } else {
            File file = new File(currentPath + File.separator + name);
            if (isFile) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                file.mkdir();
            }
            updateFileList();
        }
    }

    @Override
    public void onDeleteClick() {
        StringBuilder builder = new StringBuilder();
        builder.append(mContext.getString(R.string.delete_snack_tips)).append("\n\n");
        for (int i = 0; i < mSelectedList.size(); i++) {
            builder.append(i + 1).append(". ").append(mSelectedList.get(i).getName()).append("\n");
        }
        mView.showDeleteDialog(builder.toString());
    }

    @Override
    public void onDeleteDialogPositiveClick() {
        deleteSelectedList();
        mView.enterColorTheme();
    }

    /**
     * 删除选中的文件列表
     */
    @SuppressWarnings("unchecked")
    private void deleteSelectedList() {
        boolean isDeleteSuc = true;
        List<File> deleteList = (List<File>) mSelectedList.clone();
        for (File file : deleteList) {
            try {
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    FileUtils.forceDelete(file);
                }
            } catch (IOException e) {
                isDeleteSuc = false;
                e.printStackTrace();
            }
        }
        updateFileList();
        mView.showDeleteResult(isDeleteSuc);
    }

    @Override
    public void onCopyClick() {
        mPasteAction.mPasteList.clear();
        mPasteAction.mPasteList.addAll(mSelectedList);
        mPasteAction.mIsCutMode = false;
        mView.enterColorTheme();
        mView.enterPasteMode();
    }

    @Override
    public void onCutClick() {
        mPasteAction.mPasteList.clear();
        mPasteAction.mPasteList.addAll(mSelectedList);
        mPasteAction.mIsCutMode = true;
        mView.enterColorTheme();
        mView.enterPasteMode();
    }

    @Override
    public void onRenameClick() {
        if (mSelectedList.isEmpty()) {
            return;
        }
        mView.showRenameDialog(mSelectedList.get(0));
    }

    @Override
    public void onRenameDialogPositiveClick(File file, String newName) {
        renameSelectedFile(file, newName);
        updateFileList();
        mView.enterColorTheme();
    }

    /**
     * 重命名选中的文件
     */
    private void renameSelectedFile(File selectedFile, String name) {
        File renameFile = new File(selectedFile.getParentFile().getPath() + File.separator + name);
        selectedFile.renameTo(renameFile);
    }

    @Override
    public void onOpenWithClick() {
        if (mSelectedList.size() != 1) {
            return;
        }
        mView.openFile(mSelectedList.get(0));
    }

    @Override
    public void onAddShortCutClick() {
        File selectedFile;
        if (mSelectedList.size() == 1) {
            selectedFile = mSelectedList.get(0);
        } else if (mSelectedList.size() == 0) {
            selectedFile = mCurFile;
        } else {
            return;
        }
        Intent shortcutIntent = new Intent(mContext.getApplicationContext(), MainActivity.class);
        shortcutIntent.putExtra(MainActivity.EXTRA_PATH, selectedFile.getPath());
        mContext.sendBroadcast(
                ShortcutUtil.getShortCutIntent(
                        mContext,
                        selectedFile.getName(),
                        selectedFile.isFile() ? R.drawable.ic_shortcut_file : R.drawable.ic_shortcut_folder,
                        shortcutIntent));
        mView.enterColorTheme();
        Toast.makeText(mContext, R.string.add_shortcut_suc, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPropertyClick() {
        if (mSelectedList.isEmpty()) {
            return;
        }
        mView.showPropertyDialog(mSelectedList.get(0));
    }

    /**
     * 删除粘贴板的文件列表
     */
    @Override
    public void onPasteClick() {
        boolean pasteSuc = true;
        boolean updateImmediate = true;
        for (File file : mPasteAction.mPasteList) {
            try {
                File dest = new File(mCurFile.getPath() + File.separator + file.getName());
                if (file.isDirectory()) {
                    updateImmediate &= pasteFolder(file, dest);
                } else {
                    updateImmediate &= pasteFile(file, dest);
                }
            } catch (IOException e) {
                e.printStackTrace();
                pasteSuc = false;
            }
        }
        mPasteAction.mPasteList.clear();
        if (updateImmediate) {
            onPasteFinish(pasteSuc);
        }
    }

    private void onPasteFinish(boolean pasteSuc) {
        mView.showPasteResult(pasteSuc);
        updateFileList();
        mView.exitPasteMode();
    }

    /**
     * 粘贴文件夹
     *
     * @return 是否直接拷贝
     */
    private boolean pasteFolder(File src, File dest) throws IOException {
        if (dest.exists()) {
            mView.showPasteFolderDialog(src, dest);
            return false;
        } else {
            FileUtils.copyDirectory(src, dest);
            if (mPasteAction.mIsCutMode) {
                FileUtils.deleteDirectory(src);
            }
            return true;
        }
    }

    @Override
    public void onPasteFolderPositiveClick(File src, File dest) {
        boolean pasteSuc = true;
        try {
            FileUtils.copyDirectory(src, getFolderNameNotDup(dest));
            if (mPasteAction.mIsCutMode) {
                FileUtils.deleteDirectory(src);
            }
        } catch (IOException e) {
            e.printStackTrace();
            pasteSuc = false;
        }
        onPasteFinish(pasteSuc);
    }

    @Override
    public void onPasteFolderNeutralClick(File src, File dest) {
        boolean pasteSuc = true;
        try {
            if (src.equals(dest)) {
                pasteSuc = false;
            } else {
                FileUtils.deleteDirectory(dest);
                FileUtils.copyDirectory(src, getFolderNameNotDup(dest));
                if (mPasteAction.mIsCutMode) {
                    FileUtils.deleteDirectory(src);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            pasteSuc = false;
        }
        onPasteFinish(pasteSuc);
    }

    /**
     * 获取名字不重复的文件夹
     *
     * @param dest 目标文件
     */
    private File getFolderNameNotDup(File dest) {
        String srcName = dest.getName();
        String parentPath = dest.getParent();
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            File file = new File(parentPath + File.separator + srcName + "_" + i);
            if (!file.exists()) {
                return file;
            }
        }
        return dest;
    }

    @Override
    public void onPasteFilePositiveClick(File src, File dest) {
        boolean pasteSuc = true;
        try {
            FileUtils.copyFile(src, getFileNameNotDup(dest));
            if (mPasteAction.mIsCutMode) {
                FileUtils.forceDelete(src);
            }
        } catch (IOException e) {
            e.printStackTrace();
            pasteSuc = false;
        }
        onPasteFinish(pasteSuc);
    }

    /**
     * 获取名字不重复的文件
     *
     * @param dest 目标文件
     */
    private File getFileNameNotDup(File dest) {
        String srcName = dest.getName();
        String parentPath = dest.getParent();
        int dotIndex = srcName.lastIndexOf(".");
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            String newName;
            if (dotIndex != -1) {
                newName = srcName.substring(0, dotIndex) + "_" + i + srcName.substring(dotIndex, srcName.length());
            } else {
                newName = srcName + "_" + i;
            }
            File file = new File(parentPath + File.separator + newName);
            if (!file.exists()) {
                return file;
            }
        }
        return dest;
    }

    @Override
    public void onPasteFileNeutralClick(File src, File dest) {
        boolean pasteSuc = true;
        try {
            if (src.equals(dest)) {
                pasteSuc = false;
            } else {
                FileUtils.forceDelete(dest);
                FileUtils.copyFile(src, dest);
                if (mPasteAction.mIsCutMode) {
                    FileUtils.forceDelete(src);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            pasteSuc = false;
        }
        onPasteFinish(pasteSuc);
    }

    /**
     * 粘贴文件
     *
     * @return 是否直接拷贝
     */
    private boolean pasteFile(File src, File dest) throws IOException {
        if (dest.exists()) {
            mView.showPasteFileDialog(src, dest);
            return false;
        } else {
            FileUtils.copyFile(src, dest);
            if (mPasteAction.mIsCutMode) {
                FileUtils.forceDelete(src);
            }
            return true;
        }
    }

}
