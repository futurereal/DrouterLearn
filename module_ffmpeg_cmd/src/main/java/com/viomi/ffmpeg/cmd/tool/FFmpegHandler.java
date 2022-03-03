package com.viomi.ffmpeg.cmd.tool;

import android.os.Handler;
import android.util.Log;

import com.viomi.ffmpeg.cmd.tool.ffmpeg.JsonParseTool;
import com.viomi.ffmpeg.cmd.tool.ffmpeg.MediaBean;
import com.viomi.ffmpeg.cmd.tool.ffmpeg.OnHandleListener;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Handler of FFmpeg and FFprobe
 * Created by frank on 2019/11/11.
 */
public class FFmpegHandler {

    private final static String TAG = FFmpegHandler.class.getSimpleName();

    public final static int MSG_BEGIN = 9012;

    public final static int MSG_PROGRESS = 1002;

    public final static int MSG_FINISH = 1112;

    public final static int MSG_CONTINUE = 2012;

    public final static int MSG_INFO = 2222;

    private final Handler mHandler;
    private final ExecuteListener executeListener;

    private boolean isContinue = false;
    private boolean isCancel = false;

    public FFmpegHandler(Handler mHandler) {
        this.executeListener = new ExecuteListener();
        this.mHandler = mHandler;
    }

    public void isContinue(boolean isContinue) {
        this.isContinue = isContinue;
    }

    /**
     * execute the command of FFmpeg
     *
     * @param commandLine commandLine
     * @param cmdType     类型
     */
    public void executeFFmpegCmd(final String[] commandLine, int cmdType) {
        Log.e(TAG, "executeFFmpegCmd: " + commandLine.length);
        isCancel = false;
        executeListener.setIsPropb(cmdType);
        executeListener.setIsPropb(false);
        mHandler.removeMessages(MSG_PROGRESS);
        mHandler.removeCallbacksAndMessages(null);
        FFmpegCmdNative.execute(commandLine, executeListener);
    }

    /**
     * execute multi commands of FFmpeg
     *
     * @param commandList the list of command
     */
    public void executeFFmpegCmdList(final List<String[]> commandList, int cmdType) {
        if (commandList == null) {
            return;
        }
        isCancel = false;
        Log.i(TAG, "executeFFmpegCmdList: ");
        executeListener.setIsPropb(cmdType);
        executeListener.setIsPropb(false);
        FFmpegCmdNative.execute(commandList, executeListener);
    }

    /**
     * execute the command of FFprobe
     *
     * @param commandLine commandLine
     */
    public void executeFFprobeCmd(final String[] commandLine) {
        if (commandLine == null) {
            return;
        }
        isCancel = false;
        executeListener.setIsPropb(true);
        FFmpegCmdNative.executeProbe(commandLine, executeListener);
    }

    /**
     * cancel the running task, and exit quietly
     *
     * @param cancel cancel the task when flag is true
     */
    public void cancelExecute(boolean cancel) {
        Log.i(TAG, "cancelExecute: ");
        FFmpegCmdNative.cancelTask(cancel);
        isCancel = cancel;
    }


    class ExecuteListener implements OnHandleListener {
        int cmdType;
        boolean isProp = false;

        public void setIsPropb(int cmdType) {
            this.cmdType = cmdType;
        }

        public void setIsPropb(boolean isProp) {
            this.isProp = isProp;
        }

        @Override
        public void onBegin() {
            Log.i(TAG, "handle onBegin...");
            mHandler.obtainMessage(MSG_BEGIN).sendToTarget();
        }

        @Override
        public void onMsg(@NotNull String msg) {
            Log.i(TAG, "onMsg: " + msg);
            mHandler.obtainMessage(MSG_INFO).sendToTarget();
        }

        @Override
        public void onProgress(int progress, int duration) {
            Log.i(TAG, "onProgress: progresss: " + progress + " duration: " + duration);
            mHandler.obtainMessage(MSG_PROGRESS, progress, duration).sendToTarget();
            ViomiRxBus.getInstance().post(CommonConstant.MSG_CMD_PROGRESS, progress);
        }

        @Override
        public void onEnd(int resultCode, String resultMsg) {
            Log.i(TAG, "onEnd: resultCode: " + resultCode + " resultMSg: "+resultMsg +" isCancle: "+isCancel);
            if (isCancel) {
                return;
            }
            ViomiRxBus.getInstance().post(CommonConstant.MSG_CMD_FINISH, cmdType);
            Log.i(TAG, "MSG_CMD_FINISH... " + cmdType);
            if (isContinue) {
                mHandler.obtainMessage(MSG_CONTINUE).sendToTarget();
            } else {
                mHandler.obtainMessage(MSG_FINISH).sendToTarget();
            }
            if (isProp) {
                MediaBean mediaBean = null;
                if (resultMsg != null && !resultMsg.isEmpty()) {
                    mediaBean = JsonParseTool.INSTANCE.parseMediaFormat(resultMsg);
                }
                mHandler.obtainMessage(MSG_FINISH, mediaBean).sendToTarget();
            }
        }
    }

}
