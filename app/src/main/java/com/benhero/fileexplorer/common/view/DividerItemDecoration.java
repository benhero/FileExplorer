package com.benhero.fileexplorer.common.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 分割线
 *
 * @author benhero
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    /**
     * 水平方向
     */
    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    /**
     * 垂直方向
     */
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;

    // 画笔
    private Paint mPaint;

    // 布局方向
    private int mOrientation;
    // 分割线颜色
    private int mColor = Color.WHITE;
    // 分割线尺寸
    private int mSize;

    public DividerItemDecoration() {
        this(VERTICAL);
    }

    public DividerItemDecoration(int orientation) {
        this.mOrientation = orientation;
        mPaint = new Paint();
        mPaint.setColor(mColor);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (mOrientation == VERTICAL) {
            drawHorizontal(c, parent);
        } else if (mOrientation == HORIZONTAL) {
            drawVertical(c, parent);
        }
    }

    /**
     * 设置分割线颜色
     *
     * @param color 颜色
     */
    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
    }

    /**
     * 设置分割线尺寸
     *
     * @param size 尺寸
     */
    public void setSize(int size) {
        this.mSize = size;
    }

    // 绘制垂直分割线
    protected void drawVertical(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mSize;

            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    // 绘制水平分割线
    protected void drawHorizontal(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mSize;

            c.drawRect(left, top, right, bottom, mPaint);
        }
    }
}

