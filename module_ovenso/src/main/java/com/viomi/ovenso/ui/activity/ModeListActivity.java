package com.viomi.ovenso.ui.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.common.BaseTitleActivity;
import com.viomi.ovenso.helper.ModesHelper;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityModeListBinding;
import com.viomi.ovenso.ui.activity.custommode.CustomModeActivity;
import com.viomi.ovenso.ui.adapter.ModeListAdapter;
import com.viomi.ovenso.util.ItemDecorationUtil;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ljh on 2020/11/10.
 * Description:烹饪模式、辅助模式
 */
@Route(path = ViomiRouterConstant.OVENSO_MODE_LIST)
public class ModeListActivity extends BaseTitleActivity<ActivityModeListBinding> {
    private static final String TAG = "ModeListActivity";
    public static final String MODE_KEY = "modeTypeKey";
    public static final String MODE_COOK = "烹饪";
    public static final String MODE_ASSISTANT = "辅助";
    private static final int MODE_ID_CUSTOM = 201;
    ModeListAdapter mModeListAdapter;
    List<ModeTypeEntity> modeTypeList;
    private String modeName;

    @Override
    protected int getChildContentViewId() {
        return R.layout.activity_mode_list;
    }

    @Override
    protected void initIntentData() {
        modeName = getIntent().getStringExtra(MODE_KEY);
        Log.i(TAG, "initIntentData:modeName:  " + modeName);
        mModeListAdapter = new ModeListAdapter();
    }

    @Override
    protected String getTitleName() {
        return modeName;
    }

    @Override
    protected void initChildUi() {
        Log.i(TAG, "initUi: ");
        childViewBinding.modelistRecyclerverModes.addItemDecoration(ItemDecorationUtil.linearHorDecor(ApplicationUtils.getContext(),
                24));
        mModeListAdapter.setOnItemClickListener((parent, view, position, id) -> {//更新参数
            ModeTypeEntity modeTypeEntity = modeTypeList.get(position);
            if (modeTypeEntity.getModeId() == MODE_ID_CUSTOM) {
                // 跳转DIY模式，包含一个微波
                ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MODE_CUSTOM)
                        .withParcelable(CustomModeActivity.KEY_MODE_ENTITY, modeTypeEntity).navigation();
                return;
            }
            ArrayList<ModeTypeEntity> modeTypeEntityArrayList = new ArrayList<>();
            modeTypeEntityArrayList.add(modeTypeEntity);
            String itemName = modeTypeEntity.getName();
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MODE_DETAIL)
                    .withString(ModeDetailActivity.KEY_MODE_NAME, itemName)
//                    .withBoolean(ModeDetailActivity.KEY_CUSTOM_TYPE, false)
                    .withParcelableArrayList(ModeDetailActivity.KEY_MODETYPE_LIST, modeTypeEntityArrayList).navigation();
        });
        mModeListAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });
        childViewBinding.modelistRecyclerverModes.setAdapter(mModeListAdapter);
    }

    @Override
    protected void initData() {
        int modeIdArray = 0;
        if (TextUtils.equals(modeName, MODE_COOK)) {
            modeIdArray = R.array.modeid_cook;
        } else if (TextUtils.equals(modeName, MODE_ASSISTANT)) {
            modeIdArray = R.array.modeid_assistant;
        }
        modeTypeList = ModesHelper.getModeTypeList(modeIdArray);
        Log.d(TAG, "initData :" + this.modeTypeList.size());
        mModeListAdapter.setModeList(this.modeTypeList);
    }

    @Override
    protected void initListener() {
    }

}

