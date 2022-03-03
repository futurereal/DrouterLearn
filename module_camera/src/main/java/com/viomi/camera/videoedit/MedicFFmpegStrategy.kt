package com.viomi.camera.videoedit

import android.util.Log
import java.util.ArrayList

/**
 *@description:
 *@data:2021/12/10
 */
class MedicFFmpegStrategy : FFmpegStrategy {
    override fun cutOneSecond(recipeName: String, startTime: ArrayList<Int>) {
        Log.i(TAG, "cutOneSecond: ")
    }

    override fun mergerVideo(fileName: String, fileAmount: Int) {
    }

    override fun getPictureFromVideo(fileName: String, filePath: String){
    }

    override fun getFFmpegType(): String {
        var simpleName = MedicFFmpegStrategy.TAG
        Log.i(TAG, "getFFmpegType:$simpleName ")
        return simpleName
    }

    override fun compressVideo(videoPathOrg: String, videoPathDest: String) {
        TODO("Not yet implemented")
    }

    override fun stopEdit() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "MedicFFmpegStrategy"
    }
}