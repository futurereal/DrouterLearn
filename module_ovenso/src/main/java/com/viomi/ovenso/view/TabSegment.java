package com.viomi.ovenso.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.viomi.ovenso.microwave.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TabSegment extends View {

    private float mInitSpacing, mTextSpacing, mHalfTextSpacing;
    private float mHalfWidth, mHalfHeight, mQuarterHeight;
    private final Context mContext;
    private Paint mPaint;
    private List<String> mDataList = new ArrayList<>();
    private int mSelectedIndex = 0;
    private float mScrollDistance;
    private float mLastTouchY;
    private final boolean mCanScrollLoop = true;
    private OnSelectListener mOnSelectListener;
    private int mTextColor;
    Typeface mTypefaceRegular, mTypefaceMedium;

    private final Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private final Handler mHandler = new ScrollHandler(this);

    /**
     * 自动回滚到中间的速度
     */
    private static final float AUTO_SCROLL_SPEED = 10;

    /**
     * 选择结果回调接口
     */
    public interface OnSelectListener {
        void onSelect(View view, String selected);
    }

    private static class ScrollTimerTask extends TimerTask {
        private final WeakReference<Handler> mWeakHandler;

        private ScrollTimerTask(Handler handler) {
            mWeakHandler = new WeakReference<>(handler);
        }

        @Override
        public void run() {
            Handler handler = mWeakHandler.get();
            if (handler == null) return;

            handler.sendEmptyMessage(0);
        }
    }

    private static class ScrollHandler extends Handler {
        private final WeakReference<TabSegment> mWeakView;

        private ScrollHandler(TabSegment view) {
            mWeakView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            TabSegment view = mWeakView.get();
            if (view == null) return;

            view.keepScrolling();
        }
    }

    public TabSegment(Context context) {
        super(context);
        this.mContext = context;
    }

    public TabSegment(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mTextColor = context.getResources().getColor(R.color.white);
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mTypefaceRegular = ResourcesCompat.getFont(mContext, R.font.noto_sans_sc_regular);
//        mTypefaceMedium = ResourcesCompat.getFont(mContext, R.font.noto_sans_sc_medium);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //半宽
        mHalfWidth = getMeasuredWidth() / 2f;
        int height = getMeasuredHeight();
        //半高
        mHalfHeight = height / 2f;
        //四分之一高
        mQuarterHeight = height / 4f;

        float temp1, temp2;
        mPaint.setTypeface(mTypefaceMedium);
        mPaint.setTextSize(32);
        temp1 = mPaint.getFontSpacing() / 2f;

        mPaint.setTypeface(mTypefaceRegular);
        mPaint.setTextSize(28);
        temp2 = mPaint.getFontSpacing() / 2f;

        mTextSpacing = mInitSpacing + temp1 + temp2;
        mHalfTextSpacing = mTextSpacing / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSelectedIndex >= mDataList.size()) return;

        // 绘制中间选中的 text
        drawText(canvas, mTextColor, mScrollDistance, mDataList.get(mSelectedIndex), mSelectedIndex, -1);
        drawIndicator(canvas);

        // 绘制选中上方的 text
        for (int i = 1; i <= mSelectedIndex; i++) {
            drawText(canvas, mTextColor, mScrollDistance - i * mTextSpacing,
                    mDataList.get(mSelectedIndex - i), mSelectedIndex - i, 0);
        }

        // 绘制选中下方的 text
        int size = mDataList.size() - mSelectedIndex;
        for (int i = 1; i < size; i++) {
            drawText(canvas, mTextColor, mScrollDistance + i * mTextSpacing,
                    mDataList.get(mSelectedIndex + i), mSelectedIndex + i, 1);
        }
    }

    private void drawText(Canvas canvas, int textColor, float offsetY, String text, int index, int state) {
        if (TextUtils.isEmpty(text)) return;
        //Math.pow(x,y)表示x的y次方
        float alp = 1;
        int temp = Math.abs(mSelectedIndex - index);
        if (temp == 0) {
            alp = 1.0f;
        } else if (temp == 1) {
            alp = 0.6f;
        } else if (temp == 2) {
            alp = 0.4f;
        } else if (temp == 3) {
            alp = 0.2f;
        }
        alp = alp < 0 ? 0 : alp;
        if (state == -1) {
            mPaint.setTypeface(mTypefaceMedium);
            mPaint.setTextSize(32);
        } else {
            mPaint.setTypeface(mTypefaceRegular);
            mPaint.setTextSize(28);
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(textColor);
        mPaint.setAlpha((int) (255 * alp));
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        float baseline = mHalfHeight + offsetY - (fm.top + fm.bottom) / 2f;
        Log.d("TabSegment","text="+text+" mHalfWidth:"+mHalfWidth+" y="+baseline+"measureW:"+mPaint.measureText(text) );
        canvas.drawText(text, mHalfWidth+10, baseline, mPaint);
    }

    /**
     * 绘制中间的标签条
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        canvas.drawRoundRect(0, mHalfHeight - 15, 10, mHalfHeight + 15, 8, 8, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                cancelTimerTask();
                mLastTouchY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float offsetY = event.getY();
                mScrollDistance += offsetY - mLastTouchY;
                if (mScrollDistance > mHalfTextSpacing) {
                    if (!mCanScrollLoop) {
                        if (mSelectedIndex == 0) {
                            mLastTouchY = offsetY;
                            invalidate();
                            break;
                        } else {
                            mSelectedIndex--;
                        }
                    } else {
                        // 往下滑超过离开距离，将末尾元素移到首位
                        moveTailToHead();
                    }
                    mScrollDistance -= mTextSpacing;
                } else if (mScrollDistance < -mHalfTextSpacing) {
                    if (!mCanScrollLoop) {
                        if (mSelectedIndex == mDataList.size() - 1) {
                            mLastTouchY = offsetY;
                            invalidate();
                            break;
                        } else {
                            mSelectedIndex++;
                        }
                    } else {
                        // 往上滑超过离开距离，将首位元素移到末尾
                        moveHeadToTail();
                    }
                    mScrollDistance += mTextSpacing;
                }
                mLastTouchY = offsetY;
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                // 抬起手后 mSelectedIndex 由当前位置滚动到中间选中位置
                if (Math.abs(mScrollDistance) < 0.01) {
                    mScrollDistance = 0;
                    break;
                }
                cancelTimerTask();
                mTimerTask = new ScrollTimerTask(mHandler);
                mTimer.schedule(mTimerTask, 0, 10);
                break;
        }
        return true;
    }

    private void cancelTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.purge();
        }
    }

    private void moveTailToHead() {
        if (!mCanScrollLoop || mDataList.isEmpty()) return;

        String tail = mDataList.get(mDataList.size() - 1);
        mDataList.remove(mDataList.size() - 1);
        mDataList.add(0, tail);
    }

    private void moveHeadToTail() {
        if (!mCanScrollLoop || mDataList.isEmpty()) return;

        String head = mDataList.get(0);
        mDataList.remove(0);
        mDataList.add(head);
    }

    private void keepScrolling() {
        if (Math.abs(mScrollDistance) < AUTO_SCROLL_SPEED) {
            mScrollDistance = 0;
            if (mTimerTask != null) {
                cancelTimerTask();

                if (mOnSelectListener != null && mSelectedIndex < mDataList.size()) {
                    mOnSelectListener.onSelect(this, mDataList.get(mSelectedIndex));
                }
            }
        } else if (mScrollDistance > 0) {
            // 向下滚动
            mScrollDistance -= AUTO_SCROLL_SPEED;
        } else {
            // 向上滚动
            mScrollDistance += AUTO_SCROLL_SPEED;
        }
        invalidate();
    }

    /**
     * 设置数据
     */
    public void setDataList(List<String> list) {
        if (list == null || list.isEmpty()) return;

        mDataList = list;
        // 重置 mSelectedIndex，防止溢出
        mSelectedIndex = 0;
        if (list.size() <= 3) {//整体按3项排列
            mInitSpacing = 77;
        } else {//整体按5项排列
            mInitSpacing = 40;
        }
        invalidate();
    }

    /**
     * 选择选中项
     */
    public void setSelected(int index) {
        if (index >= mDataList.size()) return;

        mSelectedIndex = index;
        if (mCanScrollLoop) {
            // 可循环滚动时，mSelectedIndex 值固定为 mDataList / 2
            int position = mDataList.size() / 2 - mSelectedIndex;
            if (position < 0) {
                for (int i = 0; i < -position; i++) {
                    moveHeadToTail();
                    mSelectedIndex--;
                }
            } else if (position > 0) {
                for (int i = 0; i < position; i++) {
                    moveTailToHead();
                    mSelectedIndex++;
                }
            }
        }
        if (mOnSelectListener != null) {
            mOnSelectListener.onSelect(this, mDataList.get(mSelectedIndex));
        }
        invalidate();
    }

    /**
     * 设置选择结果监听
     */
    public void setOnSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
    }
}
