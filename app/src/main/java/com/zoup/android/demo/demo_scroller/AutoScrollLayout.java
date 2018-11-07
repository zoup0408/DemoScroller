package com.zoup.android.demo.demo_scroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by zoup on 2018/11/6
 * E-Mail：2479008771@qq.com
 */
public class AutoScrollLayout extends ViewGroup {

    /**
     * 滚动操作的实例
     */
    private Scroller scroller;

    /**
     * 最小移动像素
     */
    private int touchSlop;

    /**
     * ACTION_DOWN 时Y坐标
     */
    private float yDown;

    /**
     * ACTION_MOVE 时Y坐标
     */
    private float yMove;

    /**
     * 最新的Y坐标
     */
    private float yLastMove;

    /**
     * 滚动上边界
     */
    private int topBorder;

    /**
     * 滚动下边界
     */
    private int bottomBorder;

    /**
     * 可视item个数
     */
    private int visibleCount = 3;

    public AutoScrollLayout(Context context) {
        super(context);
        init(context);
    }

    public AutoScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        scroller = new Scroller(context);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledPagingTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                childView.layout(0, i * childView.getMeasuredHeight(), childView.getMeasuredWidth(), (i + 1) * childView.getMeasuredHeight());
            }
            //初始化上下边界值
            topBorder = getChildAt(0).getTop();
            bottomBorder = getChildAt(getChildCount() - 1).getBottom();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yDown = ev.getRawY();
                yLastMove = yDown;
                break;
            case MotionEvent.ACTION_MOVE:
                yMove = ev.getRawY();
                yLastMove = yMove;
                if (Math.abs(yMove - yDown) >= touchSlop) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int itemHeight = getHeight() / visibleCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                yMove = event.getRawY();
                //向下滑动阈值判断
                int scrolledY = (int) (yLastMove - yMove);
                if (getScrollY() + scrolledY < topBorder) {
                    scrollTo(0, topBorder);
                    return true;
                }
                //向上滑动阈值判断
                else if (getScrollY() + getHeight() + scrolledY > bottomBorder) {
                    scrollTo(0, bottomBorder - getHeight());
                    return true;
                }
                scrollBy(0, scrolledY);
                yLastMove = yMove;
            case MotionEvent.ACTION_UP:
                // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
                int targetIndex = (getScrollY() + itemHeight / 2) / itemHeight;
                int dy = targetIndex * itemHeight - getScrollY();
                // 调用startScroll()方法来初始化滚动数据并刷新界面
                scroller.startScroll(0, getScrollY(), 0, dy);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}
