package com.viomi.waterpurifier.edison.ui.fragment;

import android.util.Log;

import com.viomi.ovensocommon.ViomiBaseFragment;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.router.annotation.Route;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;
import com.viomi.waterpurifier.edison.databinding.FragmentFilterBinding;
import com.viomi.waterpurifier.edison.entity.FilterEntity;
import com.viomi.waterpurifier.edison.ui.adapter.FilterAdapter;
import com.viomi.waterpurifier.edison.util.WaterUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;

@Route(path = ViomiRouterConstant.WATER_FRAGMENT_FILTER)
public class FilterFragment extends ViomiBaseFragment<FragmentFilterBinding> {
    private static final String TAG = "FilterFragment";
    List<FilterEntity> filterEntityList;
    private FilterAdapter filterAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_filter;
    }

    @Override
    public void initData() {
        Log.i(TAG, "initData: ");
        filterEntityList = WaterUtils.getFilterEntityList();
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        filterAdapter = new FilterAdapter(filterEntityList);
        viewDataBinding.filterReccylerview.setAdapter(filterAdapter);
    }
    @Override
    public void initListener() {
        Log.i(TAG, "initListener: ");
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(event -> {
            int msgId = event.getMsgId();
            Object msgObj = event.getMsgObject();
            if (msgId == WaterBusEvent.MSG_FILTER_PROPERTY) {
                PropertyEntity propertyEntity = (PropertyEntity) msgObj;
                updateFillterLife(propertyEntity);
                return;
            }
        });
        addDisposable(disposable);
    }

    private void updateFillterLife(PropertyEntity propertyEntity) {
        Log.i(TAG, "updateFillterLife: propertyEntity: " + propertyEntity);
        for (int i = 0; i < filterEntityList.size(); i++) {
            FilterEntity filterEntity = filterEntityList.get(i);
            if (filterEntity.getLifeLevelSiid() == propertyEntity.getSid() && filterEntity.getLifeLevelPiid() == propertyEntity.getPid()) {
                int lifeLevel = (int) propertyEntity.getContent();
                filterEntityList.get(i).setLefePercent(lifeLevel);
                filterAdapter.notifyDataSetChanged();
                break;
            }
        }
    }


}
