package com.viomi.waterpurifier.edison.factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.viomi.waterpurifier.edison.viewmodule.WaterMainViewModel;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: WaterPurifier
 * @Package: com.viomi.waterpurifier.factory
 * @ClassName: ProjectViewModuleFactory
 * @Description: 项目ViewModule工厂
 * @Author: randysu
 * @CreateDate: 2020/4/27 2:19 PM
 * @UpdateUser:
 * @UpdateDate: 2020/4/27 2:19 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class ProjectViewModuleFactory extends ViewModelProvider.NewInstanceFactory {
    private static volatile ProjectViewModuleFactory instance;
    private final Application application;

    private ProjectViewModuleFactory(Application application) {
        this.application = application;
    }


    public static ProjectViewModuleFactory getInstance(Application application) {
        if (instance == null) {
            synchronized (ProjectViewModuleFactory.class) {
                if (instance == null) {
                    instance = new ProjectViewModuleFactory(application);
                }
            }
        }

        return instance;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WaterMainViewModel.class)) {
            return (T) new WaterMainViewModel(application);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }

}
