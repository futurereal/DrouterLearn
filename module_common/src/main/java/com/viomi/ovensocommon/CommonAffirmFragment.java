package com.viomi.ovensocommon;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.viomi.common.R;
import com.viomi.common.databinding.DialogCommonAffirmBinding;

public class CommonAffirmFragment extends BaseDialogFragment<DialogCommonAffirmBinding> {
    private static final String TAG = "CommonAffirmFragment";
    boolean mHasHeaderImage = false;

    OnNegativeClickListener negativeClickListener;
    OnPositiveClickListener positiveClickListener;
    DialogInterface.OnDismissListener dismissListener;

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_common_affirm;
    }

    public static Bundle getBundle(String title, String content, String cancelText, String sureText, boolean showHeadImg, int headRes) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("cancelText", cancelText);
        bundle.putString("sureText", sureText);
        bundle.putBoolean("showHeadImg", showHeadImg);
        bundle.putInt("headRes", headRes);
        return bundle;
    }

    @Override
    protected void initView() {
        Bundle bundle = getArguments();
        String title = "", content = "", cancel = "", sure = "";
        if (bundle != null) {
            title = bundle.getString("title");
            content = bundle.getString("content");
            cancel = bundle.getString("cancelText");
            sure = bundle.getString("sureText");
        }
        mHasHeaderImage = bundle.getBoolean("showHeadImg");
        if (!TextUtils.isEmpty(title)) {
            viewDataBinding.confirmTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            viewDataBinding.tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(cancel)) {
            viewDataBinding.tvCancel.setText(cancel);
        } else {
            viewDataBinding.tvCancel.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(sure)) {
            viewDataBinding.tvSure.setText(sure);
        } else {
            viewDataBinding.tvSure.setVisibility(View.GONE);
        }
        if (mHasHeaderImage) {
            viewDataBinding.imgHeader.setVisibility(View.VISIBLE);
            viewDataBinding.imgHeader.setImageResource(bundle.getInt("headRes"));

            ConstraintLayout.LayoutParams titleParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            titleParams.startToStart = R.id.dialog;
            titleParams.endToEnd = R.id.dialog;
            titleParams.topToTop = R.id.dialog;
            titleParams.topMargin = (173);
            viewDataBinding.confirmTitle.setLayoutParams(titleParams);

            ConstraintLayout.LayoutParams contentParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            contentParams.startToStart = R.id.dialog;
            contentParams.endToEnd = R.id.dialog;
            contentParams.topToTop = R.id.dialog;
            contentParams.topMargin = (254);
            viewDataBinding.tvContent.setLayoutParams(contentParams);
        }
        Log.d(TAG, "onViewCreated");
    }

    @Override
    protected void initListener() {
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        viewDataBinding.tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = getDialog();
                if (dialog != null) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onPositiveClick(dialog);
                    } else {
                        dialog.dismiss();
                    }
                }
            }
        });
        viewDataBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = getDialog();
                if (dialog != null) {
                    if (negativeClickListener != null) {
                        negativeClickListener.onNegativeClick(dialog);
                    } else {
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                wlp.width = 722;
                wlp.height = 380;
                window.setAttributes(wlp);
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
        Log.d(TAG, "onStart");
    }

    public void setNegativeClickListener(OnNegativeClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
    }

    public void setPositiveClickListener(OnPositiveClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
    }

    @Override
    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        dismissListener = listener;
    }

    public void setPostiveTip(String postiveTip) {
        viewDataBinding.tvSure.setText(postiveTip);
    }

    public void setContentTip(String contentTip) {
        viewDataBinding.tvContent.setText(contentTip);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public interface OnPositiveClickListener {
        void onPositiveClick(Dialog dialog);
    }

    public interface OnNegativeClickListener {
        void onNegativeClick(Dialog dialog);
    }

}
