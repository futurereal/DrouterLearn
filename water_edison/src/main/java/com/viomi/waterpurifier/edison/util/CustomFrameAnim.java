package com.viomi.waterpurifier.edison.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.viomi.common.ApplicationUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * @author by xinqi on 2021-02-20.
 * @describe 帧动画
 */
public class CustomFrameAnim {
    private final String TAG = "CustomFrameAnim";
    // 动画间隔时间 200ms
    private final int FRAME_DURATION = 600;
    private OnFinishListener onFinishListener;
    private OnPlayListener onPlayListener;
    private volatile int currentIndex;
    private volatile int pictureCount;
    private final ImageView imageView;
    private Context context;
    private volatile String imgName;
    private boolean oneshot = true;

    private Disposable mDisposable;
    private BitmapFactory.Options options;
    private Resources resource;

    public CustomFrameAnim(ImageView imageView) {
        this.imageView = imageView;
    }

    public void resetResource(String imgName, int length, boolean oneshot) {
        Log.i(TAG, "resetResource: imgName: " + imgName + "  length: " + length);
        this.imgName = imgName;
        this.pictureCount = length;
        this.oneshot = oneshot;
        context = ApplicationUtils.getContext();
        resource = context.getResources();
    }
    public void startAnim() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        currentIndex = 0;
        mDisposable = Observable.interval(0, FRAME_DURATION, TimeUnit.MILLISECONDS)
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(aLong -> {
                    if (currentIndex < pictureCount) {
                        updateImageView();
                    } else if (currentIndex == pictureCount && !oneshot) {
                        startAnim();
                    } else {
                        stopAnim();
                    }
                }, Throwable::printStackTrace);
    }


    public void stopAnim() {
        Log.i(TAG, "stopAnim");
        if (onFinishListener != null) {
            onFinishListener.onFinished();
        }
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    private void updateImageView() {
        String currentResourceName = imgName + currentIndex;
//        Log.i(TAG, "loadImage: currentResourceName: " + currentResourceName);
        Bitmap currentBitmap = WaterBitmapUtils.getBitmap(currentResourceName);
//        Bitmap currentBitmap = WaterBitmapUtils.getBitmapInUse(currentResourceName);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(resource, currentBitmap);
        imageView.setImageDrawable(bitmapDrawable);
        if (onPlayListener != null) {
            onPlayListener.onPlay(currentIndex);
        }
        currentIndex++;
    }

    public interface OnFinishListener {
        void onFinished();
    }

    public interface OnPlayListener {
        void onPlay(int index);
    }
}
