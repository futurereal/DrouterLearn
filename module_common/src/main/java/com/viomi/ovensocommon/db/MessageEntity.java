package com.viomi.ovensocommon.db;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tb_message")
public class MessageEntity implements Parcelable {
    private static final String TAG = "MessageEntity";
    public static final int TYPE_ERROR = 1;
    public static final int TYPE_VIDEO = 2;

    @PrimaryKey
    @SerializedName("message_time")
    @ColumnInfo(name = "message_time")
    private long messageTime;

    @Expose
    @SerializedName("message_type")
    @ColumnInfo(name = "message_type")
    private int messageType;

    @Expose
    @SerializedName("message_reId")
    @ColumnInfo(name = "message_reId")
    private int iconResId;

    @Expose
    @SerializedName("message_title")
    @ColumnInfo(name = "message_title")
    private String messageTitle;

    @Expose
    @SerializedName("message_content")
    @ColumnInfo(name = "message_content")
    private String messageContent;

    private boolean isSelect = false;

    @Embedded
    public VideoInfo videoInfo;

    public MessageEntity() {
    }

    protected MessageEntity(Parcel in) {
        messageTime = in.readInt();
        messageType = in.readInt();
        iconResId = in.readInt();
        messageTitle = in.readString();
        messageContent = in.readString();
        videoInfo = in.readParcelable(getClass().getClassLoader());
    }

    public static final Creator<MessageEntity> CREATOR = new Creator<MessageEntity>() {
        @Override
        public MessageEntity createFromParcel(Parcel in) {
            return new MessageEntity(in);
        }

        @Override
        public MessageEntity[] newArray(int size) {
            return new MessageEntity[size];
        }
    };

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(messageTime);
        dest.writeInt(messageType);
        dest.writeInt(iconResId);
        dest.writeString(messageTitle);
        dest.writeString(messageContent);
        dest.writeParcelable(videoInfo, 1);
    }
}
