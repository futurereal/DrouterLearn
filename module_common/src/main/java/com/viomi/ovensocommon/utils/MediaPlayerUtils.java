package com.viomi.ovensocommon.utils;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovensocommon.CommonPreference;

/**
 * Created by Ljh on 2020/12/29.
 * Description:本地语音播报
 */
public class MediaPlayerUtils {
    private static final String TAG = "MediaPlayerUtils";

    private static MediaPlayerUtils mInstance;
    private static MediaPlayer player;

    public static MediaPlayerUtils getInstance() {
        if (mInstance == null) {
            synchronized (MediaPlayerUtils.class) {
                if (mInstance == null) {
                    mInstance = new MediaPlayerUtils();
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    public MediaPlayerUtils() {
        init();
    }

    private void init() {
        Log.d(TAG, "init");
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion");
            }
        });
    }

    public void startPlayRawResource(int rawResourceId) {
        boolean isVoiceReprot = CommonPreference.getInstance().getIsVoiceReprot();
        Log.i(TAG, "startPlay: isVoiceReprot : " + isVoiceReprot);
        if (!isVoiceReprot) {
            Log.i(TAG, "startPlay: isVoiceRport false return");
            return;
        }
        new Thread((new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "startPlay: begain report");
                if (player == null) {
                    init();
                }
                try {
                    player.reset();
                    AssetFileDescriptor file = ApplicationUtils.getContext().getResources().openRawResourceFd(rawResourceId);
                    player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                    player.setLooping(false);
                    player.prepareAsync();
                    player.setOnPreparedListener(preparedListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }

    private final MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (player != null) {
                player.start();
            }
        }
    };

    public void stopPlay() {
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
        player = null;
    }

}

