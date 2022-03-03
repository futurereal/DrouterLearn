package com.viomi.ovensocommon.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.viomi.common.ApplicationUtils;
import com.viomi.common.R;

/**
 * Created with Android Studio
 * Author:Ljh
 * Date:2020/1/21
 **/
public class ViomiToastUtil {
    public static final int TEXT_SIZE = 40;
    public static final int PADDING_LEFT = 60;
    public static final int PADDING_TOP = 32;
    public static final int PADDING_RIGHT = 60;
    public static final int PADDING_BOTTOM = 32;
    public static final int RADIUS = 16;
    static Toast normalToast;
    private static TextView nomalToastView;

    static View centerToastView;
    static Toast centerToast;
    private static final String TAG = "ViomiToastUtil";
    private static final int TOAST_TIME = 2 * 1000;

    private static final int RESOURCEID_SUCCESS = R.drawable.toast_success;
    private static final int RESOURCEID_FAIL = R.drawable.toast_fail;
    private static TextView contentView;
    private static ImageView contentIcon;
    private static Handler mainHandler;


    public static synchronized void showToastNormal(String msg, int duration) {
        Log.i(TAG, "showToastNormal: ");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (normalToast == null) {
                    Context context = ApplicationUtils.getContext().getApplicationContext();
                    nomalToastView = new TextView(context);
                    nomalToastView.setGravity(Gravity.CENTER);
                    nomalToastView.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
                    nomalToastView.setTextColor(Color.WHITE);
                    nomalToastView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setCornerRadius(RADIUS);
                    gradientDrawable.setColor(context.getColor(R.color.toast_colorbg));
                    nomalToastView.setBackgroundDrawable(gradientDrawable);
                    normalToast = new Toast(ApplicationUtils.getContext().getApplicationContext());
                    normalToast.setDuration(duration);
                    normalToast.setGravity(Gravity.CENTER, 0, 0);
                    normalToast.setView(nomalToastView);
                }
                nomalToastView.setText(msg);
                normalToast.show();
            }
        });

    }

    /**
     * Toast 居中显示
     */
    @SuppressLint("ShowToast")
    public static void showToastCenter(String content) {
        Log.i(TAG, "showToastCenter: " + centerToast);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (centerToast == null) {
                    centerToast = createCenterToast(ApplicationUtils.getContext());
                }
                if (contentIcon.getVisibility() == View.VISIBLE) {
                    contentIcon.setVisibility(View.GONE);
                }
                contentView.setText(content);
                centerToast.show();
            }
        });
    }

    /**
     * Toast 成功提示
     */
    @SuppressLint("ShowToast")
    public static void showToastSuccess(Context context, String content) {
        Log.i(TAG, "showToastSuccess: ");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (centerToast == null) {
                    centerToast = createCenterToast(context);
                }
                contentIcon.setImageResource(RESOURCEID_SUCCESS);
                contentView.setText(content);
                centerToast.show();
            }
        });
    }

    /**
     * Toast 失败提示
     */
    @SuppressLint("ShowToast")
    public static void showToastFail(Context context, String content) {
        Log.i(TAG, "showToastFail: ");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (centerToast == null) {
                    centerToast = createCenterToast(context);
                }
                contentIcon.setImageResource(RESOURCEID_FAIL);
                contentView.setText(content);
                centerToast.show();
            }
        });
    }
    public static void showWindowToast(String totalContent) {
        Log.i(TAG, "showWindowToast : " + totalContent);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        WindowManager windowManager = (WindowManager) ApplicationUtils.getContext().getSystemService(Context.WINDOW_SERVICE);
        createCenterToast(ApplicationUtils.getContext());
        centerToastView = LayoutInflater.from(ApplicationUtils.getContext()).inflate(R.layout.common_toast, null);
        windowManager.addView(centerToastView, layoutParams);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                windowManager.removeView(centerToastView);
            }
        };
        new Handler(Looper.getMainLooper()).postDelayed(runnable, TOAST_TIME);
    }

    public static Toast createCenterToast(Context context) {
        Log.i(TAG, "createCenterToast: ");
        mainHandler = new Handler(Looper.getMainLooper());
        Toast centerToast = new Toast(context);
        centerToastView = LayoutInflater.from(context).inflate(R.layout.common_toast, null);
        contentView = centerToastView.findViewById(R.id.commontoast_content);
        contentIcon = centerToastView.findViewById(R.id.commontoast_icon);
        centerToast.setView(centerToastView);
        centerToast.setGravity(Gravity.CENTER, 0, 0);
        return centerToast;
    }


    public static void dismiss() {
        if (normalToast != null) {
            normalToast.cancel();
        }
    }

}
