package com.viomi.waterpurifier.edison.ui.fragment;

import android.util.Log;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.rxbus.ViomiRxBusEvent;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.router.annotation.Route;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.databinding.FragmentMineralBinding;
import com.viomi.waterpurifier.edison.entity.MineralEntity;
import com.viomi.waterpurifier.edison.serial.WaterSerialManager;
import com.viomi.waterpurifier.edison.ui.adapter.MineralAdapter;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * @author lxq
 * @date 2021-07-27
 * @description 矿物质设置
 */
@Route(path = ViomiRouterConstant.WATER_FRAGMENT_MINERAL)
public class MineralFragment extends BaseDialogFragment<FragmentMineralBinding> {
    private static final String TAG = "MineralFragment";
    ArrayList<MineralEntity> mineralEntityList;
    private MineralAdapter mineralAdpater;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_mineral;
    }

    @Override
    protected void initView() {
        mineralEntityList = getMineralEntitnyList();
        mineralAdpater = new MineralAdapter(mineralEntityList);
        viewDataBinding.mineralRecyclerview.setAdapter(mineralAdpater);
    }

    private ArrayList<MineralEntity> getMineralEntitnyList() {
        Log.i(TAG, "getMineralEntitnyList: ");
        ArrayList<MineralEntity> mineralEntityList = new ArrayList<>();
        int[] minaralNameIds = new int[]{R.string.mineral_low, R.string.mineral_middle, R.string.mineral_high};
        int[] mineralContentIds = new int[]{R.string.mineral_low_tip, R.string.mineral_middle_tip, R.string.mineral_high_tip};
        int mineralType = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.MINERAL_TYPE.siid, WaterPropEnum.MINERAL_TYPE.piid, WaterConstant.DEFAULT_MINERAL_TYPE);
        for (int i = 0; i < minaralNameIds.length; i++) {
            MineralEntity mineralEntity = new MineralEntity();
            mineralEntity.setName(getString(minaralNameIds[i]));
            mineralEntity.setContent(getString(mineralContentIds[i]));
            if (i == mineralType - 1) {
                mineralEntity.setSelect(true);
            }
            mineralEntityList.add(mineralEntity);
        }
        Log.i(TAG, "getMineralEntitnyList: " + mineralEntityList.size());
        return mineralEntityList;
    }

    @Override
    public void initListener() {
        mineralAdpater.setMineralSelectListener(new MineralAdapter.MineralSelectListener() {
            @Override
            public void mineralSelect(int position) {
                Log.i(TAG, "mineralSelect: position: " + position);
                WaterSerialManager.getInstance().writeProperty(new PropertyEntity(WaterPropEnum.MINERAL_TYPE.siid, WaterPropEnum.MINERAL_TYPE.piid, position + 1));
            }
        });

        Disposable waterDisposable = ViomiRxBus.getInstance().subscribeUi(new Consumer<ViomiRxBusEvent>() {
            @Override
            public void accept(ViomiRxBusEvent viomiRxBusEvent) throws Exception {
                int msgId = viomiRxBusEvent.getMsgId();
                if (msgId != WaterBusEvent.MSG_PROPERTY_MINERAL) {
                    return;
                }
                int mineralType = (int) viomiRxBusEvent.getMsgObject();
                Log.i(TAG, "accept: minerType: "+mineralType);
                updateMineralRecyclerView(mineralType - 1);
            }
        });
        addDispose(waterDisposable);
    }

    private void updateMineralRecyclerView(int position) {
        for (int i = 0; i < mineralEntityList.size(); i++) {
            if (i == position) {
                mineralEntityList.get(i).setSelect(true);
            } else {
                mineralEntityList.get(i).setSelect(false);
            }
        }
        mineralAdpater.notifyDataSetChanged();
    }

}
