package com.viomi.camera.videoedit

/**
 *@description:
 *@data:2021/12/14
 */
object StrateyController {
    fun getStategyType(): String {
        return CmdFFmpegStrategy().getFFmpegType()
    }
}