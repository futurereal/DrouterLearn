package com.viomi.ovenso.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.microwave.R;

import java.util.ArrayList;
import java.util.List;

public class
HorizonPickerView extends View {
    private static final String TAG = "HorizonPickerView";
    // 选中文字
    private static final int DEFAULT_CENTER_TEXT_COLOR = ApplicationUtils.getContext().getColor(R.color.viomi_green);
    private static final int DEFAULT_CENTER_TEXT_SIZE = 34;
    private static final int DEFAULT_UNIT_COLOR = Color.WHITE;
    private static final int DEFAULT_UNIT_SIZE = 34;
    private static final int DEFAULT_CIRCLE_WIDTH = 130;
    private static final int DEFAULT_NORMAL_TEXT_SIZE = 28;
    private static final int DEFAULT_NORMAL_TEXT_COLOR = Color.WHITE;
    private static final float DEFAULT_STEP_LENGTH = 1f;
    public static final int CIRCLE_RADIUS = 22;
    private String unitText;
    private int mHalfWidth, mHalfHeight;
    private int halfDivider;
    public static final int HALF = 2;
    private final List<Pair<String, String>> dataList = new ArrayList<>();
    private int mSelectIndex = 0;

    private Scroller mScroller;
    private VelocityTracker mTracker;
    private int mFlingVelocity;
    private int mLastX, mMove;

    private TextPaint unitPaint;
    private TextPaint normalTextPaint;
    private TextPaint centerTextPaint;
    private Paint circlePaint;

    private OnSelectListener mOnSelectListener;
    private float circleLeft;
    private float circleRight;
    private float centerCircleWidth;
    private int unitTextColor;
    private int unitTextSize;

    private int centerTextColor;
    private int centerFlagTextSize;
    private float pickerViewStep;
    private int normalTextSize;
    private int normalTextColor;

    public HorizonPickerView(Context context) {
        this(context, null);
    }

    public HorizonPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
        initOtherProperty(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizonPickerView);
        centerTextColor = typedArray.getColor(R.styleable.HorizonPickerView_pickerview_centerTextColor, DEFAULT_CENTER_TEXT_COLOR);
        centerFlagTextSize = typedArray.getInt(R.styleable.HorizonPickerView_pickerview_centerTextSize, DEFAULT_CENTER_TEXT_SIZE);
        unitTextColor = typedArray.getColor(R.styleable.HorizonPickerView_pickerview_unitTextColor, DEFAULT_UNIT_COLOR);
        unitTextSize = typedArray.getInt(R.styleable.HorizonPickerView_pickerview_unitTextSize, DEFAULT_UNIT_SIZE);
        unitText = typedArray.getString(R.styleable.HorizonPickerView_pickerview_unitText);
        centerCircleWidth = typedArray.getColor(R.styleable.HorizonPickerView_pickerview_circleWidth, DEFAULT_CIRCLE_WIDTH);
        normalTextSize = typedArray.getInt(R.styleable.HorizonPickerView_pickerview_normalTextSize, DEFAULT_NORMAL_TEXT_SIZE);
        normalTextColor = typedArray.getColor(R.styleable.HorizonPickerView_pickerview_normalTextSize, DEFAULT_NORMAL_TEXT_COLOR);
        halfDivider = (int) (centerCircleWidth / 3);
        pickerViewStep = typedArray.getFloat(R.styleable.HorizonPickerView_pickerview_stepLength, DEFAULT_STEP_LENGTH);
        typedArray.recycle();
    }

    private void initPaint() {
        Log.i(TAG, "initPaint: ");
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        // 标志位边框
        unitPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setStyle(Paint.Style.FILL);
        unitPaint.setTextAlign(Paint.Align.CENTER);
        unitPaint.setColor(unitTextColor);
        unitPaint.setTextSize(unitTextSize);
        // 选项文字
        normalTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        normalTextPaint.setStyle(Paint.Style.FILL);
        normalTextPaint.setTextAlign(Paint.Align.CENTER);
        normalTextPaint.setTextSize(normalTextSize);
        normalTextPaint.setColor(normalTextColor);
        // 标志位选项文字
        centerTextPaint = new TextPaint(normalTextPaint);
        centerTextPaint.setColor(centerTextColor);
        centerTextPaint.setTextSize(centerFlagTextSize);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(centerTextColor);
        circlePaint.setStrokeWidth(2);
    }

    private void initOtherProperty(Context context) {
        mScroller = new Scroller(context);
        mTracker = VelocityTracker.obtain();
        mFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthLength = MeasureSpec.getSize(widthMeasureSpec);
        Log.i(TAG, "onMeasure: widthMode: " + widthMode + "  widthLength:" + widthLength);
        if (MeasureSpec.AT_MOST == widthMode) {
            widthLength = 400;
        }
        Log.i(TAG, "onMeasure: widthLength: " + widthLength);

        int viewHeigth = (int) (centerTextPaint.getTextSize() * 2);
        int width = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);
        Log.i(TAG, "onMeasure: viewHeigth: " + viewHeigth + " viewWidth: " + width);
        setMeasuredDimension(width, viewHeigth);
        mHalfWidth = getMeasuredWidth() / HALF;
        mHalfHeight = getMeasuredHeight() / HALF;
        Log.i(TAG, "onMeasure: mHalfWidth: " + mHalfWidth + "  mHalfHeight: " + mHalfHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "onSizeChanged: w: " + w + "  h: " + h + " oldW: " + oldw + " oldh: " + oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: draw: " + dataList.size());
        if (dataList.size() == 0) {
            return;
        }
        if (mSelectIndex >= dataList.size()) {
            mSelectIndex = 0;
        } else if (mSelectIndex < 0) {
            mSelectIndex = dataList.size() - 1;
        }
        Log.i(TAG, "onDraw: mSelectIndex: " + mSelectIndex);
        // 先画圆 和单位
        drawCenter(canvas);
        drawLeftRightText(canvas);
    }

    /**
     * 只绘制一次就好了
     *
     * @param canvas 画布
     */
    private void drawCenter(Canvas canvas) {
        // 画圆  大小是 中间文字的两倍
        circleLeft = mHalfWidth - centerCircleWidth / 2;
        circleRight = circleLeft + centerCircleWidth;
        Log.i(TAG, "drawCenter: circleLeft: " + circleLeft + " circleLeft: " + circleRight);
        Log.i(TAG, "drawCenter: mHalfWidth: " + mHalfWidth);
        canvas.drawRoundRect(circleLeft, 1, circleRight, getHeight(), CIRCLE_RADIUS, CIRCLE_RADIUS, circlePaint);
        // 选中文字的宽度
        String centerText = dataList.get(mSelectIndex).first;
        float centerTextWidth = centerTextPaint.measureText(centerText);
        Log.i(TAG, "drawCenter:centerTextWidth: " + centerTextWidth);
        Log.i(TAG, "drawCenter: unitText: " + unitText);
        // 没有单位
        if (TextUtils.isEmpty(unitText)) {
            Log.i(TAG, "drawCenter: is empty");
            // 只画中间的文字
            FontMetrics targetFontMetrics = centerTextPaint.getFontMetrics();
            float targetBaseLine = mHalfHeight - (targetFontMetrics.bottom + targetFontMetrics.top) / HALF;
            canvas.drawText(centerText, mHalfWidth - mMove, targetBaseLine, centerTextPaint);
            return;
        }
        // 有单位
        float unitTextWidth = Layout.getDesiredWidth(unitText, unitPaint);
        Log.i(TAG, "drawCenter: unitTextWidth: " + unitTextWidth);
        Log.i(TAG, "drawCenter: centerTextWidth: " + centerTextWidth);
        int halfTotalWidth = (int) ((unitTextWidth + centerTextWidth) / 2);
        int firstX = (int) (mHalfWidth - halfTotalWidth + (centerTextWidth / 2));
        // 中间的文字
        FontMetrics targetFontMetrics = centerTextPaint.getFontMetrics();
        float targetBaseLine = mHalfHeight - (targetFontMetrics.bottom + targetFontMetrics.top) / HALF;
        canvas.drawText(centerText, firstX - mMove, targetBaseLine, centerTextPaint);
        // 单位
        Log.i(TAG, "drawCenter: firstX:" + firstX + " halfTotalWidth: " + halfTotalWidth);
        float unitX = firstX + halfTotalWidth + (unitTextWidth / 2);
        FontMetrics fm = unitPaint.getFontMetrics();
        float baseY = mHalfHeight - (fm.bottom + fm.top) / HALF;
        Log.i(TAG, "drawCenter: unitX: " + unitX + "  baseY: " + baseY);
        canvas.drawText(unitText, unitX, baseY, unitPaint);
    }

    /**
     * 设置数据
     *
     * @param selectedValue 默认选择值
     * @param maxValue      最大值
     * @param minValue      最小值
     */
    public void setIntData(int selectedValue, int minValue, int maxValue) {
        Log.i(TAG, "setIntData: selectedValue: " + selectedValue + "  minValue: " + minValue + "  maxValue: " + maxValue);
        dataList.clear();
        for (int i = minValue; i <= maxValue; i++) {
            Pair<String, String> pair = new Pair<>(String.valueOf(i), unitText);
            dataList.add(pair);
        }
        mSelectIndex = (int) ((selectedValue - minValue) / pickerViewStep);
        invalidate();
    }

    public void setFloatData(float selectedValue, float minValue, float maxValue, float step) {
        Log.d(TAG, "setFloatData: selectedValue: " + selectedValue + "   minValue: " + minValue
                + "  maxValue: " + maxValue + "   step: " + step);
        dataList.clear();
        for (float i = minValue; i <= maxValue; i += step) {
            Pair<String, String> pair = new Pair<>(String.valueOf(i), unitText);
            dataList.add(pair);
        }
        mSelectIndex = (int) ((selectedValue - minValue) / step);
        invalidate();
    }

    public void setStringData(List<String> stringList, int selectIndex) {
        dataList.clear();
        for (String str : stringList) {
            Pair<String, String> pair = new Pair<>(str, "");
            dataList.add(pair);
        }
        mSelectIndex = selectIndex;
        invalidate();
    }

    public void setUnitText(String unitText) {
        this.unitText = unitText;
    }

    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
        circlePaint.setColor(centerTextColor);
        invalidate();
    }

    /**
     * 从中间往两边开始绘制
     */
    private void drawLeftRightText(Canvas canvas) {
        // 最多可以画多少个View
        FontMetrics textFontMetrics = normalTextPaint.getFontMetrics();
        float textBaseLine = mHalfHeight - (textFontMetrics.bottom + textFontMetrics.top) / HALF;
        String centerText = dataList.get(mSelectIndex).first;
        float normalTextWidth = normalTextPaint.measureText(centerText);
        int halfCount = (int) (((getWidth() / (normalTextWidth + 30)) + 4) / 2);
        Log.i(TAG, "drawLeftRightText: halfCount:  " + halfCount);
        // 画左边的
        // 左边的偏移量
        int padding = 20;
        int marginCirlceLeft = padding;
        // 画左边的文字
        for (int i = mSelectIndex - 1; i > mSelectIndex - halfCount; i--) {
            int realPosition = i < 0 ? dataList.size() + i : i;
            String leftText = dataList.get(realPosition).first;
            float leftTextSize = normalTextPaint.measureText(leftText);
            Log.i(TAG, "drawLeftRightText: " + leftText + " leftTextSize: " + leftTextSize);
            int centerLeftPosition = (int) (circleLeft - leftTextSize / 2 - mMove - marginCirlceLeft);
            Log.i(TAG, "drawLeftRightText: centerLeftPosition: " + centerLeftPosition + " textBaseLine: " + textBaseLine);
            canvas.drawText(leftText, centerLeftPosition, textBaseLine, normalTextPaint);
            marginCirlceLeft = (int) (marginCirlceLeft + leftTextSize + padding);
            Log.i(TAG, "drawLeftRightText: marginCirlceLeft: " + marginCirlceLeft);
        }
        int marginCirlceLeftRight = padding;
        // 画右边的文字
        for (int i = mSelectIndex + 1; i < mSelectIndex + halfCount; i++) {
            int realPosition = i;
            if (i >= dataList.size()) {
                realPosition = i - dataList.size();
            }
            String rightText = dataList.get(realPosition).first;
            float rightTextSize = normalTextPaint.measureText(rightText);
            Log.i(TAG, "drawLeftRightText: " + rightText + " leftSize: " + rightTextSize);
            int centerRightPosition = (int) (circleRight + rightTextSize / 2 - mMove + marginCirlceLeftRight);
            Log.i(TAG, "drawLeftRightText: centerRightPosition: " + centerRightPosition + " textBaseLine: " + textBaseLine);
            canvas.drawText(rightText, centerRightPosition, textBaseLine, normalTextPaint);
            marginCirlceLeftRight = (int) (marginCirlceLeftRight + rightTextSize + padding);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
                changeMoveAndValue(mMove);
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
            if (mScroller.getCurrX() == mScroller.getFinalX()) {
                // 滑动结束
                countMoveEnd();
            } else {
                // 滑动中
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                changeMoveAndValue(mMove);
                mLastX = xPosition;
            }
        }
    }

    private void changeMoveAndValue(int mMove) {
        Log.i(TAG, "changeMoveAndValue: mMoved" + mMove);
        int tValue = this.mMove / halfDivider;
        if (Math.abs(tValue) > 0) {
            mSelectIndex += tValue;
            this.mMove -= (tValue * halfDivider);
        }
        postInvalidate();
    }

    private void countMoveEnd() {
        Log.i(TAG, "countMoveEnd: mMove: " + mMove + " halfDivider: " + halfDivider);
        int roundMove = Math.round(mMove / halfDivider);
        mSelectIndex = mSelectIndex + roundMove;
        Log.d(TAG, "countMoveEnd: mSelectIndex: " + mSelectIndex);
        mLastX = 0;
        mMove = 0;
        notifyValueChange();
        postInvalidate();
    }

    private void countVelocityTracker() {
        mTracker.computeCurrentVelocity(1000);
        float xVelocity = mTracker.getXVelocity();
        Log.d(TAG, "countVelocityTracker: xVelocity: " + xVelocity);
        if (Math.abs(xVelocity) > mFlingVelocity) {
            // 滑动一段距离
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    private void notifyValueChange() {
        if (mSelectIndex == dataList.size()) {
            Log.i(TAG, "notifyValueChange: size is big return");
            return;
        }
        if (mOnSelectListener != null) {
            mOnSelectListener.onSelect(this, dataList.get(mSelectIndex));
        }
    }

    /**
     * 选择结果回调接口
     */
    public interface OnSelectListener {
        void onSelect(View view, Pair<String, String> selectedValue);
    }

    /**
     * 设置选择结果监听
     */
    public void setOnSelectListener(OnSelectListener listener) {
        this.mOnSelectListener = listener;
    }
}
