package com.viomi.ovensocommon.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

/**
 * @author zhangdz
 * @date 2021/9/22
 */
@Entity(tableName = "tb_video")
public class VideoInfo implements Parcelable {
    @PrimaryKey
    @Expose
    @NotNull
    @SerializedName("video_index")
    @ColumnInfo(name = "video_index")
    private String videoIndex;
    // 菜谱名称
    @Expose
    @SerializedName("video_name")
    @ColumnInfo(name = "video_name")
    private String recipeName;
    // 类型
    @Expose
    @SerializedName("mode_id")
    @ColumnInfo(name = "mode_id")
    private String modeId;
    // 视频拍摄时间
    private int recordTime;
    // 视频文件
    @Expose
    @SerializedName("video_path")
    @ColumnInfo(name = "video_path")
    private String videoFilePath;
    // 视频封面文件
    @Expose
    @SerializedName("cover_path")
    @ColumnInfo(name = "cover_path")
    private String coverFilePath;

    public VideoInfo() {
    }

    protected VideoInfo(Parcel in) {
        videoIndex = in.readString();
        recipeName = in.readString();
        modeId = in.readString();
        recordTime = in.readInt();
        videoFilePath = in.readString();
        coverFilePath = in.readString();
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    public String getVideoIndex() {
        return videoIndex;
    }

    public void setVideoIndex(String videoIndex) {
        this.videoIndex = videoIndex;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getModeId() {
        return modeId;
    }

    public void setModeId(String modeId) {
        this.modeId = modeId;
    }

    public int getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(int recordTime) {
        this.recordTime = recordTime;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(String videoFilePath) {
        this.videoFilePath = videoFilePath;
    }

    public String getCoverFilePath() {
        return coverFilePath;
    }

    public void setCoverFilePath(String coverFilePath) {
        this.coverFilePath = coverFilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "videoIndex='" + videoIndex + '\'' +
                ", recipeName='" + recipeName + '\'' +
                ", modeId='" + modeId + '\'' +
                ", recordTime=" + recordTime +
                ", videoFilePath='" + videoFilePath + '\'' +
                ", coverFilePath='" + coverFilePath + '\'' +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoIndex);
        dest.writeString(recipeName);
        dest.writeString(modeId);
        dest.writeInt(recordTime);
        dest.writeString(videoFilePath);
        dest.writeString(coverFilePath);
    }
}
