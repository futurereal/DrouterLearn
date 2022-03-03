package com.viomi.ovenso.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import com.viomi.ovenso.microwave.R;

import java.util.ArrayList;
import java.util.List;

public class HorizonWheelViewTest extends View {

    private final static String TAG = HorizonWheelViewTest.class.getSimpleName();

    // 选中文字
    int centerTextColor = Color.parseColor("#28BECA");
    // 标志位文字
    int centerFlagTextSize = 24;
    int centerFlagTextColor = Color.parseColor("#28BECA");
    String centerFlagText = "";

    private int mHalfWidth, mHalfHeight;
    private final static int HALF_DIVIDER = 50;

    public static final int TYPE_HALF = 2;
    private final List<Object> dataList = new ArrayList<>();
    private int mMaxDoubleIndex = 0;
    private int mSelectIndex = 0;

    private final Scroller mScroller;
    private VelocityTracker mTracker;
    private final int minTracker;
    private int mLastX, mMove;

    private TextPaint unitPaint;
    private Paint boxPaint;
    private TextPaint textPaint;
    private TextPaint targetPaint;

    private OnSelectListener mOnSelectListener;

    public HorizonWheelViewTest(Context context) {
        this(context, null);
    }

    public HorizonWheelViewTest(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
        mScroller = new Scroller(context);
        minTracker = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    /**
     * 设置数据
     *
     * @param selectedValue 默认选择值
     * @param maxValue      最大值
     * @param minValue      最小值
     */
    public void setIntData(int selectedValue, int maxValue, int minValue) {
        Log.i(TAG, "setIntData: selectedValue: " + selectedValue + "  maxValue: " + maxValue + "  mixValue: " + minValue);
        dataList.clear();
        for (int i = minValue; i <= maxValue; i++) {
            dataList.add(i);
        }
        mMaxDoubleIndex = (dataList.size() - 1) * 2;
        mSelectIndex = (selectedValue - minValue) * 2;
        Log.d(TAG, "setIntData: mSelectIndex: " + mSelectIndex + "  mMaxDoubleIndex: " + mMaxDoubleIndex);
        if (mSelectIndex > mMaxDoubleIndex) {
            return;
        }
        invalidate();
    }

    public void setStringData(List<String> stringList, int selectIndex) {
        dataList.clear();
        dataList.addAll(stringList);
        mMaxDoubleIndex = (dataList.size() - 1) * 2;
        mSelectIndex = selectIndex * 2;
        Log.d(TAG, "setIntData: mSelectIndex: " + mSelectIndex + "  mMaxDoubleIndex: " + mMaxDoubleIndex);
        if (mSelectIndex > mMaxDoubleIndex) {
            return;
        }
        invalidate();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizonPickerView);
        centerTextColor = 1;
        centerFlagTextSize = 1;
        centerFlagTextColor = 1;
        typedArray.recycle();
    }

    private void initPaint() {
        // 标志位单位
        unitPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setStyle(Paint.Style.FILL);
        unitPaint.setTextAlign(Paint.Align.RIGHT);
        unitPaint.setTextSize(centerFlagTextSize);
        unitPaint.setColor(centerFlagTextColor);
        // 标志位边框
        boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setColor(centerFlagTextColor);
        boxPaint.setStrokeWidth(2);
        // 选项文字
        int textColor = Color.parseColor("#E7EBFD");
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(32);
        textPaint.setColor(textColor);
        // 标志位选项文字
        targetPaint = new TextPaint(textPaint);
        targetPaint.setColor(centerFlagTextColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHalfWidth = getMeasuredWidth() / TYPE_HALF;
        mHalfHeight = getMeasuredHeight() / TYPE_HALF;
        Log.i(TAG, "onMeasure: mHalfWidth: " + mHalfWidth + "  mHalfHeight: " + mHalfHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataList.size() == 0) {
            return;
        }
        drawText(canvas);
        drawCheckBoxAndUnit(canvas);
    }

    /**
     * 绘制边框 和 标志位文字
     */
    private void drawCheckBoxAndUnit(Canvas canvas) {
        canvas.save();

        float textWidth = 0;
        if (!TextUtils.isEmpty(centerFlagText)) {
            textWidth = Layout.getDesiredWidth("一", unitPaint) * centerFlagText.length();
            Paint.FontMetrics fm = unitPaint.getFontMetrics();
            float baseY = mHalfHeight - (fm.bottom + fm.top) / TYPE_HALF;
            float baseX = mHalfWidth + HALF_DIVIDER + (textWidth / TYPE_HALF);
            canvas.drawText(centerFlagText, baseX, baseY, unitPaint);
        }

        float left = mHalfWidth - HALF_DIVIDER - (textWidth / TYPE_HALF) - 3;
        float right = mHalfWidth + HALF_DIVIDER + (textWidth / TYPE_HALF) + 3;
        canvas.drawRoundRect(left, 1, right, (mHalfHeight * TYPE_HALF - 1), 22, 22, boxPaint);
        canvas.restore();
    }


    /**
     * 从中间往两边开始绘制
     */
    private void drawText(Canvas canvas) {
        canvas.save();

        int drawCount = 0;
        float xPosition = 0;
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float yPosition = (mHalfHeight - (fm.bottom + fm.top) / TYPE_HALF);

        for (int i = 0; drawCount <= 4 * mHalfWidth; i++) {
            // 画左边
            xPosition = (mHalfWidth - mMove) + i * HALF_DIVIDER;
            if (xPosition + getPaddingRight() < (mHalfWidth * TYPE_HALF)) {
                if (i != 0 && ((mSelectIndex + i) % TYPE_HALF == 0)) {
                    int leftIndex = (mSelectIndex + i) / 2;
                    if (leftIndex == dataList.size()) {
                        leftIndex = 0;
                    } else if (leftIndex == dataList.size() + 1) {
                        leftIndex = 1;
                    }
//                    Log.d(TAG, "drawText: left index:  " + leftIndex);
                    canvas.drawText(String.valueOf(dataList.get(leftIndex)), xPosition, yPosition, textPaint);
                }
            }

            // 画右边
            xPosition = (mHalfWidth - mMove) - i * HALF_DIVIDER;
            if (xPosition > getPaddingLeft()) {
                if ((mSelectIndex - i) % TYPE_HALF == 0) {
                    int rightIndex = (mSelectIndex - i) / 2;
                    if (rightIndex == -1) {
                        rightIndex = dataList.size() - 1;
                    } else if (rightIndex == -2) {
                        rightIndex = dataList.size() - 2;
                    }
//                    Log.d(TAG, "drawText: right index:  " + rightIndex);
                    if (i != 0) {
                        canvas.drawText(String.valueOf(dataList.get(rightIndex)), xPosition, yPosition, textPaint);
                    } else {
                        // 画中间选中项
                        canvas.drawText(String.valueOf(dataList.get(rightIndex)), xPosition, yPosition, targetPaint);
                    }
                }
            }

            drawCount += 2 * HALF_DIVIDER;
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTracker == null) {
            mTracker = VelocityTracker.obtain();
        }
        mTracker.addMovement(event);
        int action = event.getAction();
        int xPosition = (int) event.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                mLastX = xPosition;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker();
                return false;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            Log.i(TAG, "computeScroll: " + mScroller.getCurrX() + "  " + mScroller.getFinalX());
            if (mScroller.getCurrX() == mScroller.getFinalX()) {
                // 滑动结束
                countMoveEnd();
            } else {
                // 滑动中
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                Log.i(TAG, "computeScroll: mMove: " + mMove + " mLastPostition: " + mLastX);
                changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }

    private void changeMoveAndValue() {
        int tValue = (int) (mMove / (HALF_DIVIDER));
        if (Math.abs(tValue) > 0) {
            mSelectIndex += tValue;
            mMove -= (tValue * HALF_DIVIDER);
            Log.d(TAG, "changeMoveAndValue: mSelectIndex: " + mSelectIndex);
            if (mSelectIndex < 0) {
                mSelectIndex = mMaxDoubleIndex;
            } else if (mSelectIndex > mMaxDoubleIndex) {
                mSelectIndex = 0;
            }
        }
        postInvalidate();
    }

    private void countMoveEnd() {
        int roundMove = Math.round(mMove / HALF_DIVIDER);
        mSelectIndex = mSelectIndex + roundMove;

        // 如果选中下标是单数，则加一
        if (mSelectIndex % 2 == 1) {
            mSelectIndex++;
        }
        Log.d(TAG, "countMoveEnd: mSelectIndex: " + mSelectIndex);
        if (mSelectIndex < 0) {
            mSelectIndex = mMaxDoubleIndex;
        } else if (mSelectIndex > mMaxDoubleIndex) {
            mSelectIndex = 0;
        }

        mLastX = 0;
        mMove = 0;
        notifyValueChange();
        postInvalidate();
    }

    private void countVelocityTracker() {
        mTracker.computeCurrentVelocity(1000);
        float xVelocity = mTracker.getXVelocity();
        Log.d(TAG, "countVelocityTracker: xVelocity: " + xVelocity + "  minTracker: " + minTracker);
        if (Math.abs(xVelocity) > minTracker) {
            // 滑动一段距离
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    private void notifyValueChange() {
        int index = mSelectIndex / TYPE_HALF;
        Log.d(TAG, "notifyValueChange: value: " + dataList.get(index));
        mOnSelectListener.onSelect(dataList.get(index));
    }

    /**
     * 选择结果回调接口
     */
    public interface OnSelectListener {
        void onSelect(Object selectedValue);
    }

    /**
     * 设置选择结果监听
     */
    public void setOnSelectListener(OnSelectListener listener) {
        this.mOnSelectListener = listener;
    }
}
