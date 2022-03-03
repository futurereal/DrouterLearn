package com.viomi.ffmpeg.cmd.tool.ffmpeg

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ThreadPoolUtil {

    private val executor = Executors.newSingleThreadExecutor()

    fun executeSingleThreadPool(runnable: Runnable): ExecutorService {
        executor.submit(runnable)
        return executor
    }

}
