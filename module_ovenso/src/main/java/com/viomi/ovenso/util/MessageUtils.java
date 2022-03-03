package com.viomi.ovenso.util;

import android.util.Log;

import com.blankj.utilcode.util.TimeUtils;
import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovensocommon.db.MessageEntity;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.db.ViomiRoomDatabase;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.utils.VideoFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 烤箱故障工具类
 */
public class MessageUtils {
    private static final String TAG = "MessageUtils";

    public static void addMessage(MessageEntity messageEntity) {
        List<MessageEntity> itemList = getMessageList();
        if (itemList.size() >= 20) {
            MessageEntity messageEntityDelete = itemList.get(itemList.size() - 1);
            itemList.remove(messageEntityDelete);
            ViomiRoomDatabase.getDatabase().messageInfoDao().deleteMessage(messageEntityDelete);
        }
        itemList.add(0, messageEntity);
        ViomiRoomDatabase.getDatabase().messageInfoDao().insert(messageEntity);

    }

    public static long addMessage(VideoInfo videoInfo) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessageTime(TimeUtils.getNowMills());
        messageEntity.setMessageContent(ApplicationUtils.getContext().getString(R.string.video_upload_fail_content));
        messageEntity.setIconResId(R.drawable.messagelist_normal);
        messageEntity.setVideoInfo(videoInfo);
        messageEntity.setMessageTitle(ApplicationUtils.getContext().getString(R.string.video_upload_fail));
        messageEntity.setMessageType(MessageEntity.TYPE_VIDEO);
        long insertResult = ViomiRoomDatabase.getDatabase().messageInfoDao().insert(messageEntity);
        Log.i(TAG, "addMessage: insertResult: " + insertResult);
        return insertResult;
    }

    public static List<MessageEntity> getMessageList() {
        List<MessageEntity> messageEntityArrayList = ViomiRoomDatabase.getDatabase().messageInfoDao().getMessageList();
        Log.i(TAG, "getMessageList: " + messageEntityArrayList.size());
        return messageEntityArrayList;
    }

    public static void addTestData() {
        ArrayList<MessageEntity> messageEntities = new ArrayList<>();
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setVideoIndex("111111anme1");
        videoInfo.setModeId("34");
        videoInfo.setRecipeName("微波");
        String dirPath = VideoFileUtils.INSTANCE.getVideoDirResult();
        if (!new File(dirPath).exists()) {
            new File(dirPath).mkdirs();
        }
        videoInfo.setVideoFilePath(dirPath + "/" + "微波.mp4");
        videoInfo.setCoverFilePath(dirPath + "/" + "微波.jpg");
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessageTime(TimeUtils.getNowMills());
        messageEntity.setMessageContent(ApplicationUtils.getContext().getString(R.string.video_upload_fail_content));
        messageEntity.setIconResId(R.drawable.messagelist_normal);
        messageEntity.setVideoInfo(videoInfo);
        messageEntity.setMessageTitle(ApplicationUtils.getContext().getString(R.string.video_upload_fail));
        messageEntity.setMessageType(MessageEntity.TYPE_VIDEO);
        messageEntities.add(messageEntity);
        long insertResult = ViomiRoomDatabase.getDatabase().messageInfoDao().insert(messageEntity);
        Log.i(TAG, "getMessageList:insertResult:  " + insertResult);
        messageEntity = new MessageEntity();
        messageEntity.setMessageTime(TimeUtils.getNowMills());
        messageEntity.setMessageTitle(ApplicationUtils.getContext().getString(R.string.ovenso_devicefalut_pan_low));
        messageEntity.setMessageContent(ApplicationUtils.getContext().getString(R.string.ovenso_devicefalut_pan_content));
        messageEntity.setMessageType(MessageEntity.TYPE_ERROR);
        messageEntity.setIconResId(R.drawable.messagelist_error);
        messageEntities.add(messageEntity);
        insertResult = ViomiRoomDatabase.getDatabase().messageInfoDao().insert(messageEntity);
        Log.i(TAG, "getMessageList:insertResult:  " + insertResult);
    }

    public static int deleteMessage(MessageEntity messageEntity) {
        int deleteResult = ViomiRoomDatabase.getDatabase().messageInfoDao().deleteMessage(messageEntity);
        return deleteResult;
    }


    public static long deleteMessage(VideoInfo videoInfo) {
        Log.i(TAG, "deleteMessage: videoInfo: " + videoInfo);
        if (videoInfo == null) {
            Log.i(TAG, "deleteMessage: videoInfo is null return");
            return 0;
        }
        MessageEntity messageEntity = ViomiRoomDatabase.getDatabase().messageInfoDao().getMessageByVideoIndex(videoInfo.getVideoIndex());
        Log.i(TAG, "deleteMessage: messageEntity: " + messageEntity);
        if (messageEntity == null) {
            Log.i(TAG, "deleteMessage:  messageEntity is null return");
            return 0;
        }
        int deleteResult = ViomiRoomDatabase.getDatabase().messageInfoDao().deleteMessage(messageEntity);
        Log.i(TAG, "deleteMessage: deleteResult: " + deleteResult);
        ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_DELETE_IM, messageEntity);
        return deleteResult;
    }
}
