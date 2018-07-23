package com.benhero.fileexplorer.function.fileexplorer;

import com.benhero.fileexplorer.base.BasePresenter;
import com.benhero.fileexplorer.base.BaseView;
import com.benhero.fileexplorer.function.fileexplorer.ui.fragment.FileExplorerFragment;

import java.io.File;
import java.util.List;

/**
 * FileExplorerContract
 *
 * @author benhero
 */
public interface FileExplorerContract {

    interface View extends BaseView<Presenter> {
        // Toolbar
        void enterPasteMode();

        void exitPasteMode();

        void enterColorTheme();

        void enterDarkTheme();

        void updateActionMode(List<File> selectedList);

        void setSubtitle(String subtitle);

        void addNavigationViewPath(String path);

        void removeNavigationViewLastPath();

        void clearNavigationViewPath();

        // 列表
        int getCurrentPosition();

        void scrollRecycleView(int position);

        void updateRecycleView(List<File> fileList);

        void openFile(File file);

        void showUnknownTypeFileClick();

        // 文件操作
        void showCreateFileDialog(final String currentPath);

        void showCreateDialogNameEmptyTips();

        void showDeleteDialog(String message);

        void showDeleteResult(boolean isDeleteSuc);

        void showRenameDialog(File file);

        void showPropertyDialog(File file);

        void showPasteFolderDialog(File src, File dest);

        void showPasteFileDialog(File src, File dest);

        void showPasteResult(boolean pasteSuc);
    }

    interface Presenter extends BasePresenter {
        boolean onBackPress();

        // Toolbar
        void onCreateActionMode();

        void onDestroyActionMode();

        void onNodeRemove(int removeCount);

        // 列表
        boolean isFileSelected(File file);

        void onAllSelectClick();

        void onItemClick(int position, FileExplorerFragment.FileExplorerRecyclerAdapter.ViewHolder holder);

        void updateSelectedState(int position, FileExplorerFragment.FileExplorerRecyclerAdapter.ViewHolder holder);

        // 文件操作
        void onFloatButtonClick();

        void onCreateDialogPositiveClick(String name, String currentPath, boolean isFile);

        void onDeleteClick();

        void onDeleteDialogPositiveClick();

        void onCopyClick();

        void onCutClick();

        void onRenameClick();

        void onRenameDialogPositiveClick(File file, String newName);

        void onOpenWithClick();

        void onAddShortCutClick();

        void onPropertyClick();

        void onPasteClick();

        void onPasteFolderPositiveClick(File src, File dest);

        void onPasteFolderNeutralClick(File src, File dest);

        void onPasteFilePositiveClick(File src, File dest);

        void onPasteFileNeutralClick(File src, File dest);

        void gotoPath(String path);
    }
}
