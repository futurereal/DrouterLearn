package com.viomi.camera.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.viomi.camera.CameraConstants;
import com.viomi.camera.R;
import com.viomi.camera.databinding.FragmentUploadResultBinding;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;

/**
 * @author admin
 * 提示框
 */
public class UploadResultFragment extends BaseDialogFragment<FragmentUploadResultBinding> {
    private final static String TAG = "UploadResultFragment";
    public static final String KEY_VIDEOINFO = "keyVideoInfo";
    public static final String KEY_UPLOAD_RESULT = "keyUploadResult";
    public static final String KEY_TYPE = "keyUploadType";
    private VideoInfo videoInfo;
    private int uploadType;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_upload_result;
    }

    @Override
    protected void initView() {
        Bundle bundle = getArguments();
        boolean uploadResult = bundle.getBoolean(KEY_UPLOAD_RESULT);
        videoInfo = bundle.getParcelable(KEY_VIDEOINFO);
        Log.i(TAG, "initChildView: uploadResult: " + uploadResult);
        if (!uploadResult) {
            uploadType = bundle.getInt(KEY_TYPE);
            String uploadName = "";
            String uploadPath = "";
            if (uploadType == CameraConstants.UPLOAD_TYPE_VIDEO) {
                uploadName = getString(R.string.camera_video);

            } else if (uploadType == CameraConstants.UPLOAD_TYPE_COVER) {
                uploadName = getString(R.string.camera_cover);
            }
            String title = getString(R.string.upload_failure_text);
            viewDataBinding.uploadresultTitle.setText(uploadName + title);
            viewDataBinding.uploadresultContent.setText(getString(R.string.upload_failtip));
            viewDataBinding.uploadresultFinish.setVisibility(View.GONE);
            viewDataBinding.uploadresultGroup.setVisibility(View.VISIBLE);
            long insertResult = OvensoServiceFactory.getInstance().getOvenService().insertVideoInfoMessage(videoInfo);
            Log.i(TAG, "initView: insertResult: " + insertResult);
            return;
        }
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.uploadresultRetry.setOnClickListener(v -> {
            dismissAllowingStateLoss();
            CameraFragmentManager.getInstance().showVideoUploadFragemnt(videoInfo);
        });

        viewDataBinding.uploadresultClose.setOnClickListener(v -> {
            dismissAllowingStateLoss();
            ViomiRxBus.getInstance().post(CommonConstant.MSG_FINISH_ACTIVITY);
        });
        viewDataBinding.uploadresultFinish.setOnClickListener(v -> {
            dismissAllowingStateLoss();
            OvensoServiceFactory.getInstance().getOvenService().deleteVideoMessage(videoInfo);
            ViomiRxBus.getInstance().post(CommonConstant.MSG_FINISH_ACTIVITY);
        });
    }
}
