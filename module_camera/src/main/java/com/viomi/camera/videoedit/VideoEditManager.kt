package com.viomi.ffmpeg.cmd.tool.ffmpeg

import android.util.Log
import com.viomi.camera.fragment.CameraFragment
import com.viomi.camera.utils.CameraUtils
import com.viomi.camera.videoedit.FFmpegStrategy
import com.viomi.camera.videoedit.FFmpegStrategyFactory
import com.viomi.camera.videoedit.StrateyController
import com.viomi.ovensocommon.utils.VideoFileUtils
import com.viomi.ffmpeg.codec.utils.VideoEditUtils
import java.util.ArrayList

class VideoEditManager private constructor() {
    // 构造方法里面初始化函数
    init {
        var strategyFactory = FFmpegStrategyFactory()
        strategyFactory.init()
        var currentStategyType = StrateyController.getStategyType()
        ffmpegStrategy = strategyFactory.getFFmpegStrategy(currentStategyType)
    }

    companion object {
        private const val TAG = "VideoEditManager"
        private var ffmpegStrategy: FFmpegStrategy? = null
        private var mInstance: VideoEditManager? = null
        fun getInstance(): VideoEditManager? {
            if (mInstance == null) {
                synchronized(VideoEditManager::class.java) {
                    if (mInstance == null) {
                        mInstance = VideoEditManager()
                    }
                }
            }
            return mInstance
        }
    }

    fun cutVideo(recipeName: String, videoAmount: Int) {
        Log.i(TAG, "cutVideo: $recipeName")
        var videoOrgPath = VideoFileUtils.getVideoPath(recipeName, VideoFileUtils.videoDirOrg)
        var videoTotalTime = CameraUtils.getVideoTime(videoOrgPath)
        Log.i(TAG, "cutVideo: $videoTotalTime")
        var videoStartTimeList = getStartTimeList(videoTotalTime, videoAmount)
        // 需要创建文件夹但是不需要创建文件，否则都是编译不通过的
        ffmpegStrategy?.cutOneSecond(recipeName, videoStartTimeList)
    }

    /**
     * 合并视频
     */
    fun mergetVideo(videoName: String, videoAccount: Int) {
        ffmpegStrategy?.mergerVideo(videoName, videoAccount)
    }

    fun compressVideo(videoPathOrg: String, videoPathDest: String) {
        ffmpegStrategy?.compressVideo(videoPathOrg, videoPathDest)
    }

    fun makeCover(recipeName: String, filePath: String) {
        ffmpegStrategy?.getPictureFromVideo(recipeName, filePath)
    }

    fun stopEdit() {
        ffmpegStrategy?.stopEdit()
    }

    private fun getStartTimeList(videoTotalTime: Int, videoAmount: Int): ArrayList<Int> {
        // 计算截取数据的集合
        val cutTimeList = ArrayList<Int>(videoAmount)
        // 取第一秒
        cutTimeList.add(1)
        var stepCount = videoTotalTime / videoAmount
        Log.i(TAG, "cutVideo: stepCount : $stepCount")
        if (stepCount <= 0) {
            stepCount = 1
        }
        val begainTime = 1
        for (i in 1 until VideoEditUtils.CUT_TOTAL_TIME - 1) {
            val time = begainTime + i * stepCount
            Log.i(TAG, "cutVideo: time  $time")
            cutTimeList.add(time)
        }
        // 取最后1s
        cutTimeList.add(videoTotalTime - 1)
        return cutTimeList
    }


}