package com.viomi.ovenso.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.viomi.ovenso.microwave.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 说明：内容选择器，例如年月日、省市区等
 * 作者：
 * 添加时间：
 * 修改人：
 * 修改时间：
 */
public class PickerView extends View {

    private final Context mContext;

    private Paint mPaint;
    private int mLightColor, mDividerColor;
    private float mHalfWidth, mHalfHeight, mQuarterHeight;
    private float mTextSpacing, mHalfTextSpacing;

    private float mScrollDistance;
    private float mLastTouchY;
    private List<String> mDataList = new ArrayList<>();
    private int mSelectedIndex;
    private boolean mCanScroll = true;
    private boolean mCanScrollLoop = true;
    private OnSelectListener mOnSelectListener;
    private ObjectAnimator mScrollAnim;
    private boolean mCanShowAnim = true;

    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private final Handler mHandler = new ScrollHandler(this);

    /**
     * 自动回滚到中间的速度
     */
    private static final float AUTO_SCROLL_SPEED = 10;

    private float mBaseLine = 0;
    private float mTop = 0;
    private float mAscent = 0;
    private float mBottom = 0;
    private float mOffsetX = 0;

    private String mTips = "";

    Typeface mTypefaceRegular;

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
        private final WeakReference<PickerView> mWeakView;

        private ScrollHandler(PickerView view) {
            mWeakView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            PickerView view = mWeakView.get();
            if (view == null) return;

            view.keepScrolling();
        }
    }

    public PickerView(Context context) {
        super(context);

        mContext = context;
        initPaint();
    }

    public PickerView(Context context, String tips) {
        super(context);

        mContext = context;
        mTips = tips;
        initPaint();
    }

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        initPaint();
    }

    private void initPaint() {
//        mTypefaceRegular = ResourcesCompat.getFont(mContext, R.font.noto_sans_sc_regular);
        mLightColor = ContextCompat.getColor(mContext, R.color.viomi_green);
        mDividerColor = ContextCompat.getColor(mContext, R.color.color_d8);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

        mPaint.setTypeface(mTypefaceRegular);
        mPaint.setTextSize((42));

        mTextSpacing = mPaint.getFontSpacing();
        //一半字体间隔
        mHalfTextSpacing = mTextSpacing / 2f;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSelectedIndex >= mDataList.size()) return;

        // 绘制选中的 text
        float baseline = drawText(canvas, mLightColor, mScrollDistance, mDataList.get(mSelectedIndex), mSelectedIndex, 0);
        if (mBaseLine == 0) {
            mBaseLine = baseline;
            mTop = mBaseLine + mPaint.getFontMetrics().top;
            mAscent = mBaseLine + mPaint.getFontMetrics().ascent;
            mBottom = mBaseLine + mPaint.getFontMetrics().bottom;
            mOffsetX = mPaint.measureText(mDataList.get(mSelectedIndex));
        }
        if (mBaseLine != 0) {
            //drawLine(canvas, mDividerColor, mHalfHeight - 39);
            //drawLine(canvas, mDividerColor, mHalfHeight + 39);
            mPaint.setColor(mLightColor);
            drawTipsText(canvas, mLightColor, mHalfWidth + mOffsetX / 2 + 8, mBaseLine - 3, mTips);
        }
        // 绘制选中上方的 text
        for (int i = 1; i <= mSelectedIndex; i++) {
            drawText(canvas, mLightColor, mScrollDistance,
                    mDataList.get(mSelectedIndex - i), mSelectedIndex - i, 0);
        }

        // 绘制选中下方的 text
        int size = mDataList.size() - mSelectedIndex;
        for (int i = 1; i < size; i++) {
            drawText(canvas, mLightColor, mScrollDistance,
                    mDataList.get(mSelectedIndex + i), mSelectedIndex + i, 1);
        }
    }

    private void drawTipsText(Canvas canvas, int textColor, float startX, float startY, String text) {
        if (TextUtils.isEmpty(text)) return;
        mPaint.setTextSize((21));
        mPaint.setColor(textColor);
        //Paint.FontMetrics fm = mPaint.getFontMetrics();
        //float baseline = mAscent + fm.descent + (fm.bottom - fm.top) - fm.bottom;
        startX += 10;
        canvas.drawText(text, startX, startY, mPaint);
    }

    private float drawText(Canvas canvas, int textColor, float offsetY, String text, int index, int state) {
        if (TextUtils.isEmpty(text)) return 0;
        //Math.pow(x,y)表示x的y次方
        float scale = 1 - (float) Math.pow(offsetY / mQuarterHeight, 2);
        int temp = Math.abs(mSelectedIndex - index);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextAlign(Align.CENTER);
        if (temp == 0) {
            scale = 1.0f;
        } else if (temp == 1) {
            scale = 0.6f;
            textColor = ContextCompat.getColor(mContext, R.color.color_c8);
            if (state == 0) {
                offsetY = offsetY - (18 + (mBottom - mTop) / 2);
            } else {
                offsetY = offsetY + 18 + (mBottom - mTop) / 2;
            }
        } else if (temp == 2) {
            scale = 0.4f;
            if (state == 0) {
                offsetY = offsetY - (78 + (mBottom - mTop) / 2);
            } else {
                offsetY = offsetY + 78 + (mBottom - mTop) / 2;
            }
            textColor = ContextCompat.getColor(mContext, R.color.color_68);
        } else {
            return 0;
        }

        if (temp == 0) {
            mPaint.setTextSize((42));
        } else if (temp == 1) {
            mPaint.setTextSize((36));
        } else if (temp == 2) {
            mPaint.setTextSize((32));
        }

        mPaint.setColor(textColor);
        mPaint.setAlpha((int) (255 * scale));
        // text 居中绘制，mHalfHeight + offsetY 是 text 的中心坐标
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        float baseline = mHalfHeight + offsetY - (fm.top + fm.bottom) / 2f;
        canvas.drawText(text, mHalfWidth, baseline, mPaint);
        return baseline;
    }

    private void drawLine(Canvas canvas, int lineColor, float startY) {
        mPaint.setColor(lineColor);
        mPaint.setAlpha((int) (255 * 0.2f));
        canvas.drawLine(0, startY, mHalfWidth * 2, startY, mPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mCanScroll && super.dispatchTouchEvent(event);
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

    public String getSelectItem() {
        return this.mDataList.get(mSelectedIndex);
    }

    /**
     * 设置选择结果监听
     */
    public void setOnSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
    }

    /**
     * 是否允许滚动
     */
    public void setCanScroll(boolean canScroll) {
        mCanScroll = canScroll;
    }

    /**
     * 是否允许循环滚动
     */
    public void setCanScrollLoop(boolean canLoop) {
        mCanScrollLoop = canLoop;
    }

    /**
     * 执行滚动动画
     */
    public void startAnim() {
        if (!mCanShowAnim) return;

        if (mScrollAnim == null) {
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
            mScrollAnim = ObjectAnimator.ofPropertyValuesHolder(this, alpha, scaleX, scaleY).setDuration(200);
        }

        if (!mScrollAnim.isRunning()) {
            mScrollAnim.start();
        }
    }

    /**
     * 是否允许滚动动画
     */
    public void setCanShowAnim(boolean canShowAnim) {
        mCanShowAnim = canShowAnim;
    }

    /**
     * 销毁资源
     */
    public void onDestroy() {
        mOnSelectListener = null;
        mHandler.removeCallbacksAndMessages(null);
        if (mScrollAnim != null && mScrollAnim.isRunning()) {
            mScrollAnim.cancel();
        }
        cancelTimerTask();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

}