package com.viomi.ovenso.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ItemMessageTitleBinding;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;
import com.viomi.ovensocommon.db.MessageEntity;

import java.util.List;

/**
 * Created by Ljh on 2020/9/25
 */
public class MessageListAdapter extends BaseRecyclerViewAdapter<MessageListAdapter.Holder> {
    //
    private List<MessageEntity> messageEntities;

    public MessageListAdapter(List<MessageEntity> messageEntities) {
        this.messageEntities = messageEntities;
    }

    public void update(List<MessageEntity> messageEntities) {
        this.messageEntities = messageEntities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        ItemMessageTitleBinding itemMessageTitleBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_message_title, parent, false);
        return new Holder(itemMessageTitleBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return messageEntities == null ? 0 : messageEntities.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final ItemMessageTitleBinding itemMessageTitleBinding;
        private final MessageListAdapter adapter;

        Holder(ItemMessageTitleBinding itemMessageTitleBinding, MessageListAdapter adapter) {
            super(itemMessageTitleBinding.getRoot());
            this.itemMessageTitleBinding = itemMessageTitleBinding;
            this.adapter = adapter;
        }

        private void bindView(int position) {
            MessageEntity messageEntity = messageEntities.get(position);
            itemMessageTitleBinding.messagetitleIcon.setImageResource(messageEntity.getIconResId());
            itemMessageTitleBinding.messagetitleName.setText(messageEntity.getMessageTitle());
            boolean isSelect = messageEntity.isSelect();
            if (isSelect) {
                itemMessageTitleBinding.getRoot().setBackgroundResource(R.color.color_basetitle_bg);
            } else {
                itemMessageTitleBinding.getRoot().setBackgroundResource(R.color.messagetitle_normal_bg);
            }
            itemMessageTitleBinding.getRoot().setOnClickListener(view -> {
                adapter.onItemHolderClick(this, 1000);
            });
            itemMessageTitleBinding.getRoot().setOnLongClickListener(view -> {
                return adapter.onItemHolderLongClick(this);
            });
        }
    }
}
