package com.viomi.ovensocommon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.viomi.common.R;


/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.view
 * @ClassName: MenuSelectBGLayout
 * @Description:
 * @Author: randysu
 * @CreateDate: 2020/2/26 10:47 AM
 * @UpdateUser:
 * @UpdateDate: 2020/2/26 10:47 AM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class MenuSelectBGLayout extends RelativeLayout {

    /**
     * 选中的背景色
     */
    private int selectedBGColor;
    /**
     * 不选中的背景色
     */
    private int unselectedBGColor;

    public MenuSelectBGLayout(Context context) {
        this(context, null);
    }

    public MenuSelectBGLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuSelectBGLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        selectedBGColor = R.color.menufragment_select;
        unselectedBGColor = R.color.menufragment_unselect;
        setBackgroundColor(unselectedBGColor);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setBackgroundColor(selected ? selectedBGColor : unselectedBGColor);
    }

}
