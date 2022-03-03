package com.viomi.modulesetting.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;


/**
 * App 检查更新
 * Created by William on 2018/2/22.
 */
public class AppUpdateResult implements Parcelable {
    @JSONField(name = "total")
    private int total;// 总数

    @JSONField(name = "data")
    private List<Data> data;

    public AppUpdateResult() {

    }

    protected AppUpdateResult(Parcel in) {
        total = in.readInt();
    }


    public void setTotal(int total) {
        this.total = total;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public List<Data> getData() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(total);
    }

    public final Creator<AppUpdateResult> CREATOR = new Creator<AppUpdateResult>() {
        @Override
        public AppUpdateResult createFromParcel(Parcel in) {
            return new AppUpdateResult(in);
        }

        @Override
        public AppUpdateResult[] newArray(int size) {
            return new AppUpdateResult[size];
        }
    };

    public class Data implements Parcelable {
        @JSONField(name = "id")
        private int id;

        @JSONField(name = "packageName")
        private String packageName;// 包名

        @JSONField(name = "code")
        private int code;

        @JSONField(name = "channel")
        private String channel;// 渠道

        @JSONField(name = "detail")
        private String detail;// 更新说明

        @JSONField(name = "url")
        private String url;// Apk 下载链接

        @JSONField(name = "createtime")
        private String createtime;// 创建时间

        public Data() {

        }

        protected Data(Parcel in) {
            id = in.readInt();
            packageName = in.readString();
            code = in.readInt();
            channel = in.readString();
            detail = in.readString();
            url = in.readString();
            createtime = in.readString();
        }

        public final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        public void setId(int id) {
            this.id = id;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public int getId() {
            return id;
        }

        public String getPackageName() {
            return packageName;
        }

        public int getCode() {
            return code;
        }

        public String getChannel() {
            return channel;
        }

        public String getDetail() {
            return detail;
        }

        public String getUrl() {
            return url;
        }

        public String getCreatetime() {
            return createtime;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(packageName);
            dest.writeInt(code);
            dest.writeString(channel);
            dest.writeString(detail);
            dest.writeString(url);
            dest.writeString(createtime);
        }
    }
}