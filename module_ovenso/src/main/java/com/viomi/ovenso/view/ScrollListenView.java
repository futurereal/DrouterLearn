package com.viomi.ovenso.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 *
 */
public class ScrollListenView extends ScrollView {

    public ScrollListenView(Context context) {
        super(context);
    }

    public ScrollListenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollListenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int verticalScrollRange = computeVerticalScrollRange();
        if (getScrollY() == 0) {
            mOnScrollChangeListener.onScrollToStart();
            return;
        } else if (verticalScrollRange > getHeight()) {
            if (getScrollY() == (verticalScrollRange - getHeight())) {
                mOnScrollChangeListener.onScrollToEnd();
                return;
            }
        }
        mOnScrollChangeListener.onScroll();
    }


    public interface OnScrollChangeListener {

        //滑动到顶部时的回调
        void onScrollToStart();

        //滑动到底部时的回调
        void onScrollToEnd();

        void onScroll();

        void canScroll(boolean can);
    }

    OnScrollChangeListener mOnScrollChangeListener;

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        mOnScrollChangeListener = onScrollChangeListener;
    }

    public int getVerticalScrollRange() {
        return computeVerticalScrollRange();
    }

    public boolean canScroll() {
        return getVerticalScrollRange() > getHeight();
    }

    public void canScrollCallback(){
        mOnScrollChangeListener.canScroll(canScroll());
    }

    public void initLayoutListener() {
        if (mOnScrollChangeListener == null) {
            throw new IllegalStateException("Please call setOnScrollChangeListener method");
        }
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (getHeight() > 0) {
                            canScrollCallback();
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
    }
}
