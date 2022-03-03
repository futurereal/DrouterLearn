package com.viomi.waterpurifier.edison.util;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 长按指定时长
 */
public class LongClickUtil {

    private static final String TAG = LongClickUtil.class.getName();

    public static final int CUSTOM_LONG_PRESS_CODE = 1001;

    /**
     * @param handler       外界handler(为了减少handler的泛滥使用,最好全局传handler引用,如果没有就直接传 new Handler())
     * @param longClickView 被长按的视图(任意控件)
     * @param delayMillis   长按时间,毫秒
     */
    public static void setLongClick(Handler handler, View longClickView, long delayMillis) {
        longClickView.setOnTouchListener(new View.OnTouchListener() {
            private final int MOVE_MAX = 80;
            private int mLastMotionX;
            private int mLastMotionY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "ACTION_DOWN  start " + System.currentTimeMillis());
                        handler.removeMessages(CUSTOM_LONG_PRESS_CODE);
                        mLastMotionX = x;
                        mLastMotionY = y;
                        handler.sendEmptyMessageDelayed(CUSTOM_LONG_PRESS_CODE, delayMillis);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i(TAG, "ACTION_MOVE");
                        Log.e(TAG, "x - mLastMotionX : " + (x - mLastMotionX) + "  y - mLastMotionY : " + (y - mLastMotionY));
                        if (Math.abs(x - mLastMotionX) >= MOVE_MAX
                                || Math.abs(y - mLastMotionY) >= MOVE_MAX) {
                            handler.removeMessages(CUSTOM_LONG_PRESS_CODE);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "ACTION_UP");
                        handler.removeMessages(CUSTOM_LONG_PRESS_CODE);
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
    }

}
