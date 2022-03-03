package com.viomi.waterpurifier.edison.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.SizeUtils;
import com.viomi.waterpurifier.edison.R;

/**
 *
 */
public class CirclePercentBar extends View {
    private static final String TAG = "CirclePercentBar";
    private final Context mContext;

    private final int mArcColor;
    private final int mArcWidth;
    private final int mCenterTextColor;
    private final int mCenterTextSize;
    //    private int mCircleRadius;
    private Paint arcPaint;
    private Paint arcCirclePaint;
    private Paint centerTextPaint;
    private RectF arcRectF;
    private Rect textBoundRect;
    private float currentProgress = 0f;
    private final String mSuffix = "%";
    private final int arcStartColor;
    private final int arcEndColor;
    private Paint startCirclePaint;
    private float mCenterX;
    private float mCenterY;
    private boolean isProgressVisible = true;

    public CirclePercentBar(Context context) {
        this(context, null);
    }

    public CirclePercentBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePercentBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirclePercentBar, defStyleAttr, 0);
        mArcColor = typedArray.getColor(R.styleable.CirclePercentBar_arcColor, 0xff0000);
        mArcWidth = typedArray.getDimensionPixelSize(R.styleable.CirclePercentBar_arcWidth, SizeUtils.dp2px(20));
        mCenterTextColor = typedArray.getColor(R.styleable.CirclePercentBar_centerTextColor, 0x0000ff);
        mCenterTextSize = typedArray.getDimensionPixelSize(R.styleable.CirclePercentBar_centerTextSize, SizeUtils.dp2px(20));
//        mCircleRadius = typedArray.getDimensionPixelSize(R.styleable.CirclePercentBar_circleRadius, SizeUtils.dp2px(100));
        arcStartColor = typedArray.getColor(R.styleable.CirclePercentBar_arcStartColor,
                ContextCompat.getColor(mContext, R.color.white));
        arcEndColor = typedArray.getColor(R.styleable.CirclePercentBar_arcEndColor,
                ContextCompat.getColor(mContext, R.color.black));
        Log.i(TAG, "CirclePercentBar:mArcWidth:  " + mArcWidth);
        typedArray.recycle();

        initPaint();

    }

    private void initPaint() {
        Log.i(TAG, "initPaint: ");
        startCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        startCirclePaint.setStyle(Paint.Style.FILL);
        startCirclePaint.setStrokeWidth(mArcWidth);
        startCirclePaint.setColor(arcStartColor);
        // 圆圈
        arcCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcCirclePaint.setStyle(Paint.Style.STROKE);
        arcCirclePaint.setStrokeWidth(mArcWidth);
        arcCirclePaint.setColor(arcStartColor);
        arcCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        // 圆弧
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(mArcWidth);
        arcPaint.setColor(mArcColor);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        // 文字
        centerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerTextPaint.setStyle(Paint.Style.FILL);
        centerTextPaint.setColor(mCenterTextColor);
        centerTextPaint.setTextSize(mCenterTextSize);

        //圓弧的外接矩形
        arcRectF = new RectF();
        //文字的边界矩形
        textBoundRect = new Rect();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.i(TAG, "measureDimension: specMode = " + specMode);
        Log.i(TAG, "measureDimension: specSize = " + specSize);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        Log.i(TAG, "measureDimension: result: " + result);
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw: getWidth: " + getWidth());
        Log.i(TAG, "onDraw: getHeight: " + getHeight());
        canvas.save();
        // 从顶部开始
        canvas.rotate(-90, mCenterX, mCenterY);
        // 外圈
        canvas.drawArc(arcRectF, 0, 360, false, arcCirclePaint);
        float sweepAngle = 360 * currentProgress / 100;
        // 内圈
        canvas.drawArc(arcRectF, 0, sweepAngle, false, arcPaint);
        canvas.restore();
        if (isProgressVisible == false) {
            return;
        }
        // 文字
        centerTextPaint.setTextSize(120);
        String data = String.valueOf((int) currentProgress);
        centerTextPaint.getTextBounds(data, 0, data.length(), textBoundRect);
        canvas.drawText(data, getWidth() / 2 - textBoundRect.width() / 2, getHeight() / 2 + textBoundRect.height() / 2, centerTextPaint);
        float textWidth = centerTextPaint.measureText(data);
        centerTextPaint.setTextSize(30);
        canvas.drawText(mSuffix, getWidth() / 2 - textBoundRect.width() / 2 + textWidth, getHeight() / 2 + textBoundRect.height() / 2 - 50, centerTextPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "onSizeChanged: width: " + w + " height: " + h);
        arcRectF.left = getPaddingLeft();
        arcRectF.top = getPaddingTop();
        arcRectF.right = (w - getPaddingRight());
        arcRectF.bottom = (h - getPaddingBottom());
        mCenterX = arcRectF.centerX();
        mCenterY = arcRectF.centerY();
        //得考虑画圆的线的宽度
        arcRectF.inset(mArcWidth / 2, mArcWidth / 2);
    }

    public void setCurrentProgress(float progress) {
        currentProgress = progress;
        invalidate();
    }

    public void setProgressVisible(boolean isProgressVisible) {
        this.isProgressVisible = isProgressVisible;
        invalidate();
    }
}
