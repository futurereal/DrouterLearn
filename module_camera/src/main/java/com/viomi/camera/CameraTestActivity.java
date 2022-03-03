package com.viomi.camera;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.viomi.camera.databinding.ActivityCameraTestBinding;
import com.viomi.camera.fragment.CameraFragmentManager;
import com.viomi.ffmpeg.cmd.tool.ffmpeg.VideoEditManager;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.componentservice.camera.CameraServiceFactory;
import com.viomi.ovensocommon.utils.VideoFileUtils;
import com.viomi.ovensocommon.BaseActivity;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;

import java.io.File;

import io.reactivex.disposables.CompositeDisposable;

@Route(path = ViomiRouterConstant.CAMERA_TEST_MAIN)
public class CameraTestActivity extends BaseActivity<ActivityCameraTestBinding> {
    private static final String TAG = "CameraTestActivity";
    //    private static final String RECIPE_NAME = "微波";
    private static final String RECIPE_NAME = "脆烤";
    private static final int RECIPE_TOTAL_TIME = 590;
    private static final int RECIPE_TOTAL_TIME_LESS = 580;
    public static final String MODE_ID = "17";
    private CompositeDisposable mCompositeDisposable;

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
        mCompositeDisposable.add(ViomiRxBus.getInstance().subscribeUi(ovenBusEvent -> {
            Log.i(TAG, "eventId: " + ovenBusEvent.getMsgId());
            int msgId = ovenBusEvent.getMsgId();
            Object msgObject = ovenBusEvent.getMsgObject();
            if (CameraRxBusEvent.MSG_RECORD_WITH_VIDEO == ovenBusEvent.getMsgId()) {
            }
            if (msgId == CommonConstant.MSG_SHOW_EDITE_FRAGMENT) {
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setRecipeName(RECIPE_NAME);
                videoInfo.setModeId(MODE_ID);
                videoInfo.setRecordTime(RECIPE_TOTAL_TIME);
                showEditFragment(videoInfo);
                return;
            }
        }));
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_camera_test;
    }

    public void startCamera(View view) {
        Log.i(TAG, "startCamera: ");
        // 默认是有问题的
        ViomiRouter.getInstance().build(ViomiRouterConstant.CAMERA_RECORD)
                .withString(ViomiRouterConstant.CAMERA_KEY_RECIPENAME, RECIPE_NAME)
                .withBoolean(ViomiRouterConstant.CAMERA_KEY_COOKING, true)
                .withString(ViomiRouterConstant.CAMERA_KEY_MODEID, MODE_ID)
                .navigation(CameraTestActivity.this);
    }

    private void showEditFragment(VideoInfo videoInfo) {
        // 需要在 onResume 状态 才能弹出 FragmentDialog
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            CameraServiceFactory.getInstance().getCameraService().showEditVideoFragment(videoInfo);
        }, 300);
    }

    public void videoCut(View view) {
        Log.i(TAG, "videoCut: ");
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setRecipeName(RECIPE_NAME);
        videoInfo.setRecordTime(RECIPE_TOTAL_TIME);
        videoInfo.setModeId(MODE_ID);
        CameraFragmentManager.getInstance().showVideoEditFragemnt(videoInfo);
    }

    public void videoMerge(View view) {
        Log.i(TAG, "videoMerge: ");
//        VideoEditManager.Companion.getInstance().mergetVideo(RECIPE_NAME, 14);
//        VideoEditManager.Companion.getInstance().compressVideo(RECIPE_NAME);
    }

    public void videoCover(View view) {
        Log.i(TAG, "videoCover: ");
        String srcPath = VideoFileUtils.INSTANCE.getVideoPath(RECIPE_NAME, VideoFileUtils.INSTANCE.getVideoDirResult());
        VideoEditManager.Companion.getInstance().makeCover(RECIPE_NAME, srcPath);
    }

    public void uploadAll(View view) {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setRecipeName(RECIPE_NAME);
        String videoFilePath = VideoFileUtils.INSTANCE.getVideoPath(RECIPE_NAME, VideoFileUtils.INSTANCE.getVideoDirResult());
        String videoCoverPath = VideoFileUtils.INSTANCE.getCoverPath(RECIPE_NAME, VideoFileUtils.INSTANCE.getVideoDirResult());
        Log.i(TAG, "uploadVideo: videoExist: " + new File(videoFilePath).exists());
        videoInfo.setVideoFilePath(videoFilePath);
        videoInfo.setCoverFilePath(videoCoverPath);
        videoInfo.setVideoIndex("101");
        videoInfo.setModeId(MODE_ID);
        videoInfo.setRecordTime(RECIPE_TOTAL_TIME);
        Log.i(TAG, "uploadVideo: videoInfo: " + videoInfo);
        CameraFragmentManager.getInstance().showVideoUploadFragemnt(videoInfo);
    }

    public void deleteAllVideo(View view) {
        CameraServiceFactory.getInstance().getCameraService().deleteAllVideo();
    }

    public void getAllViedeoInfo(View view) {

    }

    public void startCompress(View view) {
        String videoOrgDir = VideoFileUtils.INSTANCE.getVideoDirOrg();
        String videoOrgPath = VideoFileUtils.INSTANCE.getVideoPath(RECIPE_NAME, videoOrgDir);
        String videoFinalPath = VideoFileUtils.INSTANCE.getVideoPath(RECIPE_NAME + VideoFileUtils.INSTANCE.getVIDEO_RESULT_TEMP(), VideoFileUtils.INSTANCE.getVideoDirResult());
        Log.i(TAG, "initView: createResult: " + videoOrgPath + "  videoFinalPath: " + videoFinalPath);
        VideoEditManager.Companion.getInstance().compressVideo(videoOrgPath, videoFinalPath);
    }

    public void gotoMain(View view) {
        Log.i(TAG, "gotoMain: ");
        ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MESSAGE_LIST).navigation();
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