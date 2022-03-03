package com.viomi.ffmpeg.codec;

import android.util.Log;
import android.view.View;

import com.viomi.ovensocommon.utils.VideoFileUtils;

import com.viomi.ffmpeg.codec.databinding.ActivityFfmpegCodecBinding;
import com.viomi.ffmpeg.codec.utils.VideoEditCallback;
import com.viomi.ffmpeg.codec.utils.VideoEditUtils;
import com.viomi.ovensocommon.BaseActivity;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

public class FFmpegCodecActivity extends BaseActivity<ActivityFfmpegCodecBinding> {
    private static final String TAG = "FFmpegCmdActivity";
    private static final int CUT_TOTAL_TIME = 15;
    private final String RECIPE_NAME = "测试菜谱";
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_ffmpeg_codec;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {
        super.initListener();
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }

        viewDataBinding.ffmpegcodecCutvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "-------------剪切视频1 :");
                String videoName = "微波";
                int videoTotalTime = 50;
                String origVideoPath = VideoFileUtils.INSTANCE.createVideoFile(videoName, VideoFileUtils.INSTANCE.getVideoDirOrg());
                Log.i(TAG, "cutVideo:origVideoPath:  " + origVideoPath);
                // 计算截取数据的集合
                ArrayList<Integer> startTimeList = new ArrayList<>(CUT_TOTAL_TIME);
                // 取第一秒
                startTimeList.add(1);
                int stepCount = videoTotalTime / CUT_TOTAL_TIME;
                Log.i(TAG, "cutVideo: stepCount : " + stepCount);
                if (stepCount <= 0) {
                    stepCount = 1;
                }
                int begainTime = 1;
                for (int i = 1; i < CUT_TOTAL_TIME - 2; i++) {
                    int time = begainTime + i * stepCount;
                    startTimeList.add(time);
                }
                // 取最后1s
                startTimeList.add(videoTotalTime - 5);
                for (int i = 0; i < startTimeList.size(); i++) {
                    String targetVideoPath = VideoFileUtils.INSTANCE.createVideoCoverFile(videoName + i, VideoFileUtils.INSTANCE.getVideoDirCut());
                    Log.i(TAG, "initChildUi: origVideoPath " + origVideoPath + " targetVideoPath: " + targetVideoPath);
                }
                VideoEditUtils.cutVideo(30, "微博", new VideoEditCallback() {
                    @Override
                    public void onProgressUpdate(int progress) {

                    }

                    @Override
                    public void onEditFinish(String destFilePah) {

                    }
                });
            }

        });

        viewDataBinding.ffmpegcodecMergevideo.setOnClickListener(v -> {
            Log.d(TAG, "-------------合成视频: list: ");
            String videoName = "微波";
            int videoTotalTime = 50;
            String origVideoPath = VideoFileUtils.INSTANCE.getVideoPath(videoName, VideoFileUtils.INSTANCE.getVideoDirOrg());
            Log.i(TAG, "merge:origVideoPath:  " + origVideoPath);
            // 计算截取数据的集合
            ArrayList<Integer> startTimeList = new ArrayList<>(CUT_TOTAL_TIME);
            // 取第一秒
            startTimeList.add(1);
            int stepCount = videoTotalTime / CUT_TOTAL_TIME;
            Log.i(TAG, "mergeVideo: stepCount : " + stepCount);
            if (stepCount <= 0) {
                stepCount = 1;
            }
            int begainTime = 1;
            for (int i = 1; i < CUT_TOTAL_TIME - 2; i++) {
                int time = begainTime + i * stepCount;
                startTimeList.add(time);
            }
            // 取最后1s
            startTimeList.add(videoTotalTime - 5);
            ArrayList<String> allVideoPath = new ArrayList<>();
            for (int i = 0; i < startTimeList.size(); i++) {
                String targetVideoPath = VideoFileUtils.INSTANCE.getVideoPath(videoName + i, VideoFileUtils.INSTANCE.getVideoDirCut());
                allVideoPath.add(targetVideoPath);
            }

        });

        viewDataBinding.ffmpegcodecExtractPicture.setOnClickListener(v -> {

        });

    }


    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }
}