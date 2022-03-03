package com.viomi.waterpurifier.edison.ui.fragment;

import android.app.Dialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.rxbus.ViomiRxBusEvent;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;
import com.viomi.waterpurifier.edison.databinding.FragmentFilterWashBinding;
import com.viomi.waterpurifier.edison.entity.FilterWashTitleEntity;
import com.viomi.waterpurifier.edison.ui.adapter.FilterWashTitleAdapter;
import com.viomi.waterpurifier.edison.ui.adapter.RecyclerViewDecoration;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @date 2021-07-26
 * @Description 滤芯清洗的界面
 */
public class FilterWashFragment extends BaseDialogFragment<FragmentFilterWashBinding> {
    private final static String TAG = "FilterWashFragment";
    private ArrayList<FilterWashTitleEntity> filterWashTitleEntityArrayList;
    private int beforePosition = 0;
    private FilterWashTitleAdapter filterWashTitleAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_filter_wash;
    }

    @Override
    protected void initView() {
        filterWashTitleEntityArrayList = initTitleData();
        filterWashTitleAdapter = new FilterWashTitleAdapter(filterWashTitleEntityArrayList);
        viewDataBinding.filterwashStepRecyclerView.setAdapter(filterWashTitleAdapter);
        // 用来设置间距
        RecyclerViewDecoration recyclerViewDecoration = new RecyclerViewDecoration(-80);
        viewDataBinding.filterwashStepRecyclerView.addItemDecoration(recyclerViewDecoration);
    }

    private ArrayList<FilterWashTitleEntity> initTitleData() {
        int[] titleNameIds = new int[]{R.string.fitlerwash_step_one, R.string.fitlerwash_step_two, R.string.fitlerwash_step_three};
        ArrayList<FilterWashTitleEntity> filterWashTitleEntityArrayList = new ArrayList<>(titleNameIds.length);
        for (int titleNameId : titleNameIds) {
            FilterWashTitleEntity filterWashTitleEntity = new FilterWashTitleEntity();
            String commonTitle = getString(R.string.fitlerwash_title_common);
            String titleName = String.format(commonTitle, getString(titleNameId));
            filterWashTitleEntity.setTitleName(titleName);
            filterWashTitleEntityArrayList.add(filterWashTitleEntity);
        }
        beforePosition = 0;
        Log.i(TAG, "initTitleData: filterWashTitleEntityArrayList : size: " + filterWashTitleEntityArrayList.size());
        return filterWashTitleEntityArrayList;
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.filterwashOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
        Disposable filterWashDisposable = ViomiRxBus.getInstance().subscribeUi(new Consumer<ViomiRxBusEvent>() {
            @Override
            public void accept(ViomiRxBusEvent viomiRxBusEvent) throws Exception {
                switch (viomiRxBusEvent.getMsgId()) {
                    case WaterBusEvent.MSG_FILTER_WASH_PROGRESS:
                        int progress = (int) viomiRxBusEvent.getMsgObject();
                        Log.i(TAG, "accept: progress:  " + progress);
                        viewDataBinding.filerwashProgress.setCurrentProgress(progress);
                        if (progress == 33) {
                            filterWashTitleEntityArrayList.get(0).setStepStatus(FilterWashTitleEntity.STATUS_COMING);
                            filterWashTitleAdapter.notifyDataSetChanged();
                            viewDataBinding.filerwashChangeWater.setVisibility(View.VISIBLE);
                            viewDataBinding.filerwashProgress.setProgressVisible(false);
                            return;
                        }
                        if (progress == 66) {
                            filterWashTitleEntityArrayList.get(1).setStepStatus(FilterWashTitleEntity.STATUS_COMING);
                            viewDataBinding.filerwashChangeWater.setVisibility(View.VISIBLE);
                            viewDataBinding.filerwashProgress.setProgressVisible(false);
                            filterWashTitleAdapter.notifyDataSetChanged();
                            return;
                        }
                        if (progress == 99) {
                            filterWashTitleEntityArrayList.get(2).setStepStatus(FilterWashTitleEntity.STATUS_COMING);
                            viewDataBinding.filerwashChangeWater.setVisibility(View.VISIBLE);
                            viewDataBinding.filerwashProgress.setProgressVisible(false);
                            filterWashTitleAdapter.notifyDataSetChanged();
                            return;
                        }
                        viewDataBinding.filerwashChangeWater.setVisibility(View.GONE);
                        viewDataBinding.filerwashProgress.setProgressVisible(true);
                        filterWashTitleEntityArrayList.get(beforePosition).setStepStatus(FilterWashTitleEntity.STATUS_COMING);
                        if (progress == 100) {
                            viewDataBinding.filerwashGroupFinish.setVisibility(View.VISIBLE);
                            viewDataBinding.filerwashProgress.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        });
        addDispose(filterWashDisposable);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setBackgroundDrawableResource(R.color.transparent);
    }

}
