package com.viomi.ovenso.view;


import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

public class RotateImage extends AppCompatImageView {
    private static final String TAG = "RotateImage";
    ValueAnimator mAnimator;

    public RotateImage(Context context) {
        super(context);
        init();
    }

    public RotateImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotateImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        mAnimator = ValueAnimator.ofInt(0, 365);
        mAnimator.addUpdateListener(valueAnimator -> setRotation((int) valueAnimator.getAnimatedValue()));
        mAnimator.setDuration(4000);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {

        } else {
            rotate(false);
        }
    }
    public void rotate(boolean isOn) {
        Log.i(TAG, "rotate: isOn : "+isOn);
        if (mAnimator == null || getVisibility() != View.VISIBLE) {
            Log.i(TAG, "rotate: is null or hide return ");
            return;
        }
        if (isOn) {
            mAnimator.start();
        } else {
            mAnimator.cancel();
        }
    }
}
