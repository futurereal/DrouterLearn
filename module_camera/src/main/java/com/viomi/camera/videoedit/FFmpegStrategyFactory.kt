package com.viomi.camera.videoedit

import android.util.Log

/**
 *@description:
 *@data:2021/12/10
 */
class FFmpegStrategyFactory {
    private var strategyMap = HashMap<String, FFmpegStrategy>()

    fun init() {
        var cmdFFmpegStrategy = CmdFFmpegStrategy()
        strategyMap.put(cmdFFmpegStrategy.getFFmpegType(), cmdFFmpegStrategy)
        var medicFFmpegStrategy = MedicFFmpegStrategy()
        strategyMap.put(medicFFmpegStrategy.getFFmpegType(), medicFFmpegStrategy)
    }

    fun getFFmpegStrategy(ffmpegType: String): FFmpegStrategy? {
        Log.i(TAG, "getFFmpegStrategy: ffmpegType: $ffmpegType")
        var ffmpegStrategy = strategyMap.get(ffmpegType)
        return ffmpegStrategy
    }

    companion object {
        private const val TAG = "FFmpegFactory"
    }
}