package com.viomi.ovenso.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.viomi.ovenso.ui.view.MessageFaultView;
import com.viomi.ovenso.ui.view.MessageVideoView;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.db.MessageEntity;
import com.viomi.router.annotation.Route;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 日期与时间
 * @Author: randysu
 */
@Route(path = ViomiRouterConstant.OVENS0_MESSAGECONTENT)
public class MessageContentFragment extends Fragment {
    private static final String TAG = "MessageContentFragment";
    public static final String KEY_MESSAGE_ENTITY = "keyMessageEntity";
    private MessageEntity messageEntity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        Bundle bundle = getArguments();
        messageEntity = bundle.getParcelable(KEY_MESSAGE_ENTITY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        int messageType = messageEntity.getMessageType();
        Log.i(TAG, "onCreateView: messageType: "+messageType);
        if (messageType == MessageEntity.TYPE_ERROR) {
            MessageFaultView messageFaultView = new MessageFaultView(getContext());
            messageFaultView.setMessageEntity(messageEntity);
            view = messageFaultView;
        } else if (messageType == MessageEntity.TYPE_VIDEO) {
            MessageVideoView messageVideoView = new MessageVideoView(getContext());
            messageVideoView.setMessageEntity(messageEntity);
            view = messageVideoView;
        }
        return view;
    }


}
