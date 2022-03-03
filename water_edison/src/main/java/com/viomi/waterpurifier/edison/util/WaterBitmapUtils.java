package com.viomi.waterpurifier.edison.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.viomi.common.ApplicationUtils;
import com.viomi.waterpurifier.edison.R;


/**
 * @description: 图片缓存的逻辑
 * @data:2021/12/17
 */
public class WaterBitmapUtils {
    private static final String TAG = "WaterBitmapUtils";
    private static BitmapLruCache bitmapLrucache;
    private static BitmapFactory.Options options;

    public static void putBitmap(String fileName, Bitmap bitmap) {
        if (bitmapLrucache == null) {
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            Log.i(TAG, "putBitmap: maxMemor: " + maxMemory);
            bitmapLrucache = new BitmapLruCache(maxMemory);
        }
        bitmapLrucache.put(fileName, bitmap);
    }

    /**
     * 使用缓存 降低CPU
     *
     * @param fileName
     * @return
     */
    public static Bitmap getBitmap(String fileName) {
//        Log.i(TAG, "getBitmap: bitmap: " + fileName);
        if (bitmapLrucache != null && bitmapLrucache.get(fileName) != null) {
//            Log.i(TAG, "getBitmap: getCache");
            return bitmapLrucache.get(fileName);
        }
        Context context = ApplicationUtils.getContext();
        int resId = context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap fileBitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        putBitmap(fileName, fileBitmap);
        return fileBitmap;
    }

    /**
     * 内存复用 降低内存
     *
     * @param currentResourceName
     * @return
     */
    public static Bitmap getBitmapInUse(String currentResourceName) {
        Context context = ApplicationUtils.getContext();
        int resId = context.getResources().getIdentifier(currentResourceName, "drawable", context.getPackageName());
        Resources resource = context.getResources();
        // bitmap 内存 复用， drawable 减少内存的使用
        if (options == null) {
            options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap commonBitmap = BitmapFactory.decodeResource(resource, R.drawable.theme_default_big_cup_0, options);
            options.inBitmap = commonBitmap;
        }
        // 需要执行缓存的处理，避免图片加载过多
        Bitmap currentBitmap = BitmapFactory.decodeResource(resource, resId, options);
        return currentBitmap;
    }

    /**
     * 清空缓存的逻辑处理
     */
    public static void cleanAllCachedBitmap() {
        Log.i(TAG, "cleanAllCachedBitmap: ");
        if (bitmapLrucache == null) {
            return;
        }
        bitmapLrucache.remove("");
    }
}


class BitmapLruCache extends LruCache<String, Bitmap> {
    private static final String TAG = "BitmapLruCache";

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        int bitmapSize = bitmap.getWidth() * bitmap.getHeight() * 4;
//        Log.i(TAG, "sizeOf: BitmapSize: " + bitmapSize);
        return bitmapSize;
    }

    @Override
    public void trimToSize(int maxSize) {
//        Log.i(TAG, "trimToSize: size: " + maxSize);
        super.trimToSize(maxSize);
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        Log.i(TAG, "entryRemoved: ");
    }
}

