package com.viomi.ovenso.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Ljh on 2020/11/10
 * Description:卡片动效容器
 */
public class CardSlidingView extends ViewGroup {
    private static final String TAG = "CardSlidingView";
    //
    int containerWidth, containerHeight;
    int childWidth, childHeight;
    // 分别记录上次滑动的坐标
    private int mLastX = 0, mLastY = 0;
    // 分别记录上次滑动的坐标(onInterceptTouchEvent)
    private int mLastXIntercept = 0, mLastYIntercept = 0;
    private int scrollDistance = 0, scrollDistanceBack;
    private final int maxScroll = 756 - 305;//451从设计稿提取
    private final int childHdiv = 25; //child之间间隔25
    //
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    //
    public CardSlidingView(Context context) {
        this(context, null);
    }

    public CardSlidingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardSlidingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG, "CardSlidingView: ");
        if (mScroller == null) {
            mScroller = new Scroller(getContext());
            mVelocityTracker = VelocityTracker.obtain();
        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int currentX = (int) event.getX();
        int currentY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    Log.i(TAG, "onInterceptTouchEvent: ACTION_DOWN  isFinishFalse");
                    intercepted = true;
                }
                if (getScrollX() > 5 && currentX < 305 - 116) {
                    Log.i(TAG, "scroolx < 5 or x too small");
                    Log.i(TAG,"getScrollX:"+getScrollX() +" currentX:"+currentX);
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = currentX - mLastXIntercept;
                int deltaY = currentY - mLastYIntercept;
                intercepted = Math.abs(deltaX) > Math.abs(deltaY);
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
            default:
                break;
        }
        Log.d(TAG, "action: " + event.getAction() + " intercepted:" + intercepted);
        mLastX = mLastXIntercept = currentX;
        mLastY = mLastYIntercept = currentY;
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: envent: " + event.getAction());
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d(TAG, "ACTION_MOVE");
                int deltaX = x - mLastX;//大于0左滑，小于0右滑
                int deltaY = y - mLastY;
                if (scrollDistance - deltaX <= 0) {//已经滑到最左边

                } else if (scrollDistance - deltaX >= maxScroll/*contentWidth - containerWidth*/) {//已经滑到最右边

                } else {
                    scrollDistance += (-deltaX);
                    scrollBy(-deltaX, 0);
                    changeChild(scrollDistance);
                    //Log.d(TAG, "scrollBy:" + (-deltaX) + " scrollDistance:" + scrollDistance);
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                mVelocityTracker.clear();
                if (Math.abs(scrollDistance - scrollDistanceBack) >= childWidth / 2) {
                    if (scrollDistance - scrollDistanceBack > 0) {//左滑，缩放
                        smoothScrollBy(maxScroll - scrollDistance);
                        scrollDistance = scrollDistanceBack = maxScroll;
                    } else {//右滑，变回原大小
                        smoothScrollBy(-getScrollX());
                        scrollDistance = scrollDistanceBack = 0;
                    }
                } else {//恢复
                    //Log.d(TAG, " scrollDistance:" + scrollDistance + " scrollDistanceBack:" + scrollDistanceBack);
                    smoothScrollBy(-(scrollDistance - scrollDistanceBack));
                    scrollDistance = scrollDistanceBack;
                }
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    /**
     *
     * @param scrollX
     */
    private void changeChild(int scrollX) {
        Log.d(TAG, "changeChild srolled:" + scrollX);
        View child0 = getChildAt(0);
        int left0 = 0;
        int leftLimit0 = childWidth + childHdiv + childWidth + childHdiv - 163;
        child0.setScaleX(1 - 0.2f * scrollX / maxScroll);
        child0.setScaleY(1 - 0.2f * scrollX / maxScroll);
        child0.setPivotX(0);
        child0.setPivotY(childHeight / 2);
        float leftResult = 1.0f * scrollX / maxScroll * (leftLimit0 - left0);
        child0.setTranslationX(leftResult);
        ////
        View child1 = getChildAt(1);
        int left1 = childWidth + childHdiv;
        int leftLimit1 = childWidth + childHdiv + childWidth + childHdiv - 98;
        child1.setPivotX(0);
        child1.setPivotY(childHeight / 2);
        child1.setScaleX(1 - 0.1f * scrollX / maxScroll);
        child1.setScaleY(1 - 0.1f * scrollX / maxScroll);
        float leftResult1 = 1.0f * scrollX / maxScroll * (leftLimit1 - left1);
        child1.setTranslationX(leftResult1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "measureWidth:" + measureWidth + "  measureHeight:" + measureHeight);
        containerWidth = measureWidth;
        containerHeight = measureHeight;
        //测量子控件
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "onLayout: ");
        int count = getChildCount();
        int diff = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            childHeight = child.getMeasuredHeight();//400
            childWidth = child.getMeasuredWidth();//296
            //位置0-296 321-617 642-938 963-1259 1284-1580
            int left = diff;
            int top = containerHeight / 2 - childHeight / 2;
            diff += childWidth + childHdiv;
            child.layout(left, top, left + childWidth, top + childHeight);
        }
    }

    /**
     * 水平方向滑动的距离，正值会使滚动向左滚动
     *
     * @param dx
     */
    private void smoothScrollBy(int dx) {
        Log.d(TAG,"smoothScrollBy getScrollX="+getScrollX()+" dx="+dx);
        mScroller.startScroll(getScrollX(), 0, dx, 0, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //Log.d(TAG,"computeScrollOffset getCurrX = "+mScroller.getCurrX());
            changeChild(mScroller.getCurrX());
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }

}

