package com.viomi.waterpurifier.edison.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.databinding.FragmentFilterSelectBinding;
import com.viomi.waterpurifier.edison.presenter.FilterResetPresenter;
import com.viomi.waterpurifier.edison.presenter.FlushContact;

import java.util.ArrayList;


public class FilterSelectFragment extends BaseDialogFragment<FragmentFilterSelectBinding> implements FlushContact.View {
    public static final String KEY_CLEAR_TYPE = "keyCleanType";
    FlushContact.Presenter presenter;
    ArrayList<Integer> filterIndexList = new ArrayList<>();
    private static final int TYPE_CLEAR_ONLY = 0;
    public static final int TYPE_CLEAR_RESET = 1;
    private static final int INDEX_RO = 0;
    private static final int INDEX_FOUR_ONE = 1;
    int type;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_filter_select;
    }

    @Override
    protected void initView() {
        presenter = new FilterResetPresenter(this);
        presenter.subscribe(this);
        if (getArguments() != null) {
            type = getArguments().getInt(KEY_CLEAR_TYPE, TYPE_CLEAR_ONLY);
        }
        if (type == TYPE_CLEAR_RESET) {
            viewDataBinding.filterselectTitle.setText(getActivity().getString(R.string.setting_filter_title2));
            viewDataBinding.filterselectClean.setText(getActivity().getString(R.string.setting_filter_action2));
        }
    }

    @Override
    protected void initListener() {
        viewDataBinding.filterselectCancel.setOnClickListener(v -> dismissAllowingStateLoss());
        viewDataBinding.filterselectFouroneSelector.setOnClickListener(v -> {
            boolean targetSelect = !viewDataBinding.filterselectFouroneSelector.isSelected();
            viewDataBinding.filterselectFouroneSelector.setSelected(targetSelect);
            if (targetSelect) {
                filterIndexList.add(INDEX_FOUR_ONE);
            } else {
                filterIndexList.remove(INDEX_FOUR_ONE);
            }
            refreshClearButton();
        });
        viewDataBinding.filterselectRoSelect.setOnClickListener(v -> {
            boolean targetSelect = !viewDataBinding.filterselectRoSelect.isSelected();
            viewDataBinding.filterselectRoSelect.setSelected(targetSelect);
            if (targetSelect) {
                filterIndexList.add(INDEX_RO);
            } else {
                filterIndexList.remove(INDEX_RO);
            }
            refreshClearButton();
        });
        viewDataBinding.filterselectClean.setOnClickListener(v -> {
            dismissAllowingStateLoss();
            presenter.checkAndFlush(getActivity(), type, filterIndexList);
        });
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        presenter.unSubscribe();
    }

    void refreshClearButton() {
        boolean clickable = filterIndexList.size() > 0;
        viewDataBinding.filterselectClean.setAlpha(clickable ? 1f : 0.6f);
        viewDataBinding.filterselectClean.setClickable(clickable);
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        wlp.height = 549;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }


    @Override
    public void process(int process) {

    }
}
