package com.benhero.fileexplorer.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.benhero.fileexplorer.R;

/**
 * PathNavigationView
 *
 * @author benhero
 */
public class PathNavigationView extends HorizontalScrollView implements View.OnClickListener {

    private Context mContext;
    private LinearLayout mContentLayout;
    private PathNodeViewListener mNodeClickListener;

    public PathNavigationView(Context context) {
        super(context);
        init(context);
    }

    public PathNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PathNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setHorizontalScrollBarEnabled(false);
        mContentLayout = new LinearLayout(mContext);
        mContentLayout.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mContentLayout, params);
    }

    public void setNodeClickListener(PathNodeViewListener nodeClickListener) {
        mNodeClickListener = nodeClickListener;
    }

    public void addPath(String path) {
        PathNodeView nodeView = new PathNodeView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        nodeView.setOnClickListener(this);
        nodeView.setLinkVisible(false);
        nodeView.setPath(path);
        mContentLayout.addView(nodeView, params);
        int childCount = mContentLayout.getChildCount();
        if (childCount > 1) {
            // 设置上一个节点的连接符可见
            View lastNode = mContentLayout.getChildAt(childCount - 2);
            if (lastNode instanceof PathNodeView) {
                ((PathNodeView) lastNode).setLinkVisible(true);
                ((PathNodeView) lastNode).setIsLastNode(false);
            }
        }
        post(new Runnable() {
            @Override
            public void run() {
                fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });
    }

    public void removeLastPath() {
        int childCount = mContentLayout.getChildCount();
        if (childCount > 1) {
            // 移除最后一个节点
            mContentLayout.removeViewAt(childCount - 1);
            View lastSecondNode = mContentLayout.getChildAt(childCount - 2);
            // 隐藏倒数第二个节点的连接符
            if (lastSecondNode instanceof PathNodeView) {
                ((PathNodeView) lastSecondNode).setLinkVisible(false);
                ((PathNodeView) lastSecondNode).setIsLastNode(true);
            }
        }
    }

    public void removeAllPath() {
        mContentLayout.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof PathNodeView) {
            int index = mContentLayout.indexOfChild(v);
            int count = mContentLayout.getChildCount();
            if (index != count - 1) {
                mContentLayout.removeViews(index + 1, 0);
                mNodeClickListener.onNodeRemove(count - 1 - index);
            }
        }
    }

    /**
     * 路径节点View
     *
     * @author benhero
     */
    private class PathNodeView extends LinearLayout {
        private TextView mPath;
        private ImageView mLink;

        public PathNodeView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.path_node_layout, this);
            mPath = (TextView) findViewById(R.id.path_node_path);
            mLink = (ImageView) findViewById(R.id.path_node_arrow);
        }

        public void setPath(String path) {
            mPath.setText(path);
        }

        public void setLinkVisible(boolean visible) {
            mLink.setVisibility(visible ? VISIBLE : GONE);
        }

        public void setIsLastNode(boolean lastNode) {
            mPath.setAlpha(lastNode ? 1f : 0.40f);
        }
    }

    /**
     * 路径节点点击事件监听器
     *
     * @author benhero
     */
    public interface PathNodeViewListener {

        /**
         * 移除节点
         *
         * @param removeCount 移走的节点个数
         */
        void onNodeRemove(int removeCount);
    }

}
