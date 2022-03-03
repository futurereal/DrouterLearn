package com.viomi.ovenso.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.TimeUtils;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.MessageVideoBinding;
import com.viomi.ovensocommon.componentservice.camera.CameraServiceFactory;
import com.viomi.ovensocommon.db.MessageEntity;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;


/**
 * @description:
 * @data:2021/9/17
 */
public class MessageVideoView extends ConstraintLayout implements MessageData {
    private static final String TAG = "MessageVideoView";
    private final MessageVideoBinding messageVideoBinding;
    private MessageEntity messageEntity;

    public MessageVideoView(@NonNull Context context) {
        this(context, null);
    }

    public MessageVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        messageVideoBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.message_video, null, false);
        addView(messageVideoBinding.getRoot());
        initData();
    }

    @Override
    public void setMessageEntity(MessageEntity messageEntity) {
        this.messageEntity = messageEntity;
        long messageTime = messageEntity.getMessageTime();
        String messageTimeStr = TimeUtils.millis2String(messageTime, "yyyy.MM.dd");
        messageVideoBinding.videoTime.setText(messageTimeStr);
        messageVideoBinding.videoTitle.setText(messageEntity.getMessageTitle());
        String messagContent = messageEntity.getMessageContent();
        String messageContentFinal = String.format(messagContent, messageEntity.getVideoInfo().getRecipeName());
        messageVideoBinding.videoContent.setText(messageContentFinal);
    }

    private void initData() {
        messageVideoBinding.videoDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_DELETE_MESSAGE, messageEntity);
            }
        });
        messageVideoBinding.videoUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoInfo videoInfo = messageEntity.getVideoInfo();
                Log.i(TAG, "onClick: videoInfo: "+videoInfo);
                CameraServiceFactory.getInstance().getCameraService().showUploadVideoFragement(videoInfo);
            }
        });
    }
}
