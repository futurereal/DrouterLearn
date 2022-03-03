package com.viomi.ovensocommon.utils

import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.FileUtils
import com.viomi.common.ApplicationUtils
import java.io.File

object VideoFileUtils {
    private const val TAG = "VideoFileUtils"
    val dirPath = ApplicationUtils.getContext()?.externalCacheDir?.absolutePath
    private val VIDEO_DIR_ORG = "videoOrg"
    val videoDirOrg = dirPath + File.separator + VIDEO_DIR_ORG
    private val VIDEO_DIR_CUT = "videoCut"
    val videoDirCut = dirPath + File.separator + VIDEO_DIR_CUT
    val VIDEO_DIR_MERGE_TEMP = "videoMergeTemp"
    val videoDirMergeTemp = dirPath + File.separator + VIDEO_DIR_MERGE_TEMP
    private val VIDEO_DIR_RESULT = "videoResult"
    val videoDirResult = dirPath + File.separator + VIDEO_DIR_RESULT
    val PICTURE_TYPE_SUFFIX = ".jpg"
    val VIDEO_TYEP_SUFFIX = ".mp4"
    val VIDEO_RESULT_TEMP = "temp"

    fun createVideoFile(recipeName: String?, videoDir: String): String {
        Log.i(TAG, "createVideoFile: recipeName = $recipeName videoDir = $videoDir")
        if (!createVideoDir(videoDir)) return ""
        var videoFiltPath = videoDir + File.separator + recipeName + VIDEO_TYEP_SUFFIX
        var videoResult = FileUtils.createFileByDeleteOldFile(videoFiltPath)
        if (videoResult == false) {
            Log.i(TAG, "createOrgVideoFile: createVideoFile fail return ")
            return ""
        }
        Log.i(TAG, "createVideoFile: videoResult: " + videoResult)
        return videoFiltPath
    }

    fun createVideoDir(videoDir: String): Boolean {
        var dirCreateResult = FileUtils.createOrExistsDir(videoDir)
        Log.i(TAG, "createOrgVideoFile: $dirCreateResult")
        if (dirCreateResult == false) {
            Log.i(TAG, "createOrgVideoFile: createDirFile fail return ")
            return false
        }
        // 剪切 或者临时文件 删除
        if (TextUtils.equals(videoDir, videoDirCut) || TextUtils.equals(
                videoDir,
                videoDirMergeTemp
            )
        ) {
            FileUtils.deleteFilesInDir(videoDir)
        }
        return true
    }

    fun getVideoPath(recipeName: String, videoDir: String): String {
        return videoDir + File.separator + recipeName + VIDEO_TYEP_SUFFIX
    }

    fun getCoverPath(recipeName: String, videoDir: String): String {
        return videoDir + File.separator + recipeName + PICTURE_TYPE_SUFFIX
    }

    fun createVideoCoverFile(recipeName: String, videoDir: String): String {
        var videoCoverPath = ""
        var dirCreateResult = FileUtils.createOrExistsDir(videoDir)
        Log.i(TAG, "createOrgVideoFile: $dirCreateResult")
        if (dirCreateResult == false) {
            Log.i(TAG, "createOrgVideoFile: createDirFile fail return ")
            return videoCoverPath
        }
        var videoCoverFile =
            videoDir + File.separator + recipeName + PICTURE_TYPE_SUFFIX
        var fileCreateResult = FileUtils.createFileByDeleteOldFile(videoCoverFile)
        Log.i(TAG, "createVideoFile: videoResult: " + fileCreateResult)
        return videoCoverPath
    }

    fun getVideoCoverPath(recipeName: String, videoDir: String): String {
        return videoDir + File.separator + recipeName + PICTURE_TYPE_SUFFIX
    }

}