package com.viomi.ovenso.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 *
 */
public class ShaderScrollView extends ScrollView {

    public ShaderScrollView(Context context) {
        super(context);
    }

    public ShaderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShaderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    }

    OnScrollChangeListener mOnScrollChangeListener;

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        mOnScrollChangeListener = onScrollChangeListener;
    }

    public int getVerticalScrollRange() {
        return computeVerticalScrollRange();
    }
}
