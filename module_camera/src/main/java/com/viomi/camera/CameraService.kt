/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.viomi.camera

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.camera2.*
import android.media.MediaCodec
import android.media.MediaRecorder
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ScreenUtils
import com.viomi.camera.databinding.CameraRecordBinding
import com.viomi.camera.databinding.DialogReacordStopBinding
import com.viomi.camera.utils.getPreviewOutputSize
import com.viomi.common.ApplicationUtils
import com.viomi.ovensocommon.CommonConstant
import com.viomi.ovensocommon.ViomiRouterConstant
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory
import com.viomi.ovensocommon.toast.ViomiToastUtil
import com.viomi.ovensocommon.utils.VideoFileUtils
import com.viomi.router.core.ViomiRouter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraService : Service() {
    private var cameraRecordBinding: CameraRecordBinding? = null

    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    /** [Handler] corresponding to [cameraThread] */
    private val cameraHandler = Handler(cameraThread.looper)

    /** Captures frames from a [CameraDevice] for our video recording */
    private lateinit var session: CameraCaptureSession

    /** The [CameraDevice] that will be opened in this fragment */
    private lateinit var camera: CameraDevice

    /** Saves the video recording */
    private var recorder: MediaRecorder? = null

    private var videoOutputFilePath: String = ""
    private var beginRecordTime: Long? = 0
    private var alphaAnimation: Animation? = null
    private var isCooking: Boolean = false
    private var recipeName: String? = null
    private var modeId: String? = null
    private var currentState: Int = STATE_IDLE
    private lateinit var previewWindowManager: WindowManager
    private lateinit var windowLayoutParameter: WindowManager.LayoutParams
    private var mContext: Context? = null

    private lateinit var stopDialogView: View
    private lateinit var dialogParam: WindowManager.LayoutParams
    private var recordTime: Int = 0
    private var pauseTime: Long = 0

    private val cameraServiceHandler: Handler by lazy {
        val cameraServiceHandler: Handler = object : Handler(Looper.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> {
                        recordTime =
                            (((SystemClock.elapsedRealtime() - beginRecordTime!!) / TIME_ONE_MILLS).toInt())
                        val hour = (recordTime) / 3600//总秒数-换算成天的秒数=剩余的秒数    剩余的秒数换算为小时
                        val minute =
                            (recordTime - 3600 * hour) / 60  //总秒数-换算成天的秒数-换算成小时的秒数=剩余的秒数    剩余的秒数换算为分
                        val second =
                            recordTime - 3600 * hour - 60 * minute  //总秒数-换算成天的秒数-换算成小时的秒数-换算为分的秒数=剩余的秒数

                        val hourStr = if (hour >= TIME_TEN) hour.toString() else ("0$hour")
                        val minuteStr =
                            if (minute >= TIME_TEN) minute.toString() else ("0$minute")
                        val secondStr =
                            if (second >= TIME_TEN) second.toString() else ("0$second")
                        cameraRecordBinding?.recordTime?.text = "$hourStr:$minuteStr:$secondStr"
                        cameraServiceHandler.sendEmptyMessageDelayed(
                            MSG_UPDATE_TIME,
                            TIME_ONE_MILLS
                        )
                    }
                }
            }
        }
        cameraServiceHandler
    }

    /**
     * Setup a persistent [Surface] for the recorder so we can use it as an output target for the
     * camera session without preparing the recorder
     */
    private val recorderSurface: Surface by lazy {
        Log.i(TAG, "recorderSurface lazy: ")
        // Get a persistent Surface from MediaCodec, don't forget to release when done
        val surface = MediaCodec.createPersistentInputSurface()
        // Prepare and release a dummy MediaRecorder with our new surface
        // Required to allocate an appropriately sized buffer before passing the Surface as the
        //  output target to the capture session
        initRecorder(surface).apply {
            try {
                prepare()
                release()
            } catch (e: Exception) {
                Log.i(TAG, "Exception: " + e.message)
            }
        }

        surface
    }


    /** Requests used for preview only in the [CameraCaptureSession] */
    private val previewRequest: CaptureRequest by lazy {
        // Capture request holds references to target surfaces
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            // Add the preview surface target
            cameraRecordBinding?.recordSurface?.holder?.let { addTarget(it.surface) }
        }.build()
    }


    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: ")
        mContext = ApplicationUtils.getContext()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: action  " + intent?.action)
        Log.i(TAG, "onStartCommand: extras " + intent?.extras)
        // 非空判断会闪退
        if (intent != null && intent.extras != null) {
            isCooking = intent.extras?.getBoolean(ViomiRouterConstant.CAMERA_KEY_COOKING)!!
            recipeName = intent.extras?.getString(ViomiRouterConstant.CAMERA_KEY_RECIPENAME)
            modeId = intent.extras?.getString(ViomiRouterConstant.CAMERA_KEY_MODEID)
            Log.i(TAG, "onStartCommand: isBeginCook: $isCooking")
            Log.i(TAG, "onStartCommand: recipeName: $recipeName")
            Log.i(TAG, "onStartCommand: modeId: $modeId")
            // 删除之前存的视频信息
            FileUtils.deleteAllInDir(VideoFileUtils.videoDirOrg)
            videoOutputFilePath =
                VideoFileUtils.createVideoFile(recipeName, VideoFileUtils.videoDirOrg)
            Log.i(TAG, "onStartCommand: videoOutputFilePath: " + File(videoOutputFilePath).exists())
        }
        intent?.let { executeAction(it) }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun executeAction(intent: Intent) {
        val action = intent.action
        Log.i(TAG, "executeAction: " + action)
        when (action) {
            ACTION_PREVIEW -> startPreview()
            ACTION_VIDEO_START -> startRecord()
            ACTION_VIDEO_RESTART -> restartRecord()
            ACTION_VIDEO_PAUSE -> pauseRecord()
            ACTION_VIDEO_STOP -> stopRecord()
            ACTION_PREVIEW_REDUCE -> setPreViewMin()
            ACTION_PREVIEW_INCREASE -> setPreViewMax()
        }
    }

    private fun setPreViewMax() {
        Log.i(TAG, "setPreViewMax: ")
        val width = ScreenUtils.getScreenWidth() - VIEW_DETAIL_X
        val height = ScreenUtils.getScreenHeight()
        windowLayoutParameter.width = width
        windowLayoutParameter.height = height
        windowLayoutParameter.gravity = Gravity.END
        previewWindowManager.updateViewLayout(
            cameraRecordBinding?.root?.rootView,
            windowLayoutParameter
        )
    }

    private fun setPreViewMin() {
        Log.i(TAG, "setPreViewMin: ")
        windowLayoutParameter.width = 1
        windowLayoutParameter.height = 1
        previewWindowManager.updateViewLayout(
            cameraRecordBinding?.root?.rootView,
            windowLayoutParameter
        )
    }

    private fun startPreview() {
        Log.i(TAG, "startPreview: ")
        initPreviewView()
        initStopDialog()
    }

    private fun initPreviewView() {
        // 如果没有系统配合 打开高级 --- 悬浮窗权限，需要手动开启悬浮窗权限
        // 配置参数
        val type = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_PHONE
        else
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        val width = ScreenUtils.getScreenWidth() - VIEW_DETAIL_X
        Log.i(TAG, "initPreviewView: $width")
        val height = ScreenUtils.getScreenHeight()
        // FLAG_NOT_TOUCH_MODAL 弹窗之外的地方能点，否则不能点
        windowLayoutParameter = WindowManager.LayoutParams(
            width,
            height,
            type,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        windowLayoutParameter.gravity = Gravity.END
        //初始化View 添加配置
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        cameraRecordBinding = DataBindingUtil.inflate<CameraRecordBinding>(
            layoutInflater,
            R.layout.camera_record,
            null,
            false
        )
        // 解决 property that could have been changed by this time
        cameraRecordBinding?.let {
            it.recordBegain.setOnClickListener {
                if (!isCooking) {
//                ViomiToastUtil.showWindowToast(mContext!!.getString(R.string.state_not_cooktip))
                    return@setOnClickListener
                }
                Log.d(TAG, "initPreviewView: currentState: $currentState")
                when (currentState) {
                    STATE_IDLE -> startRecord()
                    STATE_RECODING -> showStopDialog()
                    STATE_PAUSE -> restartRecord()
                }
            }

            it.recordSurface.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceDestroyed(holder: SurfaceHolder) = Unit
                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) = Unit

                override fun surfaceCreated(holder: SurfaceHolder) {
                    Log.i(TAG, "surfaceCreated: ")
                    // Selects appropriate preview size and configures view finder
                    val previewSize = getPreviewOutputSize(
                        it.recordSurface.display, characteristics, SurfaceHolder::class.java
                    )
                    Log.d(TAG, "Selected preview size: $previewSize")
                    it.recordSurface.setAspectRatio(previewSize.width, previewSize.height)
                    // To ensure that size is set, initialize camera in the view's thread
                    it.recordSurface.post {
                        GlobalScope.launch {
                            initializeCamera()
                        }
                    }
                }
            })
        }
        previewWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        previewWindowManager.addView(cameraRecordBinding?.root?.rootView, windowLayoutParameter)
    }

    private fun startRecord() {
        Log.i(TAG, "startRecord: ")
        // Start recording repeating requests, which will stop the ongoing preview
        //  repeating requests without having to explicitly call `session.stopRepeating`
        session.setRepeatingRequest(recordRequest, null, cameraHandler)
        if (recorder == null) {
            recorder = initRecorder(recorderSurface)
        }
        Log.i(TAG, "startRecord: recorder: $recorder")
        // Finalizes recorder setup and starts recording
        recorder!!.prepare()
        recorder!!.start()
        // 状态改变
        cameraRecordBinding?.recordBegain?.setBackgroundResource(R.drawable.camera_capture_stop)
        // 播放动画，并且开启事件
        currentState = STATE_RECODING
        startRecordTipAnim()
        beginRecordTime = SystemClock.elapsedRealtime()
        cameraServiceHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, TIME_ONE_MILLS)
        OvensoServiceFactory.getInstance().ovenService.updateRecordStatus(
            CommonConstant.RECORD_STATE_RECORDING,
        )
    }

    private fun startRecordTipAnim() {
        if (alphaAnimation == null) {
            alphaAnimation = AlphaAnimation(0.1f, 1.0f)
            alphaAnimation?.duration = 300
            alphaAnimation?.repeatCount = Animation.INFINITE
            alphaAnimation?.repeatMode = Animation.REVERSE
        }
        cameraRecordBinding?.recordImgtip?.startAnimation(alphaAnimation)
    }

    private fun stopRecordTipAnim() {
        cameraRecordBinding?.recordImgtip?.clearAnimation()
    }

    private fun restartRecord() {
        Log.i(TAG, "restartRecord: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder?.resume()
            currentState = STATE_RECODING
            cameraRecordBinding?.recordBegain?.setBackgroundResource(R.drawable.camera_capture_stop)
            startRecordTipAnim()
            pauseTime = SystemClock.elapsedRealtime() - pauseTime
            beginRecordTime = beginRecordTime?.plus(pauseTime.plus(recordTime))
            cameraServiceHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            OvensoServiceFactory.getInstance().ovenService.updateRecordStatus(
                CommonConstant.RECORD_STATE_RECORDING,
            )
        }
    }

    private fun pauseRecord() {
        Log.i(TAG, "pauseRecord: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder?.pause()
            currentState = STATE_PAUSE
            cameraRecordBinding?.recordBegain?.setBackgroundResource(R.drawable.camera_capture_pause)
            pauseTime = SystemClock.elapsedRealtime()
            stopRecordTipAnim()
            cameraServiceHandler.removeMessages(MSG_UPDATE_TIME)
            OvensoServiceFactory.getInstance().ovenService.updateRecordStatus(
                CommonConstant.RECORD_STATE_PAUSE,
            )
        } else {
            Toast.makeText(this, getString(R.string.version_low), Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecord() {
        Log.i(TAG, "stopRecord: ")
        recorder?.stop()
        recorder?.release()
        recorder = null
        recordTime = 0
        beginRecordTime = 0
        pauseTime = 0
        currentState = STATE_STOP
        cameraRecordBinding?.recordBegain?.setBackgroundResource(R.drawable.camera_capture_begain)
        stopRecordTipAnim()
        cameraServiceHandler.removeMessages(MSG_UPDATE_TIME)
        stopServiceAndBack()
    }

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        Log.i(TAG, "initCameraManager: ")
        val context = ApplicationUtils.getContext()
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** [CameraCharacteristics] corresponding to the provided Camera ID */
    private val characteristics: CameraCharacteristics by lazy {
        Log.i(TAG, "characteristics init: ")
        val cameraManager =
            mContext?.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val cameraList = getCarmeraList(cameraManager)
        cameraManager.getCameraCharacteristics(cameraList[0].cameraId)
    }

    /** Lists all video-capable cameras and supported resolution and FPS combinations */
    @SuppressLint("InlinedApi")
    private fun getCarmeraList(cameraManager: CameraManager): List<CameraInfo> {
        Log.i(TAG, "getCarmeraList: ")
        val availableCameras: MutableList<CameraInfo> = mutableListOf()

        cameraManager.cameraIdList.forEach { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            Log.i(TAG, "getCarmeraList: lensOrientationString")
            val orientation = lensOrientationString(
                characteristics.get(CameraCharacteristics.LENS_FACING)!!
            )

            // Query the available capabilities and output formats
            val capabilities = characteristics.get(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
            )!!
            val cameraConfig = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
            )!!

            // Return cameras that declare to be backward compatible
            if (capabilities.contains(
                    CameraCharacteristics
                        .REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
                )
            ) {
                // Recording should always be done in the most efficient format, which is
                //  the format native to the camera framework
                val targetClass = MediaRecorder::class.java

                // For each size, list the expected FPS
                cameraConfig.getOutputSizes(targetClass).forEach { size ->
                    // Get the number of seconds that each frame will take to process
                    val secondsPerFrame =
                        cameraConfig.getOutputMinFrameDuration(targetClass, size) /
                                1_000_000_000.0
                    // Compute the frames per second to let user select a configuration
                    val fps = if (secondsPerFrame > 0) (1.0 / secondsPerFrame).toInt() else 0
                    val fpsLabel = if (fps > 0) "$fps" else "N/A"
                    availableCameras.add(
                        CameraInfo(
                            "$orientation ($id) $size $fpsLabel FPS", id, size, fps
                        )
                    )
                }
            }
        }

        return availableCameras
    }

    /** Requests used for preview and recording in the [CameraCaptureSession] */
    private val recordRequest: CaptureRequest by lazy {
        // Capture request holds references to target surfaces
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
            // Add the preview and recording surface targets
            cameraRecordBinding?.recordSurface?.holder?.let { addTarget(it.surface) }
            addTarget(recorderSurface)
            // Sets user requested FPS for all targets
            set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(30, 30))
        }.build()
    }

    /** Creates a [MediaRecorder] instance using the provided [Surface] as input */
    private fun initRecorder(surface: Surface) = MediaRecorder().apply {
        Log.i(TAG, "initRecorder: $videoOutputFilePath")
//        setAudioSource(MediaRecorder.AudioSource.MIC)
        setVideoSource(MediaRecorder.VideoSource.SURFACE)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setOutputFile(videoOutputFilePath)
        setVideoEncodingBitRate(RECORDER_VIDEO_BITRATE)
        setVideoFrameRate(20)
        setVideoSize(640, 480)
        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
//        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setInputSurface(surface)
    }

    private suspend fun initializeCamera() {
        val cameraList = getCarmeraList(cameraManager)
        Log.i(TAG, "initializeCamera: cameraList: " + cameraList.size)
        if (cameraList.isEmpty()) {
            ViomiToastUtil.showWindowToast(mContext!!.getString(R.string.camera_init_fail))
            return
        }
        try {
            camera = openCamera(cameraManager, cameraList[0].cameraId, cameraHandler)
        } catch (e: Exception) {
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                ViomiToastUtil.showWindowToast(mContext!!.getString(R.string.camera_no_permission))
            }
            return
        }

        // Creates list of Surfaces where the camera will output frames
        val targets =
            cameraRecordBinding?.recordSurface?.holder?.let { listOf(it.surface, recorderSurface) }
        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, targets, cameraHandler)
        // Sends the capture request as frequently as possible until the session is torn down or
        //  session.stopRepeating() is called
        session.setRepeatingRequest(previewRequest, null, cameraHandler)
        Log.i(TAG, "initializeCamera: $isCooking")
        if (isCooking) {
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                startRecord()
            }
        }
    }

    /** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) = cont.resume(device)

            override fun onDisconnected(device: CameraDevice) {
                Log.w(TAG, "Camera $cameraId has been disconnected")
            }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e(TAG, exc.message, exc)
                if (cont.isActive) cont.resumeWithException(exc)
            }
        }, handler)
    }

    /**
     * Creates a [CameraCaptureSession] and returns the configured session (as the result of the
     * suspend coroutine)
     */
    private suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>?,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->

        // Creates a capture session using the predefined targets, and defines a session state
        // callback which resumes the coroutine once the session is configured
        if (targets != null) {
            device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {

                override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    val exc = RuntimeException("Camera ${device.id} session configuration failed")
                    Log.e(TAG, exc.message, exc)
                    cont.resumeWithException(exc)
                }
            }, handler)
        }
    }

    private fun initStopDialog() {
        val type = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_PHONE
        else
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        dialogParam = WindowManager.LayoutParams(
            ScreenUtils.getScreenWidth(),
            ScreenUtils.getScreenHeight(),
            type,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        dialogParam.gravity = Gravity.CENTER

        Log.d(TAG, "dialogParam.width :" + dialogParam.width)
        Log.d(TAG, "dialogParam.height :" + dialogParam.height)
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var dialogrecordBinding = DataBindingUtil.inflate<DialogReacordStopBinding>(
            layoutInflater,
            R.layout.dialog_reacord_stop,
            null,
            false
        )
        stopDialogView = dialogrecordBinding.root.rootView
        dialogrecordBinding.recordstopCancel.setOnClickListener {
            Log.d(TAG, "initDialog: tvCancel onclick")
            if (stopDialogView.windowToken != null) {
                previewWindowManager.removeView(stopDialogView)
            }
        }
        dialogrecordBinding.recordstopSure.setOnClickListener {
            Log.d(TAG, "initDialog: tvSure onclick")
            stopRecord()
        }
    }

    private fun showStopDialog() {
        if (stopDialogView.windowToken != null) {
            previewWindowManager.updateViewLayout(stopDialogView, dialogParam)
        } else {
            previewWindowManager.addView(stopDialogView, dialogParam)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG, "onBind: ")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: isServiceWork")
        cameraThread.quitSafely()
        recorder?.release()
        recorderSurface.release()
        //移除预览的图
        if (cameraServiceHandler != null) {
            cameraServiceHandler.removeCallbacksAndMessages(null)
        }
        if (!TextUtils.isEmpty(recipeName)) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_COOK_RUNNING).navigation()
            OvensoServiceFactory.getInstance().ovenService.updateRecordStatus(CommonConstant.RECORD_STATE_FINISH)
        }

    }

    /**
     * 停止服务并且返回
     */
    private fun stopServiceAndBack() {
        Log.i(TAG, "stopServiceAndBack: " + recipeName)
        // 移除dialog
        if (stopDialogView.windowToken != null) {
            previewWindowManager.removeView(stopDialogView)
        }
        // 移除预览界面
        if (cameraRecordBinding?.root?.rootView?.windowToken != null) {
            previewWindowManager.removeView(cameraRecordBinding?.root?.rootView)
        }
        stopSelf()
    }

    companion object {
        private val TAG = CameraService::class.java.simpleName
        const val ACTION_PREVIEW = "com.viomi.ovenso.camera.video.CameraPreview"
        const val ACTION_VIDEO_START = "com.viomi.ovenso.camera.video.CameraVideoStart"
        const val ACTION_VIDEO_RESTART = "com.viomi.ovenso.camera.video.CameraVideoRestart"
        const val ACTION_VIDEO_PAUSE = "com.viomi.ovenso.camera.video.CameraVideoPause"
        const val ACTION_VIDEO_STOP = "com.viomi.ovenso.camera.video.CameraVideoStop"
        const val ACTION_PREVIEW_REDUCE = "com.viomi.ovenso.camera.video.preview.reduce"
        const val ACTION_PREVIEW_INCREASE = "com.viomi.ovenso.camera.video.preview.increase"
        const val RECORDER_VIDEO_BITRATE: Int = 10_000_000
        const val VIEW_DETAIL_X: Int = 100

        // 录像状态
        const val STATE_RECODING: Int = 1
        const val STATE_PAUSE: Int = 2
        const val STATE_STOP: Int = 3
        const val STATE_IDLE: Int = 4

        const val MSG_UPDATE_TIME: Int = 1004
        const val TIME_TEN: Long = 10
        const val TIME_ONE_MILLS: Long = 1000

        private data class CameraInfo(
            val name: String,
            val cameraId: String,
            val size: Size,
            val fps: Int
        )

        private fun lensOrientationString(value: Int) = when (value) {
            CameraCharacteristics.LENS_FACING_BACK -> "Back"
            CameraCharacteristics.LENS_FACING_FRONT -> "Front"
            CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
            else -> "Unknown"
        }
    }
}