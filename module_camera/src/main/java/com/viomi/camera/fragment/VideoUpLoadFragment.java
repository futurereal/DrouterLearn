package com.viomi.camera.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.viomi.camera.CameraConstants;
import com.viomi.camera.CameraRxBusEvent;
import com.viomi.camera.R;
import com.viomi.camera.databinding.FragmentVideoUploadBinding;
import com.viomi.camera.http.CameraRequestManager;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;

import io.reactivex.disposables.Disposable;

/**
 * @author admin
 * 上传视频
 */
public class VideoUpLoadFragment extends BaseDialogFragment<FragmentVideoUploadBinding> {
    private static final String TAG = "VideoUpLoadFragment";
    private final VideoInfo videoInfo;
    private int currentUploadType = CameraConstants.UPLOAD_TYPE_VIDEO;

    public VideoUpLoadFragment(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_video_upload;
    }


    @Override
    protected void initView() {
        Log.i(TAG, "initChildView: ");
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.uploadCancel.setOnClickListener(v -> {
            Log.i(TAG, "initListener: dismisss");
            long insertResult = OvensoServiceFactory.getInstance().getOvenService().insertVideoInfoMessage(videoInfo);
            Log.i(TAG, "initView: insertResult: " + insertResult);
            dismissAllowingStateLoss();
            CameraRequestManager.getInstance().cancelUpload();
        });
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            Object msgObj = viomiRxBusEvent.getMsgObject();
            Log.i(TAG, "initListener: " + viomiRxBusEvent);
            if (msgId == CameraRxBusEvent.MSG_UPLOAD_PREPARE) {
                viewDataBinding.uploadContent.setText(R.string.upload_prepare);
                viewDataBinding.uploadProgress.setVisibility(View.GONE);
                return;
            }
            if (msgId == CameraRxBusEvent.MSG_UPLOAD_PROGRESS) {
                if (viewDataBinding.uploadProgress.getVisibility() != View.VISIBLE) {
                    viewDataBinding.uploadProgress.setVisibility(View.VISIBLE);
                    if (currentUploadType == CameraConstants.UPLOAD_TYPE_COVER) {
                        viewDataBinding.uploadContent.setText(R.string.upload_cover_tip);
                    }
                    if (currentUploadType == CameraConstants.UPLOAD_TYPE_VIDEO) {
                        viewDataBinding.uploadContent.setText(R.string.upload_video_tip);
                    }
                }
                int progress = (int) msgObj;
                viewDataBinding.uploadProgress.setText(String.valueOf(progress));
                return;
            }

            if (msgId == CameraRxBusEvent.MSG_UPLOAD_VIDEO_SUCCESS) {
                String videoId = (String) msgObj;
                CameraRequestManager.getInstance().uploadCover(videoId, videoInfo.getCoverFilePath());
                currentUploadType = CameraConstants.UPLOAD_TYPE_COVER;
                return;
            }
            if (msgId == CameraRxBusEvent.MSG_UPLOAD_COVER_SUCCESS) {
                dismissAllowingStateLoss();
                showVideoUpLoadResultFragment(true, videoInfo);
//                FileUtils.deleteAllInDir(VideoFileUtils.INSTANCE.getVideoDirResult());
                return;
            }

            if (msgId == CameraRxBusEvent.MSG_UPLOAD_VIDEO_FAIL) {
                showVideoUpLoadResultFragment(false, videoInfo);
                return;
            }

            if (msgId == CameraRxBusEvent.MSG_UPLOAD_COVER_FAIL) {
                showVideoUpLoadResultFragment(false, videoInfo);
                return;
            }
        });
        addDispose(disposable);
    }

    @Override
    public void onResume() {
        super.onResume();
        CameraRequestManager.getInstance().uploadVideo(videoInfo);
    }

    public void showVideoUpLoadResultFragment(boolean uploadResult, VideoInfo videoInfo) {
        Log.d(TAG, "showVideoUpLoadResultFragment: ");
        dismissAllowingStateLoss();
        FragmentActivity mActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        UploadResultFragment uploadResultFragment = new UploadResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(UploadResultFragment.KEY_VIDEOINFO, videoInfo);
        bundle.putBoolean(UploadResultFragment.KEY_UPLOAD_RESULT, uploadResult);
        bundle.putInt(UploadResultFragment.KEY_TYPE, currentUploadType);
        uploadResultFragment.setArguments(bundle);
        uploadResultFragment.setCancelable(false);
        uploadResultFragment.show(mActivity.getSupportFragmentManager(), "TipDialog");
    }


}
