package com.download.notification;

import android.util.Log;

import com.download.task.DownloadTaskInfo;
import com.download.task.DownloadTaskListener;

import junit.framework.Assert;

/**
 * Created by yangtianfei on 17-6-29.
 */
public class NotificationTaskListener implements DownloadTaskListener {

    private final FileDownloadNotificationHelper helper;

    public NotificationTaskListener(FileDownloadNotificationHelper helper) {
        Assert.assertNotNull("FileDownloadNotificationHelper must not null", helper);
        this.helper = helper;
    }

    public FileDownloadNotificationHelper getHelper() {
        return helper;
    }

    public void addNotificationItem(DownloadTaskInfo task) {
        if (disableNotification(task)) {
            return;
        }

        final BaseNotificationItem n = create(task);
        if (n != null) {
            //noinspection unchecked
            this.helper.add(n);
        }
    }

    /**
     * The notification item with the {@code task} is told to destroy.
     *
     * @param task The task used to identify the will be destroyed notification item.
     */
    public void destroyNotification(DownloadTaskInfo task) {
        if (disableNotification(task)) {
            return;
        }

        this.helper.showIndeterminate((int)task.getId(), task.getStatus());

        final BaseNotificationItem n = this.helper.remove((int)task.getId());
        if (!interceptCancel(task, n) && n != null) {
            n.cancel();
        }
    }

    public void showIndeterminate(DownloadTaskInfo task) {
        if (disableNotification(task)) {
            return;
        }

        this.helper.showIndeterminate((int)task.getId(), task.getStatus());
    }

    public void showProgress(DownloadTaskInfo task, long soFarBytes, long totalBytes) {
        if (disableNotification(task)) {
            return;
        }

        this.helper.showProgress((int)task.getId(), soFarBytes, totalBytes);
    }

    /**
     * @param task The task used to bind with the will be created notification item.
     * @return The notification item is related with the {@code task}.
     */
    protected BaseNotificationItem create(DownloadTaskInfo task) {
        return new NotificationItem((int) task.getId(), task.getTitle(), "");
    }

    /**
     * @param task             The task.
     * @param notificationItem The notification item.
     * @return {@code true} if you want to survive the notification item, and we will don't  cancel
     * the relate notification from the notification panel when the relate task is finished,
     * {@code false} otherwise.
     * <p>
     * <strong>Default:</strong> {@code false}
     */
    protected boolean interceptCancel(DownloadTaskInfo task, BaseNotificationItem notificationItem) {
        return false;
    }

    /**
     * @param task The task.
     * @return {@code true} if you want to disable the internal notification lifecycle, and in this
     * case all method about the notification will be invalid, {@code false} otherwise.
     * <p>
     * <strong>Default:</strong> {@code false}
     */
    protected boolean disableNotification(final DownloadTaskInfo task) {
        return false;
    }

    @Override
    public void onReady(DownloadTaskInfo downloadInfo) {
//        Log.i("notification", "onReady");
        addNotificationItem(downloadInfo);
        showIndeterminate(downloadInfo);
    }

    @Override
    public void onStart(DownloadTaskInfo downloadInfo) {
        showIndeterminate(downloadInfo);
    }

    @Override
    public void onPause(DownloadTaskInfo downloadInfo) {
        destroyNotification(downloadInfo);
    }

    @Override
    public void update(DownloadTaskInfo downloadInfo, long tatolSize, long downloadSize) {
        showProgress(downloadInfo, downloadSize, tatolSize);
    }

    @Override
    public void onCancel(DownloadTaskInfo downloadInfo) {
        destroyNotification(downloadInfo);
    }

    @Override
    public void onComplete(DownloadTaskInfo downloadInfo, String path) {
        destroyNotification(downloadInfo);
    }

    @Override
    public void onFail(DownloadTaskInfo downloadInfo, int code, String msg) {
        destroyNotification(downloadInfo);
    }
}
