package com.viomi.modulesetting.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.viomi.vlog.Vlog

/**
 *
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * <b>Project:</b> viomi_washer <br>
 * <b>Package:</b> com.viomi.washer.widget <br>
 * <b>Create Date:</b> 2020/11/6 <br>
 * <b>@author:</b> qingyong <br>
 * <b>Address:</b> qingyong@viomi.com <br>
 * <b>Description:</b> 长按虽然是这里实现的，但是解锁逻辑，我们直接使用LockProgressView的回调就行了，那里回调了，
 * 直接改这里的view的设置，那里没回调，这里只要走到up 或者 cancel 就是失败的<br>
 */
class LongTouchImageView : AppCompatImageView {

    /**
     * 进度更新监听
     */
    interface OnLongPressListener {
        /**
         * 开始
         */
        fun onStart()

        /**
         * 取消
         */
        fun onCancel()
    }

    private var startTime: Long = 0
    private var pressEnd = false
    private var longPressListener: OnLongPressListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        isClickable = true
    }

    fun setListener(listener: OnLongPressListener) {
        this.longPressListener = listener
    }

    fun setPressEnd() {
        pressEnd = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startTime = event.eventTime
                Vlog.i("LongTouchImageView", "Down Time:$startTime")
                longPressListener?.onStart()
                pressEnd = false
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                var end = event.eventTime
                Vlog.i("LongTouchImageView", "Up Time:$end diff:" + (end - startTime))
                if (!pressEnd) {
                    //已经结束了就不用管这里了
                    longPressListener?.onCancel()
                }
            }
        }
        return super.onTouchEvent(event)
    }

}