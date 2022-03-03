package com.viomi.ffmpeg.codec.utils;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.viomi.ovensocommon.utils.VideoFileUtils;
import com.viomi.ffmpeg.codec.videoedit.ffmpegnative.FFmpegEdit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lixinqi
 * @date 2021/09/09
 * @description 视频编辑工具类
 */
public class VideoEditUtils {
    private final static String TAG = "VideoEditUtils";
    // 剪切视频的时间10s
    public static final int CUT_TOTAL_TIME = 15;
    // 每个视频的 时间
    private static final int CUT_DURATION = 1;
    private static int videoIndex = 1;

    /**
     * 剪切视频
     *
     * @param videoTotalTime 原始视频的长度
     * @param videoName      截切视频的名字
     * @param callback       剪切完成回调
     **/
    public static void cutVideo(Integer videoTotalTime, String videoName, VideoEditCallback callback) {
        String targetVideoName = videoName + "_Cut";
        List<String> targetVideoPathList = new ArrayList<>();
        String origVideoPath = VideoFileUtils.INSTANCE.getVideoPath(videoName, VideoFileUtils.INSTANCE.getVideoDirOrg());
        Log.i(TAG, "cutVideo:origVideoPath:  " + origVideoPath);
        ArrayList<Integer> startTimeList = getStartTimeList(videoTotalTime);
        videoIndex = 1;
        // 分发时间开始剪切
        Observable.fromIterable(startTimeList)
                .observeOn(Schedulers.newThread())
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer begainTime) throws Exception {
                        Log.i(TAG, "apply: begainTime: " + begainTime + " totalTime:" + videoTotalTime);
                        String targetVideoPath = VideoFileUtils.INSTANCE.getVideoPath(videoName + videoIndex, VideoFileUtils.INSTANCE.getVideoDirCut());
                        Log.i(TAG, "apply: targetVideoPath: " + targetVideoPath);
                        FFmpegEdit.doCut(origVideoPath, targetVideoPath, begainTime, CUT_DURATION);
                        videoIndex++;
                        return begainTime;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer begainTime) {
                        int progress = begainTime * 100 / (videoTotalTime - 2);
                        Log.i(TAG, "accept: progress : " + progress);
                        callback.onProgressUpdate(progress);
                    }
                });

    }

    @NonNull
    private static ArrayList<Integer> getStartTimeList(Integer videoTotalTime) {
        // 计算截取数据的集合
        ArrayList<Integer> cutTimeList = new ArrayList<>(CUT_TOTAL_TIME);
        // 取第一秒
        cutTimeList.add(1);
        int stepCount = videoTotalTime / CUT_TOTAL_TIME;
        Log.i(TAG, "cutVideo: stepCount : " + stepCount);
        if (stepCount <= 0) {
            stepCount = 1;
        }
        int begainTime = 1;
        for (int i = 1; i < CUT_TOTAL_TIME - 2; i++) {
            int time = begainTime + i * stepCount;
            Log.i(TAG, "cutVideo: time  " + time);
            cutTimeList.add(time);
        }
        // 取最后1s
        cutTimeList.add(44);
        return cutTimeList;
    }

    static String destFilePah;
    static String srcFileOnePath = "";
    static String srcFileTwoPath = "";

    /**
     * 拼接视频
     **/
    public static void mergeVideo(String recipeName, VideoEditCallback callback) {
        Log.d(TAG, "mergeVideo: ");
        videoIndex = 1;
        ArrayList<Integer> indexList = new ArrayList<>();
        for (int i = 1; i < CUT_TOTAL_TIME - 1; i++) {
            indexList.add(i);
        }
        Observable.fromIterable(indexList)
                .observeOn(Schedulers.newThread())
                .map(time -> {
                    Log.i(TAG, "apply: time: " + time);
                    if (videoIndex == 1) {
                        srcFileOnePath = VideoFileUtils.INSTANCE.getVideoPath(recipeName + videoIndex, VideoFileUtils.INSTANCE.getVideoDirCut());
                        srcFileTwoPath = VideoFileUtils.INSTANCE.getVideoPath(recipeName + (videoIndex + 1), VideoFileUtils.INSTANCE.getVideoDirCut());
                        destFilePah = VideoFileUtils.INSTANCE.getVideoPath(recipeName + videoIndex, VideoFileUtils.INSTANCE.getVideoDirMergeTemp());
                        videoIndex = videoIndex + 2;
                    } else {
                        long destFileLength = FileUtils.getFileLength(destFilePah);
                        long srcFileOnePathLength = FileUtils.getFileLength(srcFileOnePath);
                        Log.i(TAG, "apply: destFileLength : " + destFileLength + "   srcFileOnePathLength: " + srcFileOnePathLength);
                        Log.i(TAG, "apply: destFileLength : " + destFilePah + "   srcFileOnePathLength: " + srcFileOnePath);
                        if (destFileLength <= srcFileOnePathLength) {
                            videoIndex = videoIndex + 1;
                            return time;
                        }
                        srcFileOnePath = destFilePah;
                        srcFileTwoPath = VideoFileUtils.INSTANCE.getVideoPath(recipeName + videoIndex, VideoFileUtils.INSTANCE.getVideoDirCut());
                        destFilePah = VideoFileUtils.INSTANCE.getVideoPath(recipeName + videoIndex, VideoFileUtils.INSTANCE.getVideoDirMergeTemp());
                        videoIndex = videoIndex + 1;
                    }
                    Log.i(TAG, "apply: srcFileOnePath " + time + "   " + srcFileOnePath);
                    Log.i(TAG, "apply: srcFileTwoPath " + time + "   " + srcFileTwoPath);
                    Log.i(TAG, "apply: srcFiledestPath " + time + "   " + destFilePah);
                    FFmpegEdit.MergeTwo(srcFileOnePath, srcFileTwoPath, destFilePah);
                    Log.i(TAG, "apply: srcFiledestPath  end " + time);
                    return time;
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer currentIndex) throws Exception {
                        Log.i(TAG, "accept: currentIndex : " + currentIndex + "  CUT_TOTAL_TIME:" + CUT_TOTAL_TIME);
                        int progress = currentIndex * 100 / (CUT_TOTAL_TIME - 2);
                        Log.d(TAG, "mergeVideo: progress: " + progress);
                        callback.onProgressUpdate(progress);
                        if (progress == 100) {
                            callback.onEditFinish(destFilePah);
                        }
                    }
                });
    }


    /**
     * 截取视频封面
     *
     * @param srcVideoPath 视频源文件
     * @param recipeName   菜谱的名字
     **/
    public static String makeCover(String srcVideoPath, String recipeName) {
        Log.d(TAG, "makeCover: ");
        String start = "00:00:01";
        String videoCoverPath = VideoFileUtils.INSTANCE.getVideoCoverPath(recipeName, VideoFileUtils.INSTANCE.getVideoDirResult());
        FFmpegEdit.doJpgGet(srcVideoPath, videoCoverPath, start, false, 1);
        return videoCoverPath;
    }

    public static void cutVideo(String recipeName) {
        String videoPath = VideoFileUtils.INSTANCE.getVideoPath(recipeName, VideoFileUtils.INSTANCE.getVideoDirResult());
        int videoTotalTime = getVideoTime(videoPath);


    }


    public static int getVideoTime(String videoFilePath) {
        Log.i(TAG, "getVideoTime: exist: " + FileUtils.isFileExists(videoFilePath));
        MediaPlayer mediaPlayer = new MediaPlayer();
        int duration = 0;
        try {
            mediaPlayer.setDataSource(videoFilePath);
            mediaPlayer.prepare();
            long durationLong = mediaPlayer.getDuration();
            duration = (int) (durationLong / 1000);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "getVideoTime: " + e.getMessage());
        } finally {
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        duration = 0;
        if (duration == 0) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoFilePath);
            duration = Integer.parseInt(mediaMetadataRetriever.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
        }
        Log.i(TAG, "getVideoTime: duration: " + duration);
        return duration;
    }

}
