package com.viomi.ffmpeg.cmd.tool.ffmpeg

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.viomi.ffmpeg.cmd.tool.FFmpegHandler
import com.viomi.ffmpeg.cmd.tool.FFmpegCommandUtil
import com.viomi.ovensocommon.utils.VideoFileUtils
import java.io.File
import java.util.ArrayList

class FFmpegCmdManager private constructor() {


    companion object {
        private lateinit var ffmpegHandler: FFmpegHandler
        private lateinit var mainHandler: Handler
        private var mInstance: FFmpegCmdManager? = null
        fun getInstance(): FFmpegCmdManager? {
            if (mInstance == null) {
                synchronized(FFmpegCmdManager::class.java) {
                    if (mInstance == null) {
                        mInstance = FFmpegCmdManager()
                        mainHandler = Handler(Looper.getMainLooper())
                        ffmpegHandler = FFmpegHandler(mainHandler)
                    }
                }
            }
            return mInstance
        }

        private const val TAG = "FFmpegCmdManager"
        const val TYPE_CUT_FINISH = 1
        const val TYPE_MERGE_FINISH = 2
        const val TYPE_COMPRESS_FINISH = 3
        const val TYPE_COVER_FINISH = 4
    }

    /**
     * 剪切视频
     */
    fun cutVideo(recipeName: String, startTimeList: ArrayList<Int>, duration: Int) {
        Log.i(TAG, "cutVideo: ")
        var createCutDirResult = VideoFileUtils.createVideoDir(VideoFileUtils.videoDirCut)
        if (!createCutDirResult) {
            Log.i(TAG, "cutVideo: createCutDirResult falit return")
            return
        }
        var srcPath = VideoFileUtils.getVideoPath(recipeName, VideoFileUtils.videoDirOrg)
        val commandList = ArrayList<Array<String>>()
        var size = startTimeList.size
        for (index in 0 until size) {
            var startTime = startTimeList.get(index)
            Log.i(TAG, "cutVideo:startTime= $startTime")
            var outPutPath =
                VideoFileUtils.getVideoPath(recipeName + index, VideoFileUtils.videoDirCut)
            Log.i(TAG, "mergetVideo: destPath = $outPutPath")
            var commandLine = FFmpegCommandUtil.cutVideo(srcPath, outPutPath, startTime, duration)
            commandList.add(commandLine)
        }
        ffmpegHandler.executeFFmpegCmdList(commandList, TYPE_CUT_FINISH)
    }

    /**
     * 合并视频
     */
    fun mergetVideo(allVideoPath: ArrayList<String>, videoMergePath: String) {
        // 执行ffmpeg 的指令 文件路径的文件夹一定要存在，否则会报错 ，文件可以没有
        var createMergeDirResult = VideoFileUtils.createVideoDir(VideoFileUtils.videoDirMergeTemp)
        var createVideoDirResult = VideoFileUtils.createVideoDir(VideoFileUtils.videoDirResult)
        var createVideTempResult = VideoFileUtils.createVideoDir(VideoFileUtils.videoDirMergeTemp)
        if (!createMergeDirResult || !createVideoDirResult || !createVideTempResult) {
            Log.i(TAG, "mergetVideo: createDir failt return")
            return
        }
        var outPutFileList = ArrayList<String>()
        val commandList = ArrayList<Array<String>>()
        var size = allVideoPath.size
        // 生成临时文件的指令 ts
        for (index in 0 until size) {
            var srcPath = allVideoPath[index]
            Log.i(TAG, "mergetVideo:srcPath= $srcPath")
            var outPutPath =
                VideoFileUtils.videoDirMergeTemp + File.separator + "output" + index + ".ts"
            Log.i(TAG, "mergetVideo: destPath = $outPutPath")
            outPutFileList.add(outPutPath)
            var transformCmd =
                FFmpegCommandUtil.transformVideoWithEncode(srcPath, outPutPath)
            commandList.add(transformCmd)
        }
        // 临时文件的路径
        val tsPathsFile =
            VideoFileUtils.videoDirMergeTemp + File.separator + "listFile.txt"
        Log.i(TAG, "mergetVideo: listPath = $tsPathsFile")
        // 把所有的路径 写入到txt 里面
        CmdFileUtil.createListFile(tsPathsFile, outPutFileList)
        val jointVideoCmd = FFmpegCommandUtil.jointVideo(tsPathsFile, videoMergePath)
        commandList.add(jointVideoCmd)
        ffmpegHandler.executeFFmpegCmdList(commandList, TYPE_MERGE_FINISH)
    }

    fun compressVideo(videoFilePath: String, videoResultPath: String) {
        var videoCompressCmd = FFmpegCommandUtil.videoCompress(videoFilePath, videoResultPath)
        ffmpegHandler.executeFFmpegCmd(videoCompressCmd, TYPE_COMPRESS_FINISH)
    }

    fun makeVideoCover(fileMergePath: String, videoCoverPath: String) {
        var screenShotCmd =
            FFmpegCommandUtil.screenShot(fileMergePath, 1f, videoCoverPath)
        ffmpegHandler.executeFFmpegCmd(screenShotCmd, TYPE_COVER_FINISH)
    }

    fun cancelExecute() {
        Log.i(TAG, "cancelExecute: ")
        ffmpegHandler.cancelExecute(true)
    }


}