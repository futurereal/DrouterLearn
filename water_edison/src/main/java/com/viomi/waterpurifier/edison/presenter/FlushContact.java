package com.viomi.waterpurifier.edison.presenter;

import androidx.fragment.app.FragmentActivity;

import com.viomi.waterpurifier.edison.entity.FilterEntity;

import java.util.ArrayList;

public class FlushContact {

    public interface View {
        void process(int process);
    }

    public interface Presenter extends BasePresenter<View> {
        void flush();

        void stopsFlush();

        void checkAndFlush(FragmentActivity activity, int type, ArrayList<Integer> filters);

        void startResetFilter(FilterEntity filterEntity);
    }
}
