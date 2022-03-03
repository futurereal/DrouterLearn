package com.viomi.modulesetting.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author hailang
 * @date 2020/9/8 000810:59
 */
public class HotspotLoadingView extends androidx.appcompat.widget.AppCompatImageView {
    ValueAnimator mAnimator;

    public HotspotLoadingView(@NonNull Context context) {
        super(context);
    }

    public HotspotLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HotspotLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            rotate();
        } else {
            cancel();
        }
    }

    private void rotate() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = ValueAnimator.ofInt(0, 365);
        mAnimator.addUpdateListener(valueAnimator -> setRotation((int) valueAnimator.getAnimatedValue()));
        mAnimator.setDuration(600);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.start();
    }

    void cancel() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }
}
