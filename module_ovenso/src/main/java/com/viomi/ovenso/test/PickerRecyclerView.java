package com.viomi.ovenso.test;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.util.ItemDecorationUtil;

import java.util.ArrayList;

/**
 * @description:
 * @data:2022/1/20
 */
public class PickerRecyclerView extends RecyclerView {
    private static final String TAG = "PickerRecyclerView";
    private static final int MSG_UPDATE = 1;
    private PickerAdapter pickerAdapter;
    private final Handler recyclerHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            pickerAdapter.notifyDataSetChanged();
        }
    };

    public PickerRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public PickerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addItemDecoration(ItemDecorationUtil.linearHorDecor(ApplicationUtils.getContext(),
                20));
        initData();
    }

    private void initData() {
        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(this);
        ArrayList<PickerEntity> pickerEntityArrayList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PickerEntity pickerEntity = new PickerEntity();
            pickerEntity.setName(String.valueOf(i));
            pickerEntityArrayList.add(pickerEntity);
        }
        pickerAdapter = new PickerAdapter(pickerEntityArrayList);
        setAdapter(pickerAdapter);
        addOnScrollListener(new OnScrollListener() {
            private static final int MIN_SCALE = 3;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i(TAG, "onScrolled: dx: " + dx);
                int childCount = recyclerView.getChildCount();
                Log.i(TAG, "onScrolled: childCount: " + childCount);
                View child = recyclerView.getChildAt(childCount / 2);
                int position = recyclerView.getChildAdapterPosition(child);
                pickerAdapter.setCenterPosition(position);
                recyclerHandler.removeMessages(MSG_UPDATE);
                recyclerHandler.sendEmptyMessageDelayed(MSG_UPDATE, 60);
            }
        });
    }
}
