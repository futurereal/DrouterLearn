package com.viomi.ovenso.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.TimeUtils;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.MessageFaultBinding;
import com.viomi.ovensocommon.db.MessageEntity;


/**
 * @description:
 * @data:2021/9/17
 */
public class MessageFaultView extends ConstraintLayout implements MessageData {
    private static final String TAG = "MessageFaultView";
    private final MessageFaultBinding faultMessageView;
    private MessageEntity messageEntity;


    public MessageFaultView(@NonNull Context context) {
        this(context, null);
    }

    public MessageFaultView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageFaultView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        faultMessageView = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.message_fault, null, false);
        addView(faultMessageView.getRoot());
        initData();
    }

    @Override
    public void setMessageEntity(MessageEntity messageEntity) {
        this.messageEntity = messageEntity;
        Log.i(TAG, "setMessageEntity: ");
        long messageTime = messageEntity.getMessageTime();
        String messageTimeStr = TimeUtils.millis2String(messageTime,"yyyy.MM.dd");
        faultMessageView.faultTime.setText(messageTimeStr);
        faultMessageView.faultContent.setText(messageEntity.getMessageContent());
        faultMessageView.faultTitle.setText(messageEntity.getMessageTitle());
    }

    private void initData() {

    }
}
