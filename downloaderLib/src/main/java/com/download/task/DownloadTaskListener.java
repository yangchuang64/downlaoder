package com.download.task;

/**
 * Created by yangtianfei on 17-5-14.
 */

public interface DownloadTaskListener {
    void onReady(DownloadTaskInfo downloadInfo);

    void onStart(DownloadTaskInfo downloadInfo);

    void onPause(DownloadTaskInfo downloadInfo);

    void update(DownloadTaskInfo downloadInfo, long tatolSize, long downloadSize);

    void onCancel(DownloadTaskInfo downloadInfo);

    void onComplete(DownloadTaskInfo downloadInfo, String path);

    void onFail(DownloadTaskInfo downloadInfo, int code, String msg);
}
