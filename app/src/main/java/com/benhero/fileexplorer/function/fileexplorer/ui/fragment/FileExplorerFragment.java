package com.benhero.fileexplorer.function.fileexplorer.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.benhero.fileexplorer.R;
import com.benhero.fileexplorer.base.presentation.ui.fragment.BaseFragment;
import com.benhero.fileexplorer.common.file.FileIntentUtil;
import com.benhero.fileexplorer.common.file.FileType;
import com.benhero.fileexplorer.common.file.FileTypeUtil;
import com.benhero.fileexplorer.common.file.FileUtil;
import com.benhero.fileexplorer.common.time.DateUtil;
import com.benhero.fileexplorer.common.view.ClickProtectUtil;
import com.benhero.fileexplorer.common.view.CropCircleTransformation;
import com.benhero.fileexplorer.common.view.DividerItemDecoration;
import com.benhero.fileexplorer.common.view.PathNavigationView;
import com.benhero.fileexplorer.common.window.WindowUtil;
import com.benhero.fileexplorer.function.fileexplorer.FileExplorerContract;
import com.benhero.fileexplorer.function.fileexplorer.presenter.FileExplorerPresenter;
import com.benhero.fileexplorer.function.home.ui.activity.MainActivity;
import com.benhero.fileexplorer.function.home.ui.activity.SettingsActivity;
import com.jayfeng.lesscode.core.DisplayLess;
import com.squareup.picasso.Picasso;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件浏览器Fragment
 *
 * @author benhero
 */
public class FileExplorerFragment extends BaseFragment
        implements PathNavigationView.PathNodeViewListener, View.OnClickListener, FileExplorerContract.View {
    private RecyclerView mRecyclerView;
    private FileExplorerRecyclerAdapter mAdapter;
    private FileExplorerContract.Presenter mPresenter;

    private LinearLayoutManager mLayoutManager;
    private PathNavigationView mNavigationView;
    private ActionMode mActionMode;
    private FloatingActionButton mFloatButton;
    private View mNavigationLayout;
    private Toolbar mToolbar;

    private boolean mIsInPasteMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new FileExplorerRecyclerAdapter();
    }

    @Override
    public void setPresenter(FileExplorerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_explorer_fragment, container, false);

        mNavigationLayout = view.findViewById(R.id.file_exp_path_navigation_layout);
        mNavigationView = (PathNavigationView) view.findViewById(R.id.file_exp_path_navigation);
        mNavigationView.setNodeClickListener(this);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setSubtitleTextAppearance(getActivity(), R.style.AppTheme_PopupOverlay_Subtitle);
        ((MainActivity) getActivity()).setToolbar(mToolbar);
        setHasOptionsMenu(true);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.file_exp_recycler_view);
        mFloatButton = (FloatingActionButton) view.findViewById(R.id.file_exp_fab);
        mFloatButton.setOnClickListener(this);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerViewListener());
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration decor = new DividerItemDecoration();
        decor.setSize(DisplayLess.$dp2px(1));
        mRecyclerView.addItemDecoration(decor);

        ((DragScrollBar) view.findViewById(R.id.file_exp_scroll_bar))
                .setIndicator(new AlphabetIndicator(this.getActivity()), true);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new FileExplorerPresenter(this, getActivity());
        handleIntent(getActivity().getIntent());
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.clearOnScrollListeners();
    }

    @Override
    public boolean onBackPress() {
        return mPresenter.onBackPress();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home, menu);
        menu.findItem(R.id.action_paste).setVisible(mIsInPasteMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_paste:
                mPresenter.onPasteClick();
                return true;
            case R.id.action_add_shortcut:
                mPresenter.onAddShortCutClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mFloatButton)) {
            mPresenter.onFloatButtonClick();
        }
    }

    /**
     * 适配器
     */
    public class FileExplorerRecyclerAdapter extends
            RecyclerView.Adapter<FileExplorerRecyclerAdapter.ViewHolder> implements INameableAdapter {
        private ArrayList<File> mFileList = new ArrayList<>();

        @Override
        public Character getCharacterForElement(int element) {
            // 体验优化处理：避免所指示的Item刚好超出屏幕时才显示游标，所以尽可能将列表可见的第二项作为游标的首位
            // 引起Bug：列表的第二位不会展示游标
            if (element >= mFileList.size()) {
                return '?';
            }
            File indexFile;
            if (element == 0 || element == mFileList.size() - 1) {
                // 首位和末位都显示本身
                indexFile = mFileList.get(element);
            } else {
                // 其他的显示下一个位置的值
                indexFile = mFileList.get(element + 1);
            }
            return indexFile.getName().charAt(0);
        }

        /**
         * ViewHolder
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
            }

            View mRoot;
            ImageView mIcon;
            TextView mName;
            TextView mTime;
            TextView mSize;

            @Override
            public String toString() {
                return "ViewHolder{" +
                        "mRoot=" + mRoot +
                        ", mIcon=" + mIcon +
                        ", mName=" + mName +
                        ", mTime=" + mTime +
                        ", mSize=" + mSize +
                        '}';
            }
        }

        private void setFileList(List<File> fileList) {
            mFileList.clear();
            mFileList.addAll(fileList);
        }

        public void updateFileList(List<File> fileList) {
            setFileList(fileList);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.file_list_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            holder.mIcon = (ImageView) view.findViewById(R.id.file_list_item_icon);
            holder.mName = (TextView) view.findViewById(R.id.file_list_item_file_name);
            holder.mTime = (TextView) view.findViewById(R.id.file_list_item_time);
            holder.mSize = (TextView) view.findViewById(R.id.file_list_item_size);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            File file = mFileList.get(position);
            holder.mName.setText(file.getName());

            String time = DateUtil.simpleFormatter(getActivity(), file.lastModified());
            holder.mTime.setText(time);
            if (file.isFile()) {
                holder.mIcon.setColorFilter(null);
                holder.mSize.setText(formatSize(file.length()));
                FileType type = FileTypeUtil.getType(file.getPath());
                int iconSize = (int) (getResources().getDimension(R.dimen.list_icon_size));
                if (type.equals(FileType.IMAGE)) {
                    Picasso.with(getActivity()).load(file)
                            .resize(iconSize, iconSize)
                            .placeholder(R.drawable.ic_image)
                            .error(R.drawable.ic_broken_image)
                            .centerCrop()
                            .transform(new CropCircleTransformation())
                            .into(holder.mIcon);
                } else if (type.equals(FileType.AUDIO)) {
                    Picasso.with(getActivity()).load(file)
                            .resize(iconSize, iconSize)
                            .placeholder(R.drawable.ic_music)
                            .error(R.drawable.ic_broken_image)
                            .centerCrop()
                            .transform(new CropCircleTransformation())
                            .into(holder.mIcon);
                } else if (type.equals(FileType.VIDEO)) {
                    Picasso.with(getActivity()).load(file)
                            .resize(iconSize, iconSize)
                            .placeholder(R.drawable.ic_video)
                            .error(R.drawable.ic_broken_image)
                            .centerCrop()
                            .transform(new CropCircleTransformation())
                            .into(holder.mIcon);
                } else if (type.equals(FileType.APK)) {
                    Picasso.with(getActivity()).load(file)
                            .resize(iconSize, iconSize)
                            .placeholder(R.drawable.ic_android)
                            .error(R.drawable.ic_broken_image)
                            .centerCrop()
                            .into(holder.mIcon);
                } else if (type.equals(FileType.DOC)) {
                    holder.mIcon.setImageResource(R.drawable.ic_document);
                } else {
                    holder.mIcon.setImageResource(R.drawable.ic_file);
                }
            } else {
                holder.mIcon.setColorFilter(getColorCompat(R.color.colorPrimary));
                holder.mIcon.setImageResource(R.drawable.ic_folder);
            }
            holder.mSize.setVisibility(file.isFile() ? View.VISIBLE : View.INVISIBLE);
            holder.itemView.setSelected(mPresenter.isFileSelected(file));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onItemClick(holder.getAdapterPosition(), holder);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mPresenter.updateSelectedState(holder.getAdapterPosition(), holder);
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFileList.size();
        }
    }

    private String formatSize(long size) {
        return Formatter.formatFileSize(getActivity().getApplicationContext(), size);
    }

    public void handleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        String path = extras.getString(MainActivity.EXTRA_PATH);
        gotoPath(path);
    }

    public void gotoPath(String path) {
        mPresenter.gotoPath(path);
    }

    //*************************************************** Toolbar ******************************************************//
    @Override
    public void enterPasteMode() {
        mIsInPasteMode = true;
        ((AppCompatActivity) getActivity()).supportInvalidateOptionsMenu();
    }

    @Override
    public void exitPasteMode() {
        mIsInPasteMode = false;
        ((AppCompatActivity) getActivity()).supportInvalidateOptionsMenu();
    }

    @Override
    public void enterColorTheme() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public void enterDarkTheme() {
        final Activity activity = getActivity();
        if (!(activity instanceof AppCompatActivity)) {
            return;
        }
        final AppCompatActivity compatActivity = (AppCompatActivity) activity;
        mActionMode = compatActivity.startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mPresenter.onCreateActionMode();
                mNavigationLayout.setBackgroundColor(getColorCompat(R.color.action_mode_primary_color));
                WindowUtil.setSystemBarColor(activity, getColorCompat(R.color.action_mode_primary_color));
                menu.hasVisibleItems();
                mode.setTitleOptionalHint(false);
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.contextual, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.copy:
                        mPresenter.onCopyClick();
                        break;
                    case R.id.delete:
                        mPresenter.onDeleteClick();
                        break;
                    case R.id.all_select:
                        mPresenter.onAllSelectClick();
                        break;
                    case R.id.cut:
                        mPresenter.onCutClick();
                        break;
                    case R.id.rename:
                        mPresenter.onRenameClick();
                        break;
                    case R.id.open_with:
                        mPresenter.onOpenWithClick();
                        break;
                    case R.id.add_shortcut:
                        mPresenter.onAddShortCutClick();
                        break;
                    case R.id.property:
                        mPresenter.onPropertyClick();
                        break;
                    default:
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mPresenter.onDestroyActionMode();
                mNavigationLayout.setBackgroundColor(getColorCompat(R.color.colorPrimary));
                WindowUtil.setSystemBarColor(activity, getColorCompat(R.color.colorPrimaryDark));
            }
        });
    }

    /**
     * 更新ActionMode状态
     */
    @Override
    public void updateActionMode(List<File> selectedList) {
        if (mActionMode == null) {
            return;
        }
        mActionMode.setTitle(selectedList.size() + "");
        mActionMode.getMenu().findItem(R.id.rename).setVisible(selectedList.size() == 1);
        mActionMode.getMenu().findItem(R.id.add_shortcut).setVisible(selectedList.size() == 1);
        mActionMode.getMenu().findItem(R.id.property).setVisible(selectedList.size() == 1);
        mActionMode.getMenu().findItem(R.id.open_with).setVisible(selectedList.size() == 1
                && selectedList.get(0).isFile());
    }

    @Override
    public void setSubtitle(String subtitle) {
        mToolbar.setSubtitle(subtitle);
    }

    @Override
    public void addNavigationViewPath(String path) {
        mNavigationView.addPath(path);
    }

    @Override
    public void removeNavigationViewLastPath() {
        mNavigationView.removeLastPath();
    }

    @Override
    public void clearNavigationViewPath() {
        mNavigationView.removeAllPath();
    }

    @Override
    public void onNodeRemove(int removeCount) {
        enterColorTheme();
        mPresenter.onNodeRemove(removeCount);
    }

    //*************************************************** 列表 ******************************************************//

    @Override
    public int getCurrentPosition() {
        return mLayoutManager.findFirstCompletelyVisibleItemPosition();
    }

    private boolean mIsMove = false;
    private int mIndex = 0;

    @Override
    public void scrollRecycleView(int position) {
        mIndex = position;
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = mLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (position <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            mRecyclerView.scrollToPosition(position);
        } else if (position <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = mRecyclerView.getChildAt(position - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            mRecyclerView.scrollToPosition(position);
            //这里这个变量是用在RecyclerView滚动监听里面的
            mIsMove = true;
        }
    }

    private class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在这里进行第二次滚动（最后的100米！）
            if (mIsMove) {
                mIsMove = false;
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                int n = mIndex - mLayoutManager.findFirstVisibleItemPosition();
                if (0 <= n && n < mRecyclerView.getChildCount()) {
                    //获取要置顶的项顶部离RecyclerView顶部的距离
                    int top = mRecyclerView.getChildAt(n).getTop();
                    //最后的移动
                    mRecyclerView.scrollBy(0, top);
                }
            }
        }
    }

    @Override
    public void updateRecycleView(List<File> fileList) {
        mAdapter.updateFileList(fileList);
    }

    @Override
    public void openFile(File file) {
        if (!ClickProtectUtil.isToClick(mRecyclerView)) {
            return;
        }
        try {
            Intent intent = FileIntentUtil.openFile(file.getPath());
            getActivity().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showUnknownTypeFileClick() {
        Toast.makeText(getActivity(), "Unknown Type", Toast.LENGTH_SHORT).show();
    }

    //*************************************************** 文件操作 ******************************************************//

    @Override
    public void showCreateFileDialog(final String currentPath) {
        View content = View.inflate(getActivity(), R.layout.create_file_dialog_layout, null);
        final EditText editText = (EditText) content.findViewById(R.id.create_file_dialog_edit);
        final RadioButton fileRadio = (RadioButton) content.findViewById(R.id.create_file_dialog_radio_file);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.new_file))
                .setView(content)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.onCreateDialogPositiveClick(
                                editText.getText().toString().trim(),
                                currentPath,
                                fileRadio.isChecked());
                    }
                }).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        alertDialog.show();
    }

    @Override
    public void showCreateDialogNameEmptyTips() {
        Toast.makeText(getActivity(), R.string.create_name_empty_tips, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDeleteDialog(String message) {
        SpannableString spannable = new SpannableString(message);
        spannable.setSpan(
                new ForegroundColorSpan(getColorCompat(R.color.secondaryText)), 0,
                spannable.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.property))
                .setMessage(spannable)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.onDeleteDialogPositiveClick();
                    }
                }).create().show();
    }

    @Override
    public void showDeleteResult(boolean isDeleteSuc) {
        Toast.makeText(getActivity(), isDeleteSuc ? "Delete suc" : "Delete fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRenameDialog(final File file) {
        // 获取对话框视图
        View content = View.inflate(getActivity(), R.layout.dialog_rename_file_layout, null);
        final EditText editText = (EditText) content.findViewById(R.id.rename_file_dialog_edit);
        // 设置默认选中内容
        String fileName = file.getName();
        editText.setText(fileName);
        int dotIndex = fileName.lastIndexOf(FileUtil.DOT);
        editText.setSelection(0, dotIndex != -1 ? dotIndex : fileName.length());
        // 设置对话框
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.rename))
                .setView(content)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.onRenameDialogPositiveClick(file, editText.getText().toString());
                    }
                }).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        alertDialog.show();
    }

    @Override
    public void showPropertyDialog(File file) {
        StringBuilder builder = new StringBuilder();
        builder.append("Name: ").append(file.getName()).append("\n");
        builder.append("Path: ").append(file.getParent()).append("\n");
        String time = DateUtil.detailFormatter(file.lastModified());
        builder.append("Time: ").append(time).append("\n");
        builder.append("Size: ").append(formatSize(file.length())).append("\n");
        builder.append("Type: ").append(file.isFile() ?
                FileTypeUtil.getType(file.getPath()).name() : "FOLDER").append("\n");
//        boolean canRead = file.canRead();
//        boolean canWrite = file.canWrite();
//        boolean canExecute = file.canExecute();

        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.property))
                .setMessage(builder.toString())
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ok), null)
                .create().show();
    }

    @Override
    public void showPasteFolderDialog(final File src, final File dest) {
        String fileName = dest.getName();
        SpannableString spannable = new SpannableString(fileName + getString(R.string.paste_dup_tips));
        spannable.setSpan(
                new ForegroundColorSpan(getColorCompat(R.color.secondaryText)), fileName.length(),
                spannable.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.paste))
                .setMessage(spannable)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.retain), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.onPasteFolderPositiveClick(src, dest);
                    }
                })
                .setNeutralButton(getString(R.string.replace), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.onPasteFolderNeutralClick(src, dest);
                    }
                })
                .create().show();
    }

    @Override
    public void showPasteFileDialog(final File src, final File dest) {
        String fileName = dest.getName();
        SpannableString spannable = new SpannableString(fileName + getString(R.string.paste_dup_tips));
        spannable.setSpan(
                new ForegroundColorSpan(getColorCompat(R.color.secondaryText)), fileName.length(),
                spannable.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.paste))
                .setMessage(spannable)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.retain), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.onPasteFilePositiveClick(src, dest);
                    }
                })
                .setNeutralButton(getString(R.string.replace), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.onPasteFileNeutralClick(src, dest);
                    }
                })
                .create().show();
    }

    @Override
    public void showPasteResult(boolean pasteSuc) {
        Toast.makeText(getActivity(), pasteSuc ? R.string.paste_suc : R.string.paste_fail, Toast.LENGTH_SHORT).show();
    }

}