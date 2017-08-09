package com.download.task;

/**
 * Created by yangtianfei on 17-6-29.
 */
public class SimpleDownloadTaskListener implements DownloadTaskListener {
    @Override
    public void onReady(DownloadTaskInfo downloadInfo) {

    }

    @Override
    public void onStart(DownloadTaskInfo downloadInfo) {

    }

    @Override
    public void onPause(DownloadTaskInfo downloadInfo) {

    }

    @Override
    public void update(DownloadTaskInfo downloadInfo, long tatolSize, long downloadSize) {

    }

    @Override
    public void onCancel(DownloadTaskInfo downloadInfo) {

    }

    @Override
    public void onComplete(DownloadTaskInfo downloadInfo, String path) {

    }

    @Override
    public void onFail(DownloadTaskInfo downloadInfo, int code, String msg) {

    }
}
