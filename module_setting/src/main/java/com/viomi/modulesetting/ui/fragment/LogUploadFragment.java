package com.viomi.modulesetting.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.viomi.modulesetting.R;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.router.annotation.Route;
import com.viomi.vlog.LogUpLoadFragment;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 */
@Route(path = ViomiRouterConstant.SETTING_FRAGMENT_LOG)
public class LogUploadFragment extends LogUpLoadFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_upload, container, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
