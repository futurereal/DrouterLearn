package com.viomi.camera.videoedit

import android.util.Log
import com.viomi.ffmpeg.cmd.tool.ffmpeg.FFmpegCmdManager
import com.viomi.ovensocommon.utils.VideoFileUtils
import java.io.File

/**
 *@description:
 *@data:2021/12/10
 */
class CmdFFmpegStrategy : FFmpegStrategy {
    override fun getFFmpegType(): String {
        var simpleName = CmdFFmpegStrategy.TAG
        Log.i(TAG, "getFFmpegType:$simpleName ")
        return simpleName
    }

    override fun cutOneSecond(recipeName: String, startTimeList: ArrayList<Int>) {
        Log.i(TAG, "cutOneSecond: ")
        FFmpegCmdManager.getInstance()?.cutVideo(recipeName, startTimeList, 1)
    }

    override fun mergerVideo(fileName: String, fileAmount: Int) {
        Log.i(TAG, "mergerVideo: ")
        var mergetFilePathList = ArrayList<String>()
        for (index in 0 until fileAmount) {
            var filePath = VideoFileUtils.getVideoPath(fileName + index, VideoFileUtils.videoDirCut)
            mergetFilePathList.add(filePath)
        }
        var destPath = VideoFileUtils.getVideoPath(
            fileName + VideoFileUtils.VIDEO_RESULT_TEMP,
            VideoFileUtils.videoDirResult
        )
        FFmpegCmdManager.getInstance()?.mergetVideo(mergetFilePathList, destPath)
    }

    override fun getPictureFromVideo(fileName: String, filePath: String) {
        Log.i(TAG, "getPictureFromVideo: $filePath")
        if (!File(filePath).exists()) {
            Log.i(TAG, "getPictureFromVideo: mergeFise is not exist return")
            return
        }
        Log.i(TAG, "getPictureFromVideo: $filePath")
        var destFilePath = VideoFileUtils.getCoverPath(fileName, VideoFileUtils.videoDirResult)
        FFmpegCmdManager.getInstance()?.makeVideoCover(filePath, destFilePath)
    }

    override fun compressVideo(videoPathOrg: String, videoPathDest: String) {
        FFmpegCmdManager.getInstance()?.compressVideo(videoPathOrg, videoPathDest)
    }

    override fun stopEdit() {
        Log.i(TAG, "stopEdit: ")
        FFmpegCmdManager.getInstance()?.cancelExecute()
    }

    companion object {
        private const val TAG = "CmdFFmpegStrategy"
    }
}