package com.viomi.waterpurifier.edison.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.viomi.waterpurifier.edison.R
import kotlin.math.min


/**
 * 自定义的 进度条师徒
 */
open class CircleProgressView : View {

    private lateinit var commonPaint: Paint
    private var circleInnerStyle: Int? = 0
    private var textTipUnit: String? = null
    private var centerTipDrawableId: Drawable? = null
    private var startAngleEdgreee: Float = 0f
    private var circleOutVisible: Boolean = false
    private var textTipSize: Int = 0
    private val DEFAULT_MAX = 100
    private val MAX_DEGREE = 360.0F

    private var textTip = ""
    private var textTipColor = 0

    private val mRect = Rect()
    private val mPoint = PointF()
    private lateinit var textTipPaint: TextPaint

    val PROGRESS_START = 0
    val PROGRESS_END = 100
    val PROGRESS_TOTAL_TIME = 3000L


    /**
     * 边框的宽度
     */
    private var circleWidth = 8F

    /**
     * 边框圆圈的内层背景色
     */
    private var circleInnerColor = 0

    /**
     * 边框圆圈的进度背景色
     */
    private var circleOutStartColor = Color.parseColor("#087F7E")
    private var circleOutEndColor = Color.parseColor("#7EF1F4")

    private lateinit var circleInnerPaint: Paint
    private lateinit var circleOuterPaint: Paint
    private lateinit var mShader: SweepGradient
    private val progressInnerRectF = RectF()
    private var progressOuterRectF = RectF()
    private var mRadius: Float = 0F
    private var currentProgress: Float = 0F
    private var progressMax: Int = DEFAULT_MAX
    private var mCenterX = 0F
    private var mCenterY = 0F

    /**
     * 进度条进度
     */
    private var mProgressAnimator: ValueAnimator? = null
    private var mProgressUpdateListener: OnProgressUpdateListener? = null

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttrSet(attrs)
        initData()
    }


    private fun initAttrSet(attrs: AttributeSet?) {
        val typedArray: TypedArray? =
            context?.obtainStyledAttributes(attrs, R.styleable.circle_progressview)
        typedArray?.let {
            // 提示的文字
            textTip = it.getText(R.styleable.circle_progressview_android_text) as String
            Log.i(TAG, "initAttrSet: textTip: " + textTip)
            textTipUnit = it.getString(R.styleable.circle_progressview_texttip_unit)
            Log.i(TAG, "initAttrSet: textTipUnit: " + textTipUnit)
            textTipSize = it.getDimensionPixelSize(
                R.styleable.circle_progressview_android_textSize,
                DEFAULT_TIP_SIZE
            )
            Log.i(TAG, "initAttrSet: textTipSize: " + textTipSize)
            textTipColor =
                it.getColor(R.styleable.circle_progressview_android_textColor, DEFAULT_TIP_COLOR)
            // drawableSrc
            centerTipDrawableId =
                it.getDrawable(R.styleable.circle_progressview_drawable_srcid)
            Log.i(TAG, "initAttrSet: centerTipDrawable: " + centerTipDrawableId)
            //内圆的颜色
            circleInnerColor =
                it.getColor(R.styleable.circle_progressview_inner_color, 0)
            //内圆风格
            circleInnerStyle =
                it.getInt(R.styleable.circle_progressview_inner_style, DEFAULT_INNER_STYLE)

            // 是否
            circleOutVisible = it.getBoolean(
                R.styleable.circle_progressview_outer_visible,
                DEFAULT_OUTER_VISIBLE
            )
            // 圆弧的宽度
            circleWidth = it.getDimensionPixelSize(
                R.styleable.circle_progressview_circle_width,
                DEFAULT_CIRCLE_WIDTH
            ).toFloat()
            circleOutStartColor = it.getColor(
                R.styleable.circle_progressview_outer_start_color,
                DEFAULT_OUTER_STARTCOLOR
            )
            circleOutEndColor = it.getColor(
                R.styleable.circle_progressview_outer_end_color,
                DEFAULT_OUTER_STARTCOLOR
            )
            progressMax = it.getInt(R.styleable.circle_progressview_progress_max, DEFAULT_MAX)
            currentProgress = it.getFloat(
                R.styleable.circle_progressview_progress_current,
                DEFAULT_PRGRESS_CURRENT
            )
            startAngleEdgreee = it.getFloat(
                R.styleable.circle_progressview_start_angle_degree,
                DEFAULT_START_ANGLE_DEGREE
            )
            it.recycle()
        }
    }


    private fun initData() {
        Log.i(TAG, "initData: ")
        commonPaint =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.LINEAR_TEXT_FLAG)
        commonPaint.isAntiAlias = true
        // 提示的文字
        textTipPaint = TextPaint(commonPaint)
        textTipPaint.color = textTipColor
        textTipPaint.textSize = textTipSize.toFloat()
        // 内圆
        circleInnerPaint = Paint(commonPaint)
        circleInnerPaint.color = circleInnerColor
        circleInnerPaint.strokeWidth = circleWidth
        circleInnerPaint.style = Paint.Style.FILL
        //设置笔触为圆角
        circleInnerPaint.strokeCap = Paint.Cap.ROUND
        circleInnerPaint.shader = null
        // 外圆
        circleOuterPaint = Paint(commonPaint)
        circleOuterPaint.color = circleOutStartColor
        circleOuterPaint.strokeWidth = circleWidth + 3
        circleOuterPaint.style = Paint.Style.STROKE
        //设置笔触为圆角
        circleOuterPaint.strokeCap = Paint.Cap.ROUND
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.i(TAG, "onDraw: onDrawable ")
        canvas.save()
        Log.i(TAG, "onDraw: mCenterX: " + mCenterX + "  mCenterY:" + mCenterY)
        canvas.rotate(startAngleEdgreee, mCenterX, mCenterY)
        var radius = width / 2 - 20f
        //内部的圆环
        Log.i(TAG, "onDraw: draw inner circle " + radius)
        canvas.drawCircle(mCenterX, mCenterY, radius, circleInnerPaint)
        //外部的圆环
        Log.i(TAG, "onDraw: draw outer circle " + circleOutVisible)
        if (circleOutVisible) {
            var sweepAngle = currentProgress * 360f / 100
            canvas.drawArc(progressOuterRectF, 360f, sweepAngle, false, circleOuterPaint)
        }
        canvas.restore()
        Log.i(TAG, "onDraw: textTip: " + textTip)
        // 只显示文字提示
        if (!TextUtils.isEmpty(textTip)) {
            mPoint.x = width / 2 - getTextWidth() / 2
            mPoint.y = height / 2 - ((textTipPaint.descent() + textTipPaint.ascent()) / 2)
            canvas.drawText(textTip, mPoint.x, mPoint.y, textTipPaint)
        }
        Log.i(TAG, "onDraw: centerTipDrawableId: " + centerTipDrawableId)
        var currentDrawable = centerTipDrawableId
        if (currentProgress == 100f) {
            currentDrawable = resources.getDrawable(R.drawable.childlock_unlock, null)
        }
        if (currentDrawable != null) {
            // 画中心的图片
            var bitmapDrawable = currentDrawable as BitmapDrawable
            var bitmap = bitmapDrawable.bitmap
            var left = mCenterX - bitmap.width / 2
            var top = mCenterY - bitmap.height / 2
            Log.i(TAG, "onDraw: left: " + left + " top: " + top)
            canvas.drawBitmap(bitmap, left, top, commonPaint)
        }

    }

    /**
     * 触摸时间的处理
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i(TAG, "onTouchEvent: " + event?.action)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                circleInnerPaint.color =
                    resources.getColor(R.color.childlock_innerclolor_select, null)
                startProgressByTime(PROGRESS_START, PROGRESS_END, PROGRESS_TOTAL_TIME)
                return true
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {
                if (currentProgress != 100f) {
                    circleInnerPaint.color = circleInnerColor
                    mProgressAnimator?.cancel()
                    updateCircleProgress(0f)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 指定时间，开始进度
     *
     * @param from 从哪个进度开始
     * @param to 到哪个进度结束
     * @param duration    执行时间
     */
    open fun startProgressByTime(from: Int, to: Int, duration: Long) {
        Log.i(TAG, "startProgressByTime: ")
        if (mProgressAnimator == null) {
            mProgressAnimator = ValueAnimator.ofInt(from, to)
        }
        mProgressAnimator!!.interpolator = LinearInterpolator()
        mProgressAnimator!!.duration = duration
        mProgressAnimator!!.addUpdateListener { animation ->
            val cValue = animation.animatedValue as Int
            Log.i(TAG, "startProgressByTime: cValue: " + cValue)
            updateCircleProgress(cValue.toFloat())
            mProgressUpdateListener?.onProgressUpdate(cValue)
            if (cValue == 100) {
                mProgressUpdateListener?.onEnd()
            }
        }
        mProgressAnimator!!.start()
    }

    @Synchronized
    open fun updateCircleProgress(progress: Float) {
        Log.i(TAG, "updateCircleProgress: progress: " + progress)
        this.currentProgress = progress
        postInvalidate()
    }


    open fun setListener(listener: OnProgressUpdateListener) {
        this.mProgressUpdateListener = listener
    }


    /**
     * topText宽度
     *
     * @return 宽度
     */
    private fun getTextWidth(): Float {
        return textTipPaint.measureText(textTip)
    }


    /**
     * text高度
     *
     * @return 高度
     */
    private fun getTextHeight(): Float {
        return textTipPaint.descent() - textTipPaint.ascent()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        updateTextColors()
    }

    /**
     * 更新颜色
     */
    private fun updateTextColors() {
        var inval = false
        /*    val color = mTextColor!!.getColorForState(drawableState, 0)
            if (color != mCurrentColor) {
                mCurrentColor = color
                inval = true
            }*/
        if (inval) {
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i(TAG, "onSizeChanged: ")
        progressInnerRectF.left = paddingLeft.toFloat()
        progressInnerRectF.top = paddingTop.toFloat()
        progressInnerRectF.right = (w - paddingRight).toFloat()
        progressInnerRectF.bottom = (h - paddingBottom).toFloat()
        mCenterX = progressInnerRectF.centerX()
        mCenterY = progressInnerRectF.centerY()
        mRadius = min(progressInnerRectF.width(), progressInnerRectF.height()) / 2
        //得考虑画圆的线的宽度
        progressInnerRectF.inset(circleWidth / 2, circleWidth / 2)

        progressOuterRectF.left = paddingLeft.toFloat()
        progressOuterRectF.top = paddingTop.toFloat()
        progressOuterRectF.right = (w - paddingRight).toFloat()
        progressOuterRectF.bottom = (h - paddingBottom).toFloat()
        mCenterX = progressOuterRectF.centerX()
        mCenterY = progressOuterRectF.centerY()
        mRadius = min(progressOuterRectF.width(), progressOuterRectF.height()) / 2
        //得考虑画圆的线的宽度
        progressOuterRectF.inset(circleWidth / 2, circleWidth / 2)

        mShader = SweepGradient(
            mCenterX,
            mCenterY,
            intArrayOf(circleOutStartColor, circleOutEndColor),
            floatArrayOf(0.0F, 1.0F)
        )
        //arc = radian * radius
        val radian = circleWidth / Math.PI * 2.0F / mRadius
        val rotateDegrees = -Math.toDegrees(radian)
        val matrix = Matrix()
        matrix.setRotate(rotateDegrees.toFloat(), mCenterY, mCenterY)
        mShader.setLocalMatrix(matrix)
        // 设置 outPaint 的shader
        circleOuterPaint.shader = mShader
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.i(TAG, "onMeasure: ")
        var width = measureSpec(widthMeasureSpec)
        var height = measureSpec(heightMeasureSpec)
        Log.i(TAG, "onMeasure: width: " + width + "  height: " + height)
        setMeasuredDimension(width, height)
    }

    private fun measureSpec(measureSpec: Int): Int {
        val finalSize: Int
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        Log.i(TAG, "measureSpec: mode: " + mode + "  size: " + size)
        //默认大小
        val defaultSize = 220
        //指定宽高则直接返回
        finalSize = when (mode) {
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
        Log.i(TAG, "measureSpec: resultSize: " + finalSize)
        return finalSize
    }

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

    companion object {
        private const val TAG = "CircleProgressView"
        private const val DEFAULT_TIP_SIZE = 24
        private const val DEFAULT_TIP_COLOR = Color.GREEN
        private const val DEFAULT_TIP_DRAEABLE_PADDING = 0
        private const val DEFAULT_OUTER_TIP_SIZE = 24
        private const val DEFAULT_OUTER_TIP_COLOR = Color.WHITE
        private const val DEFAULT_INNER_COLOR = Color.GREEN
        private val DEFAULT_INNER_STYLE = Paint.Style.STROKE.ordinal
        private const val DEFAULT_OUTER_STARTCOLOR = Color.GREEN
        private const val DEFAULT_OUTER_ENDCOLOR = Color.GREEN
        private const val DEFAULT_CIRCLE_WIDTH = 10
        private const val DEFAULT_PRGRESS_MAX = 100
        private const val DEFAULT_PROGRESSTIP_COLOR = Color.WHITE
        private const val DEFAULT_PROGRESSTIP_UNIT = "%"

        /**
         * 弧线的开始角度，默认是0，是水平的，我们要从下面开始画
         */
        private const val DEFAULT_PRGRESS_CURRENT = -90F
        private const val DEFAULT_START_ANGLE_DEGREE = 0f
        private const val DEFAULT_OUTER_VISIBLE = false

    }

}