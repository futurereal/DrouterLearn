package com.viomi.modulesetting.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 登录引导页面适配器
 */
public class LoginGuidePageFragmentAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentArrayList;

    public LoginGuidePageFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        fragmentArrayList = fragmentList;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return fragmentArrayList == null ? 0 : fragmentArrayList.indexOf(object);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList == null ? 0 : fragmentArrayList.size();
    }

}
