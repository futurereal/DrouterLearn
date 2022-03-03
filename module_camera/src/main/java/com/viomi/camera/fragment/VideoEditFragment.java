package com.viomi.camera.fragment;

import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.viomi.camera.R;
import com.viomi.camera.databinding.FragmentVideoEditBinding;
import com.viomi.ffmpeg.cmd.tool.ffmpeg.FFmpegCmdManager;
import com.viomi.ffmpeg.cmd.tool.ffmpeg.VideoEditManager;
import com.viomi.ffmpeg.codec.utils.VideoEditUtils;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.utils.VideoFileUtils;

import java.io.File;

import io.reactivex.disposables.Disposable;

/**
 * @author lixinqi
 * @date 2021/09/18
 * 剪切、拼接、封面视频
 */
public class VideoEditFragment extends BaseDialogFragment<FragmentVideoEditBinding> {
    private final static String TAG = "VideoEditFragment";
    public static final String KEY_VIDEO_INFO = "keyVideoInfo";
    private VideoInfo videoInfo;
    private String videoName;
    private int videoTotalTime;
    private String finalVidePath;


    @Override
    protected void initView() {
        videoInfo = getArguments().getParcelable(KEY_VIDEO_INFO);
        Log.i(TAG, "initView: videoInfo: " + videoInfo);
        videoName = videoInfo.getRecipeName();
        finalVidePath = VideoFileUtils.INSTANCE.getVideoPath(videoName, VideoFileUtils.INSTANCE.getVideoDirOrg());
        videoTotalTime = VideoEditUtils.getVideoTime(finalVidePath);
        Log.i(TAG, "initView: videoTotalTime: " + videoTotalTime);
        int videoTimeTwo = videoInfo.getRecordTime();
        Log.i(TAG, "initView: videoTimeTwo: " + videoTimeTwo);
        videoInfo.setVideoFilePath(finalVidePath);
        videoInfo.setRecordTime(videoTotalTime);
        Log.i(TAG, "VideoEditFragment: videoTotalTime: " + videoTotalTime);
        if (videoTotalTime == 0) {
            Log.i(TAG, "VideoEditFragment: videoTotalTime errror: ");
            return;
        }
        //                FileUtils.deleteAllInDir(VideoFileUtils.INSTANCE.getVideoDirOrg());
        FileUtils.deleteAllInDir(VideoFileUtils.INSTANCE.getVideoDirMergeTemp());
        FileUtils.deleteAllInDir(VideoFileUtils.INSTANCE.getVideoDirCut());
        if (videoTotalTime <= VideoEditUtils.CUT_TOTAL_TIME) {
            String videoOrgDir = VideoFileUtils.INSTANCE.getVideoDirOrg();
            String videoOrgPath = VideoFileUtils.INSTANCE.getVideoPath(videoName, videoOrgDir);
            String resultDirpath = VideoFileUtils.INSTANCE.getVideoDirResult();
            // ffmpeg 处理的文件夹一定要存在
            if (!new File(resultDirpath).exists()) {
                FileUtils.createOrExistsDir(resultDirpath);
            }
            String videoFinalPath = VideoFileUtils.INSTANCE.getVideoPath(videoName, VideoFileUtils.INSTANCE.getVideoDirResult());
            Log.i(TAG, "initView: createResult: " + videoOrgPath + "  finalPath: " + finalVidePath);
            viewDataBinding.videoeditStop.setText(R.string.videoedit_compress_cancle);
            viewDataBinding.videoeditContent.setText(R.string.videoedit_compress);
            VideoEditManager.Companion.getInstance().compressVideo(videoOrgPath, videoFinalPath);
        } else {
            VideoEditManager.Companion.getInstance().cutVideo(videoName, VideoEditUtils.CUT_TOTAL_TIME);
        }
    }

    @Override
    protected void initListener() {
        viewDataBinding.videoeditStop.setOnClickListener(v -> {
            Log.i(TAG, "initListener: ");
            VideoEditManager.Companion.getInstance().stopEdit();
            dismissAllowingStateLoss();
        });
        // 删除所有的临时文件
        Disposable videoEditDisposable = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            Object msgObj = viomiRxBusEvent.getMsgObject();
            Log.i(TAG, "initListener: " + viomiRxBusEvent);
            if (msgId == CommonConstant.MSG_CMD_PROGRESS) {
                if (viewDataBinding.videoeditProgress.getVisibility() != View.VISIBLE) {
                    viewDataBinding.videoeditProgress.setVisibility(View.VISIBLE);
                }
                viewDataBinding.videoeditProgress.setText((int) msgObj + "%");
                return;
            }
            if (msgId != CommonConstant.MSG_CMD_FINISH) {
                return;
            }
            int cmdType = (int) msgObj;
            Log.i(TAG, "initListener: cmdType: " + cmdType);
            if (cmdType == FFmpegCmdManager.TYPE_CUT_FINISH) {
                viewDataBinding.videoeditContent.setText(R.string.videoedit_merging);
                viewDataBinding.videoeditStop.setText(R.string.videoedit_merging_cancle);
                VideoEditManager.Companion.getInstance().mergetVideo(videoName, VideoEditUtils.CUT_TOTAL_TIME);
                return;
            }
            if (cmdType == FFmpegCmdManager.TYPE_MERGE_FINISH) {
                viewDataBinding.videoeditContent.setText(R.string.videoedit_compress);
                viewDataBinding.videoeditStop.setText(R.string.videoedit_compress_cancle);
                String videoCompressOrgPath = VideoFileUtils.INSTANCE.getVideoPath(
                        videoName + VideoFileUtils.INSTANCE.getVIDEO_RESULT_TEMP(),
                        VideoFileUtils.INSTANCE.getVideoDirResult());
                String videoCompressFinalPath = VideoFileUtils.INSTANCE.getVideoPath(videoName,
                        VideoFileUtils.INSTANCE.getVideoDirResult());
                VideoEditManager.Companion.getInstance().compressVideo(videoCompressOrgPath, videoCompressFinalPath);
                return;
            }
            if (cmdType == FFmpegCmdManager.TYPE_COMPRESS_FINISH) {
                viewDataBinding.videoeditProgress.setVisibility(View.GONE);
                viewDataBinding.videoeditContent.setText(R.string.videoedit_makecover);
                viewDataBinding.videoeditStop.setText(R.string.videoedit_makecover_cancle);
                // 删除所有的临时文件
                FileUtils.deleteAllInDir(VideoFileUtils.INSTANCE.getVideoDirMergeTemp());
                FileUtils.deleteAllInDir(VideoFileUtils.INSTANCE.getVideoDirCut());
                String viewTempPath = VideoFileUtils.INSTANCE.getVideoPath(videoName + VideoFileUtils.INSTANCE.getVIDEO_RESULT_TEMP(),
                        VideoFileUtils.INSTANCE.getVideoDirResult());
                FileUtils.delete(new File(viewTempPath));
                finalVidePath = VideoFileUtils.INSTANCE.getVideoPath(videoName, VideoFileUtils.INSTANCE.getVideoDirResult());
                VideoEditManager.Companion.getInstance().makeCover(videoName, finalVidePath);
                return;
            }
            if (cmdType == FFmpegCmdManager.TYPE_COVER_FINISH) {
                showUploadFragment(finalVidePath);
                dismissAllowingStateLoss();
                return;
            }
        });
        addDispose(videoEditDisposable);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_video_edit;
    }


    private void showUploadFragment(String finalVidePath) {
        Log.d(TAG, "makeCover: finalVidePath : " + finalVidePath);
        String coverPath = VideoFileUtils.INSTANCE.getCoverPath(videoName, VideoFileUtils.INSTANCE.getVideoDirResult());
        videoInfo.setCoverFilePath(coverPath);
        videoInfo.setVideoFilePath(finalVidePath);
        videoInfo.setRecipeName(videoName);
        videoInfo.setVideoIndex(videoName + System.currentTimeMillis());
        CameraFragmentManager.getInstance().showVideoUploadFragemnt(videoInfo);
    }
}
