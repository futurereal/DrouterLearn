package com.viomi.camera.videoedit

import java.util.ArrayList

/**
 *@description:
 *@data:2021/12/10
 */
interface FFmpegStrategy {

    fun cutOneSecond(recipeName: String, startTime: ArrayList<Int>)
    fun mergerVideo(recipeName: String, fileAmount: Int)
    fun getPictureFromVideo(fileName: String, filePath: String)
    fun getFFmpegType(): String
    fun compressVideo(videoPathOrg: String, videoPathDest: String)
    fun stopEdit()

    companion object {
        const val TAG = "FFmpegStrategy"
        const val FFMPEG_TYPE_CMD = 1
        const val FFMPEG_TYPE_MEDIC = 2
    }
}