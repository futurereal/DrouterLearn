package com.viomi.ovensocommon.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 自定义的SeekBar
 * @Author: randysu
 */
public class CommonSettingSeekBar extends View {
    private static final String TAG = "CommonSettingSeekBar";
    private Paint drawPaint;
    private final int backgroundColor = Color.parseColor("#142739");
    private final int processColor = Color.parseColor("#28BECA");
    private final int devideColor = Color.WHITE;

    private final Context mContext;

    private static int BG_ROUND_CONNER;
    private static int DEVIDE_ROUND_CONNER;
    private static int DEVIDE_WIDTH;

    // 拖动
    private int indicatorWidth = 0;
    private float tempTouchX = 0;
    private float stepWidth = 0;
    private float indicatorEndX = 0;


    boolean showValue = false;
    int maxValue = 100;
    int minValue;
    int progressValue;
    int step = 1;
    int margin;

    private OnValueChangeListener listener;

    public CommonSettingSeekBar(Context context) {
        this(context, null);
    }

    public CommonSettingSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonSettingSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        Log.i(TAG, "CommonSettingSeekBar: constructure ");
        init(context);
    }

    private void init(Context context) {
        Log.i(TAG, "init: ");
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        margin = 20;
        BG_ROUND_CONNER = dpToPx(context, 15);
        DEVIDE_ROUND_CONNER = dpToPx(context, 5);
        DEVIDE_WIDTH = dpToPx(context, 8);
        indicatorWidth = dpToPx(mContext, 42);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = dpToPx(mContext, 66);
        Log.i(TAG, "onMeasure: width: " + width + " height: " + height);
        setMeasuredDimension(width, height);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: ");
        int measureWidth = getMeasuredWidth();
        int measureHeight = getMeasuredHeight();

        // 画背景
        drawPaint.setColor(backgroundColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(0, 0, measureWidth, measureHeight, BG_ROUND_CONNER, BG_ROUND_CONNER, drawPaint);
        } else {
            canvas.drawRect(0, 0, measureWidth, measureHeight, drawPaint);
        }
        //画最大值
        if (showValue) {
            Paint.FontMetrics fontMetrics = drawPaint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            float baseline = measureHeight / 2f + distance;
            //max
            drawPaint.setColor(Color.parseColor("#99ffffff"));
            drawPaint.setTextAlign(Paint.Align.RIGHT);
            drawPaint.setTextSize(28);
            canvas.drawText(maxValue + "", measureWidth - margin, baseline, drawPaint);
            drawPaint.setAlpha(255);
        }

        // 画进度背景
        drawPaint.setColor(processColor);
        Log.i(TAG, "onDraw: indicatorEndX : " + indicatorEndX);
        RectF processRect = new RectF(0, 0, indicatorEndX, measureHeight);
        canvas.drawRoundRect(processRect, BG_ROUND_CONNER, BG_ROUND_CONNER, drawPaint);

        // 中间 的条
        drawPaint.setColor(devideColor);
        float devideLeft = indicatorEndX - dpToPx(mContext, 17) - DEVIDE_WIDTH;
        float devideTop = dpToPx(mContext, 16);
        float devideRight = devideLeft + DEVIDE_WIDTH;
        float devideBottom = measureHeight - dpToPx(mContext, 16);
        RectF devideRect = new RectF(devideLeft, devideTop, devideRight, devideBottom);
        canvas.drawRoundRect(devideRect, DEVIDE_ROUND_CONNER, DEVIDE_ROUND_CONNER, drawPaint);
        // 是否显示文字提示
        if (showValue == false) {
            return;
        }
        Paint.FontMetrics fontMetrics = drawPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = measureHeight / 2f + distance;
        //min
        float textWith = drawPaint.measureText(minValue + "");
        if (devideLeft > textWith + margin * 2) {
            drawPaint.setColor(Color.parseColor("#99ffffff"));
            drawPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(minValue + "", margin, baseline, drawPaint);
        }
        //current value
        drawPaint.setColor(Color.WHITE);
        float valueWith = drawPaint.measureText(maxValue + "");
        if (devideLeft - margin - valueWith > textWith + margin * 2) {
            drawPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(progressValue + "", devideLeft - margin, baseline, drawPaint);
        } else {
            drawPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(progressValue + "", devideRight + margin, baseline, drawPaint);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "onSizeChanged: w: " + w + " h: " + h);
        refresh();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (listener != null && listener.beforeTouch()) {
            return false;
        }
        int measureWidth = getMeasuredWidth();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                tempTouchX = event.getX();

                if (tempTouchX < indicatorWidth / 2) {
                    indicatorEndX = indicatorWidth;
                } else if (tempTouchX > measureWidth - indicatorWidth / 2) {
                    indicatorEndX = measureWidth;
                } else {
                    indicatorEndX = tempTouchX + indicatorWidth / 2;
                }

                int steps = Math.round((indicatorEndX - indicatorWidth) / stepWidth);
                progressValue = minValue + steps * step;
                if (progressValue < minValue) {
                    progressValue = minValue;
                } else if (progressValue > maxValue) {
                    progressValue = maxValue;
                }
                invalidate();

                if (listener != null) {
                    listener.onTouch();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                float currentX = event.getX();
                float offset = currentX - tempTouchX;  // 上一次对比当前位置的偏移量

                if (currentX < indicatorWidth / 2) {
                    indicatorEndX = indicatorWidth;
                } else if (currentX > measureWidth - indicatorWidth / 2) {
                    indicatorEndX = measureWidth;
                } else {
                    indicatorEndX = indicatorEndX + offset;
                }

                int steps1 = Math.round((indicatorEndX - indicatorWidth) / stepWidth);
                progressValue = minValue + steps1 * step;
                if (progressValue < minValue) {
                    progressValue = minValue;
                } else if (progressValue > maxValue) {
                    progressValue = maxValue;
                }
                indicatorEndX = (progressValue - minValue) * 1f / step * stepWidth + indicatorWidth;
                currentX = indicatorEndX;

                invalidate();
                tempTouchX = currentX;

                if (listener != null) {
                    listener.onDragging(progressValue);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(true);
                if (listener != null) {
                    listener.onSelectedPercentage(progressValue);
                }
                break;
        }

        return true;
    }


    /**
     * 设置当前百分比
     *
     * @param progressValue
     */
    public void setProgressValue(int progressValue) {
        Log.i(TAG, "setProgressValue: progressValue: " + progressValue);
        if (progressValue < minValue) {
            progressValue = minValue;
        }
        if (progressValue > maxValue) {
            progressValue = maxValue;
        }
        this.progressValue = progressValue;
        postDelayed(() -> refresh(), 50);
    }

    public void setStep(int step) {
        this.step = step;
        postDelayed(() -> refresh(), 50);
    }

    void refresh() {
        stepWidth = (getMeasuredWidth() - indicatorWidth) * 1f / (maxValue - minValue) * step;
        indicatorEndX = (progressValue - minValue) * 1f / step * stepWidth + indicatorWidth;
        Log.i(TAG, "refresh: stepWidth: " + stepWidth);
        invalidate();
    }


    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        if (progressValue > minValue) {
            progressValue = minValue;
        }
        postDelayed(() -> refresh(), 50);
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        if (progressValue < minValue) {
            progressValue = minValue;
        }
        postDelayed(() -> refresh(), 50);
    }

    public void setShowValue(boolean showValue) {
        this.showValue = showValue;
        refresh();
    }

    /**
     * @param listener
     */
    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public int getStepWidth() {
        return step;
    }

    /**
     *
     */
    public interface OnValueChangeListener {
        void onSelectedPercentage(int selectedPercentage);

        void onTouch();

        boolean beforeTouch();

        void onDragging(int selectedPercentage);
    }

    /**
     * dp 转 px
     */
    public int dpToPx(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

}
