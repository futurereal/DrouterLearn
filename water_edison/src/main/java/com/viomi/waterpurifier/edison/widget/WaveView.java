package com.viomi.waterpurifier.edison.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

public class WaveView extends View {
    private final static String TAG = WaveView.class.getSimpleName();
    private int mWaveLength;
    private int mScreenWidth;
    private int mScreenHeight;
    private int targetCenterY = 95;
    private int mCenterY = targetCenterY;
    private int mWaveCount;
    private int mWaveWith;
    private int offset;
    private int offset2;
    private int yoffset;
    private final int color = Color.parseColor("#7000AAFF");
    private int seconwaveOffset;

    private Path mPath;
    private Paint mPaint;
    private Paint mPaintCorner;
    RectF rectCorner = new RectF();


    int radius = 20;
    PorterDuffXfermode xfermode;
    private ValueAnimator mValueAnimatior;
    private ValueAnimator mValueAnimatior2;
    private ValueAnimator mValueAnimatior3;

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(color);

        mPaintCorner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCorner.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintCorner.setColor(color);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 需要计算出屏幕能容纳多少个波形
        mPath = new Path();
        mScreenHeight = h;
        mScreenWidth = w;
        mCenterY = h / 2;
        mWaveCount = 2; // 计算波形的个数
        mWaveWith = mScreenWidth / 3;
        seconwaveOffset = mScreenWidth / 5;
        mWaveLength = mWaveWith * 4;
        yoffset = h / 6;
        targetCenterY = mScreenHeight + yoffset;
        mCenterY = targetCenterY;
        rectCorner = new RectF(0, 0, mScreenWidth, mScreenHeight);
        start();
        setLevel(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mValueAnimatior == null || !mValueAnimatior.isRunning()) {
            return;
        }
        //第一波
        mPath.reset();
        mPath.moveTo(-mWaveLength, mCenterY);
        mPath.quadTo(mWaveWith * -4 + offset, mCenterY - yoffset, mWaveWith * -3 + offset, mCenterY);
        mPath.quadTo(mWaveWith * -2 + offset, mCenterY + yoffset, mWaveWith * -1 + offset, mCenterY);
        mPath.quadTo(0 + offset, mCenterY - yoffset, mWaveWith + offset, mCenterY);
        mPath.quadTo(mWaveWith * 2 + offset, mCenterY + yoffset, mWaveWith * 3 + offset, mCenterY);
        mPath.quadTo(mWaveWith * 4 + offset, mCenterY - yoffset, mWaveWith * 5 + offset, mCenterY);
        mPath.lineTo(mScreenWidth, mScreenHeight);
        mPath.lineTo(0, mScreenHeight);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
        mPaint.setXfermode(null);

        //第二波
        mPath.reset();
        mPath.quadTo(mWaveWith * -2 + seconwaveOffset - offset2, mCenterY + yoffset, mWaveWith * -1 + seconwaveOffset - offset2, mCenterY);
        mPath.quadTo(0 + seconwaveOffset - offset2, mCenterY - yoffset, mWaveWith + seconwaveOffset - offset2, mCenterY);
        mPath.quadTo(mWaveWith * 2 + seconwaveOffset - offset2, mCenterY + yoffset, mWaveWith * 3 + seconwaveOffset - offset2, mCenterY);
        mPath.quadTo(mWaveWith * 4 + seconwaveOffset - offset2, mCenterY - yoffset, mWaveWith * 5 + seconwaveOffset - offset2, mCenterY);
        mPath.quadTo(mWaveWith * 6 + seconwaveOffset - offset2, mCenterY + yoffset, mWaveWith * 7 + seconwaveOffset - offset2, mCenterY);
        mPath.quadTo(mWaveWith * 8 + seconwaveOffset - offset2, mCenterY - yoffset, mWaveWith * 9 + seconwaveOffset - offset2, mCenterY);
        mPath.lineTo(mScreenWidth, mScreenHeight);
        mPath.lineTo(0, mScreenHeight);
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        //左圆角
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        mPaintCorner.setColor(Color.BLACK);
        canvas.drawRect(0, getHeight() - radius, radius, getHeight(), mPaintCorner);
        mPaintCorner.setXfermode(xfermode);
        mPaintCorner.setColor(Color.WHITE);
        canvas.drawCircle(radius, getHeight() - radius, radius, mPaintCorner);
        mPaintCorner.setXfermode(null);
        canvas.restoreToCount(layerId);

        //右圆角
        layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        mPaintCorner.setColor(Color.BLACK);
        canvas.drawRect(getWidth() - radius, getHeight() - radius, getWidth(), getHeight(), mPaintCorner);
        mPaintCorner.setXfermode(xfermode);
        mPaintCorner.setColor(Color.WHITE);
        canvas.drawCircle(getWidth() - radius, getHeight() - radius, radius, mPaintCorner);
        mPaintCorner.setXfermode(null);
        canvas.restoreToCount(layerId);
    }

    public void start() {
        if (mWaveLength <= 0) {
            return;
        }
        if (mValueAnimatior != null && mValueAnimatior.isRunning()) {
            return;
        }
        mValueAnimatior = ValueAnimator.ofInt(0, mWaveLength);
        mValueAnimatior.setDuration(3000);
        mValueAnimatior.setInterpolator(new LinearInterpolator());
        mValueAnimatior.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimatior.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimatior.start();

        mValueAnimatior2 = ValueAnimator.ofInt(0, mWaveLength);
        mValueAnimatior2.setDuration(2000);
        mValueAnimatior2.setInterpolator(new LinearInterpolator());
        mValueAnimatior2.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimatior2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset2 = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        mValueAnimatior2.start();

        mValueAnimatior3 = ValueAnimator.ofInt(getHeight() + yoffset, targetCenterY);
        mValueAnimatior3.setDuration(500);
        mValueAnimatior3.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimatior3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCenterY = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimatior3.start();
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimatior != null) {
            mValueAnimatior.cancel();
        }
        if (mValueAnimatior2 != null) {
            mValueAnimatior2.cancel();
        }
        if (mValueAnimatior3 != null) {
            mValueAnimatior3.cancel();
        }
        Log.i("wave", "onDetachedFromWindow");
    }

    public void stop() {
        if (mValueAnimatior == null || !mValueAnimatior.isRunning()) {
            return;
        }
        mValueAnimatior3 = ValueAnimator.ofInt(targetCenterY, getHeight() + yoffset);
        mValueAnimatior3.setDuration(500);
        mValueAnimatior3.setInterpolator(new AccelerateInterpolator());
        mValueAnimatior3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCenterY = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimatior3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mValueAnimatior != null) {
                    mValueAnimatior.cancel();
                }
                if (mValueAnimatior2 != null) {
                    mValueAnimatior2.cancel();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mValueAnimatior3.start();
    }

    /**
     * 1 低,2 中,3 高
     */
    public void setLevel(int level) {
        switch (level) {
            case 3:
                targetCenterY = 35;
                break;
            case 2:
                targetCenterY = 65;
                break;
            default:
                targetCenterY = 95;
                break;
        }
        mValueAnimatior3 = ValueAnimator.ofInt(mCenterY, targetCenterY);
        mValueAnimatior3.setDuration(500);
        mValueAnimatior3.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimatior3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCenterY = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimatior3.start();
    }

}
