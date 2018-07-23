package com.benhero.fileexplorer.base.presentation.ui.recyclerview;

/**
 * RecyclerView滚动节点
 *
 * @author benhero
 */
public class RecyclerViewScrollNode {
    private RecyclerViewScrollNode mParent;
    private int mPosition;

    public RecyclerViewScrollNode() {
    }

    public RecyclerViewScrollNode getParent() {
        if (mParent == null) {
            return new RecyclerViewScrollNode();
        }
        return mParent;
    }

    public void setParent(RecyclerViewScrollNode parent) {
        mParent = parent;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    @Override
    public String toString() {
        return "RecyclerViewScrollNode{" +
                "mParent=" + mParent +
                ", mPosition=" + mPosition +
                '}';
    }
}
