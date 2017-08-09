/*
 * Copyright (C) loongmobile.com, Inc. All Rights Reserved.
 * website: www.loongmobile.com
 */

package com.download.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.download.constants.DownloadConstant;
import com.download.task.DownloadTaskInfo;

public class DownloadUtils {
    /**
     * Judge the WIFI state .
     *
     * @return boolean
     */
    public static boolean isWiFiActive(Context inContext) {
        Context context = inContext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null && info.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取下载文件大小
     *
     * @param size
     * @return
     */
    public static String getTotalSize(long size) {
        double g = size / (1024 * 1024 * 1024f);
        if ((int) g > 0) {
            return String.format("%.2fG", g);
        }
        double m = size / (1024 * 1024f);
        if ((int) m > 0) {
            return String.format("%.1fM", m);
        }
        double k = size / 1024f;
        if ((int) k > 0) {
            return String.format("%.1fKB", k);
        }
        return size + "B";
    }

    /**
     * 获取已下载文件大小
     *
     * @param downloadSize
     * @param totalSize
     * @return
     */
    public static String getDownloadSize(long downloadSize, long totalSize) {
        if (totalSize / (1024 * 1024 * 1024) > 0) {
            if (downloadSize / (1024 * 1024 * 1024) == 0) {
                return String.format("%.1fM", downloadSize / (1024 * 1024f));
            } else {
                return String.format("%.2fG", downloadSize / (1024 * 1024 * 1024f));
            }
        }
        if (totalSize / (1024 * 1024) > 0) {
            return String.format("%.1fM", downloadSize / (1024 * 1024f));
        }

        if (downloadSize / 1024 > 0)
            return String.format("%.1fKB", downloadSize / 1024f);

        return downloadSize + "B";
    }

    public static DownloadTaskInfo createDownloadTaskInfo(String downloadId, int type, String title, String url, String filePath, String extra) {
        DownloadTaskInfo info = new DownloadTaskInfo();
        info.setDownloadId(downloadId);
        info.setType(type);
        info.setTitle(title);
        info.setUrl(url);
        // FileUtils.getDirPath() + File.separator + FileUtils.getFileName(url)
        info.setFilePath(filePath);
        info.setExtra(extra);
        info.setStatus(DownloadConstant.STATUS_READY);
        return info;
    }
}
