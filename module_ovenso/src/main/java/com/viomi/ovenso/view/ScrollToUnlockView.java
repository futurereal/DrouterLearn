package com.viomi.ovenso.view;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viomi.ovenso.microwave.R;


/**
 * @author hailang
 * @date 2020/9/28 002810:52
 */
public class ScrollToUnlockView extends RelativeLayout {
    final String TAG = "ScrollToUnlockView";
    int with, height;
    GestureDetector detector;
    boolean selected;
    ImageView ivButtom;
    TextView tvTips;

    OnScrollResultListener listener;

    public ScrollToUnlockView(Context context) {
        this(context, null);
    }

    public ScrollToUnlockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollToUnlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_scrollto_unlock, this);
        ivButtom = findViewById(R.id.iv_button);
        tvTips = findViewById(R.id.unlock_tips);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        with = w;
        height = h;
    }

    long startTime, endTime;
    float downx, upx;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event.getAction() + " " + event.getX());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                selected = false;
                int x = (int) event.getX();
                if (x < ivButtom.getWidth()) {
                    selected = true;
                }
                startTime = System.currentTimeMillis();
                downx = event.getX();
                tvTips.animate().alpha(0f).setDuration(200).start();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!selected) {
                    break;
                }
                float scollx = (event.getX() - ivButtom.getWidth() / 2);
                if (scollx < 0) {
                    scollx = 0;
                }
                float marginRight = ivButtom.getWidth() + ((LayoutParams) ivButtom.getLayoutParams()).leftMargin * 2;

                if (scollx > with - marginRight) {
                    scollx = with - marginRight;
                }
                if (scollx == with - marginRight) {
                    ivButtom.setImageResource(R.drawable.screem_unlock);
                    ivButtom.setBackgroundResource(R.drawable.unlock_button_unlock);
                } else {
                    ivButtom.setImageResource(R.drawable.screem_lock);
                    ivButtom.setBackgroundResource(R.drawable.unlock_button);
                }
                ivButtom.setTranslationX(scollx);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                endTime = System.currentTimeMillis();
                upx = event.getX();
                float marginRight1 = ivButtom.getWidth() + ((LayoutParams) ivButtom.getLayoutParams()).leftMargin * 2;
                float velocityX = (upx - downx) / (endTime - startTime) * 1000;
                Log.d(TAG, "onTouchEvent: " + event.getAction() + " " + event.getX() + "  velocityX:" + velocityX);
                if (upx > downx && (upx > with * 2f / 3 || velocityX > 200)) {
                    ivButtom.animate().translationXBy(with - marginRight1 - ivButtom.getTranslationX()).setDuration(100).start();
                    ivButtom.setImageResource(R.drawable.screem_unlock);
                    ivButtom.setBackgroundResource(R.drawable.unlock_button_unlock);
                    postDelayed(() -> {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }, 50);
                } else {
                    ivButtom.animate().translationX(0).setDuration(50).start();
                    if (listener != null) {
                        listener.onCancel();
                    }
                    tvTips.animate().alpha(1f).setDuration(100).start();
                }
                downx = 0;
                break;
        }
        return true;
    }


    public void setListener(OnScrollResultListener listener) {
//        this.listener = listener;
    }

    public interface OnScrollResultListener {
        void onSuccess();

        void onCancel();
    }

}
