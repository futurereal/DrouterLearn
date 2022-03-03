package com.viomi.ovenso.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.viomi.ovenso.microwave.R;


public class StatusBarView extends View {
    private final Paint paint;
    private int mWidth;
    private int mHeight;

    private float mDx = 0;
    private float mFactor = 0;
    private float maxValue = 31;
    private float minValue = 16;
    private float value = 16;

    private final float DEFAULT_MAX_VALUE = 31;
    private final float DEFAULT_MIN_VALUE = 16;
    private final float DEFAULT_VALUE = 16;

    private Callback mCallback;
    private boolean enabled = true;

    public StatusBarView(Context context) {
        this(context,null);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mFactor = (mWidth - mHeight) / (maxValue - minValue);
        mDx = mFactor * (value - minValue);
        paint.setColor(getResources().getColor(R.color.viomi_green));
        canvas.drawRoundRect(0, 0, mHeight + mDx, mHeight, 15, 15, paint);
        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawRoundRect(mHeight + mDx - 17 - 8, 15f, mHeight + mDx - 17, mHeight - 15, 4, 4, paint);
    }

    private void init(Context context, AttributeSet attrs) {
        handleTypedArray(context, attrs);
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusBarView);
        maxValue = typedArray.getFloat(R.styleable.StatusBarView_tv_max_value, DEFAULT_MAX_VALUE);
        minValue = typedArray.getFloat(R.styleable.StatusBarView_tv_min_value, DEFAULT_MIN_VALUE);
        value = typedArray.getFloat(R.styleable.StatusBarView_tv_value, DEFAULT_VALUE);
        typedArray.recycle();
    }

    public void initValue(int max, int min, int value) {
        this.maxValue = max;
        this.minValue = min;
        this.value = value;
        invalidate();
    }

    public void setValue(int value) {
        this.value = value;
        invalidate();
    }

    public void setMinValue(int value) {
        this.minValue = value;
    }

    public void setMaxValue(int value) {
        this.maxValue = value;
    }

    public void getValue(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(enabled);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!enabled)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                mDx = event.getX();
                if (mDx > (mWidth - mHeight)) {
                    mDx = mWidth - mHeight;
                    value = maxValue;
                } else if (mDx <= 0) {
                    mDx = 0;
                    value = minValue;
                } else {
                    value = (int) mDx / mFactor + minValue;
                }
                if (mCallback != null) {
                    mCallback.onGetTemp((int) value);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mCallback != null) {
                    mCallback.onSetTemp((int) value);
                }
                //getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }


    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //int layerId = canvas.saveLayer(0, 0, mWidth, mHeight, paint, Canvas.ALL_SAVE_FLAG);
        //canvas.save();
        //paint.setShadowLayer(40, 0, 16, getResources().getColor(R.color.black_06_transparent));
        //paint.setColor(mContext.getResources().getColor(R.color.bg_temp_text));
        //paint.setStyle(Paint.Style.STROKE);
        //paint.setStrokeWidth(5);
        //canvas.drawRoundRect(0, 0, mHeight + 36 + mDx, mHeight, mHeight / 2, mHeight / 2, paint);
        //canvas.restoreToCount(layerId);
        //canvas.restore();
    }

    public interface Callback {
        void onGetTemp(int value);

        void onSetTemp(int value);
    }
}
