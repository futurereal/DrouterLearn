package com.viomi.ovenso.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.common.BaseTitleActivity;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityMessageListBinding;
import com.viomi.ovenso.ui.adapter.MessageListAdapter;
import com.viomi.ovenso.ui.fragment.MessageContentFragment;
import com.viomi.ovenso.util.MessageUtils;
import com.viomi.ovensocommon.CommonAffirmFragment;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.db.MessageEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.router.annotation.Route;

import java.util.List;

/**
 * Description:消息中心
 */
@Route(path = ViomiRouterConstant.OVENSO_MESSAGE_LIST)
public class MessageListActivity extends BaseTitleActivity<ActivityMessageListBinding> {
    private static final String TAG = "MessageListActivity";
    MessageListAdapter messageListAdapter;
    List<MessageEntity> messageEntities;
    private int beforeSelectPosition;
    private int currentPosition = 0;

    @Override
    protected int getChildContentViewId() {
        return R.layout.activity_message_list;
    }

    @Override
    protected String getTitleName() {
        return getString(R.string.messagelist_title);
    }

    @Override
    protected void initData() {
        messageEntities = MessageUtils.getMessageList();
        Log.i(TAG, "initData: " + messageEntities);
        if (messageEntities == null || messageEntities.size() == 0) {
            return;
        }
        messageListAdapter = new MessageListAdapter(messageEntities);
        childViewBinding.messageGroup.setVisibility(View.VISIBLE);
        childViewBinding.messageNo.setVisibility(View.GONE);
        MessageEntity firstMessage = messageEntities.get(0);
        firstMessage.setSelect(true);
        beforeSelectPosition = 0;
        childViewBinding.messageTitleList.setAdapter(messageListAdapter);
        Log.i(TAG, "initChildUi: messageListSize: " + messageEntities.size());
        updateFragment(firstMessage);
    }

    private void updateFragment(MessageEntity messageEntity) {
        Log.i(TAG, "updateFragment: ");
        MessageContentFragment messageContentFragment = new MessageContentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MessageContentFragment.KEY_MESSAGE_ENTITY, messageEntity);
        messageContentFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.message_content, messageContentFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void initChildUi() {
        childViewBinding.messageTitleList.setLayoutManager(new LinearLayoutManager(ApplicationUtils.getContext()));
    }

    @Override
    protected boolean showTheme() {
        return false;
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: " + messageListAdapter);
        if (messageListAdapter == null) {
            return;
        }
        messageListAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(TAG, "onItemClick: postion: " + position);
                MessageEntity messageEntity = messageEntities.get(position);
                messageEntity.setSelect(true);
                messageEntities.get(beforeSelectPosition).setSelect(false);
                beforeSelectPosition = position;
                messageListAdapter.notifyDataSetChanged();
                updateFragment(messageEntity);
            }
        });
        messageListAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                showDeleteDialog(position);
                return false;
            }
        });
        ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            Object objectValue = viomiRxBusEvent.getMsgObject();
            Log.i(TAG, "initListener: msgId: " + msgId);
            if (msgId == OvenBusEventConstants.MSG_DELETE_MESSAGE) {
                MessageEntity messageEntity = (MessageEntity) objectValue;
                int index = messageEntities.indexOf(messageEntity);
                Log.i(TAG, "initListener: index");
                showDeleteDialog(index);
                return;
            }
            if (msgId == OvenBusEventConstants.MSG_DELETE_IM) {
                MessageEntity messageEntity = (MessageEntity) objectValue;
                int position = messageEntities.indexOf(messageEntity);
                messageEntities.remove(position);
                if (messageEntities.size() == 0) {
                    childViewBinding.messageGroup.setVisibility(View.GONE);
                    childViewBinding.messageNo.setVisibility(View.VISIBLE);
                    return;
                }
                if (position >= messageEntities.size()) {
                    position = position - 1;
                }
                beforeSelectPosition = position;
                messageEntities.get(position).setSelect(true);
                messageListAdapter.notifyDataSetChanged();
                updateFragment(messageEntities.get(position));
                return;
            }
        });
    }

    private void showDeleteDialog(int position) {
        CommonAffirmFragment dialog = new CommonAffirmFragment();
        Bundle bundle = CommonAffirmFragment.getBundle(getString(R.string.delete_title), getString(R.string.delete_content), getString(R.string.cancel), getString(R.string.oven_sure), false, 0);
        dialog.setArguments(bundle);
        dialog.setPositiveClickListener(dialog1 -> {
            deleteItem(dialog, position);
        });
        dialog.show(getSupportFragmentManager(), "msgList");
    }

    private void deleteItem(DialogFragment dialogFragment, int position) {
        Log.i(TAG, "deleteItem: postion: " + position);
        int deleteResult = MessageUtils.deleteMessage(messageEntities.get(position));
        Log.i(TAG, "deleteVideo: deleteResult: " + deleteResult);
        if (deleteResult > 0) {
            dialogFragment.dismissAllowingStateLoss();
            messageEntities.remove(position);
            if (messageEntities.size() == 0) {
                childViewBinding.messageGroup.setVisibility(View.GONE);
                childViewBinding.messageNo.setVisibility(View.VISIBLE);
                return;
            }
            if (position >= messageEntities.size()) {
                position = position - 1;
            }
            beforeSelectPosition = position;
            messageEntities.get(position).setSelect(true);
            messageListAdapter.notifyDataSetChanged();
            updateFragment(messageEntities.get(position));
        }
    }
}

