package com.download;

import android.content.Context;

import com.download.task.DownloadTaskListener;

/**
 * Created by yangtianfei on 17-6-30.
 * download configuration
 */
public class DownloaderConfiguration {
    public final Context mContext;
    /**
     * 下载文件保存目录
     */
    final String mDownloadDir;
    /**
     * 是否只在wifi下下载
     */
    boolean mIsDownloadOnlyOnWifi;
    /**
     * 是否在通知栏现实进度
     */
    final boolean mIsNotifition;
    /**
     * 默认下载监听
     */
    final DownloadTaskListener mDefaultTaskListener;

    private DownloaderConfiguration(Builder builder) {
        mContext = builder.context;
        mDownloadDir = builder.downloadDir;
        mIsDownloadOnlyOnWifi = builder.isDownloadOnlyOnWifi;
        mIsNotifition = builder.isNotifition;
        mDefaultTaskListener = builder.defaultTaskListener;
    }

    public static class Builder {
        private Context context;
        private String downloadDir;
        private boolean isDownloadOnlyOnWifi = true;
        private boolean isNotifition;
        private DownloadTaskListener defaultTaskListener;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder downloadDir(String dir) {
            this.downloadDir = dir;
            return this;
        }

        public Builder isDownloadOnlyOnWifi(boolean isOnlyOnWifi) {
            isDownloadOnlyOnWifi = isOnlyOnWifi;
            return this;
        }

        public Builder isNotifition(boolean isNotifition) {
            this.isNotifition = isNotifition;
            return this;
        }

        public Builder defaultTaskListener(DownloadTaskListener taskListener) {
            this.defaultTaskListener = taskListener;
            return this;
        }

        public DownloaderConfiguration build() {
            return new DownloaderConfiguration(this);
        }
    }
}
