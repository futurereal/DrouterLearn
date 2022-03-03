package com.viomi.modulesetting.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.viomi.modulesetting.R
import kotlin.math.min


/**
 *
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * <b>Project:</b> viomi_washer <br>
 * <b>Package:</b> com.viomi.washer.widget <br>
 * <b>Create Date:</b> 2020/11/5 <br>
 * <b>@author:</b> qingyong <br>
 * <b>Address:</b> qingyong@viomi.com <br>
 * <b>Description:</b>  <br>
 */
open class LockProgressView : View {

    private val DEFAULT_MAX = 100
    private val MAX_DEGREE = 360.0F

    private var mText = ""
    private var mTextSize = 15
    private var mCurrentColor = 0
    private var mTextColor: ColorStateList? = null
    private var mDrawablePadding = 0
    private var mDrawable: Drawable? = null

    private val mRect = Rect()
    private val mPoint = PointF()
    private lateinit var mTextPaint: TextPaint

    /**
     * 边框的宽度
     */
    private var mBorderCircleWidth = 8F

    /**
     * 边框圆圈的内层背景色
     */
    private var mBorderInnerCircleColor = Color.parseColor("#082628")

    /**
     * 边框圆圈的进度背景色
     */
    private var mBorderOuterCircleStartColor = Color.parseColor("#087F7E")
    private var mBorderOuterCircleEndColor = Color.parseColor("#7EF1F4")

    private lateinit var mCirclePaint: Paint
    private lateinit var mShader: SweepGradient
    private val mProgressRectF = RectF()
    private var mRadius: Float = 0F
    private var mProgress: Float = 0F
    private var mMax: Int = DEFAULT_MAX
    private var mCenterX = 0F
    private var mCenterY = 0F

    /**
     * 弧线的开始角度，默认是0，是水平的，我们要从下面开始画
     */
    private var mStartAngle: Float = -90F

    /**
     * 进度条进度
     */
    private var isUpdateing: Boolean = false
    private var mProgressInterrupted = false
    private var mProgressAnimator: ValueAnimator? = null
    private var mProgressUpdateListener: OnProgressUpdateListener? = null

    /**
     * 进度更新监听
     */
    interface OnProgressUpdateListener {
        /**
         * 开始
         */
        fun onStart()

        /**
         * 进度更新
         *
         * @param currentProgress 当前进度
         */
        fun onProgressUpdate(currentProgress: Int)

        /**
         * 结束
         */
        fun onEnd()
    }

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        val typedArray: TypedArray? = context?.obtainStyledAttributes(attrs, R.styleable.LockProgressView)
        typedArray?.let {
            mText = it.getText(R.styleable.LockProgressView_android_text) as String
            if (TextUtils.isEmpty(mText)) {
                mText = "解锁中…"
            }
            mTextSize = it.getDimensionPixelSize(R.styleable.LockProgressView_android_textSize, 24)
            mTextColor = it.getColorStateList(R.styleable.LockProgressView_android_textColor)
            mDrawablePadding = it.getDimensionPixelSize(R.styleable.LockProgressView_android_drawablePadding, 0)
            mDrawable = it.getDrawable(R.styleable.LockProgressView_lock_progress_src)
            mBorderCircleWidth = it.getDimensionPixelSize(R.styleable.LockProgressView_lock_progress_border_circle_width, 8).toFloat()
            mBorderInnerCircleColor = it.getColor(R.styleable.LockProgressView_lock_progress_border_inner_circle_color, mBorderInnerCircleColor)
            mBorderOuterCircleStartColor = it.getColor(R.styleable.LockProgressView_lock_progress_border_outer_circle_start_color, mBorderOuterCircleStartColor)
            mBorderOuterCircleEndColor = it.getColor(R.styleable.LockProgressView_lock_progress_border_outer_circle_end_color, mBorderOuterCircleEndColor)
            mMax = it.getInt(R.styleable.LockProgressView_lock_progress_max, mMax)
            mProgress = it.getFloat(R.styleable.LockProgressView_lock_progress_current, mProgress)
            mStartAngle = it.getFloat(R.styleable.LockProgressView_lock_start_degree, mStartAngle)
            it.recycle()
            mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.LINEAR_TEXT_FLAG)
            if (mTextColor == null) {
                mTextColor = ColorStateList.valueOf(Color.WHITE)
            }
            val color = mTextColor!!.getColorForState(drawableState, 0)
            if (color != mCurrentColor) {
                mCurrentColor = color
            }
            mTextPaint.color = mCurrentColor
            mTextPaint.textSize = mTextSize.toFloat()

            mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.LINEAR_TEXT_FLAG)
            mCirclePaint.color = mBorderInnerCircleColor
            mCirclePaint.strokeWidth = mBorderCircleWidth
            mCirclePaint.style = Paint.Style.STROKE
            //设置笔触为圆角
            mCirclePaint.strokeCap = Paint.Cap.ROUND
        }
    }

    /**
     * 指定时间，开始进度
     *
     * @param from 从哪个进度开始
     * @param to 到哪个进度结束
     * @param duration    执行时间
     */
    open fun startProgressByTime(from: Int, to: Int, duration: Long) {
        if (mProgressAnimator == null) {
            mProgressAnimator = ValueAnimator.ofInt(from, to)
        }
        mProgressInterrupted = false
        mProgressAnimator!!.interpolator = LinearInterpolator()
        mProgressAnimator!!.duration = duration
        mProgressAnimator!!.addUpdateListener { animation ->
            val cValue = animation.animatedValue as Int
            setProgress(cValue.toFloat())
            mProgressUpdateListener?.onProgressUpdate(cValue)
        }
        mProgressAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                isUpdateing = true
                if (mProgressInterrupted) return
                mProgressUpdateListener?.onStart()
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isUpdateing = false
                //结束，设置回0
                setProgress(0F)
                if (mProgressInterrupted) return
                mProgressUpdateListener?.onEnd()
            }
        })
        mProgressAnimator!!.start()
    }

    open fun cancelProgress() {
        mProgressInterrupted = true
        mProgressAnimator?.cancel()
    }

    open fun getProgress(): Float {
        return mProgress
    }

    @Synchronized
    open fun setProgress(progress: Float) {
        this.mProgress = progress
        postInvalidate()
    }

    open fun getMax(): Int {
        return mMax
    }

    @Synchronized
    open fun setMax(max: Int) {
        mMax = max
        postInvalidate()
    }

    open fun setListener(listener: OnProgressUpdateListener) {
        this.mProgressUpdateListener = listener
    }

    /**
     * 获取drawable的宽度
     *
     * @return 宽度
     */
    private fun getDrawableWidth(): Int {
        return if (mDrawable != null) mDrawable!!.intrinsicWidth else 0
    }

    /**
     * topText宽度
     *
     * @return 宽度
     */
    private fun getTextWidth(): Float {
        return mTextPaint.measureText(mText)
    }

    /**
     * 获取drawable的宽度
     *
     * @return 宽度
     */
    private fun getDrawableHeight(): Int {
        return if (mDrawable != null) mDrawable!!.intrinsicHeight else 0
    }

    /**
     * text高度
     *
     * @return 高度
     */
    private fun getTextHeight(): Float {
        return mTextPaint.descent() - mTextPaint.ascent()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (mTextColor != null && mTextColor!!.isStateful) {
            updateTextColors()
        }
    }

    /**
     * 更新颜色
     */
    private fun updateTextColors() {
        var inval = false
        val color = mTextColor!!.getColorForState(drawableState, 0)
        if (color != mCurrentColor) {
            mCurrentColor = color
            inval = true
        }
        if (inval) {
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mProgressRectF.left = paddingLeft.toFloat()
        mProgressRectF.top = paddingTop.toFloat()
        mProgressRectF.right = (w - paddingRight).toFloat()
        mProgressRectF.bottom = (h - paddingBottom).toFloat()
        mCenterX = mProgressRectF.centerX()
        mCenterY = mProgressRectF.centerY()
        mRadius = min(mProgressRectF.width(), mProgressRectF.height()) / 2
        //得考虑画圆的线的宽度
        mProgressRectF.inset(mBorderCircleWidth / 2, mBorderCircleWidth / 2)
        mShader = SweepGradient(mCenterX, mCenterY, intArrayOf(mBorderOuterCircleStartColor, mBorderOuterCircleEndColor),
                floatArrayOf(0.0F, 1.0F))
        //arc = radian * radius
        val radian = mBorderCircleWidth / Math.PI * 2.0F / mRadius
        val rotateDegrees = -Math.toDegrees(radian)
        val matrix = Matrix()
        matrix.setRotate(rotateDegrees.toFloat(), mCenterY, mCenterY)
        mShader.setLocalMatrix(matrix)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureSpec(widthMeasureSpec), measureSpec(heightMeasureSpec))
    }

    private fun measureSpec(measureSpec: Int): Int {
        val result: Int
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        //默认大小
        val defaultSize = 232
        //指定宽高则直接返回
        result = when (mode) {
            MeasureSpec.EXACTLY -> {
                size
            }
            MeasureSpec.AT_MOST -> {
                //wrap_content的情况
                min(defaultSize, size)
            }
            else -> { //未指定，则使用默认的大小
                defaultSize
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.rotate(mStartAngle, mCenterX, mCenterY)
        mCirclePaint.shader = null
        mCirclePaint.color = mBorderInnerCircleColor
        canvas.drawArc(mProgressRectF, 0F, MAX_DEGREE, false, mCirclePaint)
        mCirclePaint.shader = mShader
        mCirclePaint.color = mBorderOuterCircleStartColor
        val angle: Float = MAX_DEGREE * mProgress / getMax()
        canvas.drawArc(mProgressRectF, 0F, angle, false, mCirclePaint)
        canvas.restore()
        // 图片，图片和文字距离，文字，竖直排列
        if (mDrawable != null) {
            val allHeight = getDrawableHeight() + mDrawablePadding + getTextHeight()
            val left = width / 2 - getDrawableWidth() / 2
            val right = left + getDrawableWidth()
            val top = height / 2 - allHeight / 2
            val bottom = top + getDrawableHeight()
            mRect.set(left, top.toInt(), right, bottom.toInt())
            mDrawable!!.bounds = mRect
            mDrawable!!.draw(canvas)
            mPoint.x = width / 2 - getTextWidth() / 2
            mPoint.y = height / 2 + allHeight / 2 - mTextPaint.descent()
            canvas.drawText(mText, mPoint.x, mPoint.y, mTextPaint)
        } else {
            mPoint.x = width / 2 - getTextWidth() / 2
            mPoint.y = height / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)
            canvas.drawText(mText, mPoint.x, mPoint.y, mTextPaint)
        }
    }

}