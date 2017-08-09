package com.example.downloader.download;

import android.content.Context;

import com.download.task.DownloadTaskInfo;
import com.download.task.DownloadTaskListener;

/**
 * Created by yangtianfei on 17-6-28.
 */
public class InstallDownloadListener implements DownloadTaskListener {

    private Context context;

    public InstallDownloadListener(Context context) {
        this.context = context;
    }

    @Override
    public void onReady(DownloadTaskInfo downloadInfo) {
//        NewGGToast.showBottomToast("正在等待下载...");
    }

    @Override
    public void onStart(DownloadTaskInfo downloadInfo) {
//        NewGGToast.showBottomToast("下载开始");
    }

    @Override
    public void onPause(DownloadTaskInfo downloadInfo) {
//        NewGGToast.showBottomToast("下载暂停");
    }

    @Override
    public void update(DownloadTaskInfo downloadInfo, long tatolSize, long downloadSize) {

    }

    @Override
    public void onCancel(DownloadTaskInfo downloadInfo) {
//        NewGGToast.showBottomToast("下载被取消");
    }

    @Override
    public void onComplete(DownloadTaskInfo downloadInfo, String path) {
//        NewGGToast.showBottomToast("下载完成");
    }

    @Override
    public void onFail(DownloadTaskInfo downloadInfo, int code, String msg) {
//        NewGGToast.showBottomToast("下载失败");
    }
}
